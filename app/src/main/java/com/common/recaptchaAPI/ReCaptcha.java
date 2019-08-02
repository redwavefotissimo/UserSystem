package com.common.recaptchaAPI;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.common.AbstractOrInterface.RestAPIInfo;
import com.common.RestAPI.RestAPISSL;
import com.common.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class ReCaptcha {

    final String siteKey = "6Lf77bAUAAAAAKkugZ3kzGczn3l7uKiC_FefhhxP";
    final String siteSecret = "6Lf77bAUAAAAAB6RlC0aPfo93RltXWlLDsXZHVAS";
    final String TAG = "ReCaptcha";
    final String finalVerifyURI = "https://www.google.com/recaptcha/api/siteverify";

    Activity mActivity;
    GoogleApiClient mGoogleApiClient;
    RestAPISSL RestAPISSL;
    ReCaptchaResults ReCaptchaResults;

    public interface ReCaptchaResults {
        public void onSuccess(String success);
        public void onError(String error);
    }

    public ReCaptcha(Activity activity){
        this.mActivity = activity;
        RestAPISSL = new RestAPISSL();
    }

    public void setReCaptchaResults(ReCaptchaResults ReCaptchaResults){
        this.ReCaptchaResults = ReCaptchaResults;
    }

    public void connectGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .build();

        mGoogleApiClient.connect();
    }

    public void useReCaptcha(){
        final Executor executor = new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {
                command.run();
            }
        };
        SafetyNet.getClient(mActivity).verifyWithRecaptcha(siteKey)
                .addOnSuccessListener(executor,
                        new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                            @Override
                            public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                // Indicates communication with reCAPTCHA service was
                                // successful.
                                String userResponseToken = response.getTokenResult();
                                if (!userResponseToken.isEmpty()) {
                                    try {
                                        if(ReCaptchaResults != null){
                                            ReCaptchaResults.onSuccess("is Success: " + doFinalVerify(userResponseToken));
                                        }
                                    }
                                    catch (Exception ex){
                                        if(ReCaptchaResults != null){
                                            ReCaptchaResults.onError(ex.toString());
                                        }
                                        Log.e(TAG, ex.toString());
                                    }
                                }
                            }
                        })
                .addOnFailureListener(executor, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            // An error occurred when communicating with the
                            // reCAPTCHA service. Refer to the status code to
                            // handle the error appropriately.
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            Log.e(TAG, "Error: " + CommonStatusCodes
                                    .getStatusCodeString(statusCode));
                            if(ReCaptchaResults != null){
                                ReCaptchaResults.onError("Error: " + CommonStatusCodes
                                        .getStatusCodeString(statusCode));
                            }
                        } else {
                            // A different, unknown type of error occurred.
                            Log.e(TAG, "Error: " + e.getMessage());
                            if(ReCaptchaResults != null){
                                ReCaptchaResults.onError(e.getMessage());
                            }
                        }
                    }
                });
    }

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks(){

        @Override
        public void onConnected(@Nullable Bundle bundle) {

        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    };

    private GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener(){

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
    };

    private boolean doFinalVerify(String response) throws Exception{
        ArrayList<RestAPIInfo> RestAPIInfoList = new ArrayList<RestAPIInfo>();

        RestAPIInfo RestAPIInfo = new RestAPIInfo();
        RestAPIInfo.fieldName = "secret";
        RestAPIInfo.fieldData = siteSecret;
        RestAPIInfoList.add(RestAPIInfo);

        RestAPIInfo = new RestAPIInfo();
        RestAPIInfo.fieldName = "response";
        RestAPIInfo.fieldData = response;
        RestAPIInfoList.add(RestAPIInfo);

        String reqResponseString = RestAPISSL.POST(finalVerifyURI, RestAPIInfoList);

        if(reqResponseString.startsWith("ERROR:")){
            throw new Exception(reqResponseString);
        }

        ReCaptchaResponseInfo ReCaptchaResponseInfo = (ReCaptchaResponseInfo) Utils.JsonStringToObject(reqResponseString, ReCaptchaResponseInfo.class);

        return ReCaptchaResponseInfo.success;
    }
}
