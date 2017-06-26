package main.java.model.gbXML;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class GbXMLUnitConversion {
    
    private static final Map<String, Double> lengthMap = new HashMap<String, Double>();
    private static final Map<String, Double> areaMap = new HashMap<String, Double>();
    private static final Map<String, Double> volumeMap = new HashMap<String, Double>();
    private static final Map<String, Double> conductivityMap = new HashMap<String, Double>();
    private static final Map<String, Double> densityMap = new HashMap<String, Double>();
    private static final Map<String, Double> specificHeatMap = new HashMap<String, Double>();
    private static final Map<String, Double> thermalResistantMap = new HashMap<String, Double>();
    private static final Map<String, Double> flowPerAreaMap = new HashMap<String, Double>();
    private static final Map<String, Double> powerMap = new HashMap<String, Double>();
    private static final Map<String, Double> flowMap = new HashMap<String, Double>();
    private static final Map<String, Double> powerPerAreaMap = new HashMap<String, Double>();
    
    static{
        powerMap.put("Watt", 1.0);
        powerMap.put("BtuPerHour", 0.29307107);
        
        areaMap.put("SquareKilometers", 1000000.0);
        areaMap.put("SquareMeters", 1.0);
        areaMap.put("SquareCentimeters", 0.0001);
        areaMap.put("SquareMillimeters", 0.000001);
        areaMap.put("SquareMiles", 259000.0);
        areaMap.put("SquareYards", 0.836127);
        areaMap.put("SquareFeet", 0.092903);
        areaMap.put("SquareInches", 0.00064516);
        
        volumeMap.put("CubicKilometers", 1000000000.0);
        volumeMap.put("CubicMeters", 1.0);
        volumeMap.put("CubicCentimeters", 0.000001);
        volumeMap.put("CubicMillimeters", 0.000000001);
        volumeMap.put("CubicMiles", 416800000.0);
        volumeMap.put("CubicYards", 0.764555);
        volumeMap.put("CubicFeet", 0.0283168);
        volumeMap.put("CubicInches", 0.0000163871);
        
        powerPerAreaMap.put("WattPerSquareMeter", 1.0);
        powerPerAreaMap.put("WattPerSquareFoot", 10.7639);
        
        flowMap.put("CFM", 0.00047194);
        flowMap.put("CubicMPerSec", 1.0);
        flowMap.put("CubicMPerHr", 0.0002778);
        flowMap.put("LPerSec", 0.001);
        flowMap.put("LPM", 0.000017);
        flowMap.put("GPH",  0.00000105);
        flowMap.put("GPM", 0.00006309);
        
        flowPerAreaMap.put("CFMPerSquareFoot", 0.00508);
        flowPerAreaMap.put("LPerSecPerSquareM", 0.001);
        flowPerAreaMap.put("CubicMPerSecPerSquareM", 1.0);
        flowPerAreaMap.put("CubicMPerHourperSquareM", 0.000278);
        
        lengthMap.put("Kilometers", 1000.0);
        lengthMap.put("Centimeters", 0.01);
        lengthMap.put("Millimeters", 0.001);
        lengthMap.put("Meters", 1.0);
        lengthMap.put("Miles", 1609.34);
        lengthMap.put("Yards", 0.9144);
        lengthMap.put("Feet", 0.3048);
        lengthMap.put("Inches", 0.0254);
        
        conductivityMap.put("WPerCmC", 0.01);
        conductivityMap.put("WPerMeterK", 1.0);
        conductivityMap.put("BtuPerHourFtF", 0.5778);
        
        densityMap.put("GramsPerCubicCm", 0.001);
        densityMap.put("LbsPerCubicIn", 0.000036);
        densityMap.put("LbsPerCubicFt", 0.062428);
        densityMap.put("KgPerCubicM", 1.0);    
        
        specificHeatMap.put("JPerKgK", 1.0);
        specificHeatMap.put("BTUPerLbF", 4190.0);
        
        thermalResistantMap.put("SquareMeterKPerW", 1.0);
        thermalResistantMap.put("HrSquareFtFPerBtu", 0.17611);
    }
        
    public static Double lengthUnitConversionRate(String from, String to){
        //if they are the same:
        // {Kilometers, Centimeters, Millimeters, Meters, Miles, Yards, Feet, Inches}
        
        if(from.equalsIgnoreCase(to)){
            return 1.0;
        }
        
        Double fromVal = 0.0;
        Double toVal = 0.0;
        
        Iterator<String> lengthItr = lengthMap.keySet().iterator();
        while(lengthItr.hasNext()){
            String unitName = lengthItr.next();
            if(unitName.equalsIgnoreCase(from)){
                //value convert "from" to meter
                fromVal = lengthMap.get(unitName);
            }else if(unitName.equalsIgnoreCase(to)){
                //value convert "to" to meter
                toVal = lengthMap.get(unitName);
            }
        }

        return fromVal / toVal;
    }
    
    public static Double conductivityUnitConversionRate(String from, String to){
        if(from.equalsIgnoreCase(to)){
            return 1.0;
        }
        
        //{WPerCmC,WPerMeterK,BtuPerHourFtF}
        Double fromVal = 0.0;
        Double toVal = 0.0;
        
        Iterator<String> conductivityItr = conductivityMap.keySet().iterator();
        while(conductivityItr.hasNext()){
            String unitName = conductivityItr.next();
            if(unitName.equalsIgnoreCase(from)){
                //value convert "from" to w/m-k
                fromVal = conductivityMap.get(unitName);
            }else if(unitName.equalsIgnoreCase(to)){
                //value convert "to" to w/m-k
                toVal = conductivityMap.get(unitName);
            }
        }
        
        return fromVal / toVal;
    }
    
    public static Double densityUnitConversionRate(String from, String to){
        
        if(from.equalsIgnoreCase(to)){
            return 1.0;
        }
        
        // {GramsPerCubicCm, LbsPerCubicIn, LbsPerCubicFt, KgPerCubicM}
        Double fromVal = 0.0;
        Double toVal = 0.0;
        
        Iterator<String> densityItr = densityMap.keySet().iterator();
        while(densityItr.hasNext()){
            String unitName = densityItr.next();
            if(unitName.equalsIgnoreCase(from)){
                //value convert "from" to kg/m3
                fromVal = densityMap.get(unitName);
            }else if(unitName.equalsIgnoreCase(to)){
                //value convert "to" to kg/m3
                toVal = densityMap.get(unitName);
            }
        }
        return fromVal / toVal;
    }
    
    public static Double specificHeatConversion(String from, String to){
        if(from.equalsIgnoreCase(to)){
            return 1.0;
        }
        
        //{JPerKgK, BTUPerLbF}
        Double fromVal = 0.0;
        Double toVal = 0.0;
        
        Iterator<String> specificHeatItr = specificHeatMap.keySet().iterator();
        while(specificHeatItr.hasNext()){
            String unitName = specificHeatItr.next();
            if(unitName.equalsIgnoreCase(from)){
                //value convert "from" to J/kgK
                fromVal = specificHeatMap.get(unitName);
            }else if(unitName.equalsIgnoreCase(to)){
                //value convert "to" to J/kgK
                toVal = specificHeatMap.get(unitName);
            }
        }
        return fromVal/toVal;
    }
    
    public static Double thermalResistantConversion(String from, String to){
        if(from.equalsIgnoreCase(to)){
            return 1.0;
        }
        
        //{SquareMeterKPerW,HrSquareFtFPerBtu}
        Double fromVal = 0.0;
        Double toVal = 0.0;
        
        Iterator<String> thermalResistantItr = thermalResistantMap.keySet().iterator();
        while(thermalResistantItr.hasNext()){
            String unitName = thermalResistantItr.next();
            if(unitName.equalsIgnoreCase(from)){
                //value convert "from" to m2/kW
                fromVal = thermalResistantMap.get(unitName);
            }else if(unitName.equalsIgnoreCase(to)){
                //value convert "to" to m2/kW
                toVal = thermalResistantMap.get(unitName);
            }
        }
        return fromVal/toVal;
    }
    
    public static Double flowPerAreaConversionRate(String from, String to){
        if(from.equalsIgnoreCase(to)){
            return 1.0;
        }
        
        //{CFMPerSquareFoot, LPerSecPerSquareM, CubicMPerSecPerSquareM, CubicMPerHourPerSquareM}
        Double fromVal = 0.0;
        Double toVal = 0.0;
        
        Iterator<String> flowPerAreaItr = flowPerAreaMap.keySet().iterator();
        while(flowPerAreaItr.hasNext()){
            String unitName = flowPerAreaItr.next();
            if(unitName.equalsIgnoreCase(from)){
                //value convert "from" to m3/s.m2
                fromVal = flowPerAreaMap.get(unitName);
            }else if(unitName.equalsIgnoreCase(to)){
                //value convert "to" to m3/s.m2
                toVal = flowPerAreaMap.get(unitName);
            }
        }
        return fromVal/toVal;
    }
    
    public static Double flowConversionRate(String from, String to){
        if(from.equalsIgnoreCase(to)){
            return 1.0;
        }
        
        Double fromVal = 1.0;
        Double toVal = 1.0;
        
        Iterator<String> flowItr = flowMap.keySet().iterator();
        while(flowItr.hasNext()){
            String unitName = flowItr.next();
            if(unitName.equalsIgnoreCase(from)){
                //value convert "from" to m3/s
                fromVal = flowMap.get(unitName);
            }else if(unitName.equalsIgnoreCase(to)){
                //value convert "to" to m3/s
                toVal = flowMap.get(unitName);
            }
        }
        return fromVal/toVal;
    }
    
    public static Double powerPerAreaRate(String from, String to){
        if(from.equalsIgnoreCase(to)){
            return 1.0;
        }
        
        Double fromVal = 1.0;
        Double toVal = 1.0;
        
        Iterator<String> powerPerAreaItr = powerPerAreaMap.keySet().iterator();
        while(powerPerAreaItr.hasNext()){
            String unitName = powerPerAreaItr.next();
            if(unitName.equalsIgnoreCase(from)){
                //value convert "from" to w/m2
                fromVal = powerPerAreaMap.get(unitName);
            }else if(unitName.equalsIgnoreCase(to)){
                //value convert "to" to w/m2
                toVal = powerPerAreaMap.get(unitName);
            }
        }
        return fromVal/toVal;
    }
    
    public static Double areaUnitRate(String from, String to){
        if(from.equalsIgnoreCase(to)){
            return 1.0;
        }
        
        Double fromVal = 1.0;
        Double toVal = 1.0;
        
        Iterator<String> areaItr = areaMap.keySet().iterator();
        while(areaItr.hasNext()){
            String unitName = areaItr.next();
            if(unitName.equalsIgnoreCase(from)){
                //value convert "from" to w/m2
                fromVal = areaMap.get(unitName);
            }else if(unitName.equalsIgnoreCase(to)){
                //value convert "to" to w/m2
                toVal = areaMap.get(unitName);
            }
        }
        return fromVal/toVal;
    }
    
    public static Double volumeUnitRate(String from, String to){
        if(from.equalsIgnoreCase(to)){
            return 1.0;
        }
        
        Double fromVal = 1.0;
        Double toVal = 1.0;
        
        Iterator<String> volumeItr = volumeMap.keySet().iterator();
        while(volumeItr.hasNext()){
            String unitName = volumeItr.next();
            if(unitName.equalsIgnoreCase(from)){
                //value convert "from" to w/m2
                fromVal = volumeMap.get(unitName);
            }else if(unitName.equalsIgnoreCase(to)){
                //value convert "to" to w/m2
                toVal = volumeMap.get(unitName);
            }
        }
        return fromVal/toVal;
    }
    
    public static Double powerRate(String from, String to){
        if(from.equalsIgnoreCase(to)){
            return 1.0;
        }
        
        Double fromVal = 1.0;
        Double toVal = 1.0;
        
        Iterator<String> powerItr = powerMap.keySet().iterator();
        while(powerItr.hasNext()){
            String unitName = powerItr.next();
            if(unitName.equalsIgnoreCase(from)){
                //value convert "from" to w/m2
                fromVal = powerMap.get(unitName);
            }else if(unitName.equalsIgnoreCase(to)){
                //value convert "to" to w/m2
                toVal = powerMap.get(unitName);
            }
        }
        return fromVal/toVal;
    }
}
