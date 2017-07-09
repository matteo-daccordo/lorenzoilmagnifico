package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

public class FamilyMember implements Serializable{

    private Map<FamilyMemberColor, Integer> members;

    public FamilyMember(){
        this.members = new EnumMap<>(FamilyMemberColor.class);
        for(FamilyMemberColor familyMemberColor : FamilyMemberColor.values())
            members.put(familyMemberColor, 0);
    }

    public void setMembers(Map<FamilyMemberColor, Integer> members){
        for(Map.Entry pair : members.entrySet())
            this.members.put((FamilyMemberColor)pair.getKey(), (int)pair.getValue());
    }

    public Map<FamilyMemberColor, Integer> getMembers(){
        return this.members;
    }

    public void setFamilyMemberValue(FamilyMemberColor color, int value){
        this.members.replace(color, value);
    }

    public void increaseFamilyMemberValue(FamilyMemberColor color, int value){
        this.members.put(color, this.members.get(color) + value);
    }

    public void decreaseFamilyMemberValue(FamilyMemberColor color, int value){
        this.members.put(color, this.members.get(color) - value);
    }

}
