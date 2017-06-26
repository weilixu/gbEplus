package main.java.model.data;

/**
 * inheritant from EplusObject, represents DesignSpecification:OutdoorAir object
 * in EnergyPlus, only zone name input is required for this object.
 * 
 * @author Weili
 *
 */
public class OutdoorDesignSpecification extends EplusObject {

	private final static String objectName = "DesignSpecification:OutdoorAir";

	private String OutdoorAirMethod;
	private String OAflowPerson;
	private String OAflowFloorArea;
	private String OAflowZone;
	private String OAflowach;
	private String OASchedule;

	private final int methodIndex = 1;
	private final int personIndex = 2;
	private final int floorAreaIndex = 3;
	private final int zoneIndex = 4;
	private final int achIndex = 5;
	private final int scheduleIndex = 6;

	public OutdoorDesignSpecification(String zoneName) {
		super(objectName, zoneName);
		this.addField(new KeyValuePair("Name", zoneName + " OutdoorAir"));
		OutdoorAirMethod = "Sum";
		OAflowPerson = "0";
		OAflowFloorArea = "0";
		OAflowZone = "0";
		OAflowach = "0";
		OASchedule = "";
		this.addField(new KeyValuePair("Outdoor Air Method", OutdoorAirMethod));
		this.addField(new KeyValuePair("Outdoor Air Flow per Person", OAflowPerson));
		this.addField(new KeyValuePair("Outdoor Air Flow per Zone Floor Area", OAflowFloorArea));
		this.addField(new KeyValuePair("Outdoor Air Flow per Zone", OAflowZone));
		this.addField(new KeyValuePair("Outdoor Air Flow Air Changes per Hour", OAflowach));
		this.addField(new KeyValuePair("Outdoor Air Flow Rate Fraction Schedule Name", OASchedule));
	}

	public void changeOutdoorAirMethod(String method) {
		this.getKeyValuePair(methodIndex).setValue(method);
	}

	public void changeAirFlowperPerson(String rate) {
		this.getKeyValuePair(personIndex).setValue(rate);
	}

	public void changeAirFlowperFloorArea(String rate) {
		this.getKeyValuePair(floorAreaIndex).setValue(rate);
	}

	public void changeAirFlowperZone(String rate) {
		this.getKeyValuePair(zoneIndex).setValue(rate);
	}

	public void changeAirFlowperACH(String rate) {
		this.getKeyValuePair(achIndex).setValue(rate);
	}

	public void changeAirFlowSchedule(String schedule) {
		this.getKeyValuePair(scheduleIndex).setValue(schedule);
	}
}
