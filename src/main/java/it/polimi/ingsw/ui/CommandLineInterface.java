package it.polimi.ingsw.ui;

import java.io.*;

/**
 * This class manages the command line interface of the game.
 */

public class CommandLineInterface extends AbstractUI {


    private static final String TITLE=  "     __     _____     ______     ________  __      ___ _________  ______\n" +
                                        "    /  /   /     \\   /  _   \\   /  ______//  \\    /  //_____  _/ /      \\ \n" +
                                        "   /  /   /   _   \\ /  (_)  /  /  /___   /    \\  /  / _____/ /  /   _    \\ \n" +
                                        "  /  /   /   (_)  //  __   /  /   ___/  /  /\\  \\/  / /_  ___/  /   (_)   / \n" +
                                        " /  /___ \\       //  /  \\ \\  /   /____ /  /  \\    /   / /______\\        / \n" +
                                        " \\______/ \\_____//__/    \\_\\/________//__/    \\__/   /________/ \\______/ \n" +
                                        "                                                                              \n" +
                                        "                         _  _     _   __           __    __  __             \n" +
                                        "                 / /    / \\/ \\   /_\\ / __ /\\  / / /_  / /   /  \\              \n"+
                                        "                / /__  /      \\ /   \\__//  \\/ / /   /  \\__ \\__/              \n";



    private PrintWriter console= new PrintWriter(new OutputStreamWriter(System.out));
    private BufferedReader keyboard= new BufferedReader(new InputStreamReader(System.in));
    private ContxtInterface contxtInterface;

    private BaseContxt context;

    public CommandLineInterface(UiController controller){
        super(controller);
        console.println(TITLE);
    }

    @Override
    public void showNewtworkMenu(){
        console.println("Loading Network Menu");

        context= new NetworkContext(contxtInterface, getController()::setNetworkSetting);
    }

    @Override
    public void showLoginMenu() throws IOException {
        try {
            console.println("You need to enter 'login' before start playing");
            String line =keyboard.readLine();
            context.handle(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        context= new LoginContxt(contxtInterface, getController()::loginPlayer);
    }

    @Override
    public void notifyLoginError() {

    }

    @Override
    public void notifyLoginSuccess() {

    }

    @Override
    public void showRoomMenu() {

    }

    @Override
    public void notifyCreatingRoomSuccess() {

    }

    @Override
    public void notifyCreatingRoomFailed() {

    }

    @Override
    public void notifyJoinRoomSuccess() {

    }

    @Override
    public void notifyJoinRoomFailed() {

    }

    @Override
    public void showGameConfigurationMenu() {

    }

    @Override
    public void notifyGameConfiguationDone() {

    }

    @Override
    public void notifyGameConfigurationError() {

    }

    @Override
    public void notifyGameBeginning() {

    }

    @Override
    public void notifyTurnBeginning(String nickname) {

    }

    @Override
    public void notifyTurnEnd(String nickname) {

    }

    @Override
    public void showMainBoard(Stato update) {

    }

    @Override
    public void showPersonalBoard(String nickname, Stato update) {

    }

    @Override
    public void showPersonalBonusTile(String nickname) {

    }

    @Override
    public void showDevelopmentCard() {

    }

    @Override
    public void showTower(Status update) {

    }

    @Override
    public void showCouncilPalace(Status update) {

    }

    @Override
    public void showMarket(Status update) {

    }

    @Override
    public void showProductionArea(Status update) {

    }

    @Override
    public void showHarvestArea(Status update) {

    }

    @Override
    public void showDices(Status update) {

    }

    @Override
    public void showImmediateEffect() {

    }

    @Override
    public void showMoveRequirements() {

    }

    @Override
    public void showAllMyCards() {

    }

    @Override
    public void showLeader() {

    }

    @Override
    public void showResources(String nickname) {

    }

    @Override
    public void showCouncilPrivilegeChoises() {

    }

    @Override
    public void notifyEnoughCardsError() {

    }

    @Override
    public void showPoints(String nickname, Status update) {

    }

    @Override
    public void notifyImmediateEffect() {

    }

    @Override
    public void notifyPermanentEffect() {

    }

    @Override
    public void notifyUnsuccessMove() {

    }

    @Override
    public void notifyOccupiedTower() {

    }

    @Override
    public void notifyOccupiedActionSpace() {

    }

    @Override
    public void showExcommunicationMenu() {

    }

    @Override
    public void notifyVaticanAction() {

    }

}
