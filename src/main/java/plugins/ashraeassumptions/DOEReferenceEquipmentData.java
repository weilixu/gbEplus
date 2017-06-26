package main.java.plugins.ashraeassumptions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import main.java.api.DataBaseType;
import main.java.api.EnergyPlusDataAPI;
import main.java.config.FilesPath;
import main.java.model.gbXML.ReverseTranslator;
import main.java.model.idf.IDFFileObject;

public class DOEReferenceEquipmentData implements EnergyPlusDataAPI{
	
	 private Element spaceMapperRoot;
	 private Element internalLoadRoot;
	 
	 public DOEReferenceEquipmentData(){
		 SAXBuilder builder = new SAXBuilder();
	        try {
	            Document spaceDoc = (Document)builder.build(new File(FilesPath.readProperty("ResourcePath") + "/spacemap.xml"));
	            Document ilDoc = (Document)builder.build(new File(FilesPath.readProperty("ResourcePath") + "/internalloads.xml"));
	            spaceMapperRoot = spaceDoc.getRootElement();
	            internalLoadRoot = ilDoc.getRootElement();
	        } catch (JDOMException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	 }
	 
	@Override
	public String dataBaseName() {
		return "DOEReferenceEquipmentData";
	}
	@Override
	public DataBaseType dataBaseType() {
		return DataBaseType.EQUIPMENT;
	}
	@Override
	public void writeInSystem(IDFFileObject objectFile, HashMap<String, String> id_to_NameMap) {
		
	}
	@Override
	public String getValueInString(String identifier) {
		return null;
	}
	@Override
	public Double getValueInDouble(String identifier) {
		return null;
	}
	@Override
	public Map<String, String[]> getValuesInHashMap(String identifier) {
	      HashMap<String, String[]> loadMap = new HashMap<String,String[]>();//loadItem, 1:value, 2 unit
	        Element spaceMap = spaceMapperRoot.getChild(identifier);
	        if(spaceMap==null){
	            //TODO Warning - spaceType is not valid reset to OfficeEnclosed
	        	identifier = "OfficeEnclosed";
	            spaceMap = spaceMapperRoot.getChild(identifier);
	        }
	        
	        //get oa building and oa space
	        String building = spaceMap.getChildText("ilBuilding");
	        String space = spaceMap.getChildText("ilSpace");
	        
	        List<Element> equipObject = internalLoadRoot.getChildren("data");
	        for(int j=0; j<equipObject.size(); j++){
	            Element equipEle = equipObject.get(j);
	            String buildingTypeAttr = equipEle.getAttributeValue("buildingType");
	            String spaceTypeAttr = equipEle.getAttributeValue("spaceType");
	            if(buildingTypeAttr.equals(building) && spaceTypeAttr.equals(space)){
	                
	                List<Element> equipmentList = equipEle.getChildren("EquipPowerPerArea");
	                for(int k=0; k<equipmentList.size(); k++){
	                    Element equip = equipmentList.get(k);
	                    String fuelType = equip.getAttributeValue("powerType");
	                    
	                    loadMap.put(fuelType, new String[2]);
	                    loadMap.get(fuelType)[0] = equip.getText();
	                    loadMap.get(fuelType)[1] = equip.getAttributeValue("unit");

	                }
	            }
	        }
	        return loadMap;
	}

	@Override
	public void writeInHVACSystem(IDFFileObject objectFile, ReverseTranslator translator) {
		
	}

}
