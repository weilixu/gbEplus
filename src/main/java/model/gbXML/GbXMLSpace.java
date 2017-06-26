package main.java.model.gbXML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.Namespace;

public class GbXMLSpace {
    
    private Namespace ns;
    
    private GbXMLThermalZone thermalZone;
    private String spaceId;
    private String storeyId;
    private String spaceName;
    private String spaceType;
    private String thermalZoneId;
    private String lightScheduleId;
    private String equipmentScheduleId;
    private String peopleScheduleId;
    private String buildingStoreyId;
    private String conditionType;
    
    //elements
    private Double infiltrationFlow;
    private String infiltrationUnit;
    private Double lightPowerPerArea;
    private Double equipPowerPerArea;
    private Double peopleNumber;
    private Double[] peopleHeatGain; //max 3
    
    //TODO
    //lighting, lighting control, airchangesperhour
    
    private Double Area;
    private Double Volume;
    
    //the coordinates under space boundary element is the middle line of the building
    //wheareas the surface elements are the inner line
    private HashMap<String, ArrayList<Double[]>> surfaceList;
    private double lengthMultiplier;
    
    private String areaUnit;
    private String volumeUnit;
    
    
    public GbXMLSpace(double lm, Namespace ns){
        lengthMultiplier = lm;
        this.ns = ns;
        surfaceList = new HashMap<String, ArrayList<Double[]>>();
    }
    
    public void setAreaUnit(String unit){
        areaUnit = unit;
    }
    
    public void setVolumeUnit(String unit){
        volumeUnit = unit;
    }
    
    public void setGbXMLThermalZone(GbXMLThermalZone zone){
        thermalZone = zone;
    }
    
    public Namespace getNs() {
        return ns;
    }

    public GbXMLThermalZone getThermalZone() {
        return thermalZone;
    }

    public String getThermalZoneId() {
        return thermalZoneId;
    }

    public String getLightScheduleId() {
        return lightScheduleId;
    }

    public String getEquipmentScheduleId() {
        return equipmentScheduleId;
    }

    public String getPeopleScheduleId() {
        return peopleScheduleId;
    }

    public String getBuildingStoreyId() {
        return buildingStoreyId;
    }

    public String getConditionType() {
        return conditionType;
    }

    public Double getInfiltrationFlow() {
        return infiltrationFlow;
    }

    public String getInfiltrationUnit() {
        return infiltrationUnit;
    }

    public Double getLightPowerPerArea() {
        return lightPowerPerArea;
    }

    public Double getEquipPowerPerArea() {
        return equipPowerPerArea;
    }

    public Double getPeopleNumber() {
        return peopleNumber;
    }

    public Double[] getPeopleHeatGain() {
        return peopleHeatGain;
    }

    public Double getArea() {
        return Area;
    }

    public Double getVolume() {
        return Volume;
    }

    public HashMap<String, ArrayList<Double[]>> getSurfaceList() {
        return surfaceList;
    }

    public double getLengthMultiplier() {
        return lengthMultiplier;
    }

    public String getAreaUnit() {
        return areaUnit;
    }

    public String getVolumeUnit() {
        return volumeUnit;
    }
    
    public String getSpaceName(){
        return spaceName;
    }
    
    public String getSpaceId(){
        return spaceId;
    }
    
    public String getSpaceType(){
        return spaceType;
    }
    
    public String getStoreyId(){
    	return storeyId;
    }

