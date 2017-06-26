package main.java.model.data;

import java.util.ArrayList;

/**
 * The data structure is used to convert the EnergyPlus data XML (edXML) to EnergyPlus data.
 * 
 * This is the class stores the Energyplus object. objectName indicates the
 * energyplus object name, <link>KeyValuePair<link> stores the values under this
 * object
 * 
 * @author Weili
 *
 */
public class EplusObject {
    private final String objectName;
    private final String reference;
    private final ArrayList<KeyValuePair> objectValues;
    private int size = 0;

    public EplusObject(String n, String r) {
	objectName = n;
	reference = r;
	objectValues = new ArrayList<KeyValuePair>();
    }

    /**
     * insert a keyvaluepair into the database at certain location This method
     * usually be used in branchlist where is specifying the connections between
     * systems.
     * 
     * @param i
     * @param field
     */
    public void insertFiled(int i, KeyValuePair field) {
	objectValues.add(i, field);
	size++;
    }

    /**
     * add fields, similar to the insert field, the only difference is it is
     * faster way to add field, but it only add a new field at the end of this
     * object
     * 
     * @param field
     */
    public void addField(KeyValuePair field) {
	objectValues.add(field);
	size++;
    }
    
    /**
     * Return true if there is special characters in this object
     * false otherwise.
     * @return
     */
    public boolean hasSpecialCharacters(){
	for(KeyValuePair kvp: objectValues){
	    if(kvp.getValue().indexOf("%")>-1){
		return true;
	    }
	}
	return false;
    }
    
    /**
     * Check if the fields in the object contains the specified character
     * @param character
     * @return
     */
    public boolean contains(String character){
	for(KeyValuePair kvp: objectValues){
	    if(kvp.getValue().contains(character)){
		return true;
	    }
	}
	return false;
    }
    
    /**
     * Precondition: There is special characters in the object that needs to be replaced.
     * Postcondition: all the special characters in this objects are replaced by "character"
     * if there is no special characters in this object and this method is being called, nothing will happen.
     * The special characters has to be words that contains %
     * @param character
     */
    public void replaceSpecialCharacters(String character){
	for(KeyValuePair kvp: objectValues){
	    if(kvp.getValue().indexOf("%")>-1){
		String value = kvp.getValue();
		String replaceValue = character+value.substring(value.indexOf("%")+1);
		kvp.setValue(replaceValue);
	    }
	}
    }

    /**
     * get the size of the object fields.
     * 
     * @return
     */
    public int getSize() {
	return size;
    }

    /**
     * get the correspondent key value pair
     * 
     * @param index
     * @return
     */
    public KeyValuePair getKeyValuePair(int index) {
	return objectValues.get(index);
    }

    public String getObjectName() {
	return objectName;
    }

    public String getReference() {
	return reference;
    }

    /**
     * clone the whole eplusobject
     */
    public EplusObject clone() {
	EplusObject temp = new EplusObject(objectName, reference);
	for (KeyValuePair kvp : objectValues) {
	    temp.addField(kvp.clone());
	}
	return temp;
    }

}
