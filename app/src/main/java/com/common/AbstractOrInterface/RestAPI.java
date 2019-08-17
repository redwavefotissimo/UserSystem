package com.common.AbstractOrInterface;

import android.app.Activity;

import com.common.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

abstract public class RestAPI {

    protected ArrayList<RestAPIInfo> RestAPIHeaderInfos;
    protected SSLContext context;

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

    public void setCustomCert(Activity activity, String certName) throws Exception{
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        // From https://www.washington.edu/itconnect/security/ca/load-der.crt
        InputStream caInput = Utils.getAssetFileConent(activity, certName);
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);

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
