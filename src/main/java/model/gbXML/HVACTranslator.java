package main.java.model.gbXML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.Namespace;

//import main.java.gbXML.hvac.GbXMLHVAC;
//import main.java.gbXML.hvac.VariableAirVolume;
import main.java.model.idf.IDFFileObject;

/**
 * Directly translates the HVAC from gbXML given this part of the data is provided by gbXML
 * TODO not completed - inadequate cases.
 * @author weilixu
 *
 */
@SuppressWarnings("unused")
public class HVACTranslator {
    private ScheduleTranslator scheduleTranslator;
    private CampusTranslator campusTranslator;
    private IDFFileObject file;
    
    private Element root;
    private Namespace ns;
    
    private HashMap<String, ArrayList<String>> airLoopToZone;
    public HVACTranslator(IDFFileObject file, ScheduleTranslator schT, CampusTranslator campusT, Element root, Namespace ns){
        this.file = file;
        scheduleTranslator = schT;
        campusTranslator = campusT;
        
        this.root = root;
        this.ns = ns;
        
        airLoopToZone = new HashMap<String, ArrayList<String>>();
        translateAirLoops();
    }
    
    /**
     * This method translates air loop into EnergyPlus air loop (technically primary loop)
     * This method also translates radiant-based systems
     * 
     * 
     */
    private void translateAirLoops(){
        List<Element> airLoopElements = root.getChildren("AirLoop",ns);
        for(int i=0; i<airLoopElements.size(); i++){
            Element airLoop = airLoopElements.get(i);
            String alId = airLoop.getAttributeValue("id",ns);
            String alName = airLoop.getChildText("Name",ns);
            if(alName==null){
                alName = "";
            }
            alName = escapeName(alId, alName);
            
            
            String controlZoneId = airLoop.getAttributeValue("controlZoneIdRef",ns);
            ArrayList<String> spaceList = campusTranslator.getSpacesInAThermalZone(controlZoneId);
            
            String type = airLoop.getAttributeValue("systemType",ns);
            
            //GbXMLHVAC hvac = null;
            if(type.equalsIgnoreCase("VariableAirVolume")){
                //hvac = new VariableAirVolume(alName, airLoop, spaceList, file,ns);
            }
        }
    }
    
    private String escapeName(String id, String name) {
        String value = id;
        if (name!=null&& !name.isEmpty()) {
            value = name;
        }
        return value.replace(",", "-").replace(";", "-");
    }
    

}
