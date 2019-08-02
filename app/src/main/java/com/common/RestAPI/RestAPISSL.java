package com.common.RestAPI;

import com.common.AbstractOrInterface.RestAPI;
import com.common.AbstractOrInterface.RestAPIInfo;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class RestAPISSL extends RestAPI {

    @Override
    public String POST(String URI, ArrayList<RestAPIInfo> RestAPIInfos) {
        final String boundary = "*****";
        final String lineEnd = "\r\n";
        final String twoHyphens = "--";
        String responseString = "";
        HttpsURLConnection conn = null;

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try {
            URL url = new URL(URI);
            conn = (HttpsURLConnection) url.openConnection();
            //conn.setConnectTimeout(1500);
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("accept-charset", "UTF-8");
            conn.setRequestProperty("Content-Type","multipart/form-data;charset=utf-8;boundary=" + boundary);

            if(this.RestAPIHeaderInfos != null){
                for(RestAPIInfo info : this.RestAPIHeaderInfos){
                    conn.setRequestProperty(info.fieldName, info.fieldData);
                }
            }

            if(RestAPIInfos != null && RestAPIInfos.size() > 0)
            {
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                for(RestAPIInfo RestAPIInfo : RestAPIInfos)
                {
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    // string data
                    dos.writeBytes("Content-Disposition: form-data; name=\""+ RestAPIInfo.fieldName +"\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(URLEncoder.encode(RestAPIInfo.fieldData,"UTF-8"));

                    dos.writeBytes(lineEnd);

                    if(RestAPIInfo.isFile)
                    {
                        File sourceFile = new File(RestAPIInfo.fieldData);
                        FileInputStream fileInputStream = new FileInputStream(sourceFile);

                        dos.writeBytes(twoHyphens + boundary + lineEnd);

                        dos.writeBytes("Content-Disposition: form-data; name=\""+ RestAPIInfo.fieldName +"\";filename=\""+ new File(RestAPIInfo.fieldData).getName() + "\"" + lineEnd);
                        dos.writeBytes(lineEnd);

                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];
                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0)
                        {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        }

                        fileInputStream.close();

                        dos.writeBytes(lineEnd);
                    }
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // close it here first
                dos.flush();
                dos.close();
            }

            int serverResponseCode = conn.getResponseCode();

            if(serverResponseCode == 200)
            {
                responseString = getStringFromInputStream(conn.getInputStream());
            }
            else
            {
                responseString = "ERROR:" + getStringFromInputStream(conn.getErrorStream());
            }
        } catch (Exception e) {
            responseString = "ERROR:" + e.toString();
        }

        return responseString;
    }

    @Override
    public String GET(String URI, ArrayList<RestAPIInfo> RestAPIInfos) {
        String responseString = "";
        HttpsURLConnection conn = null;

        try {
            URL url = new URL(URI + constructParametersAsString(RestAPIInfos));
            conn = (HttpsURLConnection) url.openConnection();
            //conn.setConnectTimeout(1500);

            int serverResponseCode = conn.getResponseCode();

            if(serverResponseCode == 200)
            {
                responseString = getStringFromInputStream(conn.getInputStream());
            }
            else
            {
                responseString = "ERROR:" + getStringFromInputStream(conn.getErrorStream());
            }
        } catch (Exception e) {
            responseString = "ERROR:" + e.toString();
        }

        return responseString;
    }

}
