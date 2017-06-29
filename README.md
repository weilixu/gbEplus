# gbEplus
gbEplus is an open source gbXML to EnergyPlus convertor written in JAVA.
The tool was firstly inspired by the [OpenStudio](https://github.com/NREL/OpenStudio) gbXML module. The initial version was developed by @github/weilixu to facilitates the creation of simple IDF geometries from revit. Quickly, after realizing the simple geometry conversion is merely enough for a successful energy simulation, Weili and @github/wanghp18 from CMU IW started working on the second version development in their spare time in CMU.

## How to use the Tool?
To run the converter, a user must define a few parameters:
1. Parser in the XML document using [JDOM](http://www.jdom.org/) (JDOM is already included in the project)
```java
  SAXBuilder builder = new SAXBuilder();
  File xmlFile = new File("/Users/weilixu/Dropbox/BCD-weili/gbXMLTest/temp.xml"); //directory of your local xml file
  Document doc;
  try{
    doc = (Document) builder.build(xmlFile);
```
2. Initiate the translator with 'doc' and a string of EnergyPlus version in the format of '#.#'. It should be noted the current version of the translator does not support version 8.7 translation.
```java
ReverseTranslator trans = new ReverseTranslator(doc, "8.6")
```
3. Register the data sources via a **DataPlugIn API** for missing data handling.
```java 
trans.registerDataPlugins(new ASHRAEConstructions());
trans.registerDataPlugins(new ASHRAELightData());
trans.registerDataPlugins(new ASHRAEOAData());
trans.registerDataPlugins(new DOEReferenceEquipmentData());
trans.registerDataPlugins(new ASHRAEHVAC());
```
4. Start translating the gbXML file to IDF with a fixed export directory
```java
trans.convert();
trans.exportFile("/Users/weilixu/Dropbox/BCD-weili/gbXMLTest");
```
The entire code can be viewed in the [RunTool.java](src/main/java/ui/RunTool.java)

## DataPlugIn API
This is a new feature in the second version of the gbEplus. This allows the designer to add in their preferred system data in the conversion process. As we know, Revit only exports geometry and constructions. However, a successful EnergyPlus simulation requires more than geometry and constructions. We also need lighting, equipment, people and HVACs...The [EnergyPlusDataAPI](src/main/java/api/EnergyPlusDataAPI.java) is designed for this purpose.
In this intereface, two methods are required to specify:
```java
public String dataBaseName();
public DataBaseType dataBaseType();
```
The first method returns the name of the plugin and the second method should return one of the [DataBaseType](src/main/java/api/DataBaseType.java). The **DataBaseType** defines the data interference point in the translation process. For example, if the plugin database type is ```java DataBaseType.CONSTRUCTION```, then the translator will use this database to impute any missing consutrions while translating gbXML.
The 
