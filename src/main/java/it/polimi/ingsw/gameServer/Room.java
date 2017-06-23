package it.polimi.ingsw.gameServer;

import it.polimi.ingsw.exceptions.NetworkException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.utility.Configuration;
import it.polimi.ingsw.utility.Debugger;
import it.polimi.ingsw.exceptions.RoomException;
import it.polimi.ingsw.server.ServerPlayer;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * This class represent a game room.
 */
public class Room {

    private static final int MIN_PLAYER_TO_START = 2;
    private static final long IMMEDIATE_START_TIME = 0L;
    private static final int LEADER_CARD_PER_PLAYER = 4;
    private static final int AGES = 3;
    private static final int TURNS_PER_AGE = 2;

    /**
     * Room identifier.
     */
    private int roomID;

    /**
     * Mutex object to synchronize room access.
     */
    private static final Object MUTEX = new Object();

    /**
     * Max player allowed in the room.
     */
    private final int maxPlayerNumber;

    /**
     * Timer used to run a task after a specified amount of time.
     */
    private Timer startGameTimer;

    /**
     * Configurator.
     */
    private Configuration roomConfiguration;

    /**
     * Maximum time before game starts.
     */
    private long maxWaitingTimeBeforeStart;

    /**
     * Player max time to make a move.
     */
    private long maxMoveWaitingTime;

    /**
     * Flag that indicates if the room is open.
     * True -> open. False -> closed.
     */
    private boolean roomOpen;

    /**
     * Instance of the server game.
     */
    private GameManager gameManager;

    /**
     * List of all players that have joined the room.
     */
    private ArrayList<ServerPlayer> players = new ArrayList();

    /**
     * Countdown latch used as semaphore.
     */
    private CountDownLatch countDownLatch;

    private Game game;

    private PlayerTurn playerTurn;

    /**
     * Class constructor.
     * Set max number of player in the room.
     * Set the room in open state.
     * Add player in the list.
     * Set room configuration.
     */
    public Room(int id, ServerPlayer serverPlayer, int number, Configuration configuration){
        players = new ArrayList<>();
        roomOpen = true;
        maxPlayerNumber = number;
        players.add(serverPlayer);
        configureGame(configuration);
        roomID = id;
    }

    /**
     * Get room id.
     * @return room id.
     */
    public int getRoomID(){
        return this.roomID;
    }

    /**
     * Get the configuration from the server and configure the room.
     * @param configuration bundle.
     */
    private void configureGame(Configuration configuration){
        this.roomConfiguration = configuration;
        maxWaitingTimeBeforeStart = configuration.getWaitingTime();
        maxMoveWaitingTime = configuration.getMoveWaitingTime();
    }

    /**
     * Method to start the timer. At the end of set time, a task is executed.
     * @param time before run the task.
     */
    private void startTimer(long time){
        startGameTimer = new Timer();
        startGameTimer.schedule(new GameHandler(), time);
    }

    /**
     * Method to reset start game timer.
     */
    private void resetTimer(){
        if(startGameTimer != null) {
            startGameTimer.cancel();
            startGameTimer.purge();
        }
    }

    public void rejoinRoom(ServerPlayer serverPlayer){
        for(int i = 0; i < players.size(); i++){
            if(players.get(i).getUsername().equals(serverPlayer.getUsername()))
                players.set(i, serverPlayer);
        }
        Debugger.printDebugMessage(serverPlayer.getUsername() + " has rejoined the previous room.");
        //serverPlayer.sendGame(gameManager.game);
        //__________________________________
    }

    /**
     * Join a player in the room. Using MUTEX concurrent access is managed.
     * @param serverPlayer who would access the room.
     * @throws RoomException if the room is full or closed.
     */
    public void joinRoom(ServerPlayer serverPlayer) throws RoomException{
        synchronized (MUTEX){
            if(roomOpen){
                players.add(serverPlayer);
                if(players.size() == maxPlayerNumber){
                    roomOpen = false;
                    resetTimer();
                    startTimer(IMMEDIATE_START_TIME);
                    Debugger.printDebugMessage("Room #" + this.roomID + " starts in " + IMMEDIATE_START_TIME + " seconds.");
                }
                else if(players.size() == MIN_PLAYER_TO_START) {
                    startTimer(maxWaitingTimeBeforeStart);
                    Debugger.printDebugMessage("Room #" + this.roomID + " starts in " + maxWaitingTimeBeforeStart/1000 + " seconds.");
                }
            }
            else
                throw new RoomException();
        }
    }

    public boolean userAlreadyJoined(ServerPlayer serverPlayer){
        for(ServerPlayer player : players)
            if(player.getUsername().equals(serverPlayer.getUsername()))
                return true;
        return false;
    }

    public void onPersonalTilesChosen(){
        countDownLatch.countDown();
    }

    public void onLeaderCardChosen() {
        countDownLatch.countDown();
    }

