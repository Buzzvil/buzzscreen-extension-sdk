package com.buzzvil.buzzscreen.sample_client;

import android.app.Application;
import android.widget.Toast;

import com.buzzvil.buzzscreen.extension.BuzzScreenClient;
import com.buzzvil.buzzscreen.sdk.BuzzScreen;
import com.buzzvil.buzzscreen.sdk.SimpleLockerActivity;

/**
 * Created by patrick on 2017. 12. 6..
 */

public class App extends Application {

    public static final String MAIN_APP_PACKAGE = "com.buzzvil.buzzscreen.sample_host";

    /**
     * Set a deeplink to use when moving the user to Main app because the conditions for turning on lockscreen are not met
     * If not set, run Main app.
     */
    public static final String DEEP_LINK_ONBOARDING = "";

    @Override
    public void onCreate() {
        super.onCreate();

        // app_key : app key for SDK usage, check in your admin
        // SimpleLockerActivity.class : Activity class of the lockscreen
        // R.drawable.image_on_fail : An image that will be shown if there is no network error or temporarily no campaigns to show on the lockscreen.
        BuzzScreen.init("my_app_key", this, SimpleLockerActivity.class, R.drawable.image_on_fail);

        // Initialization for BuzzScreenClient
        // The example code uses com.buzzvil.buzzscreen.sample_host for M app package name.
        BuzzScreenClient.init(this, MAIN_APP_PACKAGE);
        BuzzScreenClient.setHostEventListener(new BuzzScreenClient.HostEventListener() {
            @Override
            public void onLogout() {
                // When logout is called from M app
                clearTermAgree();
            }
        });
        BuzzScreenClient.setOnDeactivatedByHostListener(new BuzzScreenClient.OnDeactivatedByHostListener() {
            @Override
            public void onDeactivated() {
                // when L app lockscreen is disabled by M app.
                Toast.makeText(App.this, R.string.app_deactivated_by_m, Toast.LENGTH_LONG).show();
            }
        });

        PreferenceHelper.init(this, "LockscreenSamplePreference");
    }

    public static boolean isTermAgree() {
        return PreferenceHelper.getBoolean(PrefKeys.PREF_KEY_TERM_AGREE, false);
    }

    public static void setTermAgree(boolean agree) {
        PreferenceHelper.putBoolean(PrefKeys.PREF_KEY_TERM_AGREE, agree);
    }

    public static void clearTermAgree() {
        PreferenceHelper.remove(PrefKeys.PREF_KEY_TERM_AGREE);
    }
}