    public void translateSpace(Element element){
        //first process the attributes
        thermalZoneId = element.getAttributeValue("zoneIdRef");
        lightScheduleId = element.getAttributeValue("lightScheduleIdRef");
        equipmentScheduleId = element.getAttributeValue("equipmentScheduleIdRef");
        peopleScheduleId = element.getAttributeValue("peopleScheduleIdRef");
        buildingStoreyId = element.getAttributeValue("buildingStoreyIdRef");
        conditionType = element.getAttributeValue("conditionType");
        spaceId = element.getAttributeValue("id");
        spaceType = element.getAttributeValue("spaceType");
        storeyId = element.getAttributeValue("buildingStoreyIdRef");
        //System.out.println(spaceType);

        if(conditionType==null){
            conditionType = "Unconditioned";
            //TODO Warning - no condition type is found for this space. Set to unconditioned
        }
        
        if(spaceType==null){
            if(conditionType.equals("Heated") || conditionType.equals("Cooled")
                    || conditionType.equals("HeatedAndCooled")){
                spaceType = "OfficeEnclosed";//default to office
            }
            //WARNING - Cannot find spaceType attribute - default to OfficeEnclosed
        }
        
        if(buildingStoreyId == null){
            buildingStoreyId = "0";//WX in case there is no building story id
        }
        
        spaceName = buildingStoreyId + ":" + spaceId;
        
        //process assumptions
        Element area = element.getChild("Area",ns);
        String unit = area.getAttributeValue("unit");
        if(unit==null){
                unit = areaUnit;
        }
        Area = Double.parseDouble(area.getText()) * GbXMLUnitConversion.areaUnitRate(unit, "SquareMeters");
        
        Element volume = element.getChild("Volume",ns);
        unit = area.getAttributeValue("unit");
        if(unit==null){
            unit = volumeUnit;
        }
        Volume = Double.parseDouble(volume.getText()) * GbXMLUnitConversion.volumeUnitRate(unit, "CubicMeters");
        
        Element lppa = element.getChild("LightPowerPerArea",ns);
        if(lppa!=null){
            unit = lppa.getAttributeValue("unit");
            //buildsimhub - w/m2
            lightPowerPerArea = Double.parseDouble(lppa.getText()) * GbXMLUnitConversion.powerPerAreaRate(unit, "WattPerSquareMeter");
        }
        
        Element eppa = element.getChild("EquipPowerPerArea",ns);
        if(eppa!=null){
            unit = eppa.getAttributeValue("unit");
            //buildsimhub - w/m2
            equipPowerPerArea = Double.parseDouble(eppa.getText()) * GbXMLUnitConversion.powerPerAreaRate(unit, "WattPerSquareMeter");
        }
        
        Element pn = element.getChild("PeopleNumber",ns);
        if(pn!=null){
            unit = pn.getAttributeValue("unit");
            //buildsimhub - m2/people
            if(unit.equals("NumberOfPeople")){
                peopleNumber = Area/Double.parseDouble(pn.getText());
            }
        }
        
        List<Element> peopleHeatGains = element.getChildren("PeopleHeatGain",ns);
        if(!peopleHeatGains.isEmpty()){
            peopleHeatGain = new Double[3];//3 maximum, 1:sensible, 2:latent, 3:reserved TODO
            for(int i=0;i<peopleHeatGains.size(); i++){
                if(i>2){
                    break;
                }
                Element pgh = peopleHeatGains.get(i);
                unit = pgh.getAttributeValue("unit");
                if(unit.contains("Btu")){
                    unit = "BtuPerHour";
                }else{
                    unit = "Watt";
                }
                String type = pgh.getAttributeValue("heatGainType");
                if(type.equals("Sensible")){
                    peopleHeatGain[0] = Double.parseDouble(pgh.getText()) * GbXMLUnitConversion.powerRate(unit, "Watt");
                }else if(type.equals("Latent")){
                    peopleHeatGain[1] = Double.parseDouble(pgh.getText()) * GbXMLUnitConversion.powerRate(unit, "Watt");
                }
            }            
        }
    
        Element infiltrationFlowEle = element.getChild("InfiltrationFlow",ns);
        if(infiltrationFlowEle!=null){
            Element blowerDoorTest = element.getChild("BlowerDoorValue",ns);
            if(blowerDoorTest!=null){
                unit = blowerDoorTest.getAttributeValue("unit");
                if(unit.equals("ACH") || unit.equals("AirChangesPerHour")){
                    infiltrationUnit = "ACH";
                    infiltrationFlow = Double.parseDouble(blowerDoorTest.getText());
                }else{
                    infiltrationUnit = "m2";
                    infiltrationFlow = Double.parseDouble(blowerDoorTest.getText()) * GbXMLUnitConversion.lengthUnitConversionRate(unit, "SquareMeters");
                }                
            }
        }
        
        //TODO re process to check the data - if these are not filled, then we should call our database to retrieve these data
//        if(lightPowerPerArea==null){
//            //TODO get data according to spaceType;
//            if(spaceType==null || spaceType.equals("Office")){
//                lightPowerPerArea = 9.68783638320775;
//            }
//        }
//        
//        if(equipPowerPerArea == null){
//            //TODO get data according to spaceType;
//            if(spaceType==null || spaceType.equals("Office")){
//                equipPowerPerArea = 14.4199912777546;
//            }
//        }
//        
//        if(peopleNumber == null){
//            //TODO get data according to spaceType;
//            if(spaceType==null ||spaceType.equals("Office")){
//                peopleNumber = 20.0;
//            }else if(spaceType.isEmpty()){
//                peopleNumber = 0.0;
//            }
//        }
//        
        if(peopleHeatGain==null){
            //TODO get data according to spaceType;
            if(spaceType==null || spaceType.equals("Office")){
                peopleHeatGain = new Double[3];
                peopleHeatGain[0] = 126.0;
                peopleHeatGain[1] = 0.0;
                peopleHeatGain[2] = 0.0;
            }else if(spaceType.isEmpty()){
                peopleHeatGain = new Double[3];
                peopleHeatGain[0] = 0.0;
                peopleHeatGain[1] = 0.0;
                peopleHeatGain[2] = 0.0;
            }
        }
        
        if(infiltrationFlow == null){
            //TODO get data according to spaceType;
            if(infiltrationUnit==null){
                infiltrationUnit = "ACH";
                infiltrationFlow = 0.4;
            }else if(infiltrationUnit.equals("ACH")){
                infiltrationFlow = 0.4;
            }else if(infiltrationUnit.equals("m2")){
                infiltrationFlow = 0.4/3600 * Volume;
            }
            //TODO Warning - the infiltration flow isnot specified in the gbXML file. Reset to 0.4 ACH
        }
        
        //now process the geometry elements;
        List<Element> spaceBoundaries = element.getChildren("SpaceBoundary",ns);
        for(int j=0; j<spaceBoundaries.size(); j++){
            Element spaceBoundary = spaceBoundaries.get(j);
            String surId = spaceBoundary.getAttributeValue("surfaceIdRef");
            //System.out.println(surId);
            surfaceList.put(surId, new ArrayList<Double[]>());
            Element planarGeometry = spaceBoundary.getChild("PlanarGeometry",ns);
            if(planarGeometry!=null){
                Element polyLoop = planarGeometry.getChild("PolyLoop",ns);
                if(polyLoop!=null){
                    List<Element> cartesianPoints = polyLoop.getChildren("CartesianPoint",ns);
                    for(int k=0; k<cartesianPoints.size(); k++){
                        List<Element> coordinatePoints = cartesianPoints.get(k).getChildren("Coordinate",ns);
                        if (coordinatePoints.size() != 3) {
                            // TODO Error - one cartesianpoint should have 3 coordinates to
                            // represents x, y, z.
                            // current number of points is: coordianteElements.size(). Check
                            // surface element.getAttributeValue("id")
                            // STOP Processing
                        }
                        Double x = lengthMultiplier * Double.parseDouble(coordinatePoints.get(0).getText());
                        Double y = lengthMultiplier * Double.parseDouble(coordinatePoints.get(1).getText());
                        Double z = lengthMultiplier * Double.parseDouble(coordinatePoints.get(2).getText());
                        Double[] point = { x, y, z };
                        surfaceList.get(surId).add(point);
                    }
                }
            }
        }
    }
}
