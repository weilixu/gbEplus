package main.java.model.gbXML;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import main.java.model.idf.IDFFileObject;
import main.java.model.idf.IDFObject;

/**
 * Translates EnergyPlus to gbXML - this requires version control of the previous gbXML
 * file to fill out the missing value
 * TODO not completed yet
 * @author weilixu
 *
 */
public class ForwardTranslator {
    
    private IDFFileObject file;
    
    private Element originGBXMLRoot;
    private Namespace ns;
    
    private boolean translateWithBothSources = false;
    
    
    
    public ForwardTranslator(IDFFileObject file, Document origin){
        this.file = file;
        
        if(origin!=null){
            originGBXMLRoot = origin.getRootElement();
            ns = originGBXMLRoot.getNamespace();
            translateWithBothSources = true;
        }
    }
    
    @SuppressWarnings("unused")
    public void translateModel(){
        Element root = new Element("gbXML");
        Document doc = new Document(root);
        
        //set-up gbXML element attributes
        root.setAttribute("xmlns", "http://www.gbxml.org/schema");
        root.setAttribute("xmlns:xhtml", "http://www.w3.org/1999/xhtml");
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        root.setAttribute("xsi:schemaLocation", "http://www.gbxml.org/schema http://gbxml.org/schema/6-01/GreenBuildingXML_Ver6.01.xsd");
        root.setAttribute("temperatureUnit", "C");
        root.setAttribute("lengthUnit", "Meters");
        root.setAttribute("areaUnit", "SquareMeters");
        root.setAttribute("volumeUnit", "CubicMeters");
        root.setAttribute("useSIUnitsForResults", "true");
        root.setAttribute("version", "6.01");
        root.setAttribute("SurfaceReferenceLocation", "Centerline");
        
        //translate campus element
        Element campusElement = translateCampus();
        
        
    }
    
    @SuppressWarnings("unused")
    private Element translateCampus(){
        Element campus = new Element("Campus");
        //id
        campus.setAttribute("id", "Facility");
        
        //name
        Element name = new Element("Name");
        name.setText("Facility");//temporary solution for the gbXML file
        campus.addContent(name);
        
        //translate site information
        List<IDFObject> siteLocationList = file.getCategoryList("Site:Location");
        if(siteLocationList!=null){
            IDFObject siteLocation = siteLocationList.get(0);//only extract one - this should be an unique object
            campus.addContent(translateLocationObject(siteLocation));
        }
        
        //translate building
        Element buildingElement = translateBuilding();
        
        return null;
    }
    
    private Element translateBuilding(){
        Element buildingElement = new Element("Building");
        
        //id
        IDFObject bldg = file.getCategoryList("Building").get(0);
        buildingElement.setAttribute("id", escapeName(bldg.getDataByCommentNoUnit("Name")));
        //TODO, maybe should derive from space types (this can be done by asking users)?
        String bldgType = "Unknown";
        if(translateWithBothSources){
            String type = originGBXMLRoot.getChild("Building",ns).getAttributeValue("buildingType", ns);
            if(type!=null){
                bldgType = type;
            }
        }
        buildingElement.setAttribute("buildingType",bldgType);
        
        //name
        Element nameElement = new Element("Name");
        nameElement.setText(bldg.getDataByCommentNoUnit("Name"));
        buildingElement.addContent(nameElement);
        
        //area
        //??
        
        //storey??
        
        return null;
    }
    
    private Element translateLocationObject(IDFObject siteLocation){
        Element location = new Element("Location");
        Element locName = new Element("Name");
        locName.setText(siteLocation.getDataByCommentNoUnit("Name"));//temp solution
        location.addContent(locName);
        
        String latitude = siteLocation.getDataByCommentNoUnit("Latitude");//wait for test
        if(latitude!=null){
            Element lat = new Element("Latitude");
            lat.setText(latitude);
            location.addContent(lat);
        }

        Element longitude = new Element("Longitude");
        longitude.setText(siteLocation.getDataByCommentNoUnit("Longitude"));
        location.addContent(longitude);
        Element elevation = new Element("Elevation");
        elevation.setText(siteLocation.getDataByCommentNoUnit("Elevation"));
        location.addContent(elevation);
        // that's all you can get from an IDF file, check whether the previous doc is available
        if(translateWithBothSources){
            Element origLocation = originGBXMLRoot.getChild("Campus",ns).getChild("Location",ns);
            if(origLocation!=null){
                Element zipCode = origLocation.getChild("ZipcodeOrPostalCode",ns);//required element
                location.addContent(zipCode);
                Element cad = origLocation.getChild("CADModelAzimuth",ns);
                if(cad!=null){
                    location.addContent(cad);
                }
                Element station = origLocation.getChild("StationId",ns);
                if(station!=null){
                    location.addContent(station);
                }
            }
        }
        return location;
    }
    
    private String escapeName(String name){
        String validName = name;
        validName.replace(" ", "_");
        validName.replace("(", "_");
        validName.replace(")", "_");
        validName.replace("[", "_");
        validName.replace("]", "_");
        validName.replace("{", "_");
        validName.replace("}", "_");
        validName.replace("/", "_");
        validName.replace("\\", "_");
        //result.replace("-", "_"); // ok
        //result.replace(".", "_"); // ok
        validName.replace(":", "_"); 
        validName.replace(";", "_");
        
        return validName;
    }
    
    public void exportXMLDocument(){
        XMLOutputter xmlOutput = new XMLOutputter();

        // display ml
        xmlOutput.setFormat(Format.getPrettyFormat());
        //xmlOutput.output(doc, System.out); 
    }

}
