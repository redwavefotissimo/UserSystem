package com.common.BoxNetAPI;

import android.app.Activity;

import com.android.internal.http.multipart.MultipartEntity;
import com.common.AbstractOrInterface.RestAPIInfo;
import com.common.RestAPI.RestAPISSL;
import com.common.Utils;


import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.openssl.PEMParser;
import org.spongycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.spongycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.spongycastle.operator.InputDecryptorProvider;
import org.spongycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;

import java.io.File;
import java.io.StringReader;
import java.security.PrivateKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

public class BoxNetAPI {

    final String APIBaseURI = "https://api.box.com/2.0/";
    final String APIBaseUploadURI = "https://upload.box.com/api/2.0/files/content";
    final String authenticationUrl = "https://api.box.com/oauth2/token";
    final String credentialBoxNet = "box_net_cred.json";

    final String getItemListURI = APIBaseURI + "folders/%s/items";
    final String getRootList = APIBaseURI + "folders/0";

    BoxCredSettings boxCredSettings;
    PrivateKey key;
    String assertion;
    Token token;
    BoxItemSimpleInfo userSystemFolder;

    Activity activity;
    RestAPISSL restAPISSL;

    public BoxNetAPI(Activity activity) throws Exception{
        this.activity = activity;

        restAPISSL = new RestAPISSL();

        String credContent = Utils.StreamToString(Utils.getAssetFileConent(this.activity, credentialBoxNet));
        boxCredSettings = (BoxCredSettings) Utils.JsonStringToObject(credContent, BoxCredSettings.class);

        decriptPrivateKey();
        setJWTClaim();
        getAuthCode();
    }

    private void decriptPrivateKey() throws Exception{
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);

        // Using BouncyCastle's PEMParser we convert the
        // encrypted private key into a keypair object
        PEMParser pemParser = new PEMParser(
                new StringReader(boxCredSettings.boxAppSettings.appAuth.privateKey)
        );
        Object keyPair = pemParser.readObject();


        // Finally, we decrypt the key using the passphrase
        char[] passphrase = boxCredSettings.boxAppSettings.appAuth.passphrase.toCharArray();
        JceOpenSSLPKCS8DecryptorProviderBuilder decryptBuilder =
                new JceOpenSSLPKCS8DecryptorProviderBuilder().setProvider("SC");
        InputDecryptorProvider decryptProvider
                = decryptBuilder.build(passphrase);

        PrivateKeyInfo keyInfo
                = ((PKCS8EncryptedPrivateKeyInfo) keyPair).decryptPrivateKeyInfo(decryptProvider);

        // In the end, we will use this key in the next steps
        key = (new JcaPEMKeyConverter()).getPrivateKey(keyInfo);

