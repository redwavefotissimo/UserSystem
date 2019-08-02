package com.common.BoxNetAPI;

import android.app.Activity;

import com.common.AbstractOrInterface.RestAPIInfo;
import com.common.RestAPI.RestAPISSL;
import com.common.Utils;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;

import java.io.StringReader;
import java.security.PrivateKey;
import java.security.Security;
import java.util.ArrayList;

public class BoxNetAPI {

    final String APIBaseURI = "https://api.box.com/2.0/";
    final String authenticationUrl = "https://api.box.com/oauth2/token";
    final String credentialBoxNet = "box_net_cred.json";

    BoxCredSettings boxCredSettings;
    PrivateKey key;
    String assertion;
    Token token;

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
        Security.addProvider(new BouncyCastleProvider());

        // Using BouncyCastle's PEMParser we convert the
        // encrypted private key into a keypair object
        PEMParser pemParser = new PEMParser(
                new StringReader(boxCredSettings.boxAppSettings.appAuth.privateKey)
        );
        Object keyPair = pemParser.readObject();


        // Finally, we decrypt the key using the passphrase
        char[] passphrase = boxCredSettings.boxAppSettings.appAuth.passphrase.toCharArray();
        JceOpenSSLPKCS8DecryptorProviderBuilder decryptBuilder =
                new JceOpenSSLPKCS8DecryptorProviderBuilder().setProvider("BC");
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
        claims.setClaim("box_sub_type", "user");
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
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        jws.setHeader("typ", "JWT");
        jws.setHeader("kid", boxCredSettings.boxAppSettings.appAuth.publicKeyID);
        assertion = jws.getCompactSerialization();
    }

    private void getAuthCode() throws Exception{
        ArrayList<RestAPIInfo> restAPIInfoArrayList = new ArrayList<>();

        RestAPIInfo restAPIInfo = new RestAPIInfo();
        restAPIInfo.fieldName = "grant_type";
        restAPIInfo.fieldData = "urn:ietf:params:oauth:grant-type:jwt-bearer";
        restAPIInfoArrayList.add(restAPIInfo);

        restAPIInfo.fieldName = "assertion";
        restAPIInfo.fieldData = assertion;
        restAPIInfoArrayList.add(restAPIInfo);

        restAPIInfo.fieldName = "client_id";
        restAPIInfo.fieldData = boxCredSettings.boxAppSettings.clientID;
        restAPIInfoArrayList.add(restAPIInfo);

        restAPIInfo.fieldName = "client_secret";
        restAPIInfo.fieldData = boxCredSettings.boxAppSettings.clientSecret;
        restAPIInfoArrayList.add(restAPIInfo);

        String reqResponseString = restAPISSL.POST(authenticationUrl, restAPIInfoArrayList);

        if(reqResponseString.startsWith("ERROR:")){
            throw new Exception(reqResponseString);
        }

        token = (Token) Utils.JsonStringToObject(reqResponseString, Token.class);
    }
}
