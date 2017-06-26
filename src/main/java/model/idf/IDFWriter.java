package main.java.model.idf;

import java.util.ArrayList;

import main.java.model.idd.EnergyPlusFieldTemplate;
import main.java.model.idd.EnergyPlusObjectTemplate;
import main.java.model.idd.IddParser;

/**
 * A simple utility class writes data into EnergyPlus
 * @author weilixu
 *
 */
public class IDFWriter {
	
	private IddParser IDDData;
	private EnergyPlusObjectTemplate objectTemplate; // record the object label that is current under processing
	
    private ArrayList<String> lines;
    private ArrayList<String> units;
    private ArrayList<String> comments;
    private ArrayList<String> topComments;
    
    public IDFWriter(){
        lines = new ArrayList<String>();
        units = new ArrayList<String>();
        comments = new ArrayList<String>();
        topComments = new ArrayList<String>();
    }
    
    public void setIddParser(IddParser parser){
    	IDDData = parser;
    }
    
    public void setProcessingObject(String objectLabel){
    	if(IDDData == null){
    		//TODO raise exception
    	}
    	objectTemplate = IDDData.getObject(objectLabel);
    	
    	if(objectTemplate == null){
    		//TODO raise exception
    	}
    }
    
    public int getSizeOfFields(){
    	return objectTemplate.getNumberOfFields();
    }
    
    public String getNextAvailableFieldComment(int index){
    	return objectTemplate.getFieldTemplateByIndex(index).getFieldName();
    }
    
    /**
     * record inputs following the order of IDD data - 
     * this will trigger the rigorous IDD data check
     * if the data does not match the IDD description, there will be error raised
     * 
     * Since this method is using IDD to restrain the user behavior as well as automatically
     * fill up the comments and units, it is not necessary to input comments
     * 
     * The function should be called in the order of data inputs in the IDD.
     * 
     * You have to firstly identify the object that is under processing first
     * call set processingObject.
     */
    public void recordInputsUsingIDD(String data){
    	if(IDDData == null){
    		//TODO raise error
    	}
    	
    	int index = lines.size()-1;
    	if (index > objectTemplate.getNumberOfFields()){
    		//TODO raise error "The object ------ field is overflow"
    	}
    	
    	EnergyPlusFieldTemplate fieldTemp = objectTemplate.getFieldTemplateByIndex(index);
    	
    	if(data == null){
    		if(fieldTemp.getDefault()!=null){
    			lines.add(fieldTemp.getDefault());
    		}else if(fieldTemp.isAutoCalculatable()){
    			lines.add("AUTOCALCULATED");
    		}else if(fieldTemp.isAutoSizable()){
    			lines.add("AUTOSIZED");
    		}else{
    			lines.add("");
    		}
    	}else{
    		lines.add(data);
    	}
    	//TODO later need to confirm the checks
    	units.add(fieldTemp.getUnit());
    	comments.add(fieldTemp.getFieldName());
    }
    
    public void addObjectWithIDD(IDFFileObject file){
    	int size = lines.size();
    	
    	if(size < objectTemplate.getNumberOfMinFields()){
    		//TODO raise exception - not enough fields
    	}else if(size > objectTemplate.getNumberOfFields()){
    		//TODO raise exception - fields overflow
    	}
    	
    	if(objectTemplate.isExtensible()){
    		//if it is extensible - there is no need to fill up all the extensible fields
    		int remainder = (size - objectTemplate.getBeginningOfExtensible())%objectTemplate.numOfExtensibles();
    		if(remainder == 0){
    	        file.addIDFObject(new IDFObject(lines,units, comments, topComments));
    	        lines.clear();
    	        units.clear();
    	        comments.clear();
    	        topComments.clear();
    		}else{//fill up the remainder field
    			for(int i=remainder-1; i>=0; i--){
    				lines.add("");
    				units.add(objectTemplate.getFieldTemplateByIndex(objectTemplate.getBeginningOfExtensible() + objectTemplate.numOfExtensibles() - i).getUnit());
    				comments.add(objectTemplate.getFieldTemplateByIndex(objectTemplate.getBeginningOfExtensible() + objectTemplate.numOfExtensibles() - i).getFieldName());
    			}
    	        file.addIDFObject(new IDFObject(lines,units, comments, topComments));
    	        lines.clear();
    	        units.clear();
    	        comments.clear();
    	        topComments.clear();
    		}
    	}
    }
    
    public void recordInputs(String line, String unit, String comment, String topComments){
        lines.add(line);
        units.add(unit);
        comments.add(comment);
    }
    
    public void addObject(IDFFileObject file){
        file.addIDFObject(new IDFObject(lines,units, comments, topComments));
        lines.clear();
        units.clear();
        comments.clear();
        topComments.clear();
    }

}
