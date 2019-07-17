package com.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    public static String objectToJsonString(Object object) throws Exception{
        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(object);
    }

    public static Object JsonStringToObject(String jsonString, Class objectClass) throws Exception{
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(jsonString, objectClass);
    }

    public static String getFileMD5Hash(String fileLocation){
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
        } // Encryption algorithm
        mdEnc.update(fileLocation.getBytes(), 0, fileLocation.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while ( md5.length() < 32 ) {
            md5 = "0"+md5;
        }
        return md5;
    }

}
