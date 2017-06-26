package main.java.util;

import java.util.Random;

public class SecurityUtil {
    private final static Random RAND = new Random(System.currentTimeMillis());
    
    public static String genRandomStr(){
        return Hasher.hash(String.valueOf(RAND.nextLong()));
    }
}
