package main.java.model.idd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import main.java.config.FilesPath;
import main.java.model.idf.IDFFileObject;
import main.java.model.idf.IDFObject;

@SuppressWarnings("unused")
public class IddParser {
    
    //group type element
    private static final String GROUPTOKEN = "\\group";

    private static final String ESCAPETOKEN = "!";
    
    private EnergyPlusTemplate temp;
    
    private String version;
    
    //processing necessary indicators
    private String currentGroup = "";
    private EnergyPlusObjectTemplate currentObject = null;
    private EnergyPlusFieldTemplate currentField = null;
    
    private boolean processGroup = false; //this skips the beginning misc lines
    private boolean processField = false;
    private boolean processObject = false;
    
    private HashMap<String, ArrayList<String>> referenceListMap;
    private HashMap<String, ArrayList<String>> objectListMap;
    //private HashMap<String, IDFObject> nameToObjectMap;
    private HashMap<String, HashMap<String, IDFObject>>nameToObjectMap;//key - hashMap - name, object
    private ArrayList<String> nodeList;
        
    public IddParser(String version){
        if(version!=null){
            this.version = version;
        }else{
            //default to 8.6
            version = "8.6";
        }
                
        referenceListMap = new HashMap<String, ArrayList<String>>();
        objectListMap = new HashMap<String, ArrayList<String>>();
        nameToObjectMap = new HashMap<String, HashMap<String, IDFObject>>();
        nodeList = new ArrayList<String>();
    }
    
