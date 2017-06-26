package main.java.model.idd;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public class EnergyPlusGroupTemplate implements Serializable{
    
    
    /**
     * 
     */
    private static final long serialVersionUID = -5438896251151750110L;

    private HashMap<String, EnergyPlusObjectTemplate> objectMap;
    
    private String name; //indicate the group name
    
    public EnergyPlusGroupTemplate(String name){
        this.name = name;
        objectMap = new HashMap<String, EnergyPlusObjectTemplate>();
    }
    
    public String getGroupName(){
        return name;
    }
    
    public void addEnergyPlusObject(EnergyPlusObjectTemplate temp){
        objectMap.put(temp.getObjectName().toLowerCase(),temp);
    }
    
    public EnergyPlusObjectTemplate getObjectTemplate(String objName){
        return objectMap.get(objName);
    }
    
    public Set<String> getObjectListFromGroup(){
    	return objectMap.keySet();
    }
}
