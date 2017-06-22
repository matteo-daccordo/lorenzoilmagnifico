package it.polimi.ingsw.model;

import it.polimi.ingsw.model.effects.Effect;

import java.io.Serializable;
import java.util.*;

/**
 * This class represents the personal board abstraction.
 */
public class PersonalBoard implements Serializable{

    private static final int MAX_NUMER_OF_CARD_PER_TYPE = 6;

    /**
     * Contains all values of points and resources.
     */
    private PointsAndResources valuables;

    /**
     * bonus: save the bonus dice values for harvest/production
     */
    private Map<ActionType, Integer> harvestProductionDiceValueBonus = new HashMap<>();

    /**
     * bonus: save the bonus dice values for cards
     */
    private Map<DevelopmentCardColor, Integer> developmentCardColorDiceValueBonus = new HashMap<>();

    /**
     * discounts: save the discount cost for development cards
     */
    private Map<DevelopmentCardColor, PointsAndResources> costDiscountForDevelopmentCard = new HashMap<>();

    /**
     * Family members;
     */
    private FamilyMember familyMember;

    /**
     * Array of family members already used by the player
     */
    private ArrayList<FamilyMemberColor> familyMembersUsed = new ArrayList<>();

    /**
     * Military points required to pick up a green card and place it in a specific position of the territory card array.
     */
    private static int[] greenCardsMilitaryPointsRequirements = new int[MAX_NUMER_OF_CARD_PER_TYPE];

    /**
     * Array of cards, divided per types.
     */
    private ArrayList<DevelopmentCard> territoryCards = new ArrayList<>();
    private ArrayList<DevelopmentCard> buildingCards = new ArrayList<>();
    private ArrayList<DevelopmentCard> characterCards = new ArrayList<>();
    private ArrayList<DevelopmentCard> ventureCards = new ArrayList<>();
    private ArrayList<LeaderCard> leaderCards;

    /**
     * Personal board tile choosen by the player.
     */
    private PersonalBoardTile personalBoardTile;

    public PersonalBoard(){
        valuables = new PointsAndResources();
        for(ActionType type : ActionType.values())
            harvestProductionDiceValueBonus.put(type, 0);

        for(DevelopmentCardColor color : DevelopmentCardColor.values()) {
            costDiscountForDevelopmentCard.put(color, new PointsAndResources());
            developmentCardColorDiceValueBonus.put(color, 0);
        }
        familyMembersUsed = new ArrayList<>();
        leaderCards = new ArrayList<>();
    }

    /**
     * Set personal board tile.
     * @param personalBoardTile to set.
     */
    public void setPersonalBoardTile(PersonalBoardTile personalBoardTile){
        this.personalBoardTile = personalBoardTile;
    }

    /**
     * Get personal board tile.
     * @return personal board tile.
     */
    public PersonalBoardTile getPersonalBoardTile() {
        return this.personalBoardTile;
    }

    /**
     * Set family member
     * @param member to be set.
     */
    public void setFamilyMember(FamilyMember member){
        this.familyMember = member;
    }

    /**
     * Get family member
     * @return
     */
    public FamilyMember getFamilyMember(){
        return this.familyMember;
    }

    /**
     * Set the array of family members already used by the player
     * @param familyMember
     */
    public void setFamilyMembersUsed(FamilyMemberColor familyMember){
        this.familyMembersUsed.add(familyMember);
    }

    /**
     * Get the array of family members already used by the player
     * @return
     */
    public ArrayList<FamilyMemberColor> getFamilyMembersUsed(){
        return this.familyMembersUsed;
    }

    /**
     * Return true if a family member is already used.
     * @param familyMemberColor to check.
     * @return boolean.
     */
    public boolean familyMemberIsUsed(FamilyMemberColor familyMemberColor){
        for(FamilyMemberColor color : familyMembersUsed)
            if(familyMemberColor.equals(color))
                return true;
        return false;
    }

    /**
     * Set military points needed to place a card in a specific position.
     * @param array
     */
    public void setGreenCardsMilitaryPointsRequirements(int[] array){
        greenCardsMilitaryPointsRequirements = array;
    }

    /**
     * Get military points needed to place a card in a specific position.
     * @param index of position.
     * @return military points needed.
     */
    public int getGreenCardsMilitaryPointsRequirements(int index){
        return greenCardsMilitaryPointsRequirements[index];
    }

    public int[] getGreenCardsMilitaryPointsRequirements(){
        return greenCardsMilitaryPointsRequirements;
    }

    /**
     * Add a new card to the player's personal board
     * @param card
     */
    public void addCard(DevelopmentCard card){
        switch (card.getColor()){
            case GREEN:
                this.territoryCards.add(card);
                break;
            case YELLOW:
                this.buildingCards.add(card);
                break;
            case BLUE:
                this.characterCards.add(card);
                break;
            case PURPLE:
                this.ventureCards.add(card);
                break;
        }
    }

