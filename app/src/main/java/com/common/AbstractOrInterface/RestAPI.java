package com.common.AbstractOrInterface;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

abstract public class RestAPI {

    protected ArrayList<RestAPIInfo> RestAPIHeaderInfos;

    public void setHeaders(ArrayList<RestAPIInfo> RestAPIInfos){
        RestAPIHeaderInfos = RestAPIInfos;
    }

    public void addHeaders(String key, String value){
        if(RestAPIHeaderInfos == null){
            RestAPIHeaderInfos = new ArrayList<RestAPIInfo>();
        }
        RestAPIInfo RestAPIInfo = new RestAPIInfo();
        RestAPIInfo.fieldName = key;
        RestAPIInfo.fieldData = value;
        RestAPIHeaderInfos.add(RestAPIInfo);
    }

    public void deleteHeader(String key){
        Iterator<RestAPIInfo> restAPIInfoIterator = RestAPIHeaderInfos.iterator();
        while(restAPIInfoIterator.hasNext()){
            RestAPIInfo RestAPIInfo = restAPIInfoIterator.next();
            if(RestAPIInfo.fieldName.equals(key)){
                restAPIInfoIterator.remove();
                break;
            }
        }
    }

    abstract public String POST(String URI, ArrayList<RestAPIInfo> RestAPIInfos);

    abstract public String GET(String URI, ArrayList<RestAPIInfo> RestAPIInfos);

    abstract public String DELETE(String URI, ArrayList<RestAPIInfo> RestAPIInfos);

    abstract public String PUT(String URI, ArrayList<RestAPIInfo> RestAPIInfos);

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
