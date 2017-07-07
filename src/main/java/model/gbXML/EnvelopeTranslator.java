package main.java.model.gbXML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.Namespace;

import main.java.api.EnergyPlusDataAPI;
import main.java.model.idf.IDFFileObject;
import main.java.model.idf.IDFObject;

public class EnvelopeTranslator {

	private HashMap<String, String> bs_idToObjectMap;
	private Namespace ns;

	private ArrayList<String> lines;
	private ArrayList<String> units;
	private ArrayList<String> comments;
	private ArrayList<String> topComments;

	public EnvelopeTranslator(Namespace ns) {
		bs_idToObjectMap = new HashMap<String, String>();
		this.ns = ns;
		
		lines = new ArrayList<String>();
		units = new ArrayList<String>();
		comments = new ArrayList<String>();
		topComments = new ArrayList<String>();
	}
	
	public void generateAssumptions(IDFFileObject file, EnergyPlusDataAPI constructionAPI){
		constructionAPI.writeInSystem(file, bs_idToObjectMap);
		//TODO announce the used construction plugin name
		//System.out.println(constructionAPI.dataBaseName());
	}

	public String getObjectName(String id) {
		return bs_idToObjectMap.get(id);
	}

	public IDFObject translateMaterial(Element materialElement) {
		lines.clear();
		units.clear();
		comments.clear();
		topComments.clear();

		// only take the immediate child element
		Element nameElement = materialElement.getChild("Name", ns);
		Element thicknessElement = materialElement.getChild("Thickness", ns);
		Element thermalConductivityElement = materialElement.getChild("Conductivity", ns);
		Element densityElement = materialElement.getChild("Density", ns);
		Element specificHeatElement = materialElement.getChild("SpecificHeat", ns);
		Element rValueElement = materialElement.getChild("R-value", ns);

		if (nameElement == null) {
			// TODO ERROR - (Material element 'Name' is empty, material will not
			// be created')
			// stop the process
		}

		String id = materialElement.getAttributeValue("id");
		// add to object map
		String materialName = nameElement.getText();
		bs_idToObjectMap.put(id, materialName);

		materialName = escapeName(id, materialName);
		/*
		 * BuildSimHub Assumptions Material Name - required Roughness - default
		 * to MediumRough Thickness - required, m Conductivity - required, W/m-K
		 * Density - required, kg/m3 Specific Heat - required, J/kg-K Thermal
		 * Absorptance, IDD default of 0.9 Solar Absorptance, IDD default of 0.7
		 * Visible Absorptance, IDD default of 0.7
		 */

		if (nameElement != null && thicknessElement != null && thermalConductivityElement != null
				&& densityElement != null && specificHeatElement != null) {
			recordInputs("Material", "", "", "");
			recordInputs(materialName, "", "Name", "");
			recordInputs("MediumRough", "", "Roughness", "");
			// start process thickness - buildsimhub unit = m
			String thicknessUnit = thicknessElement.getAttributeValue("unit");
			if (thicknessUnit == null) {
				// TODO warning ('thickness unit attribute is empty, assume it
				// is meter')
				thicknessUnit = "Meters";
			}
			Double convertRate = GbXMLUnitConversion.lengthUnitConversionRate(thicknessUnit, "Meters");
			Double thickness = stringToDouble(thicknessElement.getText()) * convertRate;
			recordInputs(thickness.toString(), "m", "Thickness", "");

			// start process conductivity - buildsimhub unit = W/m-K
			String conductivityUnit = thermalConductivityElement.getAttributeValue("unit");
			if (conductivityUnit == null) {
				// TODO warning ("thermal conductivity unit attribute is empty,
				// assume it is W/m-k");
				conductivityUnit = "WPerMeterK";
			}

			convertRate = GbXMLUnitConversion.conductivityUnitConversionRate(conductivityUnit, "WPerMeterK");
			Double conductivity = stringToDouble(thermalConductivityElement.getText()) * convertRate;
			recordInputs(conductivity.toString(), "W/m-k", "Conductivity", "");

			// start process density - buildsimhub unit = kg/m3
			String densityUnit = densityElement.getAttributeValue("unit");
			if (densityUnit == null) {
				// TODO warning ("density unit attribute is empty, assume it is
				// kg/m3")
				densityUnit = "KgPerCubicM";
			}

			convertRate = GbXMLUnitConversion.densityUnitConversionRate(densityUnit, "KgPerCubicM");
			Double density = stringToDouble(densityElement.getText()) * convertRate;
			recordInputs(density.toString(), "kg/m3", "Density", "");

			// start process specific heat - buildsimhub unit = J/kg-K
			String specificHeatUnit = specificHeatElement.getAttributeValue("unit");
			if (specificHeatUnit == null) {
				// TODO warning ("specific heat unit attribute is empty, assume
				// it is J/kg-K"
				specificHeatUnit = "JPerKgK";
			}

			convertRate = GbXMLUnitConversion.specificHeatConversion(specificHeatUnit, "JPerKgK");
			Double specificHeat = stringToDouble(specificHeatElement.getText()) * convertRate;
			recordInputs(specificHeat.toString(), "J/kgK", "Specific Heat", "");

			// fill up the rest
			recordInputs("0.9", "", "Thermal Absorptance", "");
			recordInputs("0.7", "", "Solar Absorptance", "");
			recordInputs("0.7", "", "Visible Absorptance", "");
		} else if (rValueElement != null) {
			Double rvalue = Double.valueOf(rValueElement.getText());
			// the idd specifies a minimum value of 0.001 for rvalue
			rvalue = Math.max(rvalue, 0.001);
			// object
			recordInputs("Material:NoMass", "", "", "");
			recordInputs(materialName, "", "Name", "");
			recordInputs("MediumRough", "", "Roughness", "");

			// start process rvalue heat - buildsimhub unit = J/kg-K
			String thermalResistantUnit = rValueElement.getAttributeValue("unit");
			if (thermalResistantUnit == null) {
				// TODO warning ("thermal resistance unit attribute is empty,
				// assume it is m2/kW"
				thermalResistantUnit = "SquareMeterKPerW";
			}

			Double convertRate = GbXMLUnitConversion.thermalResistantConversion(thermalResistantUnit,
					"SquareMeterKPerW");
			// System.out.println("For r-value: " + convertRate);
			Double thermalResistance = stringToDouble(rValueElement.getText()) * convertRate;
			recordInputs(thermalResistance.toString(), "m2-K/W", "Thermal Resistance", "");

			// fill up the rest
			recordInputs("0.9", "", "Thermal Absorptance", "");
			recordInputs("0.7", "", "Solar Absorptance", "");
			recordInputs("0.7", "", "Visible Absorptance", "");

		} else {
			recordInputs("Material:NoMass", "", "", "");
			recordInputs(materialName, "", "Name", "");
			recordInputs("MediumRought", "", "Roughness", "");
			recordInputs("0.001", "m2-K/W", "Thermal Resistance", "");
			recordInputs("0.9", "", "Thermal Absorptance", "");
			recordInputs("0.7", "", "Solar Absorptance", "");
			recordInputs("0.7", "", "Visible Absorptance", "");
		}
		// done return
		return new IDFObject(lines, units, comments, topComments);
	}