    public void endTurn(ServerPlayer player) {
        if(playerTurn.currentPlayer().getUsername().equals(player.getUsername()))
            playerTurn.stopTimer();
    }

    /**
     * This class is used to manage the room during the game.
     */
    private class GameHandler extends TimerTask {



        /**
         * This method is executed when the time is expired. At first, it closes the room.
         * Then start the game.
         */
        @Override
        public void run(){
            setupBeforeStartGame();
            sendGameSession();
            Debugger.printDebugMessage("Game starts in room #" + getRoomID());
            startGameSession();
        }

        private void startGameSession(){
            for(int age = 1; age <= AGES; age++){
                for(int turn = 1; turn <= TURNS_PER_AGE; turn++){
                    for(ServerPlayer player : players){
                        System.out.println(player.getUsername());
                        playerTurn = new PlayerTurn(player);
                        notifyTurnStarted(player);
                        playerTurn.startTimer(maxMoveWaitingTime);
                    }
                }
            }
        }

        private void notifyTurnStarted(ServerPlayer player){
            for(ServerPlayer p : players)
                try {
                    p.notifyTurnStarted(player.getUsername(), maxMoveWaitingTime);
                } catch (NetworkException e){
                    Debugger.printDebugMessage(this.getClass().getSimpleName(), "Player offline.");
                }
        }

        /**
         * Close the room and get the game manager from configurator.
         */
        private void setupBeforeStartGame(){
            synchronized (MUTEX){
                roomOpen = false;
            }
            Debugger.printDebugMessage("[Room #" + roomID + "] : Room closed.");

            gameManager = Configurator.buildAndGetGame(players, roomConfiguration);
            players = gameManager.getStartOrder();

            personalTilesChoice(roomConfiguration.getPersonalBoardTiles());
            leaderCardsChoice(Configurator.getLeaderCards());

            game = gameManager.getGameInstance();
        }

        private void personalTilesChoice(ArrayList<PersonalBoardTile> personalBoardTileList){
            ArrayList<PersonalBoardTile> personalBoardtiles = new ArrayList<>();
            personalBoardtiles.addAll(personalBoardTileList);

            for(int i = players.size() - 1; i >= 0; i--) {
                countDownLatch = new CountDownLatch(1);
                try {
                    players.get(i).sendPersonalTile(personalBoardtiles);
                    countDownLatch.await();
                    for(int j = 0; j < personalBoardtiles.size(); j++)
                        if (personalBoardtiles.get(j).getPersonalBoardID() == players.get(i).getPersonalBoard().getPersonalBoardTile().getPersonalBoardID())
                            personalBoardtiles.remove(j);
                } catch (NetworkException | InterruptedException e) {
                    Debugger.printDebugMessage(this.getClass().getSimpleName(), "Player offline.");
                }
            }
        }

        private void leaderCardsChoice(ArrayList<LeaderCard> leaderCards){
            countDownLatch = new CountDownLatch(players.size());
            ArrayList<ServerPlayer> playersOrder = new ArrayList<>();
            playersOrder.addAll(players);
            ArrayList<LeaderCard> cards = new ArrayList<>();
            cards.addAll(leaderCards);
            Collections.shuffle(cards);
            cards = new ArrayList<>(cards.subList(0, (players.size() * LEADER_CARD_PER_PLAYER)));

            for(int i = 0; i < LEADER_CARD_PER_PLAYER; i++) {
                try {
                    sendArrays(cards, playersOrder);
                    countDownLatch.await();
                    removeChosenLeaderCards(cards);
                } catch (NetworkException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                playersOrder.add(playersOrder.remove(0));
                countDownLatch = new CountDownLatch(players.size());
            }
        }

        private void removeChosenLeaderCards(ArrayList<LeaderCard> leaderCards){
            for(ServerPlayer player : players){
                int i = player.getPersonalBoard().getLeaderCards().size() - 1;
                LeaderCard lc = player.getPersonalBoard().getLeaderCards().get(i);
                for(int j = 0; j < leaderCards.size(); j++)
                    if(leaderCards.get(j).getLeaderCardName().equals(lc.getLeaderCardName()))
                        leaderCards.remove(j);
            }
        }

        private void sendArrays(List<LeaderCard> leaderCards, List<ServerPlayer> serverPlayers) throws NetworkException{
            int cardNumberPerPlayer = leaderCards.size() / serverPlayers.size();
            int index = 0;

            for(ServerPlayer player : serverPlayers){
                player.sendLeaderCards(new ArrayList<>(leaderCards.subList(index * cardNumberPerPlayer, index * cardNumberPerPlayer + cardNumberPerPlayer)));
                index++;
            }
        }

        private void sendGameSession(){
            for(ServerPlayer player : players) {
                try {
                    player.sendGameInfo(game);
                } catch (NetworkException e) {
                    Debugger.printDebugMessage(this.getClass().getSimpleName(), "Player offline.");
                }
            }
        }

    }

}