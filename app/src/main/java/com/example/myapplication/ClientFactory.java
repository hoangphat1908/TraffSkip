package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
//import com.amazonaws.mobileconnectors.appsync.sigv4.CognitoUserPoolsAuthProvider;

public class ClientFactory {
    private static volatile AWSAppSyncClient client;

    public static synchronized void init(final Context context) {
        if (client == null) {
            final AWSConfiguration awsConfiguration = new AWSConfiguration(context);
            client = AWSAppSyncClient.builder()
                    .context(context)
                    .awsConfiguration(awsConfiguration)
                    .build();
        }
    }

    public static synchronized AWSAppSyncClient appSyncClient() {
        return client;
    }
}