	public IDFObject translateConstruction(Element element, List<Element> layerElements) {
		// set construction name
		String constructionId = element.getAttributeValue("id");
		String constructionName = escapeName(constructionId, element.getChildText("Name", ns));

		bs_idToObjectMap.put(constructionId, constructionName);
		//
		lines.clear();
		units.clear();
		comments.clear();
		topComments.clear();
		recordInputs("Construction", "", "", "");
		recordInputs(constructionName, "", "Name", "");

		// each layers
		Element layerIdElement = element.getChild("LayerId", ns);

		if (layerIdElement == null) {
			return new IDFObject(lines, units, comments, topComments);
		}

		String layerId = layerIdElement.getAttributeValue("layerIdRef");
		for (int i = 0; i < layerElements.size(); i++) {
			Element layerElement = layerElements.get(i);
			if (layerId.equals(layerElement.getAttributeValue("id"))) {
				// System.out.println(layerId + " matches " +
				// layerElement.getName());
				List<Element> materialIdElements = layerElement.getChildren("MaterialId", ns);
				// System.out.println(materialIdElements.size());
				for (int j = 0; j < materialIdElements.size(); j++) {
					String materialId = materialIdElements.get(j).getAttributeValue("materialIdRef");
					String materialName = bs_idToObjectMap.get(materialId);

					if (materialName != null) {
						recordInputs(materialName, "", "Layer" + j, "");
					}
				}
				break;
			}

		}
		return new IDFObject(lines, units, comments, topComments);
	}

