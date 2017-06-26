package main.java.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);
    
    public static void writeStringToFile(String str, String path){
        if(str==null){
            str = "";
        }
        
        try(FileWriter fw = new FileWriter(path);
                BufferedWriter bw = new BufferedWriter(fw)){
            bw.write(str);
            bw.flush();
        }catch (IOException e){
            LOG.error(e.getMessage(), e);
        }
    }
}
