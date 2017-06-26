package main.java.model.gbXML;

import java.util.ArrayList;

import org.jdom2.Namespace;

import main.java.model.idf.IDFFileObject;
import main.java.model.idf.IDFObject;

/**
 * This module added the output of the translated energyplus
 * follows the standard output format
 * 
 * @author weilixu
 *
 */
public class OutputModule {
	
	private Namespace ns;

	//for demo purpose, this will default everything to Pittsburgh
	
	
	private ArrayList<String> lines;
	private ArrayList<String> units;
	private ArrayList<String> comments;
	private ArrayList<String> topComments;
	
	public OutputModule(Namespace ns){
		this.ns = ns;
		
		lines = new ArrayList<String>();
		units = new ArrayList<String>();
		comments = new ArrayList<String>();
		topComments = new ArrayList<String>();
		
	}
	
	public void addTableSummary(IDFFileObject file){
		
		recordInputs("Output:VariableDictionary","","","");
		recordInputs("Regular","","Key Field","");
		recordInputs("","","Sort Option","");
		addObject(file);
		
		//table summary
		recordInputs("Output:Table:SummaryReports","","","");
		recordInputs("AllSummary","","Report 1 Name","");
		recordInputs("ZoneComponentLoadSummary","","Report 2 Name","");
		addObject(file);
		
		//table: style
		recordInputs("OutputControl:Table:Style","","","");
		recordInputs("HTML","","Column Separator","");
		recordInputs("JtoKWH","","Unit Conversion","");//TODO need to revisit
		addObject(file);
	}
	
	
    private void recordInputs(String line, String unit, String comment, String topComments){
        lines.add(line);
        units.add(unit);
        comments.add(comment);
    }
    
    private void addObject(IDFFileObject file){
        file.addIDFObject(new IDFObject(lines,units, comments, topComments));
        lines.clear();
        units.clear();
        comments.clear();
        topComments.clear();
    }
}