	/**
	 * This function converts the windowType elements to a simple glazing object
	 * in EnergyPlus The detail Window object is under the development
	 * 
	 * @param element
	 * @return
	 */
	public void translateWindowType(Element element, IDFFileObject file) {

		ArrayList<String> lines = new ArrayList<String>();
		ArrayList<String> units = new ArrayList<String>();
		ArrayList<String> comments = new ArrayList<String>();
		ArrayList<String> topComments = new ArrayList<String>();

		lines.add("WindowMaterial:SimpleGlazingSystem");
		units.add("");
		comments.add("");

		String windowTypeId = element.getAttributeValue("id");
		String windowName = element.getChildText("Name", ns);
		windowName = escapeName(windowTypeId, windowName);

		// bs_idToObjectMap.put(windowTypeId, windowName);

		lines.add(windowName);
		units.add("");
		comments.add("Name");

		// process data
		Double uValue = null;
		Double shgc = null;
		Double tVis = null;

		Element uValueElement = element.getChild("U-value", ns);
		if (uValueElement == null) {
			uValue = 2.4;// TODO WX: change based on climate zone
		} else {
			if (uValueElement.getAttributeValue("unit").equalsIgnoreCase("WPerSquareMeterK")) {
				uValue = Double.parseDouble(uValueElement.getText());
			} else {
				uValue = Double.parseDouble(uValueElement.getText()) * GbXMLUnitConversion
						.thermalResistantConversion(uValueElement.getAttributeValue("unit"), "WPerSquareMeterK");
			}
		}

		Element shgcElement = element.getChild("SolarHeatGainCoeff", ns);
		if(shgcElement==null){
			shgc = 0.4; //TODO change to baseline based on climate zone
		}else if(shgcElement.getAttributeValue("unit").equalsIgnoreCase("Fraction")) {
			shgc = Double.parseDouble(shgcElement.getText());
		}

		Element transmittanceElement = element.getChild("Transmittance", ns);
		if(transmittanceElement==null){
			tVis = 0.6;//TODO change to baseline based on climate zone
		}else if (transmittanceElement.getAttributeValue("type").equalsIgnoreCase("Visible")) {
			tVis = Double.parseDouble(transmittanceElement.getText());
		}

		if (uValue != null && shgc != null) {
			lines.add(uValue.toString());
			units.add("W/m2-K");
			comments.add("U-Factor");

			lines.add(shgc.toString());
			units.add("");
			comments.add("Solar Heat Gain Coefficient");

			lines.add(tVis.toString());
			units.add("");
			comments.add("Visible Transmittance");

			file.addIDFObject(new IDFObject(lines, units, comments, topComments));
		} else {
			// error handle, stop the process
			// TODO error (Window: + windowName + ", does not have uValue /
			// shgc, check the file"
			return;
		}

		// now process the construction
		lines = new ArrayList<String>();
		units = new ArrayList<String>();
		comments = new ArrayList<String>();

		lines.add("Construction");
		units.add("");
		comments.add("");

		lines.add(windowTypeId);
		units.add("");
		comments.add("");

		lines.add(windowName);
		units.add("");
		comments.add("");
		bs_idToObjectMap.put(windowTypeId, windowTypeId);

		// add the construction
		file.addIDFObject(new IDFObject(lines, units, comments, topComments));
	}

	public void setReversedConstruction(String constructionId, String constructionName) {
		bs_idToObjectMap.put(constructionId, constructionName);
	}

	public void addAirConstruction(String surfaceType, String constructionId, IDFFileObject file) {
		Double resistance = 0.0;
		if (surfaceType.equals("Ceiling") || surfaceType.equals("Floor")) {
			resistance = 0.18;
		} else {
			resistance = 0.15;
		}

		recordInputs("Material:NoMass", "", "", "");
		recordInputs(surfaceType + " Air Material", "", "Name", "");
		recordInputs("MediumRought", "", "Roughness", "");
		recordInputs(resistance.toString(), "m2-K/W", "Thermal Resistance", "");
		recordInputs("0.9", "", "Thermal Absorptance", "");
		recordInputs("0.7", "", "Solar Absorptance", "");
		recordInputs("0.7", "", "Visible Absorptance", "");
		addObject(file);

		recordInputs("Construction", "", "", "");
		recordInputs("Air " + surfaceType, "", "Name", "");
		recordInputs(surfaceType + " Air Material", "", "Layer", "");
		addObject(file);

		bs_idToObjectMap.put(constructionId, "Air " + surfaceType);

		// construction

	}

	/**
	 * utility function that converts a string to a double value writes error in
	 * the log if the string is not convertable and stop all the process.
	 * 
	 * @param text
	 * @return
	 */
	private Double stringToDouble(String text) {
		Double value = null;
		try {
			value = Double.valueOf(text);
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			// TODO, ERROR 'Value :' + text + 'Cannot convert to double'
		}
		return value;
	}

	private String escapeName(String id, String name) {
		String value = id;
		if (name != null && !name.isEmpty()) {
			value = name;
		}

		return value.replace(",", "-").replace(";", "-");
	}

	private void recordInputs(String line, String unit, String comment, String topComments) {
		lines.add(line);
		units.add(unit);
		comments.add(comment);
	}

	private void addObject(IDFFileObject file) {
		file.addIDFObject(new IDFObject(lines, units, comments, topComments));
		lines.clear();
		units.clear();
		comments.clear();
		topComments.clear();
	}
}
