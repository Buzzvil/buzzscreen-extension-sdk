package com.buzzvil.sample_client;

import android.app.Application;
import android.widget.Toast;

import com.buzzvil.buzzscreen.extension.BuzzScreenClient;
import com.buzzvil.buzzscreen.sdk.BuzzScreen;
import com.buzzvil.buzzscreen.sdk.SimpleLockerActivity;

/**
 * Created by patrick on 2017. 12. 6..
 */

public class App extends Application {

    public static final String MAIN_APP_PACKAGE = "com.buzzvil.sample_host";

    /**
     * 잠금화면을 켜기 위한 조건이 충족되지 않아서 Main 앱으로 유저를 이동시켜야 할 때 사용할 딥링크 설정
     * 설정되지 않았을 경우 Main 앱을 실행한다.
     */
    public static final String DEEP_LINK_ONBOARDING = "";

    @Override
    public void onCreate() {
        super.onCreate();

        BuzzScreen.init("my_app_key", this, SimpleLockerActivity.class, R.drawable.image_on_fail);

        BuzzScreenClient.init(this, MAIN_APP_PACKAGE);
        BuzzScreenClient.setHostEventListener(new BuzzScreenClient.HostEventListener() {
            @Override
            public void onLogout() {
                clearTermAgree();
            }
        });
        BuzzScreenClient.setOnDeactivatedByHostListener(new BuzzScreenClient.OnDeactivatedByHostListener() {
            @Override
            public void onDeactivated() {
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
