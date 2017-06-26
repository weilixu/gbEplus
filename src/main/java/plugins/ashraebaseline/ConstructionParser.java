package main.java.plugins.ashraebaseline;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.Element;

import main.java.config.FilesPath;
import main.java.model.data.EplusObject;
import main.java.model.data.KeyValuePair;

public class ConstructionParser {

	private final SAXBuilder builder;
	private final File envelop;
	private Document document;

	private final String zone;
	private ArrayList<EplusObject> objects;

	private static final String FILE_NAME = "envelope.xml";

	public ConstructionParser(String zone) {
		this.zone = zone;
		builder = new SAXBuilder();
		envelop = new File(FilesPath.readProperty("ResourcePath") + FILE_NAME);
		try {
			document = (Document) builder.build(envelop);
		} catch (Exception e) {
			e.printStackTrace();
		}

		objects = new ArrayList<EplusObject>();
		envelopeBuilder();

	}

	/**
	 * get the selected objects
	 * 
	 * @return
	 */
	public ArrayList<EplusObject> getObjects() {
		return objects;
	}

	private void envelopeBuilder() {
		Element root = document.getRootElement();
		builderHelper(root);
	}

	private void builderHelper(Element current) {
		List<Element> children = current.getChildren();
		Iterator<Element> iterator = children.iterator();
		while (iterator.hasNext()) {
			Element child = iterator.next();
			// if there is an object, find the correct climate dataset
			// System.out.println(child.getName());
			if (child.getName().equals("dataset") && zone.toString().contains(child.getAttributeValue("category"))) {
				buildObject(child);
			}
		}
	}

	/**
	 * Build the arraylist of energyplus objects under one specific dataset
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
	 * process the fields under one specific object
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
