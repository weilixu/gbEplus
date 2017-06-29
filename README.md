# gbEplus
gbEplus is an open source gbXML to EnergyPlus convertor written in JAVA.
The tool was firstly inspired by the [OpenStudio](https://github.com/NREL/OpenStudio) gbXML module. The initial version was developed by [Weili Xu](https://github.com/weilixu) to facilitates the creation of simple IDF geometries from revit. Quickly, after realizing the simple geometry conversion is merely enough for a successful energy simulation, Weili and [Haopeng Wang](https://github.com/wanghp18) from CMU IW started working on the second version development in their spare time in CMU.

[use-tool](## How to use the Tool?)
To run the converter, a user must define a few parameters:
1. Parser in the XML document using [JDOM](http://www.jdom.org/) (JDOM is already included in the project)
'''java
  SAXBuilder builder = new SAXBuilder();
  File xmlFile = new File("/Users/weilixu/Dropbox/BCD-weili/gbXMLTest/temp.xml"); //directory of your local xml file
  Document doc;
  try{
    doc = (Document) builder.build(xmlFile);
'''
2. Initiate the translator with 'doc' and a string of EnergyPlus version in the format of '#.#'. It should be noted the current version of the translator does not support version 8.7 translation.
'''java
ReverseTranslator trans = new ReverseTranslator(doc, "8.6")
'''
3. Register the data sources via a **DataPlugIn API** for missing data handling.
'''java
trans.registerDataPlugins(new ASHRAEConstructions());
trans.registerDataPlugins(new ASHRAELightData());
trans.registerDataPlugins(new ASHRAEOAData());
trans.registerDataPlugins(new DOEReferenceEquipmentData());
trans.registerDataPlugins(new ASHRAEHVAC());
'''
4. Start translating the gbXML file to IDF with a fixed export directory
'''java
trans.convert();
trans.exportFile("/Users/weilixu/Dropbox/BCD-weili/gbXMLTest");
'''
The entire code can be viewed in the [RunTool.java](src/main/java/ui/RunTool.java)
