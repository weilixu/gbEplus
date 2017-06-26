package main.java.plugins.ashraebaseline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import main.java.api.DataBaseType;
import main.java.api.EnergyPlusDataAPI;
import main.java.model.data.EplusObject;
import main.java.model.gbXML.CampusTranslator;
import main.java.model.gbXML.ReverseTranslator;
import main.java.model.gbXML.ScheduleTranslator;
import main.java.model.idf.IDFFileObject;
import main.java.model.idf.IDFObject;

public class ASHRAEHVAC implements EnergyPlusDataAPI{
	
	// HVAC related objects
	private HVACSystemFactory factory;
	private HVACSystem system;

	public ASHRAEHVAC() {
		
		
	}

	// thresholds for the system selection
	//private static final double smallFloorArea = 2300.0;
	//private static final double mediumFloorArea = 14000.0;
	//private static final int smallFloorNumber = 3;
	//private static final int mediumFloorNumber = 5;

	/**
	 * Merge the system with baseline model, this should be called after
	 */
	private void mergeSystem(IDFFileObject file, ScheduleTranslator schedule, CampusTranslator campusTranslator) {
		
		selectSystem(schedule, campusTranslator);
		
		ArrayList<String> lines = new ArrayList<String>();
		ArrayList<String> units = new ArrayList<String>();
		ArrayList<String> comments = new ArrayList<String>();
		ArrayList<String> topComments = new ArrayList<String>();
		
		HashMap<String, ArrayList<EplusObject>> hvac = system.getSystemData();
		Set<String> hvacSet = hvac.keySet();
		Iterator<String> hvacIterator = hvacSet.iterator();
		while (hvacIterator.hasNext()) {
			ArrayList<EplusObject> objectList = hvac.get(hvacIterator.next());
			for (EplusObject eo : objectList) {
				lines.add(eo.getObjectName());//add object label
				comments.add("");
				units.add("");
				// loop over the key-value pairs
				for (int i = 0; i < eo.getSize(); i++) {
					lines.add(eo.getKeyValuePair(i).getValue());
					comments.add(eo.getKeyValuePair(i).getKey());
					units.add("");
					topComments.add("");
				}
				file.addIDFObject(new IDFObject(lines,units,comments,topComments));
				// add the object to the baseline model
				lines.clear();
				units.clear();
				comments.clear();
				topComments.clear();
			}
		}
	}

	/**
	 * This should be called before removeHVACObjects. It is because we need to
	 * check district systems in the model
	 */
	private void selectSystem(ScheduleTranslator schedule, CampusTranslator campusTranslator) {
		// get required parameter
		double floorSize = campusTranslator.getTotalFloorArea();
		int floorNumber = campusTranslator.getNumberOfFloor();

		// second exam the floor size and area
//		if (floorNumber >= mediumFloorNumber && floorSize > mediumFloorArea) {
//
//			factory = new HVACSystemFactory("System Type 7");
//			System.out.println("We select System Type 7");
//
//			system = factory.createSystem();
//		} else if (floorNumber <= smallFloorNumber && floorSize <= smallFloorArea) {
//			factory = new HVACSystemFactory("System Type 3");
//			System.out.println("We select System Type 3");
//		} else {
//			system = factory.createSystem();
//			factory = new HVACSystemFactory("System Type 5");
//			System.out.println("We select System Type 5");
//		}
		
		factory = new HVACSystemFactory("System Type 3");
		system = factory.createSystem(campusTranslator);
	}
	
	@Override
	public void writeInHVACSystem(IDFFileObject objectFile, ReverseTranslator translator) {
		ScheduleTranslator schedule = translator.getScheduleTranslator();
		CampusTranslator campusTranslator = translator.getCampusTranslator();
		mergeSystem(objectFile, schedule, campusTranslator);
	}

	@Override
	public String dataBaseName() {
		return "ASHRAEHVAC";
	}

	@Override
	public DataBaseType dataBaseType() {
		return DataBaseType.HVAC;
	}

	@Override
	public void writeInSystem(IDFFileObject objectFile, HashMap<String, String> id_to_NameMap) {
		
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
}
