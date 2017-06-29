package main.java.plugins.ashraeassumptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import main.java.api.DataBaseType;
import main.java.api.EnergyPlusDataAPI;
import main.java.model.data.EplusObject;
import main.java.model.gbXML.ReverseTranslator;
import main.java.model.idf.IDFFileObject;
import main.java.model.idf.IDFObject;
import main.java.plugins.ashraebaseline.ConstructionParser;

/**
 * ASHRAE construction plugin
 * insert all the constructions according to ASHRAE 90.1 - Appendix A 
 * @author weilixu
 *
 */
public class ASHRAEConstructions implements EnergyPlusDataAPI{

	private ConstructionParser construction;
	//private HashMap<String, String> bs_idToObjectMap;

	public ASHRAEConstructions() {
		
	}

	@Override
	public String dataBaseName() {
		return "ASHRAEConstruction";
	}

	@Override
	public DataBaseType dataBaseType() {
		return DataBaseType.CONSTRUCTION;
	}

	@Override
	public void writeInSystem(IDFFileObject objectFile, HashMap<String, String> id_to_NameMap) {
		construction = new ConstructionParser("Climate Zone 4");//TODO need to adapt it later
		
		// add new building envelope related objects
		ArrayList<String> lines = new ArrayList<String>();//values
		ArrayList<String> units = new ArrayList<String>();//units
		ArrayList<String> comments = new ArrayList<String>();//
		ArrayList<String> topComments = new ArrayList<String>();

		ArrayList<EplusObject> objects = construction.getObjects();

		for (EplusObject eo : objects) {
			//record in the map
			String uniqueID = UUID.randomUUID().toString();
			id_to_NameMap.put(uniqueID, eo.getObjectName()+":" + eo.getKeyValuePair(0).getValue());
			
			lines.add(eo.getObjectName());// add object label
			comments.add("");
			units.add("");
			// loop over the key-value pairs
			for (int i = 0; i < eo.getSize(); i++) {				
				lines.add(eo.getKeyValuePair(i).getValue());
				comments.add(eo.getKeyValuePair(i).getKey());
				units.add("");
				topComments.add("");
			}
			objectFile.addIDFObject(new IDFObject(lines, units, comments, topComments));
			// add the object to the baseline model
			lines.clear();
			units.clear();
			comments.clear();
			topComments.clear();
		}
		
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeInHVACSystem(IDFFileObject objectFile, ReverseTranslator translator) {
		
	}

}
