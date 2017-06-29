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
	SCHEDULE, // schedules
	EQUIPMENT, // equipment data
	OUTDOORAIR, //outdoor air requirements
	HVAC,// HVAC data
	HVACANDSCHEDULE;//hvac with hvac schedule.
}
