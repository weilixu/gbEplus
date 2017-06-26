package main.java.model.idd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class EnergyPlusObjectTemplate implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = -2045243342890828089L;
    //object type element
    private static final String MEMOTOKEN = "\\memo";
    private static final String UNIQUETOKEN = "\\unique-object";
    private static final String REQUIREDTOKEN = "\\required-object";
    private static final String MINFIELDSTOKEN = "\\min-fields";
    private static final String OBSOLETETOKEN = "\\obsolete";
    private static final String EXTENSIBLETOKEN = "\\extensible";
    private static final String FORMATTOKEN = "\\format";
    private static final String REFCLASSNAME = "\\reference-class-name";
    
    ArrayList<EnergyPlusFieldTemplate> fieldList;
    HashMap<String, Integer> fieldNameMap; //ease for search
    HashMap<String, Integer> fieldIDMap;
    
    private String name;
    
    private int numOfFields = 0;
    
    private int numOfMinField=-1;
    private StringBuffer memo;
    
    private boolean uniqueObject = false;
    private boolean requiredObject = false;
    private boolean obsolete = false;
    private boolean extensible = false;
    private int beginExtensible = -1;
    private int numOfExtensible = -1;
    private String format;
    
    public EnergyPlusObjectTemplate(String name){
        this.name = name;
        fieldList = new ArrayList<EnergyPlusFieldTemplate>();
        fieldNameMap = new HashMap<String, Integer>();
        fieldIDMap = new HashMap<String, Integer>();
        memo = new StringBuffer();
    }
    
    /**
     * example of ID: "A1" or "N1"
     * @param ID
     * @return
     */
    public EnergyPlusFieldTemplate getFieldTemplateByID(String ID){
        return fieldList.get(fieldIDMap.get(ID));
    }
    
    public EnergyPlusFieldTemplate getFieldTemplateByName(String name){
        return fieldList.get(fieldNameMap.get(name.toLowerCase()));
    }
    
    public EnergyPlusFieldTemplate getFieldTemplateByIndex(int index){
        return fieldList.get(index);
    }
    
    //getter methods
    public String getObjectName(){
        return name;
    }
    
    public boolean isUniqueObject(){
        return uniqueObject;
    }
    
    public boolean isRequiredObject(){
        return requiredObject;
    }
    
    public boolean isObsolete(){
        return obsolete;
    }
    
    public boolean isExtensible(){
        return extensible;
    }
    
    public int numOfExtensibles(){
        return numOfExtensible;
    }
    
    public String getFormat(){
        return format;
    }
    
    public String getMemo(){
        return memo.toString();
    }
    
    /**
     * Indicates the number of minimum fields for this object
     * @return
     */
    public int getNumberOfMinFields(){
        return numOfMinField;
    }
    
    public int getNumberOfFields(){
        return numOfFields;
    }
    
    public void processElement(String line){
        if(line.contains(MEMOTOKEN)){
            memo.append(getContentFromLine(line,MEMOTOKEN));
        }else if(line.contains(UNIQUETOKEN)){
            uniqueObject = true;
        }else if(line.contains(REQUIREDTOKEN)){
            requiredObject = true;
        }else if(line.contains(MINFIELDSTOKEN)){
            numOfMinField = Integer.parseInt(getContentFromLine(line, MINFIELDSTOKEN));
        }else if(line.contains(OBSOLETETOKEN)){
            obsolete = true;
        }else if(line.contains(EXTENSIBLETOKEN)){
            extensible = true;
            
            //get the number of extensible variables
            StringBuffer sb = new StringBuffer();
            int index = line.indexOf(":")+1;
            while(index < line.length() && line.charAt(index)!=' '){
                sb.append(line.charAt(index));
                index++;
            }

            numOfExtensible = Integer.parseInt(sb.toString());
        }else if(line.contains(FORMATTOKEN)){
            format = getContentFromLine(line, FORMATTOKEN);
        }else if(line.contains(REFCLASSNAME)){
            //so far no reference class name element
        }
    }
    
    //record the beginning of extensible field sets
    public void setTheBeginningOfExtensible(){
        beginExtensible = numOfFields - 1;
    }
    
    /**
     * 
     * @return -1 no extensible, >0 yes
     */
    public Integer getBeginningOfExtensible(){
        return beginExtensible;
    }
    
    public void addObjectField(EnergyPlusFieldTemplate temp){
        //System.out.println("Field Name: " + temp.getFieldName());
        
        fieldList.add(temp);
        fieldNameMap.put(temp.getFieldName().toLowerCase(), numOfFields);//recording the location
        fieldIDMap.put(temp.getFieldType()+temp.getFieldNumber(), numOfFields);//pointer to the other field within this object
        numOfFields++;
    }
    
    private String getContentFromLine(String line, String token){
        if(line.length() == token.length()){
            //in case empty comments in a token
            return "";
        }
        return line.substring((line.indexOf(token) + token.length())+1, line.length());
    }
}
