package main.java.plugins.ashraeassumptions;

import java.util.HashMap;
import java.util.Map;

import main.java.api.DataBaseType;
import main.java.api.EnergyPlusDataAPI;
import main.java.model.gbXML.ReverseTranslator;
import main.java.model.idf.IDFFileObject;

public abstract class ASHRAEDataSet implements EnergyPlusDataAPI{
	
	@Override
	public String dataBaseName(){
		return "ASHRAE";
	}
	
	@Override
	public DataBaseType dataBaseType() {
		return null;
	}
	
	@Override
	public void writeInSystem(IDFFileObject objectFile, HashMap<String, String> id_to_NameMap){
		
	}
	

	@Override
	public String getValueInString(String identifier) {
		return null;
	}

	@Override
	public Double getValueInDouble(String identifier) {
		return null;
	}

	@Override
	public Map<String, String[]> getValuesInHashMap(String identifier) {
		return null;
	}

	@Override
	public void writeInHVACSystem(IDFFileObject objectFile, ReverseTranslator translator) {
		
	}

	@Override
	public HashMap<DataBaseType, String> requiredCoPlugins() {
		//this plugin does not require any other plugins
		return null;
	}

	@Override
	public void writeInObjects(ReverseTranslator translator) {
		//nah
	}

	

}
