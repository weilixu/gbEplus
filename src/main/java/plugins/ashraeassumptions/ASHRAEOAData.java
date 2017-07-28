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
import main.java.config.FilesPath;

/**
 * ASHRAE 62.1 OA data for the assumptions
 * 
 * @author weilixu
 *
 */
public class ASHRAEOAData extends ASHRAEDataSet {
	private Element ASHRAEOARoot;
	private Element spaceMapperRoot;

	public ASHRAEOAData() {
		SAXBuilder builder = new SAXBuilder();

		try {
			Document oaDoc = (Document) builder
					.build(new File(FilesPath.readProperty("ResourcePath") + "/ashrae62.1oa.xml"));
			Document spaceDoc = (Document) builder
					.build(new File(FilesPath.readProperty("ResourcePath") + "/spacemap.xml"));
			ASHRAEOARoot = oaDoc.getRootElement();
			spaceMapperRoot = spaceDoc.getRootElement();

		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String dataBaseName() {
		return "ASHRAEOAData";
	}

	@Override
	public DataBaseType dataBaseType() {
		return DataBaseType.OUTDOORAIR;
	}

	@Override
	public Map<String, String[]> getValuesInHashMap(String identifier) {
		Map<String, String[]> oaMap = new HashMap<String, String[]>();// oaItem,
																		// 1:value,
																		// 2
																		// unit
		Element spaceMap = spaceMapperRoot.getChild(identifier);
		if (spaceMap == null) {
			// TODO Warning - spaceType is not valid reset to OfficeEnclosed
			identifier = "OfficeEnclosed";
			spaceMap = spaceMapperRoot.getChild(identifier);
		}

		// get oa building and oa space
		String building = spaceMap.getChildText("oaBuilding");
		String space = spaceMap.getChildText("oaSpace");

		// search the item in the oa document
		List<Element> data = ASHRAEOARoot.getChildren();

		for (int i = 0; i < data.size(); i++) {
			Element d = data.get(i);
			if (d.getAttributeValue("buildingType").equals(building)
					&& d.getAttributeValue("spaceType").equals(space)) {
				// OAFlowPerArea
				Element oaArea = d.getChild("OAFlowPerArea");
				oaMap.put("OAFlowPerArea", new String[2]);
				oaMap.get("OAFlowPerArea")[0] = oaArea.getText();
				oaMap.get("OAFlowPerArea")[1] = oaArea.getAttributeValue("unit");

				// OAFlowPerPerson
				Element oaPerson = d.getChild("OAFlowPerPerson");
				oaMap.put("OAFlowPerPerson", new String[2]);
				oaMap.get("OAFlowPerPerson")[0] = oaPerson.getText();
				oaMap.get("OAFlowPerPerson")[1] = oaPerson.getAttributeValue("unit");

				// PeopleNumber
				Element people = d.getChild("PeopleNumber");
				oaMap.put("PeopleNumber", new String[2]);
				oaMap.get("PeopleNumber")[0] = people.getText();
				oaMap.get("PeopleNumber")[1] = people.getAttributeValue("unit");

				break;
			}
		}
		return (HashMap<String, String[]>) oaMap;
	}
}
