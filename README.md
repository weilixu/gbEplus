# gbEplus
gbEplus is an open source gbXML to EnergyPlus convertor written in JAVA.
The tool was firstly inspired by the [OpenStudio](https://github.com/NREL/OpenStudio) gbXML module. The initial version was developed by [weilixu](https://github.com/weilixu) to facilitates the creation of simple IDF geometries from revit. Quickly, after realizing the simple geometry conversion is merely enough for a successful energy simulation, Weili and [wanghp18](https://github.com/wanghp18) from CMU IW started working on the second version development in their spare time in CMU.
Currently, this open source project is maintained by [BuildSimHub Inc](https://www.buildsimhub.net/) and we welcome people use the tool or contribute their part to the code. 

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
### PlugIn types
In this intereface, two methods are required to specify:
```java
public String dataBaseName();
public DataBaseType dataBaseType();
```
The first method returns the name of the plugin and the second method should return one of the [DataBaseType](src/main/java/api/DataBaseType.java). The **DataBaseType** defines the data interference point in the translation process. For example, if the plugin database type is ```DataBaseType.CONSTRUCTION```, then the translator will use this database to impute any missing consutrions while translating gbXML.
The current types include:
1. **CONSTRUCTION**: include material, depends on your database structure, it can be layer-by-layer method or no:mass method
2. **FENESTRATION**
3. **INTERNALLOAD**: this include lighting, equipment and occupants.
4. **LIGHTING**: lighting
5. **SCHEDULE**: includes all the activity schedules
6. **EQUIPMENT**: plug load data
7. **OUTDOORAIR**: outdoor air requirements based on zone types
8. **HVAC**: hvac data for the building
9. **HVACANDSCHEDULE**: hvac data with hvac schedules.

### Main method to write in data 
Depends on the purpose of the plugin, the write in methods could varies. There are currently 5 different ways to write data into the translation process:
```java
public void writeInSystem(IDFFileObject objectFile, HashMap<String, String> id_to_NameMap);
public void writeInHVACSystem(IDFFileObject objectFile, ReverseTranslator translator);
public String getValueInString(String identifier);
public Double getValueInDouble(String identifier);
public Map<String, String[]> getValuesInHashMap(String identifier);
```
The first two methods allows PlugIn developers to write in entire systems into the EnergyPlus file. For example, you can write in the whole HVAC systems into the EnergyPlus file, or the entire construction set. Since it is writing in system, gbEplus are expecting the plugin developer to write in the system. Therefore, gbEplus provides its EnergyPlus data structure (*IDFFileObject*) to the developer. On top of the data structure, the translator also expects plugin developers to either 1. identify the ID to object name in the *id_to_NameMap* map or directly interact with the translator (The translator contains some of the critical information of the building such as floor area, volume etc.).

Instead of writing in data, the remaining three methods allows translator to extract values from database. Therefore, it is not necessary for developer to learn the data structure of the system because the translator takes care of the write in part. For example, if you wish to write in the office lighing power density or occupancy density into the translation.

Some Plugin examples are included in the plugin folder. These are the data extracted from the [ASHRAE](https://www.ashrae.org/) standards.

### Database format for Data Plugins
There is no fixed database format for the data plugins. As long as your data plugin understand your own database format and can be translated and write in the energyplus file. It is fine. However, gbEplus do have a default xml parser that reads a specially defined simple XML format. (The default functions to parse this type of XML will be release soon) Below is the example of the format.
```xml
<dataset setname="Construction" category="Climate Zone 1">
	<object description="Material" reference = "Climate Zone 1">
		<field description = "Name" type="String">Gypsum Board</field>
		<field description = "Roughness" type="String">MediumRough</field>
		<field description = "Thickness" type="Double">0.0159</field>
		<field description = "Conductivity" type="Double">0.16</field>
		<field description = "Density" type="Double">800.0</field>
		<field description = "Specific Heat" type="Double">1090.0</field>
		<field description = "Thermal Absorptance" type="Double">0.9</field>
		<field description = "Solar Absorptance" type = "Double">0.7</field>
		<field description = "Visible Absorptance" type = "Double">0.7</field>
	</object>
  </dataset>
```
Detail explaination of each element and attributes in the XML format can be found in this conference proceeding:
*Xu, Weili, Khee Poh Lam, Adrian Chong, and Omer T. Karaguzel. "Multi-Objective Optimization of Building Envelope, Lighting and HVAC Systems Designs." IBPSA-USA Journal 6, no. 1 (2016).* [link](http://ibpsa-usa.org/index.php/ibpusa/article/view/393).

## [](#eplus_datastructure_anchor)The EnergyPlus data structure for writing and reading
The IDF data structure consists of two classes: **IDFFileObject.java** and **IDFObject.java**. The **IDFFileObject** holds the key information of the energy model such as the version, and manage the **IDFObject**. For writing, the **IDFObject** is really the class that developer should be using.

### How to write in?
Write in is simple, you can do it by yourself, or use the **[IDFWriter](src/main/java/model/idf/IDFWriter.java)** class do the work. If you want to do it by yourself, you firstly need to define and initialize four ArrayList:
```java
ArrayList<String> lines = new ArrayList<String>(); //the values (e.g 0.0159)
ArrayList<String> units = new ArrayList<String>(); //the units (e.g. m)
ArrayList<String> comments = new ArrayList<String>(); //the comments or the keys (e.g. thickness)
ArrayList<String> topComments = new ArrayList<String>();//can be ignored if you dont have any more information regarding to this object

......

IDFObject newObject = new IDFObject(lines,units,comments,topComments);//create the object
//clean the arrays for later usage
lines.clear();
units.clear();
comments.clear();
topComments.clear();
```
Once you have successfully created the object, you have to add the object to the EnergyPlus file:
```java
IDFFileObject eplusFile = new IDFFileObject();
eplusFile.addIDFObject(newObject);
```

Another method is to use the **IDFWriter** to do the job:
```java
IDFWriter idfWriter = new IDFWriter();//initialize the writer.
idfWriter.recordInputs("Version","","",""); //1. value, 2. unit, 3. comment/key, 4. topcomment
idfWriter.recordInputs(energyPlusVersion,"","Version Identifier","");
idfWriter.addObject(file);  //remember to call this function to add object
```
### How to read?
```java
IDFFileObject eplusFile = new IDFFileObject();
eplusFile.addIDFObject(newObject);
.......
//get one type of objects
List<IDFObject> objectList = eplusFile.getCategoryList("BuildingSurface:Detailed");//retrieve all the surfaces list

//Loop over the objects
Map<String, ArrayList<IDFObject>> idfMap = eplusFile.getObjMap();
Iterator<String> objKeyItr = idfMap.keySet().iterator();
while(objKeyItr.hasNext()){
	String key = objKeyItr.next();
	ArrayList<IDFObject> objList = idfMap.get(key);
	...
}
```
The above code shows the example of how to read one type of objects or loop over the objects in the IDF file.



