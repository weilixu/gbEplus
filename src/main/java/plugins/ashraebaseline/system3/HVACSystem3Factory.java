package main.java.plugins.ashraebaseline.system3;

import java.util.ArrayList;
import java.util.HashMap;

import main.java.model.data.EplusObject;
import main.java.model.gbXML.CampusTranslator;
import main.java.plugins.ashraebaseline.HVACSystem;
import main.java.plugins.ashraebaseline.SystemParser;

/**
 * This class represent HVAC system type 3 manufacturer The class behaviors
 * includes 1. Establish the template System Type 3 2. Check Clauses for
 * components modifications: G3.1.2.2; G3.1.2.4 (Not implemented); G3.1.2.5;
 * G3.1.2.7;G3.1.2.8;G3.1.2.9(Not completed); G3.1.2.11(Not implemented yet) 3.
 * Check exceptions includes G3.1.1; G3.1.1.1 (Not implemented); G3.1.1.2 (Not
 * implemented) G3.1.1.3 (nOT implemented) 4. Manufacture correct system type 3
 * based on design case and merge it back to the whole building energy model
 * 
 * @author Weili
 *
 */

public class HVACSystem3Factory {
	// extract the template system
	private final SystemParser system = new SystemParser("System Type 3");

	private HashMap<String, ArrayList<EplusObject>> systemObjects;
	private SystemType3 systemType3;

	public HVACSystem3Factory(CampusTranslator campus) {
		systemObjects = new HashMap<String, ArrayList<EplusObject>>();
		processTemplate();
		systemType3 = new HVACSystem3(systemObjects, campus);

	}

	public HVACSystem getSystem() {
		return systemType3;
	}

	/**
	 * Separate the three systems into three data lists.
	 */
	private void processTemplate() {
		ArrayList<EplusObject> template = system.getSystem();
		for (EplusObject eo : template) {
			if (eo.getReference().equals("Supply Side System")) {
				if (!systemObjects.containsKey("Supply Side System")) {
					systemObjects.put("Supply Side System", new ArrayList<EplusObject>());
				}
				systemObjects.get("Supply Side System").add(eo);
			} else if (eo.getReference().equals("Demand Side System")) {
				if (!systemObjects.containsKey("Demand Side System")) {
					systemObjects.put("Demand Side System", new ArrayList<EplusObject>());
				}
				systemObjects.get("Demand Side System").add(eo);
			} else if (eo.getReference().equals("Plant")) {
				if (!systemObjects.containsKey("Plant")) {
					systemObjects.put("Plant", new ArrayList<EplusObject>());
				}
				systemObjects.get("Plant").add(eo);
			} else if (eo.getReference().equals("Schedule")) {
				if (!systemObjects.containsKey("Schedule")) {
					systemObjects.put("Schedule", new ArrayList<EplusObject>());
				}
				systemObjects.get("Schedule").add(eo);
			} else if (eo.getReference().equals("Global")) {
				if (!systemObjects.containsKey("Global")) {
					systemObjects.put("Global", new ArrayList<EplusObject>());
				}
				systemObjects.get("Global").add(eo);
			}
		}
	}
}
