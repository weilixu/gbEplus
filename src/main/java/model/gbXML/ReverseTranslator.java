package main.java.model.gbXML;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import com.google.gson.Gson;

import main.java.api.DataBaseType;
import main.java.api.EnergyPlusDataAPI;
import main.java.config.FilesPath;
import main.java.model.idf.IDFFileObject;
import main.java.model.idf.IDFObject;
import main.java.model.idf.IDFWriter;
import main.java.plugins.ashraeassumptions.ASHRAEConstructions;
import main.java.plugins.ashraebaseline.ASHRAEHVAC;

@SuppressWarnings("unused")
public class ReverseTranslator {
    private Gson errorLog;
    
    private Document doc;
    private Namespace ns;
    private String energyPlusVersion;//specify the version of EnergyPlus that one wish to convert
    
    private String m_temperatureUnit;
    private String m_lengthUnit;
    private String areaUnit;
    private String volumeUnit;
    private boolean m_useSIUnitsForResults;
    private Double m_lengthMultiplier;
        
    private IDFFileObject file;
    private ClimateTranslation climateTranslator;
    private EnvelopeTranslator envelopeTranslator;
    private ScheduleTranslator scheduleTranslator;
    private CampusTranslator campusTranslator;
    private OutputModule outputTranslator;
    
    private IDFWriter idfWriter;
    
    private List<EnergyPlusDataAPI> dataPluginList;
    
    public ReverseTranslator(Document d, String version){
        doc = d;
        ns = doc.getRootElement().getNamespace();
        file = new IDFFileObject();
        if(version==null){
        	energyPlusVersion = "8.6";//default to 8.6 at the current moment..        	
        }else{
        	energyPlusVersion = version;
        }
                
        envelopeTranslator = new EnvelopeTranslator(ns);
        scheduleTranslator = new ScheduleTranslator(ns);
        climateTranslator = new ClimateTranslation(ns);
        outputTranslator = new OutputModule(ns);
        
        idfWriter = new IDFWriter();
        
        //error log
        errorLog = new Gson();
        
        //plugin
        dataPluginList = new ArrayList<EnergyPlusDataAPI>();
    }
    
    public void setEnergyPlusVersion(String version){
    	energyPlusVersion = version;
    }
    
    public void convert(){  
        addEssentialFileElement();
        translateGBXML(doc.getRootElement(), doc);
    }
    
    public ScheduleTranslator getScheduleTranslator(){
    	return scheduleTranslator;
    }
    
    public CampusTranslator getCampusTranslator(){
    	return campusTranslator;
    }
    
