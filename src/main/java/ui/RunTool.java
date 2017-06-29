package main.java.ui;

import java.io.File;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import main.java.model.gbXML.ReverseTranslator;
import main.java.plugins.ashraeassumptions.ASHRAEConstructions;
import main.java.plugins.ashraeassumptions.ASHRAELightData;
import main.java.plugins.ashraeassumptions.ASHRAEOAData;
import main.java.plugins.ashraeassumptions.DOEReferenceEquipmentData;
import main.java.plugins.ashraebaseline.ASHRAEHVAC;

/**
 * Test code
 * @author weilixu
 *
 */
public class RunTool {
	
    public static void main(String[] args){

        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File("/Users/weilixu/Dropbox/BCD-weili/gbXMLTest/temp.xml");
        Document doc;
        try {
            doc = (Document) builder.build(xmlFile);
            ReverseTranslator trans = new ReverseTranslator(doc, "8.6");
            //register construction
            trans.registerDataPlugins(new ASHRAEConstructions());
            trans.registerDataPlugins(new ASHRAELightData());
            trans.registerDataPlugins(new ASHRAEOAData());
            trans.registerDataPlugins(new DOEReferenceEquipmentData());
            trans.registerDataPlugins(new ASHRAEHVAC());
            
            trans.convert();
            trans.exportFile("/Users/weilixu/Dropbox/BCD-weili/gbXMLTest");
            
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
