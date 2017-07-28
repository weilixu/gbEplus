package main.java.api;

/**
 * Type of data
 * reserved for future expansion
 * @author weilixu
 *
 */
public enum DataBaseType {
	CONSTRUCTION, //construction - include material and regardless of algorithm
	FENESTRATION, //windows
	INTERNALLOAD,//internal load should include lighting, equipment and occupants
	LIGHTING, // lighting data
	THERMALZONE,//thermal zone data - include air conditioning setpoint, OA, people and possibly, the ventilation and air conditioning zone group
	SCHEDULE, // schedules
	EQUIPMENT, // equipment data
	OUTDOORAIR, //outdoor air requirements
	HVAC,// HVAC data
	HVACANDSCHEDULE;//hvac with hvac schedule.
}