    private void translateGBXML(Element gbXML, Document doc){
        //get the document element gbXML
        //they are not mapped directly to IDF, but needed to map
        String temperatureUnit = gbXML.getAttributeValue("temperatureUnit");
        if(temperatureUnit.equalsIgnoreCase("F")){
            m_temperatureUnit = "F";
        }else if(temperatureUnit.equalsIgnoreCase("C")){
            m_temperatureUnit = "C";
        }else if(temperatureUnit.equalsIgnoreCase("K")){
            m_temperatureUnit = "K";
        }else if(temperatureUnit.equalsIgnoreCase("R")){
            m_temperatureUnit = "R";
        }else{
            //TODO should give a warning: "No temperature unit specified, using C"
            m_temperatureUnit = "C";
        }
        
        String lengthUnit = gbXML.getAttributeValue("lengthUnit");
        
        m_lengthMultiplier = GbXMLUnitConversion.lengthUnitConversionRate(lengthUnit, "Meters");
        // {SquareKilometers, SquareMeters, SquareCentimeters, SquareMillimeters, SquareMiles, SquareYards, SquareFeet, SquareInches}
        areaUnit = gbXML.getAttributeValue("areaUnit");
        
        // {CubicKilometers, CubicMeters, CubicCentimeters, CubicMillimeters, CubicMiles, CubicYards, CubicFeet, CubicInches}
        volumeUnit = gbXML.getAttributeValue("volumeUnit");
        
        // {true, false}
        String useSIUnitsForResults = gbXML.getAttributeValue("useSIUnitsForResults");
        if(useSIUnitsForResults.equalsIgnoreCase("False")){
            m_useSIUnitsForResults = false;
        }else{
            m_useSIUnitsForResults = true;
        }
        
        //do climate translation
        climateTranslator.setUpEnvironmentForBaseline(file);
        climateTranslator.setUpDesignDayConditionForBaseline(file);
        //do materials before constructions
        //TODO progress bar info
        
        List<Element> materialElements = gbXML.getChildren("Material",ns);
        if(materialElements.isEmpty()){
        	//fill in baseline material elements
        	for(int i=0; i<dataPluginList.size(); i++){
        		EnergyPlusDataAPI dataAPI = dataPluginList.get(i);
        		if(dataAPI.dataBaseType().equals(DataBaseType.CONSTRUCTION)){
        			envelopeTranslator.generateAssumptions(file, dataAPI);
        			break;//only allow one construction plugin activate
        		}
        	}
        }else{
            for(int i=0; i<materialElements.size(); i++){
                Element materialElement = materialElements.get(i);
                file.addIDFObject(envelopeTranslator.translateMaterial(materialElement));
            }//for
            //do construction before surfaces
            //TODO progress bar info
            List<Element> layerElements = gbXML.getChildren("Layer",ns);
            List<Element> contructionElements = gbXML.getChildren("Construction",ns);
            for(int i=0; i<contructionElements.size(); i++){
                Element constructionElement = contructionElements.get(i);
                file.addIDFObject(envelopeTranslator.translateConstruction(constructionElement, layerElements));
            }//for
            
            //do windows before surfaces
            //TODO progress bar info
            List<Element> windowTypeElements = gbXML.getChildren("WindowType",ns);
            for(int i=0; i<windowTypeElements.size(); i++){
                Element windowTypeElement = windowTypeElements.get(i);
                
                envelopeTranslator.translateWindowType(windowTypeElement, file);
            }
        }
        
        //do schedules before loads
        //TODO progress bar info
        List<Element> scheduleElements = gbXML.getChildren("Schedule",ns);
        if(scheduleElements.isEmpty()){
        	//fill in baseline material elements
        	for(int i=0; i<dataPluginList.size(); i++){
        		EnergyPlusDataAPI dataAPI = dataPluginList.get(i);
        		if(dataAPI.dataBaseType().equals(DataBaseType.SCHEDULE)){
        			scheduleTranslator.generateAssumptions(file, dataAPI);
        			break;//only allow one construction plugin activate
        		}
        	}
        }else{
            for(int i=0; i<scheduleElements.size(); i++){
                Element scheduleElement = scheduleElements.get(i);
                scheduleTranslator.translateSchedule(scheduleElement, gbXML, file);
            }
            //add more misc schedule types
            //progress bar process schedule finished
        }
        scheduleTranslator.addMiscScheduleTypeLimits(file);
        
        //start processing campus
        campusTranslator = new CampusTranslator(ns, m_lengthMultiplier, dataPluginList);
        campusTranslator.setAreaUnit(areaUnit);
        campusTranslator.setVolumnUnit(volumeUnit);
        campusTranslator.setEnvelopeTranslator(envelopeTranslator);
        campusTranslator.setScheduleTranslator(scheduleTranslator);
        
        //do thermal zones before spaces
        List<Element> zoneElements = gbXML.getChildren("Zone",ns);
        //TODO progress bar info
        for(int i=0; i<zoneElements.size(); i++){
            Element zoneElement = zoneElements.get(i);
            campusTranslator.translateThermalZone(zoneElement);
        }
        
        //***********************do geometry***********************
        Element campusElements = gbXML.getChild("Campus",ns);
        campusTranslator.translateCampus(campusElements, file);
        campusTranslator.convertBuilding(file);
        //file.setValueCommentPad(100);
        //System.out.println(file.getIDFFileContent());
        
        //**************do hvac**************************************
        //TODO read the HVAC system
        //TODO if no HVAC system, then baseline system selection should be implemented.
        //TODO baseline should be able to extend to residential buildings
        EnergyPlusDataAPI hvacPlugin = null;
        for(int i=0; i<dataPluginList.size(); i++){
        	if(dataPluginList.get(i).equals(DataBaseType.HVAC)){
        		hvacPlugin = dataPluginList.get(i);
        		break;
        	}
        }
        if(hvacPlugin == null){
        	hvacPlugin = new ASHRAEHVAC();//if no systems, use ASHRAE baseline
        }
        hvacPlugin.writeInHVACSystem(file, this);
        
        //*********************Output********************************
        outputTranslator.addTableSummary(file);
    }
    
