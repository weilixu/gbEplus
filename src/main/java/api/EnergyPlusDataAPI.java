package main.java.api;

import java.util.HashMap;
import java.util.Map;

import main.java.model.gbXML.ReverseTranslator;
import main.java.model.idf.IDFFileObject;

/**
 * API used to register EnergyPlus data
 * 
 * 
 * @author weilixu
 *
 */
public interface EnergyPlusDataAPI {

	public String dataBaseName();

	public DataBaseType dataBaseType();

	/**
	 * This is the method that write in a whole system into EnergyPlus file
	 * 
	 * @param objectFile:
	 *            energyplus
	 */
	public void writeInSystem(IDFFileObject objectFile, HashMap<String, String> id_to_NameMap);
	
	/**
	 * This method should only be used for HVAC system since it opened up the whole translator data
	 * So the function is well equipped with accessing all the building data if necessary
	 * @param objectFile
	 * @param translator
	 */
	public void writeInHVACSystem(IDFFileObject objectFile, ReverseTranslator translator);

	/**
	 * get the value from plugin in the format of String
	 * 
	 * @param identifier
	 *            the identifier for the value, it could be a space type
	 *            (office),
	 * @return
	 */
	public String getValueInString(String identifier);

	/**
	 * get the value from plugin in the format of Double
	 * 
	 * @param identifier
	 *            identifier the identifier for the value, it could be a space
	 *            type (office), or it could be other 
	 * @return
	 */
	public Double getValueInDouble(String identifier);
	
	/**
	 * get the value from plugin in the format of Map
	 * 
	 * @param identifier
	 *            identifier the identifier for the value, it could be a space
	 *            type (office), or it could be other 
	 * @return
	 */
	public Map<String, String[]> getValuesInHashMap(String identifier);

}
