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

/**
 * ASHRAE 90.1 lighting data
 * @author weilixu
 *
 */
public class ASHRAELightData implements EnergyPlusDataAPI{
	
    private Element spaceMapperRoot;
    private Element internalLoadRoot;
    
    public ASHRAELightData(){
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
		return "ASHRAELightData";
	}

	@Override
	public DataBaseType dataBaseType() {
		return DataBaseType.LIGHTING;
	}

	@Override
	public void writeInSystem(IDFFileObject objectFile, HashMap<String, String> id_to_NameMap) {
		//no
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
		HashMap<String, String[]> loadMap = new HashMap<String, String[]>();//loadItem, 1:value, 2 unit
        Element spaceMap = spaceMapperRoot.getChild(identifier);
        if(spaceMap==null){
            //TODO Warning - spaceType is not valid reset to OfficeEnclosed
        	identifier = "OfficeEnclosed";
            spaceMap = spaceMapperRoot.getChild(identifier);
        }
        
        //get oa building and oa space
        String light = spaceMap.getChildText("lightSpace");
        
        List<Element> lightObject = internalLoadRoot.getChildren("light");
        for(int i=0; i<lightObject.size(); i++){
            Element lightEle = lightObject.get(i);
            String spaceTypeAttr = lightEle.getAttributeValue("spaceType");
            if(spaceTypeAttr.equals(light)){
                loadMap.put("LightPowerPerArea", new String[2]);
                loadMap.get("LightPowerPerArea")[0] = lightEle.getText();
                loadMap.get("LightPowerPerArea")[1] = lightEle.getAttributeValue("unit");
            }
        }
        return loadMap;
	}

	@Override
	public void writeInHVACSystem(IDFFileObject objectFile, ReverseTranslator translator) {
		
	}

}
