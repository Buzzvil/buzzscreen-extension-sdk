package com.buzzvil.buzzscreen.sample_host;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.buzzvil.buzzscreen.extension.BuzzScreenHost;
import com.buzzvil.buzzscreen.extension.UserProfile;

/**
 * Created by patrick on 2017. 12. 6..
 */

public class App extends Application {

    public static final String LOCKSCREEN_APP_PACKAGE = "com.buzzvil.buzzscreen.sample_client";

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialization for BuzzScreenHost
        // example code uses com.buzzvil.buzzscreen.sample_client for L app package name.
        BuzzScreenHost.init(this, LOCKSCREEN_APP_PACKAGE);

        // Optional
        // Example of registering listener called when Buzzscreen is activated/deactivated in L app, or user information of L app is changed
        BuzzScreenHost.setClientEventListener(new BuzzScreenHost.ClientEventListener() {
            @Override
            public void onActivated() {
                // called when Buzzscreen is activated in L app
                Log.i("MainApp", "ClientEventListener - onActivated");
                Toast.makeText(App.this, "[Main app] Lockscreen App is activated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeactivated() {
                // called when Buzzscreen is deactivated in L app
                Log.i("MainApp", "ClientEventListener - onDeactivated");
                Toast.makeText(App.this, "[Main app] Lockscreen App is deactivated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserProfileUpdated(UserProfile userProfile) {
                // called when user information of L app is changed
                Log.i("MainApp", "ClientEventListener - onUserProfileUpdated");
            }
        });

        PreferenceHelper.init(this, "MainSamplePreference");
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(PreferenceHelper.getString(PrefKeys.PREF_KEY_USER_ID, ""));
    }

    public void login(String userId) {
        PreferenceHelper.putString(PrefKeys.PREF_KEY_USER_ID, userId);
    }

    public void logout() {
        PreferenceHelper.removeAll();
        BuzzScreenHost.logout();
    }
}