    private void addEssentialFileElement(){
        idfWriter.recordInputs("Version","","","");
        idfWriter.recordInputs(energyPlusVersion,"","Version Identifier","");
        idfWriter.addObject(file);  

        //TODO this should depends on user options - from interface
        idfWriter.recordInputs("SimulationControl","","","");
        idfWriter.recordInputs("Yes","","Do Zone Sizing Calculation","");
        idfWriter.recordInputs("Yes","","Do System Sizing Calculation","");
        idfWriter.recordInputs("Yes","","Do Plant Sizing Calculation","");
        idfWriter.recordInputs("No","","Run Simulation for Sizing Periods","");
        idfWriter.recordInputs("Yes","","Run Simulation for Weather File Run Periods","");
        idfWriter.recordInputs("Yes","","Do HVAC Sizing Simulation for Sizing Periods","");
        idfWriter.recordInputs("","","Maximum Number of HVAC Sizing Simulation Passes","");
        idfWriter.addObject(file);

        //Shadow calculation
        idfWriter.recordInputs("ShadowCalculation","","","");
        idfWriter.recordInputs("AverageOverDaysInFrequency","","Calculation Method","");
        idfWriter.recordInputs("20","","Calculation Frequency","");
        idfWriter.recordInputs("15000","","Maximum Figures in Shadow Overlap Calculations","");
        idfWriter.recordInputs("SutherlandHodgman","","Polygon Clipping Algorithm","");
        idfWriter.recordInputs("SimpleSkyDiffuseModeling","","Sky Diffuse Modeling Algorithm","");
        idfWriter.addObject(file);
        
        //inside algorithm
        idfWriter.recordInputs("SurfaceConvectionAlgorithm:Inside","","","");
        idfWriter.recordInputs("TARP","","Algorithm","");
        idfWriter.addObject(file);
        
        //outside algorithm
        idfWriter.recordInputs("SurfaceConvectionAlgorithm:Outside","","","");
        idfWriter.recordInputs("DOE-2","","Algorithm","");
        idfWriter.addObject(file);
        
        //heat balance algorithm
        idfWriter.recordInputs("HeatBalanceAlgorithm","","","");
        idfWriter.recordInputs("ConductionTransferFunction","","Algorithm","");
        idfWriter.recordInputs("2000","C","Surface Temperature Upper Limit","");
        idfWriter.recordInputs("","W/m2-K","Minimum Surface Convection Heat Transfer Coefficient Value","");
        idfWriter.recordInputs("","W/m2-K","Maximum Surface Convection Heat Transfer Coefficient Value","");
        idfWriter.addObject(file);
        
        //capacitance multiplier
        idfWriter.recordInputs("ZoneCapacitanceMultiplier:ResearchSpecial","","","");
        idfWriter.recordInputs("1","","Temperature Capacity Multiplier","");
        idfWriter.recordInputs("1","","Humidity Capacity Multiplier","");
        idfWriter.recordInputs("1","","Carbon Dioxide Capacity Multiplier","");
        idfWriter.recordInputs("1","","Generic Contaminant Capacity Multiplier","");
        idfWriter.addObject(file);
        
        //Timestep
        idfWriter.recordInputs("Timestep","","","");
        idfWriter.recordInputs("6","","Number of Timesteps per Hour","");
        idfWriter.addObject(file);
        
        //Convegence limits
        idfWriter.recordInputs("ConvergenceLimits","","","");
        idfWriter.recordInputs("1","minutes","Minimum System Timestep","");
        idfWriter.recordInputs("25","","Maximum HVAC iterations","");
        idfWriter.recordInputs("","","Minimum Plant Iterations","");
        idfWriter.recordInputs("","","Maximum plant Iterations","");
    }

    public void exportFile(String idfFilePath){
        try {
            PrintWriter out = new PrintWriter(idfFilePath + "/test.idf");
            out.println(file.getIDFFileContent());
            out.close();
            
            //GeometryFromIDFFileObject idfConverter = new GeometryFromIDFFileObject();
            //idfConverter.extractGeometry(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public IDFFileObject getIDFFileObject(){
        return file;
    }
    
    public void registerDataPlugins(EnergyPlusDataAPI dataPlugin){
    	dataPluginList.add(dataPlugin);
    }
}
