package it.polimi.ingsw.rmiClient;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.PersonalBoardTile;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This class represents the RMI client interface used to remote method invocation
 * from the server to the client.
 */
public interface RMIClientInterface extends Remote{

    void sendPersonalTiles(List<PersonalBoardTile> personalBoardTileList) throws RemoteException;

    void sendGame(Game game) throws RemoteException;

    String ping() throws RemoteException;
}
