package main.java.model.gbXML;

import org.jdom2.Element;
import org.jdom2.Namespace;

/**
 * This class is use to store and translate thermal zone related objects
 * mainly:
 * Sizing:Zone
 * DesignSpecification:ZoneAirDistribution
 * DesignSpecification:OutdoorAir
 * ZoneControl:Thermostat
 * 
 * @author weilixu
 *
 */
public class GbXMLThermalZone {
    
    private Namespace ns;
        
    private String heatScheduleId;
    private String coolScheduleId;
    private String oaScheduleId;
    private String achScheduleId;
    private String fanScheduleId;
    
    private Double DesignHeatT;
    private Double DesignCoolT;
    private Double ACH;
    private Double flowPerArea;
    private Double flowPerPerson;
    private Double OAFlowPerArea;
    private Double OAFlowPerPerson;
    private Double OAFlowPerZone;
    private Double maxOAFlowPerZone;
    private Double minOAFlowPerZone;
    private String minimumOutdoorAirControlType; //{FixedMinimum, ProportionalMinimum}
    
    private String hydronicLoopIdRef;
    private String hydronicLoopType;
    private String AirLoopId;
    private Double coolingSizingFactor;
    private Double heatingSizingFactor;
    private String baseBoardHeatingType; //{ACH, HotWater, Electric}
    private String baseboardHeatingCaapcity;

    //private List<IDFObject> zoneRelatedObjects;
    private int thermostatControlType;
    
    public GbXMLThermalZone(Namespace ns){
        this.ns = ns;
        //zoneRelatedObjects = new ArrayList<IDFObject>();
    }
    
    /**
     * @param element
     */
    public void translateThermalZone(Element element){
        //determine thermostat
        heatScheduleId = element.getAttributeValue("heatSchedIdRef");
        if(heatScheduleId==null){
            heatScheduleId = "";
        }
        coolScheduleId = element.getAttributeValue("coolSchedIdRef");
        if(coolScheduleId == null){
            coolScheduleId = "";
        }
        String designHeat = element.getChildText("DesignHeatT",ns);
        String designCool = element.getChildText("DesignCoolT",ns);
        //System.out.println(designHeat + " " + designCool);
        if(designHeat==null && designCool!=null){
            thermostatControlType = 2;
        }else if(designHeat!=null && designCool==null){
            thermostatControlType = 1;
        }else if(designHeat!=null && designCool!=null){
            DesignHeatT = Double.parseDouble(designHeat);
            DesignCoolT = Double.parseDouble(designCool);
            if(DesignHeatT == DesignCoolT){
                thermostatControlType = 3;
            }else{
                thermostatControlType = 4;
            }
        }else{
            //TODO Warning - no thermostat control type is defined, default to 4.
            thermostatControlType = 4;
        }
                
        //ACH
        String ach = element.getChildText("AirChangesPerHour",ns);
        if(ach!=null){
        	//System.out.println(ach);
            ACH = Double.parseDouble(ach);
        }
        
        //Flow Per Area
        String fpa = element.getChildText("FlowPerArea",ns);
        if(fpa!=null){
            String unit = element.getChild("FlowPerArea",ns).getAttributeValue("unit");
            flowPerArea = Double.parseDouble(fpa) * GbXMLUnitConversion.flowPerAreaConversionRate(unit, "CubicMPerSecPerSquareM");
        }
        
        //flow per person
        String fpp = element.getChildText("FlowPerPerson",ns);
        if(fpp!=null){
            String unit = element.getChild("FlowPerPerson",ns).getAttributeValue("unit");
            flowPerPerson = Double.parseDouble(fpp) * GbXMLUnitConversion.flowConversionRate(unit, "CubicMPerSec");
        }
        
        //OAFlowPerArea;
        String oafpa = element.getChildText("OAFlowPerArea",ns);
        if(oafpa!=null){
            //TODO haven't handle the fraction unit
            String unit = element.getChild("OAFlowPerArea",ns).getAttributeValue("unit");
            OAFlowPerArea = Double.parseDouble(oafpa) * GbXMLUnitConversion.flowPerAreaConversionRate(unit, "CubicMPerSecPerSquareM");
        }
        
        //OA Flow per person
        String oafpp = element.getChildText("OAFlowPerPerson",ns);
        if(oafpp!=null){
            //TODO haven't handle the fraction unit
            String unit = element.getChild("OAFlowPerPerson",ns).getAttributeValue("unit");
            OAFlowPerPerson = Double.parseDouble(oafpp) * GbXMLUnitConversion.flowConversionRate(unit, "CubicMPerSecPerSquareM");
        }
        
        //OA Flow Per Zone
        String oafpz = element.getChildText("OAFlowPerZone",ns);
        if(oafpz!=null){
            //TODO haven't handle the fraction unit
            String unit = element.getChild("OAFlowPerZone",ns).getAttributeValue("unit");
            OAFlowPerZone = Double.parseDouble(oafpz) * GbXMLUnitConversion.flowConversionRate(unit, "CubicMPerSecPerSquareM");
        }
        
        //Max OA Flow Per Zone
        String maxoafpz = element.getChildText("MaxOAFlowPerZone",ns);
        if(maxoafpz!=null){
            //TODO haven't handle the fraction unit
            String unit = element.getChild("MaxOAFlowPerZone",ns).getAttributeValue("unit");
            maxOAFlowPerZone = Double.parseDouble(maxoafpz) * GbXMLUnitConversion.flowConversionRate(unit, "CubicMPerSecPerSquareM");
        }
        
        //Min OA Flow Per Zone
        String minoafpz = element.getChildText("MinOAFlowPerZone",ns);
        if(minoafpz!=null){
            //TODO haven't handle the fraction unit
            String unit = element.getChild("MinOAFlowPerZone",ns).getAttributeValue("unit");
            minOAFlowPerZone = Double.parseDouble(minoafpz) * GbXMLUnitConversion.flowConversionRate(unit, "CubicMPerSecPerSquareM");
        }
        
        //hydronicLoop Id
        Element hydronicLoopId = element.getChild("HydronicLoopId",ns);
        if(hydronicLoopId!=null){
            hydronicLoopIdRef = hydronicLoopId.getAttributeValue("hydronicLoopIdRef");
            hydronicLoopType = hydronicLoopId.getAttributeValue("hydronicLoopType");
        }
        //airloop id
        Element airLoopId = element.getChild("AirLoopId", ns);
        if(airLoopId!=null){
            AirLoopId = airLoopId.getAttributeValue("airLoopIdRef");
        }
        //
    }
    
