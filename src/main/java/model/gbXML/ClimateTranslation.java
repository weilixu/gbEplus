package main.java.model.gbXML;


import org.jdom2.Namespace;

import main.java.model.idf.IDFFileObject;
import main.java.model.idf.IDFWriter;

/**
 * This class is used for acquiring the information from site / location or user inputs
 * Define sizing period design day (from ddy)
 * Define runperiod (assume 1 year)
 * Define special days (if any or in U.S.)
 * Define daylight saving time (if any or in U.S.)
 * Define ground temperature (from weather file)
 * Define water main temperature
 * 
 * @author weilixu
 *
 */
public class ClimateTranslation {
	private Namespace ns;

	private IDFWriter idfWriter;
	//for demo purpose, this will default everything to Pittsburgh
	
	
	public ClimateTranslation(Namespace ns){
		this.ns = ns;
		idfWriter = new IDFWriter();
	}
	
	public void setUpDesignDayConditionForBaseline(IDFFileObject file){
		//set to chicago
		//heating
		idfWriter.recordInputs("SizingPeriod:DesignDay","","","");
		idfWriter.recordInputs("CHICAGO Ann Htg 99.6% Condns DB","","Name","");
		idfWriter.recordInputs("1","","Month","");
		idfWriter.recordInputs("21","","Day of Month","");
		idfWriter.recordInputs("WinterDesignDay","","Day Type","");
		idfWriter.recordInputs("-20.6","C","Maximum Dry-Bulb Temperature","");
		idfWriter.recordInputs("0","deltaC","Daily Dry-Bulb Temperature Range","");
		idfWriter.recordInputs("","","Dry-Bulb Temperature Range Modifier Type","");
		idfWriter.recordInputs("","","Dry-Bulb Temperature Range Modifier Day Schedule Name","");
		idfWriter.recordInputs("Wetbulb","","Humidity Condition Type","");
		idfWriter.recordInputs("-20.6","C","Wetbulb or DewPoint at Maximum Dry-Bulb","");
		idfWriter.recordInputs("","","Humidity Conition Day Scheduel Name","");
		idfWriter.recordInputs("","kgWater/kgDryAir","Humidity Ratio at Maximum Dry-Bulb","");
		idfWriter.recordInputs("","J/Kg","Enthalpy at Maximum Dry-Bulb","");
		idfWriter.recordInputs("","deltaC","Daily Wet-Bulb Tempereature Range","");
		idfWriter.recordInputs("99063","Pa","Barometric Pressure","");
		idfWriter.recordInputs("4.9","m/s","Wind Speed","");
		idfWriter.recordInputs("270","deg","Wind Direction","");
		idfWriter.recordInputs("No","","Rain Indicator","");
		idfWriter.recordInputs("No","","Snow Indicator","");
		idfWriter.recordInputs("No","","Daylight Saving Time Indicator","");
		idfWriter.recordInputs("ASHRAEClearSky","","Solar Model Indicator","");
		idfWriter.recordInputs("","","Beam Solar Day Schedule Name","");
		idfWriter.recordInputs("","","Diffuse Solar Day schedule Name","");
		idfWriter.recordInputs("","","ASHRAE Clear Skey Optical Depth for Beam Irradiance (taub)","");
		idfWriter.recordInputs("","","ASHRAE Clear Sky Optical Depth for Diffuse Irradiance (taud)","");
		idfWriter.recordInputs("0","","Sky Clearness","");
		idfWriter.addObject(file);

		//cooling
		idfWriter.recordInputs("SizingPeriod:DesignDay","","","");
		idfWriter.recordInputs("CHICAGO Ann Clg 99.6% Condns WB=>MDB","","Name","");
		idfWriter.recordInputs("7","","Month","");
		idfWriter.recordInputs("21","","Day of Month","");
		idfWriter.recordInputs("SummerDesignDay","","Day Type","");
		idfWriter.recordInputs("31.2","C","Maximum Dry-Bulb Temperature","");
		idfWriter.recordInputs("10.7","deltaC","Daily Dry-Bulb Temperature Range","");
		idfWriter.recordInputs("","","Dry-Bulb Temperature Range Modifier Type","");
		idfWriter.recordInputs("","","Dry-Bulb Temperature Range Modifier Day Schedule Name","");
		idfWriter.recordInputs("Wetbulb","","Humidity Condition Type","");
		idfWriter.recordInputs("25.5","C","Wetbulb or DewPoint at Maximum Dry-Bulb","");
		idfWriter.recordInputs("","","Humidity Conition Day Scheduel Name","");
		idfWriter.recordInputs("","kgWater/kgDryAir","Humidity Ratio at Maximum Dry-Bulb","");
		idfWriter.recordInputs("","J/Kg","Enthalpy at Maximum Dry-Bulb","");
		idfWriter.recordInputs("","deltaC","Daily Wet-Bulb Tempereature Range","");
		idfWriter.recordInputs("99063","Pa","Barometric Pressure","");
		idfWriter.recordInputs("5.3","m/s","Wind Speed","");
		idfWriter.recordInputs("230","deg","Wind Direction","");
		idfWriter.recordInputs("No","","Rain Indicator","");
		idfWriter.recordInputs("No","","Snow Indicator","");
		idfWriter.recordInputs("No","","Daylight Saving Time Indicator","");
		idfWriter.recordInputs("ASHRAEClearSky","","Solar Model Indicator","");
		idfWriter.recordInputs("","","Beam Solar Day Schedule Name","");
		idfWriter.recordInputs("","","Diffuse Solar Day schedule Name","");
		idfWriter.recordInputs("","","ASHRAE Clear Skey Optical Depth for Beam Irradiance (taub)","");
		idfWriter.recordInputs("","","ASHRAE Clear Sky Optical Depth for Diffuse Irradiance (taud)","");
		idfWriter.recordInputs("1","","Sky Clearness","");
		idfWriter.addObject(file);
		
	}
	
	
	public void setUpEnvironmentForBaseline(IDFFileObject file){
		//Set up runperiod
		idfWriter.recordInputs("RunPeriod","","","");
		idfWriter.recordInputs("","","Name","");
		idfWriter.recordInputs("1","","Begin Month","");
		idfWriter.recordInputs("1","","Begin Day of Month","");
		idfWriter.recordInputs("12","","End Month","");
		idfWriter.recordInputs("12","","End Day of Month","");
		idfWriter.recordInputs("Sunday","","Day of Week for Start Day","");
		idfWriter.recordInputs("Yes","","Use Weatehr File Holidays and Special Days","");
		idfWriter.recordInputs("Yes","","Use Weather File Daylight Saving Period","");
		idfWriter.recordInputs("Yes","","Apply Weekend Holiday Rule","");
		idfWriter.recordInputs("Yes","","Use Weather File Rain Indicator","");
		idfWriter.recordInputs("Yes","","Use Weather File Snow INdicators","");
		idfWriter.recordInputs("1.0000","","Number of Times Runperiod to be Repeated","");
		idfWriter.addObject(file);
		
		//Ground temperature
		idfWriter.recordInputs("Site:GroundTemperature:BuildingSurface","","","");
		idfWriter.recordInputs("19.527","C","January Ground Temperature","");
		idfWriter.recordInputs("19.502","C","February Ground Temperature","");
		idfWriter.recordInputs("19.536","C","March Ground Temperature","");
		idfWriter.recordInputs("19.598","C","April Ground Temperature","");
		idfWriter.recordInputs("20.002","C","May Ground Temperature","");
		idfWriter.recordInputs("21.64","C","June Ground Temperature","");
		idfWriter.recordInputs("22.225","C","July Ground Temperature","");
		idfWriter.recordInputs("22.375","C","August Ground Temperature","");
		idfWriter.recordInputs("21.449","C","September Ground Temperature","");
		idfWriter.recordInputs("20.121","C","October Ground Temperature","");
		idfWriter.recordInputs("19.802","C","November Ground Temperature","");
		idfWriter.recordInputs("19.633","C","December Ground Temperature","");
		idfWriter.addObject(file);
		
	}
}
