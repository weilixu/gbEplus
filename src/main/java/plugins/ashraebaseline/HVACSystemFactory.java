package main.java.plugins.ashraebaseline;

import main.java.model.gbXML.CampusTranslator;
import main.java.plugins.ashraebaseline.system3.HVACSystem3Factory;

public class HVACSystemFactory {
	private String systemType;

	private HVACSystem system;


	public HVACSystemFactory(String system) {
		systemType = system;
	}

	/**
	 * create the HVAC system based on system types
	 * 
	 * @return
	 */
	public HVACSystem createSystem(CampusTranslator building) {
		// if(systemType.equals("System Type 7")){
		// HVACSystem7Factory factory = new HVACSystem7Factory(building);
		// system = factory.getSystem();
		// }else 
		if(systemType.equals("System Type 3")){
		 HVACSystem3Factory factory = new HVACSystem3Factory(building);
		 system = factory.getSystem();}
		// }else if(systemType.equals("System Type 5")){
		// HVACSystem5Factory factory = new HVACSystem5Factory(building);
		// system = factory.getSystem();
		// }else if(systemType.equals("System Type 8")){
		// HVACSystem8Factory factory = new HVACSystem8Factory(building);
		// system = factory.getSystem();
		// }else if(systemType.equals("System Type 4")){
		// HVACSystem4Factory factory = new HVACSystem4Factory(building);
		// system = factory.getSystem();
		// }else if(systemType.equals("System Type 6")){
		// HVACSystem6Factory factory = new HVACSystem6Factory(building);
		// system = factory.getSystem();
		// }else if(systemType.equals("System Type 1")){
		// HVACSystem1Factory factory = new HVACSystem1Factory(building);
		// system = factory.getSystem();
		// }else if(systemType.equals("System Type 2")){
		// HVACSystem2Factory factory = new HVACSystem2Factory(building);
		// system = factory.getSystem();
		// }
		return system;
	}
}
