package main.java.model.idd;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class EnergyPlusTemplate implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = -5688066942020439655L;
    
    private HashMap<String, EnergyPlusGroupTemplate> groupMap;
    
    public EnergyPlusTemplate(){
        groupMap = new HashMap<String, EnergyPlusGroupTemplate>();
    }
    
    public void addEnergyPlusGroup(EnergyPlusGroupTemplate groupTemp){
        //System.out.println("Group name:" + groupTemp.getGroupName());
        groupMap.put(groupTemp.getGroupName(), groupTemp);
    }
    
    public void addEnergyPlusObject(String group, EnergyPlusObjectTemplate objTemp){
        //System.out.println("Object name:" + objTemp.getObjectName() + " add to Group: " + group);
        groupMap.get(group).addEnergyPlusObject(objTemp);
    }
    
    public EnergyPlusObjectTemplate getEplusObjectTemplate(String obj){
        Iterator<String> keyItr = groupMap.keySet().iterator();
        while(keyItr.hasNext()){
            EnergyPlusObjectTemplate temp = groupMap.get(keyItr.next()).getObjectTemplate(obj);
            if(temp!=null){
                return temp;
            }
        }
        return null;//this object is never exist
    }
    
    public EnergyPlusGroupTemplate getEnergyPlusGroup(String group){
    	return groupMap.get(group);
    }
    
    public Set<String> getListofEnergyPlusObjectTemplate(){
    	return groupMap.keySet();
    }

}
