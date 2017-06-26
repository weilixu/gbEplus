package main.java.model.data;
/**
 * This class stores one key and value pair under an EnergyPlus object
 * key indicates the variable's name
 * value indicates the value of the variable.
 * @author Weili
 *
 */
public class KeyValuePair {
    
    private final String key;
    private String value;
    
    public KeyValuePair(String k, String v){
	key = k;
	value = v;
    }
    
    public String getKey(){
	return key;
    }
    
    public String getValue(){
	return value;
    }
    
    public void setValue(String v){
	value = v;
    }
    
    public KeyValuePair clone(){
	return new KeyValuePair(key,value);
    }
}
