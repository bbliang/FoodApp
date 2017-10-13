package com.hackgt17.foodapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hackgt17.foodapp.helpers.HttpHelper;
import com.hackgt17.foodapp.helpers.RequestPackage;
import com.hackgt17.foodapp.models.CustomData;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class CustomService extends IntentService {

    public static final String CUSTOM_SERVICE_NAME = "customService";
    public static final String CUSTOM_SERVICE_PAYLOAD = "customServicePayload";
    public static final String CUSTOM_SERVICE_ERROR = "customServiceError";
    public static final String REQUEST_PACKAGE = "requestPackage";
    public static final String REQUEST_IMAGE = "requestImage";

    public CustomService() {
        super("CustomService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RequestPackage requestPackage =
                intent.getParcelableExtra(REQUEST_PACKAGE);

        Intent messageIntent = new Intent(CUSTOM_SERVICE_NAME);
        LocalBroadcastManager manager =
                LocalBroadcastManager.getInstance(getApplicationContext());

        InputStream imageData = null;
        if (intent.getExtras().containsKey(REQUEST_IMAGE)){
           imageData = new ByteArrayInputStream(intent.getExtras().getByteArray(REQUEST_IMAGE));
        }

        //Make request to CVS, retrieve JSON file, parse/store information to CustomData object
        String response;
        try {
            //request to CVS
            response = HttpHelper.makeRequest(requestPackage, imageData);
            //parse information with Google GSON
            Gson gson = new Gson();
            CustomData customData = gson.fromJson(response, CustomData.class);
            messageIntent.putExtra(CUSTOM_SERVICE_PAYLOAD, customData);
        } catch (Exception e) {
            e.printStackTrace();
            messageIntent.putExtra(CUSTOM_SERVICE_ERROR, e.getMessage());
        }
        manager.sendBroadcast(messageIntent);
    }

}
