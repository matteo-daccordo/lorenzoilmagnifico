package it.polimi.ingsw.lorenzo;

import java.util.Scanner;

/**
 * This class is used to launch the client side game.
 */
public class GameLauncher {

    public static void main(String[] args) throws InterruptedException {
        LorenzoIlMagnifico game = new LorenzoIlMagnifico(chooseUI());
        game.start();
    }

    /**
     * get the index of the preferred user interface.
     * @return user interface index.
     */
    private static int chooseUI(){
        int choice = 0;
        Scanner scanner = new Scanner(System.in);

        System.out.println("To use the Command Line Interface you need to insert the number\ncorrespondent to command and follow the instructions.");
        do{
            try {
                System.out.println("Choose your user interface:");
                System.out.println("(1) COMMAND LINE INTERFACE");
                System.out.println("(2) GRAPHICAL INTERFACE");
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e){
                return chooseUI();
            }
        } while (choice != 1 && choice != 2);
        return choice;
    }

}
