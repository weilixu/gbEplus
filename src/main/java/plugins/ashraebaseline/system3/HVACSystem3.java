package main.java.plugins.ashraebaseline.system3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import main.java.model.data.EplusObject;
import main.java.model.data.KeyValuePair;
import main.java.model.gbXML.CampusTranslator;
import main.java.model.gbXML.GbXMLSpace;
import main.java.plugins.ashraebaseline.HVACSystemImplUtil;

/**
 * PSZ-AC, every zone has its own AHU
 * 
 * @author Weili
 *
 */
public class HVACSystem3 implements SystemType3 {
	// recording all the required data for HVAC system type 3
	private HashMap<String, ArrayList<EplusObject>> objectLists;
	
	private CampusTranslator building;

	// building object contains building information and energyplus data
	public HVACSystem3(HashMap<String, ArrayList<EplusObject>> objects, CampusTranslator building) {
		objectLists = objects;
		this.building = building;
		
		processSystems();
	}

	@Override
	public HashMap<String, ArrayList<EplusObject>> getSystemData() {
		return objectLists;
	}

	private void processSystems() {
		ArrayList<EplusObject> supplySideSystem = new ArrayList<EplusObject>();
		ArrayList<EplusObject> demandSideSystem = new ArrayList<EplusObject>();

		ArrayList<EplusObject> supplySideSystemTemplate = objectLists.get("Supply Side System");
		ArrayList<EplusObject> demandSideSystemTemplate = objectLists.get("Demand Side System");

		HashMap<String, GbXMLSpace> spaceMap = building.getSpaceMap();

		Set<String> spaceItrSet = spaceMap.keySet();
		Iterator<String> spaceMapIterator = spaceItrSet.iterator();

		int zoneCounter = 0;
		while (spaceMapIterator.hasNext()) {

			String id = spaceMapIterator.next();
			// first process the demand side system and their connection to
			// supply side system
			GbXMLSpace space = spaceMap.get(id);
				zoneCounter++;
				demandSideSystem.addAll(processDemandTemp(space.getSpaceName(), demandSideSystemTemplate));
				// add the outdoor air object for demand zone
				//demandSideSystem.add(space.getOutdoorAirObject());
				supplySideSystem.addAll(processSupplyTemp(space.getSpaceName(), supplySideSystemTemplate));
			
		}

		objectLists.put("Supply Side System", supplySideSystem);
		objectLists.put("Demand Side System", demandSideSystem);
	}

	/**
	 * process the HVAC supply air side system
	 * 
	 * @param zone
	 * @param supplySideSystemTemplate
	 * @return
	 */
	private ArrayList<EplusObject> processSupplyTemp(String zone, ArrayList<EplusObject> supplySideSystemTemplate) {
		ArrayList<EplusObject> supplyTemp = new ArrayList<EplusObject>();
		for (EplusObject eo : supplySideSystemTemplate) {
			EplusObject temp = eo.clone();

			/*
			 * replace the special characters that contains floors
			 */
			if (temp.hasSpecialCharacters()) {
				temp.replaceSpecialCharacters(zone);
			}

			// check if this is the connection between supply side and demand
			// side systems
			if (temp.getObjectName().equalsIgnoreCase("AirLoopHVAC:ZoneSplitter")) {
				KeyValuePair splitterPair = new KeyValuePair("Outlet Node Name", zone + " Zone Equip Inlet");
				temp.addField(splitterPair);
			}

			// check if this is the connection between supply side and demand
			// side systems
			if (temp.getObjectName().equalsIgnoreCase("AirLoopHVAC:ZoneMixer")) {

				KeyValuePair mixerPair = new KeyValuePair("Intlet Node Name", zone + " Return Outlet");
				temp.addField(mixerPair);
			}
			supplyTemp.add(temp);
		}
		return supplyTemp;
	}

	/**
	 * process the demand side system
	 * 
	 * @param zone
	 * @param zoneTemp
	 * @return
	 */
	private ArrayList<EplusObject> processDemandTemp(String zone, ArrayList<EplusObject> zoneTemp) {
		ArrayList<EplusObject> demandTemp = new ArrayList<EplusObject>();
		for (EplusObject eo : zoneTemp) {
			EplusObject temp = eo.clone();
			// check special characters to avoid useless loop inside the replace
			// special characters
			if (temp.hasSpecialCharacters()) {
				temp.replaceSpecialCharacters(zone);
			}
			demandTemp.add(temp);
		}
		return demandTemp;
	}
}