    public void processIdd(){
        temp = new EnergyPlusTemplate();
        try(BufferedReader br = new BufferedReader(new FileReader(new File(FilesPath.readProperty("ResourcePath")+"idd_v" + version)))){
            for(String line; (line = br.readLine())!=null;){
                //process the line
                int commentIndex = line.indexOf(ESCAPETOKEN);
                if(commentIndex > 0 && line.substring(0,line.indexOf(ESCAPETOKEN)).equals("")){
                    continue;
                }else{
                    processLines(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
        
    public JsonObject validateIDF(IDFFileObject file){
    	
    	JsonObject validationObj = new JsonObject();
    	
        Map<String, ArrayList<IDFObject>> idf = file.getObjMap();
        Iterator<String> objectNameItr = idf.keySet().iterator();
        while(objectNameItr.hasNext()){
            String objectName = objectNameItr.next();
            ArrayList<IDFObject> objectList = idf.get(objectName);
            EnergyPlusObjectTemplate objectTemplate = temp.getEplusObjectTemplate(objectName);
            JsonArray errorArray = new JsonArray();
            if(!validateObject(objectList, objectTemplate,errorArray)){
            	validationObj.addProperty("validation", "false");
            	validationObj.add(objectName, errorArray);
            	break;
            }else{
            	if(errorArray.size()!=0){
            		validationObj.add(objectName, errorArray);
            	}
            }
        }
        validationObj.addProperty("validation", "true");
        return validationObj;
    }
    
    public EnergyPlusObjectTemplate getObject(String obj){
    	
        return temp.getEplusObjectTemplate(obj.toLowerCase());
    }

    private boolean validateObject(ArrayList<IDFObject> objectList, EnergyPlusObjectTemplate objectTemplate, JsonArray errorMessage){
        for(int i=0; i<objectList.size(); i++){
            IDFObject object = objectList.get(i);
            //get the characteristics of the object
            int minField = objectTemplate.getNumberOfMinFields();
            int numberOfField = objectTemplate.getNumberOfFields();
            int beginExtensible = objectTemplate.getBeginningOfExtensible();
            int extensibleNumber = -1;
            if(beginExtensible > 0){
                extensibleNumber = objectTemplate.numOfExtensibles();
            }
            
            if(object.getData().length < minField){
            	JsonObject messageObj = new JsonObject();
            	messageObj.addProperty("type", "Severe");
            	messageObj.addProperty("name", object.getData()[0]);
            	messageObj.addProperty("message","Object has less number of field (" + object.getData().length+") then the minimum requirement:" + minField);
            	errorMessage.add(messageObj);
                return false;
            }else{
                //start read fields
                String[] data = object.getData();
                String[] comment = object.getComments();
                String[] unit = object.getUnit();
                
                for(int j=0; j<data.length; j++){
                    //if extensible, j might be a lot larger than the actual number of the field in the template
                    //check j
                    int index = j;
                    if(index>=numberOfField){
                        if(beginExtensible > 0 && extensibleNumber > 0){
                            //reset index
                            index = beginExtensible + (index - numberOfField) % extensibleNumber;
                        }else{
                           	JsonObject messageObj = new JsonObject();
                        	messageObj.addProperty("type", "Severe");
                        	messageObj.addProperty("name", object.getData()[0]);
                        	messageObj.addProperty("message","Object has more fields (" + object.getData().length + ") than its maximum requirement:" + numberOfField);
                        	errorMessage.add(messageObj);
                            return false;
                        }
                    }
                    
                    //debug purpose
//                    try{
//                        objectTemplate.getFieldTemplateByIndex(index);
//                    }catch(IndexOutOfBoundsException e){
//                        System.out.println(numberOfField + " " + index + " " + j);
//                    }
                    
                    EnergyPlusFieldTemplate ft = objectTemplate.getFieldTemplateByIndex(index);
                    
//                    if(ft.getFieldName().contains("Name")){
//                        if(nameToObjectMap.containsKey(data[j])){
//                        }else{
//                            nameToObjectMap.put(data[j], object);
//                        }
//                    }

                    if(comment[j]==null){
                        comment[j] = ft.getFieldName();
                    }
                    
                    if(unit[j] == null){
                        unit[j] = ft.getUnit();
                    }
                    
                    //check the inputs
                    if(ft.getFieldType().equals("N")){
                        //numeric value
                        Double value = null;
                        try{
                            value = Double.parseDouble(data[j]);
                        }catch(NumberFormatException e){
                            if(ft.isRequired()){
                                if(!(ft.isAutoCalculatable() || ft.isAutoSizable())){
                                   	JsonObject messageObj = new JsonObject();
                                	messageObj.addProperty("type", "Error");
                                	messageObj.addProperty("name", object.getData()[0]);
                                	messageObj.addProperty("message", data[j] + "is not a valid input for Field: '" + ft.getFieldName()+"'");
                                	errorMessage.add(messageObj);
                                	
                                }else if(ft.isAutoCalculatable() && !data[j].equalsIgnoreCase("AutoCalculate")){
                                   	JsonObject messageObj = new JsonObject();
                                	messageObj.addProperty("type", "Error");
                                	messageObj.addProperty("name", object.getData()[0]);
                                	messageObj.addProperty("message", data[j] + "is autocalculable but the key word is not AutoCalculate");
                                	errorMessage.add(messageObj);
                                	
                                }else if(ft.isAutoSizable() && !data[j].equalsIgnoreCase("autosize")){
                                   	JsonObject messageObj = new JsonObject();
                                	messageObj.addProperty("type", "Error");
                                	messageObj.addProperty("name", object.getData()[0]);
                                	messageObj.addProperty("message", data[j] + "is autosizable but the key word is not autosize");
                                	errorMessage.add(messageObj);
                                	
                                }
                            }else{
                                if(!data[j].equals("")){
                                    if(!(ft.isAutoCalculatable() || ft.isAutoSizable())){
                                       	JsonObject messageObj = new JsonObject();
                                    	messageObj.addProperty("type", "Warning");
                                    	messageObj.addProperty("name", object.getData()[0]);
                                    	messageObj.addProperty("message",data[j] + " is not a valid input for Field: '" + ft.getFieldName()+ "'.");
                                    	errorMessage.add(messageObj);
                                    	
                                    }else if(ft.isAutoCalculatable() && !data[j].equalsIgnoreCase("AutoCalculate")){
                                       	JsonObject messageObj = new JsonObject();
                                    	messageObj.addProperty("type", "Warning");
                                    	messageObj.addProperty("name", object.getData()[0]);
                                    	messageObj.addProperty("message",data[j] + " is autocalculable but the key word is not AutoCalculate");
                                    	errorMessage.add(messageObj);
                                    	
                                    }else if(ft.isAutoSizable() && !data[j].equalsIgnoreCase("autosize")){
                                     	JsonObject messageObj = new JsonObject();
                                    	messageObj.addProperty("type", "Warning");
                                    	messageObj.addProperty("name", object.getData()[0]);
                                    	messageObj.addProperty("message", data[j] + " is autosizable but the key word is not autosize");
                                    	errorMessage.add(messageObj);
                                    	
                                    }                                    
                                }
                            }
                        }//catch
                        
                        if(value!=null){
                            if(ft.getInclusiveMin()!=null && value < ft.getInclusiveMin()){
                               	JsonObject messageObj = new JsonObject();
                            	messageObj.addProperty("type", "Error");
                            	messageObj.addProperty("name", object.getData()[0]);
                            	messageObj.addProperty("message", value + " is smaller than the minimum: " + ft.getInclusiveMin() + " in Field: '" + ft.getFieldName() + "'.");
                            	errorMessage.add(messageObj);
                            	
                            }//if
                            
                            if(ft.getInclusiveMax()!=null && value > ft.getInclusiveMax()){
                               	JsonObject messageObj = new JsonObject();
                            	messageObj.addProperty("type", "Error");
                            	messageObj.addProperty("name", object.getData()[0]);
                            	messageObj.addProperty("message", value + "is large than the maximum: " + ft.getInclusiveMax() + " in Field: '" + ft.getFieldName() + "'");
                            	errorMessage.add(messageObj);
                            	
                            }//if
                        }else if(ft.getDefault()!=null){
                            data[j] = ft.getDefault();
                           	JsonObject messageObj = new JsonObject();
                        	messageObj.addProperty("type", "Warning");
                        	messageObj.addProperty("name", object.getData()[0]);
                        	messageObj.addProperty("message", "Field: '" + ft.getFieldName() + "' is empty, it is assigned with a default value: " + ft.getDefault());
                        	errorMessage.add(messageObj);
                            
                        }
                    }else{
                        //it is alpha data type
                        if(!ft.getKeys().isEmpty()){
                            if(!ft.getKeys().contains(data[j].toLowerCase()) && ft.isRequired()){
                                Object[] range = ft.getKeys().toArray();
                               	JsonObject messageObj = new JsonObject();
                            	messageObj.addProperty("type", "Warning");
                            	messageObj.addProperty("name", object.getData()[0]);
                            	messageObj.addProperty("message", "Field: '" + ft.getFieldName() + "' is a key field." + "Instead of " + data[j] + ", should select from the range of" + Arrays.toString(range));
                            	errorMessage.add(messageObj);
                                
                            }
                        }
                    }//if-else
                    if(ft.getType()!=null &&ft.getType().equals("node") && !data[j].isEmpty()){
                        nodeList.add(data[j]);
                    }
                    
                    //start forming the reference - object-List map
                    List<String> referenceList = ft.getReference();
                    for(String s : referenceList){
                        if(!referenceListMap.containsKey(s)){
                            referenceListMap.put(s, new ArrayList<String>());
                        }
                        referenceListMap.get(s).add(data[j]);//reference only uses for name field
                        
                        if(!nameToObjectMap.containsKey(s)){
                            nameToObjectMap.put(s, new HashMap<String, IDFObject>());
                        }
                        nameToObjectMap.get(s).put(data[j].toLowerCase(), object);
                    }
                    
                    if(ft.getType()!=null && ft.getType().equals("object-list")){
                        List<String> objectListRef = ft.getObjectListRef();
                        String objFieldName = objectTemplate.getObjectName().toLowerCase()+":"+ft.getFieldName().toLowerCase();
                        if(!objectListMap.containsKey(data[j])){
                            objectListMap.put(objFieldName, new ArrayList<String>());
                        }
                        for(String s: objectListRef){
                            objectListMap.get(objFieldName).add(s);
                        }
                    }
                }
            }
        }
        return true;
    }

    private void processLines(String line){
        //System.out.println(line + " " + processField);
        if(line.startsWith(GROUPTOKEN)){
            processGroup = true;
            currentGroup = subStringWithNoToken(line, GROUPTOKEN);
            
            temp.addEnergyPlusGroup(new EnergyPlusGroupTemplate(currentGroup));
        }else if(processGroup && !line.equals("")){
            if(isFirstCharactersNonWhiteSpace(line)){
                //this line indicates an EnergyPlus object and its name
                currentObject = new EnergyPlusObjectTemplate(line.substring(0, line.lastIndexOf(",")).trim());
                processObject = true;
                temp.addEnergyPlusObject(currentGroup, currentObject);
                
                //turn the field processing off
                processField = false;
                currentField = null;
            }else if(isField(line)){
                
                if(!line.contains("fields as indicated")){//in this case, they are extensible, continue repeat
                    currentField = new EnergyPlusFieldTemplate(line.trim());
                    processField = true;
                    
                    currentObject.addObjectField(currentField);
                    //turn off the flag of object processing
                    processObject = false;
                }
            }else if(processObject){
                //process Object level elements
                currentObject.processElement(line.trim());
                if(line.contains("\\required-object")){
                    //EplusMap.get
                }
            }else if(processField){
                //process Field level elements;
                if(line.contains("\\begin-extensible")){
                    currentObject.setTheBeginningOfExtensible();
                }
                currentField.processElement(line.trim());
            }
        }
    }
    
    
    private String subStringWithNoToken(String line, String token){
        return line.substring(token.length()).trim();
    }
    
    private boolean isFirstCharactersNonWhiteSpace(String line){
        //System.out.println(line);
        return !Character.isWhitespace(line.charAt(0)) && line.charAt(0)!='\\'&&!line.contains(GROUPTOKEN) && !line.contains(ESCAPETOKEN);
    }
    
    private boolean isField(String line){
        String temp = line.trim();
        //System.out.println(temp);
        if(temp.isEmpty()){
            return false;
        }
        
        if((temp.charAt(0) == 'A' || temp.charAt(0) == 'N') && (int) temp.charAt(1) < 65){
            return true;
        }
        
        return false;
        //return Character.isWhitespace(line.charAt(0)) && Character.isWhitespace(line.charAt(1)) && line.charAt(2)!='\\' && line.charAt(2)!=' ' && line.charAt(2)!='!';
    }
}