    public String getHeatScheduleId() {
        if(heatScheduleId == null){
            heatScheduleId = "";
        }
        return heatScheduleId;
    }

    public String getCoolScheduleId() {
        if(coolScheduleId == null){
            coolScheduleId = "";
        }
        return coolScheduleId;
    }

    public String getOaScheduleId() {
        if(oaScheduleId == null){
            oaScheduleId = "";
        }
        return oaScheduleId;
    }

    public String getAchScheduleId() {
        if(achScheduleId == null){
            achScheduleId = "";
        }
        return achScheduleId;
    }

    public String getFanScheduleId() {
        if(fanScheduleId == null){
            fanScheduleId = "";
        }
        return fanScheduleId;
    }

    public Double getDesignHeatT() {
        return DesignHeatT;
    }

    public Double getDesignCoolT() {
        return DesignCoolT;
    }

    public Double getACH() {
        return ACH;
    }

    public Double getFlowPerArea() {
        return flowPerArea;
    }

    public Double getFlowPerPerson() {
        return flowPerPerson;
    }

    public Double getOAFlowPerArea() {
        return OAFlowPerArea;
    }

    public Double getOAFlowPerPerson() {
        return OAFlowPerPerson;
    }

    public Double getOAFlowPerZone() {
        return OAFlowPerZone;
    }

    public Double getMaxOAFlowPerZone() {
        return maxOAFlowPerZone;
    }

    public Double getMinOAFlowPerZone() {
        return minOAFlowPerZone;
    }

    public String getMinimumOutdoorAirControlType() {
        return minimumOutdoorAirControlType;
    }

    public String getHydronicLoopIdRef() {
        return hydronicLoopIdRef;
    }

    public String getHydronicLoopType() {
        return hydronicLoopType;
    }

    public String getAirLoopId() {
        return AirLoopId;
    }

    public Double getCoolingSizingFactor() {
        return coolingSizingFactor;
    }

    public Double getHeatingSizingFactor() {
        return heatingSizingFactor;
    }

    public String getBaseBoardHeatingType() {
        return baseBoardHeatingType;
    }

    public String getBaseboardHeatingCaapcity() {
        return baseboardHeatingCaapcity;
    }

    public int getThermostatControlType() {
        return thermostatControlType;
    }
}
