package main.java.model.idf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.util.StringUtil;

public class IDFObject implements Serializable{
    private static final long serialVersionUID = 5009729187189221805L;
    
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    
    /**
     * NOTE: This class has deepClone() function, any member change should change deepClone() accordingly
     */
    
    private String objLabel = null; //category
    private String[] values = null;
    private String[] units = null;
    private String[] commentsNoUnit = null;
    private String[] topComments = null;
    
    private String objName = null; //usual the second field
    private int objLen = 0;
    
    public IDFObject(ArrayList<String> lines, 
                     ArrayList<String> units, 
                     ArrayList<String> comments, 
                     ArrayList<String> topComments){
        if(topComments!=null && !topComments.isEmpty()){
            this.topComments = topComments.toArray(new String[topComments.size()]);
        }
        
        if(lines == null){
            //special object contains end of file comments
            objLabel = "EOFComments";
            objName = objLabel;
            return;
        }
        
        this.objLabel = lines.get(0);
        if(objLabel.equals("Version") && this.topComments==null){
            //padding empty comment, to correctly show Version object
            this.topComments = new String[]{"!"};
        }
        
        objLen = lines.size();
        if(objLen>1){
            this.values = new String[objLen-1];
            this.units = new String[objLen-1];
            this.commentsNoUnit = new String[objLen-1];
            
            for(int i=1;i<objLen;i++){
                this.values[i-1] = lines.get(i).trim();
                
                this.units[i-1] = units.get(i);
                this.commentsNoUnit[i-1] = comments.get(i);
                
                if(this.units[i-1]!=null){
                    this.units[i-1] = this.units[i-1].trim();
                }
                if(this.commentsNoUnit[i-1]!=null){
                    this.commentsNoUnit[i-1] = this.commentsNoUnit[i-1].trim();
                }
            }
            
            objName = values[0];            
        }
    }

    public String getLine(int i, int pad){
        if(i<0 || i>=objLen){
            return null;
        }
        
        if(i==0){
            return objLabel+",";
        }
        
        String data = this.values[i-1];
        String unit = this.units[i-1];
        String comment = this.commentsNoUnit[i-1];
        
        if(comment!=null && !comment.isEmpty()){
            int padNeed = 100;//WX hard-set to 100 spaces

            comment = StringUtil.spaces(padNeed)+"!- "+comment;

            if(unit!=null && !unit.isEmpty()){
                comment += " {"+unit+"}";
            }
            
        }else {
            comment = "";
        }
        
        String valueEnd = ",";
        if(i==objLen-1){
            valueEnd = ";";
        }
        
        return StringUtil.spaces(4)+data+valueEnd+comment;
    }
    
    public String printStatement(){
        String lineDelimiter = "\r\n";
        
        StringBuilder sb = new StringBuilder();
        
        if(topComments!=null){
            Arrays.stream(topComments).forEach(e->sb.append(e+lineDelimiter));
        }
        
        if(values==null){
            return sb.toString();
        }
        
        for(int i=0;i<objLen;i++){
            sb.append(getLine(i, 100)).append(lineDelimiter);
        }
        
        return sb.toString();
    }
    
    
    
    public String getObjLabel(){
        return this.objLabel;
    }
    
    public void setObjLabel(String label){
        this.objLabel = label;
    }
    
    public String[] getData(){
        return this.values;
    }
    
    public String[] getUnit(){
        return this.units;
    }
    
    public String[] getComments(){
        return this.commentsNoUnit;
    }
    
    public String getName(){
        return this.objName;
    }
    
    public void setName(String name){
        this.objName = name;
    }
    
    public int getTopCommentsLen(){
        return topComments==null ? 0 : topComments.length;
    }
    
    public String[] getTopComments(){
        return this.topComments;
    }
    
    public int getObjLen(){
        return this.objLen;
    }
    
    public String getDataByCommentNoUnit(String commentNoUnit){
        for(int i=0;i<commentsNoUnit.length;i++){
            if(commentsNoUnit[i].equalsIgnoreCase(commentNoUnit)){
                return values[i];
            }
        }
        LOG.warn("Get data by comment no unit cannot find value, label:"+objLabel+" name:"+objName+", comment:"+commentNoUnit);
        return null;
    }
    
    public String getUnitByCommentNoUnit(String commentNoUnit){
        for(int i=0;i<commentsNoUnit.length;i++){
            if(commentsNoUnit[i].equalsIgnoreCase(commentNoUnit)){
                return units[i]==null ? "" : units[i];
            }
        }
        LOG.warn("Get unit by comment no unit cannot find value, label:"+objLabel+", comment:"+commentNoUnit);
        return null;
    }
}