    /**
     * Set points and resources
     * @param pointsAndResources
     */
    public void setValuables(PointsAndResources pointsAndResources){
        this.valuables = pointsAndResources;
    }

    /**
     * Get points and resources.
     * @return points and resources.
     */
    public PointsAndResources getValuables(){
        return this.valuables;
    }

    /**
     * Add territory card
     * @param card
     */
    public void addTerritoryCard(DevelopmentCard card){
        this.territoryCards.add(card);
    }

    /**
     * Get a specific territory card from the array.
     * @return a territory card.
     */
    public ArrayList<DevelopmentCard> getTerritoryCards(){
        return this.territoryCards;
    }

    /**
     * Add building card
     * @param card
     */
    public void addBuildingCard(DevelopmentCard card){
        this.buildingCards.add(card);
    }

    /**
     * Get a specific building card from the array.
     * @return a building card.
     */
    public ArrayList<DevelopmentCard> getBuildingCards(){
        return this.buildingCards;
    }

    /**
     * Add character card
     * @param card
     */
    public void addCharacterCard(DevelopmentCard card){
        this.characterCards.add(card);
    }

    /**
     * Get a specific character card from the array.
     * @return a character card.
     */
    public ArrayList<DevelopmentCard> getCharacterCards(){
        return this.characterCards;
    }

    /**
     * Add venture card
     * @param card
     */
    public void addVentureCard(DevelopmentCard card){
        this.ventureCards.add(card);
    }

    /**
     * Get a specific venture card from the array.
     * @return a venture card.
     */
    public ArrayList<DevelopmentCard> getVentureCards(){
        return this.ventureCards;
    }


    /**
     * Set the dice bonus value for harvest and production zones
     */
    public void setHarvestProductionDiceValueBonus(ActionType type, Integer value){
        this.harvestProductionDiceValueBonus.put(type, this.harvestProductionDiceValueBonus.get(type) + value);
    }

    /**
     * Get the dice bonus value for harvest and production zones
     * @return
     */
    public Map<ActionType, Integer> getHarvestProductionDiceValueBonus(){
        return this.harvestProductionDiceValueBonus;
    }

    /**
     * Set the dice development card bonus value based on card's color
     * @param cardColor
     * @param value
     */
    public void setDevelopmentCardColorDiceValueBonus(DevelopmentCardColor cardColor, Integer value){
        this.developmentCardColorDiceValueBonus.put(cardColor, this.developmentCardColorDiceValueBonus.get(cardColor) + value);
    }

    /**
     * Get the dice development card bonus value based on card's color
     * @return
     */
    public Map<DevelopmentCardColor, Integer> getDevelopmentCardColorDiceValueBonus(){
        return this.developmentCardColorDiceValueBonus;
    }

    /**
     * Set the cost discount value for a particular type of development cards
     * @param cardColor
     * @param valuables
     */
    public void setCostDiscountForDevelopmentCard(DevelopmentCardColor cardColor, PointsAndResources valuables){
        this.costDiscountForDevelopmentCard.put(cardColor, valuables);
    }

    /**
     * Get the cost discount value for a particular type of development cards
     * @return
     */
    public Map<DevelopmentCardColor, PointsAndResources> getCostDiscountForDevelopmentCard(){
        return this.costDiscountForDevelopmentCard;
    }

    public List<LeaderCard> getLeaderCards(){
        return this.leaderCards;
    }

    public void setLeaderCard(LeaderCard leaderCard){
        this.leaderCards.add(leaderCard);
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("RESOURCES\n");
        stringBuilder.append(valuables.toString());

        stringBuilder.append("FAMILY MEMBERS AVAILABLE\n");
        Iterator it = familyMember.getMembers().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(!familyMemberIsUsed((FamilyMemberColor)pair.getKey()))
                stringBuilder.append(pair.getKey().toString() + " = " + pair.getValue() + "\n");
        }

        stringBuilder.append("<TERRITORY CARDS>\n");
        for(DevelopmentCard card : territoryCards)
            stringBuilder.append(card.toString());
        stringBuilder.append("<BUILDING CARDS>\n");
        for(DevelopmentCard card : buildingCards)
            stringBuilder.append(card.toString());
        stringBuilder.append("<CHARACTERS CARDS>\n");
        for(DevelopmentCard card : characterCards)
            stringBuilder.append(card.toString());
        stringBuilder.append("<VENTURE CARDS>\n");
        for(DevelopmentCard card : ventureCards)
            stringBuilder.append(card.toString());

        return stringBuilder.toString();
    }

}