        pemParser.close();
    }

    private void setJWTClaim() throws Exception{

        // Rather than constructing the JWT assertion manually, we are
        // using the org.jose4j.jwt library.
        JwtClaims claims = new JwtClaims();
        claims.setIssuer(boxCredSettings.boxAppSettings.clientID);
        claims.setAudience(authenticationUrl);
        claims.setSubject(boxCredSettings.enterpriseID);
        claims.setClaim("box_sub_type", "enterprise");
        // This is an identifier that helps protect against
        // replay attacks
        claims.setGeneratedJwtId(64);
        // We give the assertion a lifetime of 45 seconds
        // before it expires
        claims.setExpirationTimeMinutesInTheFuture(0.75f);

        // With the claims in place, it's time to sign the assertion
        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(key);
        // The API support "RS256", "RS384", and "RS512" encryption
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA512);
        jws.setHeader("typ", "JWT");
        jws.setHeader("kid", boxCredSettings.boxAppSettings.appAuth.publicKeyID);
        assertion = jws.getCompactSerialization();
    }

    private void getAuthCode() throws Exception{
        ArrayList<RestAPIInfo> restAPIInfoArrayList = new ArrayList<>();

        RestAPIInfo restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldName = "grant_type";
        restAPIInfo.fieldData = "urn:ietf:params:oauth:grant-type:jwt-bearer";
        restAPIInfo.doEncode = false;
        restAPIInfoArrayList.add(restAPIInfo);

        restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldName = "client_id";
        restAPIInfo.fieldData = boxCredSettings.boxAppSettings.clientID;
        restAPIInfo.doEncode = false;
        restAPIInfoArrayList.add(restAPIInfo);

        restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldName = "client_secret";
        restAPIInfo.fieldData = boxCredSettings.boxAppSettings.clientSecret;
        restAPIInfo.doEncode = false;
        restAPIInfoArrayList.add(restAPIInfo);

        restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldName = "assertion";
        restAPIInfo.fieldData = assertion;
        restAPIInfo.doEncode = false;
        restAPIInfoArrayList.add(restAPIInfo);

        String reqResponseString = restAPISSL.POST(authenticationUrl, restAPIInfoArrayList);

        if(reqResponseString.startsWith("ERROR:")){
            throw new Exception(reqResponseString);
        }

        /* do not use from apache if can still able to manage using own custom code
        // Create the params for the request
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        // This specifies that we are using a JWT assertion
        // to authenticate
        params.add(new BasicNameValuePair(
                "grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"));
        // Our JWT assertion
        params.add(new BasicNameValuePair(
                "assertion", assertion));
        // The OAuth 2 client ID and secret
        params.add(new BasicNameValuePair(
                "client_id", boxCredSettings.boxAppSettings.clientID));
        params.add(new BasicNameValuePair(
                "client_secret", boxCredSettings.boxAppSettings.clientSecret));

        // Make the POST call to the authentication endpoint
        CloseableHttpClient httpClient =
                HttpClientBuilder.create().disableCookieManagement().build();
        HttpPost request = new HttpPost(authenticationUrl);
        request.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse httpResponse = httpClient.execute(request);
        HttpEntity entity = httpResponse.getEntity();
        String reqResponseString = EntityUtils.toString(entity);
        httpClient.close();*/

        token = (Token) Utils.JsonStringToObject(reqResponseString, Token.class);

        ArrayList<RestAPIInfo> restAPIInfoHeaderArrayList = new ArrayList<>();

        restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldName = "Authorization";
        restAPIInfo.fieldData = "Bearer " + token.access_token;
        restAPIInfoHeaderArrayList.add(restAPIInfo);

        restAPISSL.setHeaders(restAPIInfoHeaderArrayList);
    }

    private BoxItemInfo getRootList() throws Exception{
        String reqResponseString = restAPISSL.GET(String.format(getRootList), null);

        if(reqResponseString.startsWith("ERROR:")){
            throw new Exception(reqResponseString);
        }

        return (BoxItemInfo) Utils.JsonStringToObject(reqResponseString, BoxItemInfo.class);
    }

    private String getItemList(String folderID) throws Exception{
        String reqResponseString = restAPISSL.GET(String.format(getItemListURI, folderID), null);

        return "";
    }

    public void getUserSystemFolder() throws Exception{
        BoxItemInfo rootFolder = getRootList();

        for(BoxItemSimpleInfo boxItemInfo : rootFolder.item_collection.entries){
            if(boxItemInfo.name.equals("UserSystemFolder")){
                userSystemFolder = boxItemInfo;
                break;
            }
        }
    }

    public BoxUploadedFileInfo updloadFile(File fileToUpload) throws Exception{

        BoxUploadFIleInfo BoxUploadFIleInfo = new BoxUploadFIleInfo();
        BoxUploadFIleInfo.name = fileToUpload.getName();

        BoxUploadFIleInfo.parent = new BoxUploadFileParentInfo();
        BoxUploadFIleInfo.parent.id = userSystemFolder.id;

        ArrayList<RestAPIInfo> restAPIInfoArrayList = new ArrayList<>();

        RestAPIInfo restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldName = "attributes";
        restAPIInfo.fieldData = Utils.objectToJsonString(BoxUploadFIleInfo);
        restAPIInfo.doEncode = false;
        restAPIInfoArrayList.add(restAPIInfo);

        restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldName = "file";
        restAPIInfo.isFile = true;
        restAPIInfo.fieldData = fileToUpload.getAbsolutePath();
        restAPIInfoArrayList.add(restAPIInfo);

        String reqResponseString = restAPISSL.POST(APIBaseUploadURI, restAPIInfoArrayList);

        if(reqResponseString.startsWith("ERROR:")){
            throw new Exception(reqResponseString);
        }

        return(BoxUploadedFileInfo) Utils.JsonStringToObject(reqResponseString, BoxUploadedFileInfo.class);
    }
}
