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
    private String zoneKey;
        
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
    
    protected GbXMLThermalZone(Namespace ns){
        this.ns = ns;
        //zoneRelatedObjects = new ArrayList<IDFObject>();
    }
    
    /**
     * used for manually write in Thermal zone information - 
     * initialize will use office settings with no schedule
     * attached
     * @param zoneData
     */
    public GbXMLThermalZone(String zoneKey){
    	this.zoneKey = zoneKey;
    	
        heatScheduleId = "";
        coolScheduleId = "";
        oaScheduleId = "";
        achScheduleId = "";
        fanScheduleId = "";
        
        DesignHeatT = 21.0;
        DesignCoolT = 24.0;
        ACH = 0.0;
        flowPerArea = 0.0025;
        flowPerPerson = 0.0006;
        OAFlowPerArea = 0.0025;
        OAFlowPerPerson = 0.0006;
        OAFlowPerZone = 0.0;
        maxOAFlowPerZone = 0.0;
        minOAFlowPerZone = 0.0;
        minimumOutdoorAirControlType = ""; //{FixedMinimum, ProportionalMinimum}
        
        hydronicLoopIdRef = "";
        hydronicLoopType = "";
        AirLoopId = "";
        coolingSizingFactor = 1.15;
        heatingSizingFactor = 1.25;
        baseBoardHeatingType = ""; //{ACH, HotWater, Electric}
        baseboardHeatingCaapcity = "";
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
    
    public String getZoneKey(){
    	return zoneKey;
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

	public Namespace getNs() {
		return ns;
	}

	public void setHeatScheduleId(String heatScheduleId) {
		this.heatScheduleId = heatScheduleId;
	}

	public void setCoolScheduleId(String coolScheduleId) {
		this.coolScheduleId = coolScheduleId;
	}

	public void setOaScheduleId(String oaScheduleId) {
		this.oaScheduleId = oaScheduleId;
	}

	public void setAchScheduleId(String achScheduleId) {
		this.achScheduleId = achScheduleId;
	}

	public void setFanScheduleId(String fanScheduleId) {
		this.fanScheduleId = fanScheduleId;
	}

	public void setDesignHeatT(Double designHeatT) {
		DesignHeatT = designHeatT;
	}

	public void setDesignCoolT(Double designCoolT) {
		DesignCoolT = designCoolT;
	}

	public void setACH(Double aCH) {
		ACH = aCH;
	}

	public void setFlowPerArea(Double flowPerArea) {
		this.flowPerArea = flowPerArea;
	}

	public void setFlowPerPerson(Double flowPerPerson) {
		this.flowPerPerson = flowPerPerson;
	}

	public void setOAFlowPerArea(Double oAFlowPerArea) {
		OAFlowPerArea = oAFlowPerArea;
	}

	public void setOAFlowPerPerson(Double oAFlowPerPerson) {
		OAFlowPerPerson = oAFlowPerPerson;
	}

	public void setOAFlowPerZone(Double oAFlowPerZone) {
		OAFlowPerZone = oAFlowPerZone;
	}

	public void setMaxOAFlowPerZone(Double maxOAFlowPerZone) {
		this.maxOAFlowPerZone = maxOAFlowPerZone;
	}

	public void setMinOAFlowPerZone(Double minOAFlowPerZone) {
		this.minOAFlowPerZone = minOAFlowPerZone;
	}

	public void setMinimumOutdoorAirControlType(String minimumOutdoorAirControlType) {
		this.minimumOutdoorAirControlType = minimumOutdoorAirControlType;
	}

	public void setHydronicLoopIdRef(String hydronicLoopIdRef) {
		this.hydronicLoopIdRef = hydronicLoopIdRef;
	}

	public void setHydronicLoopType(String hydronicLoopType) {
		this.hydronicLoopType = hydronicLoopType;
	}

	public void setAirLoopId(String airLoopId) {
		AirLoopId = airLoopId;
	}

	public void setCoolingSizingFactor(Double coolingSizingFactor) {
		this.coolingSizingFactor = coolingSizingFactor;
	}

	public void setHeatingSizingFactor(Double heatingSizingFactor) {
		this.heatingSizingFactor = heatingSizingFactor;
	}

	public void setBaseBoardHeatingType(String baseBoardHeatingType) {
		this.baseBoardHeatingType = baseBoardHeatingType;
	}

	public void setBaseboardHeatingCaapcity(String baseboardHeatingCaapcity) {
		this.baseboardHeatingCaapcity = baseboardHeatingCaapcity;
	}

	public void setThermostatControlType(int thermostatControlType) {
		this.thermostatControlType = thermostatControlType;
	}
}
