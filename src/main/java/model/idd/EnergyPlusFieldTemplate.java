package main.java.model.idd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EnergyPlusFieldTemplate implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = -2596125865026506241L;
    private char fieldType; //A-alpha, N-numeric
    private Integer fieldNumber;
    
    //field type element
    private static final String FIELDTOKEN = "\\field"; //name of field
    private static final String NOTETOKEN = "\\note"; //describing the field and its valid values
    private static final String REQUIRETOKEN = "\\required-field"; //flag fields which may not be left blank
    private static final String BEGINEXTENSIBLETOKEN = "\\begin-extensible"; //first field at which the object accepts an extensible field set
    private static final String UNITSTOKEN = "\\units"; //units, SI
    private static final String IPUNITSTOKEN = "\\ip-units"; //IP-Units for IP unit processors
    private static final String UNITBASEDFIELDTOKEN = "\\unitsBasedOnField";//For field that may have multiple possible units
    private static final String MINTOKEN = "\\minimum";//inclusive minimum
    private static final String GREATMINTOKEN = "\\minimum>";//minimum must be greater than the following value
    private static final String MAXTOKEN = "\\maximum";//inclusive maximum
    private static final String LESSMAXTOKEN = "\\maximum<";//maximum must be less than the following value
    private static final String DEFAULTTOKEN = "\\default";//default for the field (if N/A then omit entire line)
    private static final String DEPRECATEDTOKEN = "\\deprecated";//no longer used - omit
    private static final String AUTOSIZETOKEN = "\\autosizable";//autosize capability - "Autosize" to trigger the flag
    private static final String AUTOCALTOKEN = "\\autocalculatable";//autocalculate capability - "Autocalculate" to trigger the flag
    private static final String TYPETOKEN = "\\type";//type of data (integer, real, alpha, choice, object-list, external-list, node)
    private static final String RETAINCASETOKEN = "\\retaincase";// retains the alphabetic case for alpha type fields
    private static final String KEYTOKEN = "\\key"; //possible value for "\type choice" use multiple \key lines
    private static final String OBJECTLISTTOKEN = "\\object-list";//name of a list of user-provided object names (use with \reference)
    private static final String EXTERNALLISTTOKEN = "\\external-list";//the value for this field should selected from a special list generated outside of the IDD file
    private static final String REFERENCETOKEN = "\\reference";//name of a list of names to which this object belongs used with "\type object-list" and with "object-list"
    
    private String fieldName;
    private StringBuffer note;
    private boolean required = false;
    private boolean beginOfExtensible = false;
    private boolean autosizable = false;
    private boolean autocalculatable = false;
    private boolean isRetainCase = false;
    
    private String unit = "";
    private String ipUnit = "";
    private String unitsBasedFieldID = null; //pointer to the unit list field
    private Double inclusiveMin = Double.NEGATIVE_INFINITY;
    private Double Min = Double.NEGATIVE_INFINITY;
    private Double inclusiveMax = Double.MAX_VALUE;
    private Double Max = Double.MAX_VALUE;
    private String fieldDefault = null;
    private String type = null;
    
    private List<String> keyList = new ArrayList<String>();
    private List<String> reference = new ArrayList<String>();
    private List<String> objectListRef = new ArrayList<String>();
    
    
    //private String objectListRef = null;
    private String externalListRef = null;
    
    public EnergyPlusFieldTemplate(String line){
        
        //first line format: A1, \field Do Zone Sizing Calculation
        fieldType = line.charAt(0);
        //get the number of variables
        StringBuffer sb = new StringBuffer();
        int index = 1;
        //System.out.println(line);
        while((line.charAt(index)!=' ' && line.charAt(index)!=',' && line.charAt(index)!=';' ) && index < line.length()){
            sb.append(line.charAt(index));
            index++;
        }
        //sb.deleteCharAt(sb.length()-1);
        
        fieldNumber = Integer.parseInt(sb.toString());        
        note = new StringBuffer();
        
        //the first line also contains the field name e.g. Do Zone Sizing Calculation
        fieldName = getContentFromLine(line, FIELDTOKEN);
    }
    
    //getter functions
    public String getFieldName(){
        return fieldName;
    }
    
    public String getFieldType(){
        return Character.toString(fieldType);
    }
    
    public Integer getFieldNumber(){
        return fieldNumber;
    }
    
    public String getNote(){
        return note.toString();
    }
    
    public boolean isRequired(){
        return required;
    }
    
    public boolean isBeginOfExtensible(){
        return beginOfExtensible;
    }
    
    public boolean isAutoSizable(){
        return autosizable;
    }
    
    public boolean isAutoCalculatable(){
        return autocalculatable;
    }
    
    public boolean isRetainCase(){
        return isRetainCase;
    }
    
    public String getUnit(){
        return unit;
    }
    
    public String getIPUnit(){
        return ipUnit;
    }
    
    public String getUnitBasedField(){
        return unitsBasedFieldID;
    }
    
    public Double getInclusiveMin(){
        return inclusiveMin;
    }
    
    public Double getMin(){
        return Min;
    }
    
    public Double getInclusiveMax(){
        return inclusiveMax;
    }
    
    public Double getMax(){
        return Max;
    }
    
    public String getDefault(){
        return fieldDefault;
    }
    
    public String getType(){
        return type;
    }
    
    public List<String> getKeys(){
        return keyList;
    }
    
    public List<String> getObjectListRef(){
        return objectListRef;
    }
    
    public String getExternalListRef(){
        return externalListRef;
    }
    
    public List<String> getReference(){
        return reference;
    }
    
    public void processElement(String line){
        if(line.contains(NOTETOKEN)){
            note.append(getContentFromLine(line,NOTETOKEN));
        }else if(line.contains(REQUIRETOKEN)){
            required = true;
        }else if(line.contains(BEGINEXTENSIBLETOKEN)){
            beginOfExtensible = true;
        }else if(line.contains(UNITSTOKEN)){
            unit = getContentFromLine(line,UNITSTOKEN);
        }else if(line.contains(IPUNITSTOKEN)){
            ipUnit = getContentFromLine(line,IPUNITSTOKEN);
        }else if(line.contains(UNITBASEDFIELDTOKEN)){
            //this means this field could have multiple units
            unitsBasedFieldID = getContentFromLine(line,UNITBASEDFIELDTOKEN);
        }else if(line.contains(MINTOKEN)){
            inclusiveMin = Double.parseDouble(getContentFromLine(line,MINTOKEN));
        }else if(line.contains(GREATMINTOKEN)){
            Min = Double.parseDouble(getContentFromLine(line, GREATMINTOKEN));
        }else if(line.contains(MAXTOKEN)){
            inclusiveMax = Double.parseDouble(getContentFromLine(line, MAXTOKEN));
        }else if(line.contains(LESSMAXTOKEN)){
            Max = Double.parseDouble(getContentFromLine(line,LESSMAXTOKEN));
        }else if(line.contains(DEFAULTTOKEN)){
            fieldDefault = getContentFromLine(line,DEFAULTTOKEN);
        }else if(line.contains(DEPRECATEDTOKEN)){
            //nothing should do - the system won't process it
            //just leave it blank or add a ,
        }else if(line.contains(AUTOSIZETOKEN)){
            autosizable = true;
        }else if(line.contains(AUTOCALTOKEN)){
            autocalculatable = true;
        }else if(line.contains(TYPETOKEN)){
            type = getContentFromLine(line,TYPETOKEN);
        }else if(line.contains(RETAINCASETOKEN)){
            isRetainCase = true;
        }else if(line.contains(KEYTOKEN)){
            keyList.add(getContentFromLine(line,KEYTOKEN).trim().toLowerCase());
        }else if(line.contains(OBJECTLISTTOKEN)){
            //this is an pointer to other field's reference
            //in object level, a reference field with ID should be indicated
            //so that the search could be simpler
            objectListRef.add(getContentFromLine(line,OBJECTLISTTOKEN));
        }else if(line.contains(EXTERNALLISTTOKEN)){
            externalListRef = getContentFromLine(line,EXTERNALLISTTOKEN);//either output:variable or output:meter
        }else if(line.contains(REFERENCETOKEN)){
            reference.add(getContentFromLine(line,REFERENCETOKEN));
        }
    }
    
    private String getContentFromLine(String line, String token){
        if(line.length() == token.length()){
            return "";
        }
        return line.substring((line.indexOf(token) + token.length())+1, line.length());
    }
    
}
