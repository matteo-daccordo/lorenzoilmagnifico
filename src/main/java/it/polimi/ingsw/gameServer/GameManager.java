package it.polimi.ingsw.gameServer;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.ServerPlayer;
import it.polimi.ingsw.utility.Configuration;

import java.util.*;

/*package-local*/ class GameManager{

    private static final int INITIAL_COINS = 5;

    /**
     * Game instance.
     */
    private Game game;

    /**
     * Development cards yellow deck.
     */
    private ArrayList<DevelopmentCard> yellowDeck;

    /**
     * Development cards green deck.
     */
    private ArrayList<DevelopmentCard> greenDeck;

    /**
     * Development cards blue deck.
     */
    private ArrayList<DevelopmentCard> blueDeck;

    /**
     * Development cards purple deck.
     */
    private ArrayList<DevelopmentCard> purpleDeck;

    /**
     * Leader cards deck.
     */
    private ArrayList<LeaderCard> leaderCards;

    /**
     * Player of the game.
     */
    private ArrayList<ServerPlayer> players;

    /**
     * Game configuration.
     */
    private Configuration configuration;

    /**
     * Class constructor.
     * @param players of the room.
     * @param configuration of the game.
     * @param developmentCards deck.
     */
    /*package-local*/ GameManager(ArrayList<ServerPlayer> players, Configuration configuration, ArrayList<DevelopmentCard> developmentCards, ArrayList<LeaderCard> leaderCards){
        this.players = players;
        this.configuration = configuration;
        this.leaderCards = leaderCards;
        game = new Game(configuration.getMainBoard(), this.players);
        setupPlayers();
        setupDecks(developmentCards);
    }

    /**
     * Setup and mix development card decks by color
     * @param deck
     */
    private void setupDecks(ArrayList<DevelopmentCard> deck){
        this.yellowDeck = new ArrayList<>();
        this.greenDeck = new ArrayList<>();
        this.blueDeck = new ArrayList<>();
        this.purpleDeck = new ArrayList<>();

        for (DevelopmentCard card : deck){
            switch (card.getColor()){
                case YELLOW:
                    this.yellowDeck.add(card);
                    Collections.shuffle(this.yellowDeck);
                    break;
                case GREEN:
                    this.greenDeck.add(card);
                    Collections.shuffle(this.greenDeck);
                    break;
                case BLUE:
                    this.blueDeck.add(card);
                    Collections.shuffle(this.blueDeck);
                    break;
                case PURPLE:
                    this.purpleDeck.add(card);
                    Collections.shuffle(this.purpleDeck);
                    break;
            }
        }
    }

    /**
     * Setup development card decks by period
     * @param deck
     * @param period
     * @return
     */
    private ArrayList<DevelopmentCard> deckForPeriod(ArrayList<DevelopmentCard> deck, int period){
        ArrayList<DevelopmentCard> deckPeriod = new ArrayList<>();
        for (DevelopmentCard card : deck)
            if (card.getPeriod() == period)
                deckPeriod.add(card);
        return deckPeriod;
    }

    /**
     * setup a development card deck by period turn
     * @param deck
     * @param turn
     * @return
     */
    private ArrayList<DevelopmentCard> deckForTurn(ArrayList<DevelopmentCard> deck, int turn){
        ArrayList<DevelopmentCard> deckTurn = new ArrayList<>(deck.subList(4 * (turn - 1), 4 * turn));
        return deckTurn;
    }

    /**
     * Method to setup towers' cards
     * @param period
     * @param turn
     */
    public void setupMainBoard(int period, int turn){
        this.game.getMainBoard().setTower(0, deckForTurn(deckForPeriod(this.greenDeck, period), turn));
        this.game.getMainBoard().setTower(1, deckForTurn(deckForPeriod(this.blueDeck, period), turn));
        this.game.getMainBoard().setTower(2, deckForTurn(deckForPeriod(this.yellowDeck, period), turn));
        this.game.getMainBoard().setTower(3, deckForTurn(deckForPeriod(this.purpleDeck, period), turn));
    }

    /**
     * Method to setup player color, personal board and increase coin amount following game rules.
     * Then the player is added to Game players map.
     */
    private void setupPlayers(){
        int i = 0;

        randomPlayerSorting();

        Map<String, PlayerColor> colors = PlayerColor.getHashMap();
        Iterator iterator = colors.entrySet().iterator();

        for(ServerPlayer player : players){
            Map.Entry pair = (Map.Entry) iterator.next();
            player.setColor((PlayerColor)pair.getValue());
            player.setPersonalBoard(createNewPersonalBoard());
            player.getPersonalBoard().getValuables().increase(ResourceType.COIN, INITIAL_COINS + i);
            this.game.getPlayersMap().put(player.getUsername(), player);
            i++;
        }
    }

    /**
     * This method sorts players randomly. This is the game order.
     */
    private void randomPlayerSorting(){
        Collections.shuffle(players);
    }

    /**
     * Get game instance.
     * @return game instance.
     */
    /*package-local*/ Game getGameInstance(){
        setupMainBoard(1, 1);
        throwDices();
        return this.game;
    }

    /**
     * Get array list that represent order player.
     * @return players array list.
     */
    /*package-local*/ ArrayList<ServerPlayer> getStartOrder(){
        return players;
    }

    /**
     * Method to initialize a new personal board.
     * @return a personal board.
     */
    private PersonalBoard createNewPersonalBoard(){
        PersonalBoard personalBoard = new PersonalBoard();
        personalBoard.getValuables().increase(ResourceType.WOOD, 2);
        personalBoard.getValuables().increase(ResourceType.STONE, 2);
        personalBoard.getValuables().increase(ResourceType.SERVANT, 3);
        personalBoard.setGreenCardsMilitaryPointsRequirements(configuration.getPersonalBoard().getGreenCardsMilitaryPointsRequirements());
        FamilyMember familyMember = new FamilyMember();
        personalBoard.setFamilyMember(familyMember);
        return personalBoard;
    }

    /**
     * Throw dices and set value in each personal board.
     */
    public void throwDices(){
        game.getDices().setValues();
        for(Player player : players)
            player.getPersonalBoard().getFamilyMember().setMembers(game.getDices().getValues());
    }
}
