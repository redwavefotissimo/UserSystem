package com.common.AbstractOrInterface;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

abstract public class RestAPI {

    abstract public String POST(String URI, ArrayList<RestAPIInfo> RestAPIInfos);

    abstract public String GET(String URI, ArrayList<RestAPIInfo> RestAPIInfos);

    protected String constructParametersAsString(ArrayList<RestAPIInfo> RestAPIInfos) throws Exception{
        String parameters = "?";

        if(RestAPIInfos != null && RestAPIInfos.size() > 0){
            for(RestAPIInfo info : RestAPIInfos){
                if(!parameters.equals("?")){
                    parameters += "&";
                }
                parameters += info.fieldName + "=" + URLEncoder.encode(info.fieldData, "UTF-8");
            }
        }else{
            parameters = "";
        }

        return parameters;
    }

    protected String getStringFromInputStream(InputStream is) throws Exception
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder("");

        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();

        return sb.toString();
    }
}
