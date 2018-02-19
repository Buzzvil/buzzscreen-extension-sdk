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

        BuzzScreenHost.init(this, LOCKSCREEN_APP_PACKAGE);

        // Optional
        // L앱의 잠금화면이 활성화/비활성화되거나 L앱에서 유저 정보가 변경되는 경우 호출되는 리스너 등록 예시
        BuzzScreenHost.setClientEventListener(new BuzzScreenHost.ClientEventListener() {
            @Override
            public void onActivated() {
                Log.i("MainApp", "ClientEventListener - onActivated");
                Toast.makeText(App.this, "[Main app] Lockscreen App is activated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeactivated() {
                Log.i("MainApp", "ClientEventListener - onDeactivated");
                Toast.makeText(App.this, "[Main app] Lockscreen App is deactivated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserProfileUpdated(UserProfile userProfile) {
                Log.i("MainApp", "ClientEventListener - onUserProfileUpdated");
                updateProfile(userProfile.getBirthYear(), userProfile.getGender(), userProfile.getRegion());
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

    public void updateProfile(int birthYear, String gender, String region) {
        PreferenceHelper.putInt(PrefKeys.PREF_KEY_USER_BIRTH_YEAR, birthYear);
        PreferenceHelper.putString(PrefKeys.PREF_KEY_USER_GENDER, gender);
        PreferenceHelper.putString(PrefKeys.PREF_KEY_USER_REGION, region);
    }
}
