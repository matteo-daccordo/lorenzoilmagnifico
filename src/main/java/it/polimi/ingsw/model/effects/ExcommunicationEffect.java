package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.model.InformationCallback;
import it.polimi.ingsw.model.Player;

import java.io.Serializable;


public abstract  class ExcommunicationEffect implements Serializable {

    /**
     * Effect type
     */
    public String effectType;

    /**
     * Method to run the effect of the card.
     * @param player
     */
    abstract public void runEffect(Player player);

    /**
     * Get a description of the current effect.
     */
    abstract public String getDescription();


}
