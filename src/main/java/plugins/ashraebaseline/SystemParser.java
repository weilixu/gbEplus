package main.java.plugins.ashraebaseline;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import main.java.config.FilesPath;
import main.java.model.data.EplusObject;
import main.java.model.data.KeyValuePair;

/**
 * This class extracts system template from database <file>hvacsystem.xml<file>
 * according to the specified system type
 * 
 * @author Weili
 *
 */
public class SystemParser {

	private final SAXBuilder builder;
	private final File system;
	private Document document;

	private ArrayList<EplusObject> objects;

	private String systemType;

	private static final String FILE_NAME = "hvacsystem_v2.xml";

	public SystemParser(String systemType) {
		builder = new SAXBuilder();
		system = new File(FilesPath.readProperty("ResourcePath") + FILE_NAME);

		this.systemType = systemType;

		// read the file
		try {
			document = (Document) builder.build(system);
		} catch (Exception e) {
			e.printStackTrace();
		}

		objects = new ArrayList<EplusObject>();
		systemBuilder();
	}

	public SystemParser(String systemType, String file) {
		builder = new SAXBuilder();
		system = new File(FilesPath.readProperty("ResourcePath") + file);

		this.systemType = systemType;

		// read the file
		try {
			document = (Document) builder.build(system);
		} catch (Exception e) {
			e.printStackTrace();
		}

		objects = new ArrayList<EplusObject>();
		systemBuilder();
	}

	public ArrayList<EplusObject> getSystem() {
		return objects;
	}

	private void systemBuilder() {
		Element root = document.getRootElement();
		builderHelper(root);
	}

	private void builderHelper(Element current) {
		List<Element> children = current.getChildren();
		Iterator<Element> iterator = children.iterator();
		while (iterator.hasNext()) {
			Element child = iterator.next();
			// if there is an object, find the correct system type dataset
			if (child.getName().equals("dataset") && child.getAttributeValue("setname").equalsIgnoreCase(systemType)) {
				buildObject(child);
			}
		}
	}

	/**
	 * object objects in the database that represents Energyplus objects
	 * 
	 * @param current
	 */
	private void buildObject(Element current) {
		List<Element> children = current.getChildren();
		Iterator<Element> iterator = children.iterator();
		while (iterator.hasNext()) {
			Element child = iterator.next();
			String category = child.getAttributeValue("description");
			String reference = child.getAttributeValue("reference");

			EplusObject ob = new EplusObject(category, reference);
			processFields(child, ob);
			objects.add(ob);
		}
	}

	/**
	 * Fields objects in the database that represents EnergyPlus object's
	 * fields.
	 * 
	 * @param node
	 * @param object
	 */
	private void processFields(Element node, EplusObject object) {
		List<Element> children = node.getChildren();
		Iterator<Element> iterator = children.iterator();
		while (iterator.hasNext()) {
			Element child = iterator.next();
			KeyValuePair pair = new KeyValuePair(child.getAttributeValue("description"), child.getText());
			object.addField(pair);
		}
	}
}
