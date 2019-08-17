package com.common.RestAPI;

import com.common.AbstractOrInterface.RestAPIInfo;

import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class RestAPISSLJson extends RestAPISSL {

    @Override
    public String POST(String URI, ArrayList<RestAPIInfo> RestAPIInfos) {
        String responseString = "";
        HttpsURLConnection conn = null;

        try{
            URL url = new URL(URI);
            conn = (HttpsURLConnection) url.openConnection();

            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("accept", "application/json");
            conn.setRequestProperty("Content-Type","application/json");

            if(this.context != null){
                conn.setSSLSocketFactory(this.context.getSocketFactory());
            }

            if(this.RestAPIHeaderInfos != null){
                for(RestAPIInfo info : this.RestAPIHeaderInfos){
                    conn.setRequestProperty(info.fieldName, info.fieldData);
                }
            }

            // always assumed 1 size
            OutputStream os = conn.getOutputStream();
            byte[] input =  RestAPIInfos.get(0).fieldData.getBytes("utf-8");
            os.write(input, 0, input.length);

            int serverResponseCode = conn.getResponseCode();

            if(serverResponseCode == 200 || serverResponseCode == 201)
            {
                responseString = getStringFromInputStream(conn.getInputStream());
            }
            else
            {
                responseString = "ERROR:" + getStringFromInputStream(conn.getErrorStream());
            }
        } catch (Exception e) {
            responseString = "ERROR:" +  e.toString();
        }
        return responseString;
    }

    @Override
    public String PUT(String URI, ArrayList<RestAPIInfo> RestAPIInfos) {
        String responseString = "";
        HttpsURLConnection conn = null;

        try{
            URL url = new URL(URI);
            conn = (HttpsURLConnection) url.openConnection();

            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("accept", "application/json");
            conn.setRequestProperty("Content-Type","application/json");

            if(this.context != null){
                conn.setSSLSocketFactory(this.context.getSocketFactory());
            }

            if(this.RestAPIHeaderInfos != null){
                for(RestAPIInfo info : this.RestAPIHeaderInfos){
                    conn.setRequestProperty(info.fieldName, info.fieldData);
                }
            }

            // always assumed 1 size
            OutputStream os = conn.getOutputStream();
            byte[] input =  RestAPIInfos.get(0).fieldData.getBytes("utf-8");
            os.write(input, 0, input.length);

            int serverResponseCode = conn.getResponseCode();

            if(serverResponseCode == 200 || serverResponseCode == 201)
            {
                responseString = getStringFromInputStream(conn.getInputStream());
            }
            else
            {
                responseString = "ERROR:" + getStringFromInputStream(conn.getErrorStream());
            }
        } catch (Exception e) {
            responseString = "ERROR:" +  e.toString();
        }
        return responseString;
    }
}
