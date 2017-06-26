package main.java.util;

import java.nio.CharBuffer;

public class StringUtil {
    public static String spaces(int n){
        if(n<=0){
            return "";
        }
        return CharBuffer.allocate(n).toString().replace('\0', ' ');
    }
}
