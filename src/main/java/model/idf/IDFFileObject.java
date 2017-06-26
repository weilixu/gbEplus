package main.java.model.idf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IDFFileObject implements Serializable{
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    
    private static final long serialVersionUID = -713045092413712529L;
    
    /**
     * NOTE: This class has deepClone() function, any member change should change deepClone() accordingly
     */

    //Key is IDF object label
    private Map<String, ArrayList<IDFObject>> objMap = null;

    public IDFFileObject(){
        this.objMap = new HashMap<>();
    }

    public List<IDFObject> getCategoryList(String idfLabel){
        return objMap.get(idfLabel.toLowerCase());
    }
    
    public Map<String, ArrayList<IDFObject>> getObjMap(){
    	return objMap;
    }

    
    public String getIDFFileContent(){
        
        StringBuilder sb = new StringBuilder();

        Set<String> labels = objMap.keySet();
        for(String label: labels){
        	List<IDFObject> objs =  getCategoryList(label);
        	for(IDFObject obj: objs){
              sb.append(obj.printStatement()).append("\r\n");
        	}
        }
        
        return sb.toString();
    }
    
    /**
     * Version should be in 8.3 format
     * @return
     */
    public String getIDFVersion(){
        ArrayList<IDFObject> versionObjects = objMap.get("version");
        if(versionObjects==null){
            versionObjects = objMap.get("Version");
        }
        
        if(versionObjects == null){
            LOG.error("No Version Object found!");
            return null;
        }
        
        IDFObject versionObject = versionObjects.get(0);
        String version = versionObject.getName();
        
        //pick first two numbers
        String[] split = version.split("\\.");
        return split[0]+"."+split[1];
    }

    public int getSize(){
        return this.objMap.size();
    }
    
    
    public boolean addIDFObject(IDFObject idfObj){
        if(idfObj==null){
            return false;
        }
        
        String label = idfObj.getObjLabel().toLowerCase();
        
        if(!objMap.containsKey(label)){
            objMap.put(label, new ArrayList<IDFObject>());
        }
        
        objMap.get(label).add(idfObj);        
              
        return true;
    }
}
