package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.gameServer.Configurator;
import it.polimi.ingsw.utility.Configuration;
import it.polimi.ingsw.utility.Debugger;
import it.polimi.ingsw.socketServer.SocketServer;
import it.polimi.ingsw.gameServer.Room;
import it.polimi.ingsw.rmiServer.RMIServer;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Main server class that extends {@link ServerInterface}.
 * This class contains the main method to launch the server.
 * It represent the game server.
 */
public class Server implements ServerInterface{

    /**
     * SocketClient port.
     */
    private static final int SOCKET_PORT = 3031;

    /**
     * RMI port.
     */
    private static final int RMI_PORT = 3032;

    /**
     * Mutex object to handle concurrency between users during loginPlayer.
     */
    private static final Object LOGIN_SIGNIN_MUTEX = new Object();

    /**
     * Mutex object to handle concurrency during room create.
     */
    private static final Object JOIN_ROOM_MUTEX = new Object();

    /**
     * RMI server.
     */
    private RMIServer rmiServer;

    /**
     * SocketClient server.
     */
    private SocketServer socketServer;

    /**
     * MySQL server.
     */
    private DBServer dbServer;

    /**
     * Map of all logged in players
     */
    private HashMap<String, ServerPlayer> players;

    /**
     * Room list.
     */
    private ArrayList<Room> rooms;

    /**
     * SQL connection object
     */
    private Connection connection;

    /**
     * Class constructor.
     */
    public Server() throws ServerException{
        rmiServer = new RMIServer(this);
        socketServer = new SocketServer(this);
        players = new HashMap<String, ServerPlayer>();
        rooms = new ArrayList<Room>();
        dbServer = new DBServer();
        configure();
    }

    /**
     * Main method to launch the server.
     * @param args passed to server.
     */
    public static void main(String[] args){
        try {
            Server server = new Server();
            server.startSocketRMIServer(SOCKET_PORT, RMI_PORT);
            server.startDatabase();
            Debugger.printStandardMessage("Socket server ready.\nRMI server ready.\nSQL server ready.");
        } catch(ServerException | SQLException e){
            Debugger.printDebugMessage("Server.java", "Error while starting the server.", e);
        }
    }

    /**
     * Load and set configurations from file.
     */
    private void configure() throws ServerException{
        try{
            Configurator.loadConfigurations();
        }catch(ConfigurationException e){
            throw new ServerException("Error in game configuration and parsing proceedings.", e);
        }
    }

    /**
     * Method to initialize and start socket server and RMI server.
     * @param socketPort of socket server.
     * @param rmiPort of RMI server.
     * @throws IOException if errors occur during initialization.
     */
    private void startSocketRMIServer(int socketPort, int rmiPort) throws ServerException{
        socketServer.startServer(socketPort);
        rmiServer.startServer(rmiPort);
    }

    /**
     * Method to initialize and start database server.
     * @throws SQLException if errors occur during initialization.
     */
    private void startDatabase() throws SQLException{
        dbServer.connectToDatabase();
    }

    /**
     * Sign in the player to server.
     * @param username of the player is trying to sign in.
     * @param password of the player is trying to sign in.
     * @throws LoginException if errors occur during sign in.
     */
    @Override
    public void signInPlayer(String username, String password) throws LoginException{
        synchronized (LOGIN_SIGNIN_MUTEX) {
            if(!players.containsKey(username))
                dbServer.signInPlayer(username, password);
            else
                throw new LoginException(LoginErrorType.USER_ALREADY_EXISTS);
        }
    }

    /**
     * Login the player to server then put username and remote player reference in the user cache (Hashmap).
     * @param player is trying to login.
     * @param username of the player is trying to login.
     * @param password of the player is trying to login.
     * @throws LoginException if errors occur during login.
     */
    @Override
    public void loginPlayer(ServerPlayer player, String username, String password) throws LoginException{
        synchronized (LOGIN_SIGNIN_MUTEX) {
            if(!players.containsKey(username)) {
                dbServer.loginPlayer(username, password);
                player.setNickname(username);
                players.put(username, player);
            }
            else
                throw new LoginException(LoginErrorType.USER_ALREADY_LOGGEDIN);

        }
    }

    /**
     * Method to get remote player reference from the user cache.
     * @param username of the remote player.
     * @return remote player that corresponds to username provided.
     */
    @Override
    public ServerPlayer getUser(String username){
        return players.get(username);
    }

    /**
     * Method used to join a player into a room.
     * @param serverPlayer who would join in a room.
     * @throws RoomException if error occurs.
     */
    @Override
    public void joinRoom(ServerPlayer serverPlayer) throws RoomException{
        synchronized (JOIN_ROOM_MUTEX){
            Room room;
            if(!rooms.isEmpty()){
                room = rooms.get(rooms.size() - 1);
                room.joinRoom(serverPlayer);
                serverPlayer.setRoom(room);
            }
            else {
                throw new RoomException("There are no rooms available!");
            }
        }
    }

    /**
     * Create a new room.
     * @param serverPlayer is creating new room.
     * @param maxPlayers allowed in the room.
     * @return configuration object.
     */
    @Override
    public Configuration createNewRoom(ServerPlayer serverPlayer, int maxPlayers) throws RoomException{
        synchronized (JOIN_ROOM_MUTEX){
            try{
                joinRoom(serverPlayer);
            }
            catch(RoomException e){
                Configuration configuration = Configurator.getConfiguration();
                Room room = new Room(serverPlayer, maxPlayers, configuration);
                rooms.add(room);
                serverPlayer.setRoom(room);
                return configuration;
            }
            throw new RoomException();
        }
    }

}
