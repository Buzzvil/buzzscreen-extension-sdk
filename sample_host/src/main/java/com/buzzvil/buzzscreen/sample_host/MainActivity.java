package com.buzzvil.buzzscreen.sample_host;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.buzzvil.buzzscreen.extension.BuzzScreenHost;
import com.buzzvil.buzzscreen.extension.UserProfile;

import java.util.Random;

public class MainActivity extends Activity {

    private ToggleButton btLogin;
    private View layoutProfile;
    private View layoutController;

    private Button btLaunchLockscreenApp;

    private App app;

    private int birthYear = 1985;
    private String gender = UserProfile.USER_GENDER_MALE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (App) getApplicationContext();

        btLogin = (ToggleButton) findViewById(R.id.login_toggle);
        layoutProfile = findViewById(R.id.profile_layout);
        layoutController = findViewById(R.id.controller_layout);
        btLaunchLockscreenApp = (Button) findViewById(R.id.lockscreen_app_launch);

        btLogin.setChecked(app.isLoggedIn());
        btLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    String userId = "testuserid" + new Random().nextInt(100000);
                    app.login(userId);

                    BuzzScreenHost.getUserProfile()
                            .setUserId(userId)
                            .setBirthYear(birthYear)
                            .setGender(gender)
                            .sync();
                } else {
                    app.logout();
                }
                updateProfileUi();
            }
        });


        btLaunchLockscreenApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuzzScreenHost.launchClient();
            }
        });
        updateProfileUi();
    }

    private void updateProfileUi() {
        if (app.isLoggedIn()) {
            layoutProfile.setVisibility(View.VISIBLE);
            layoutController.setVisibility(View.VISIBLE);
            String userId = PreferenceHelper.getString(PrefKeys.PREF_KEY_USER_ID, "");
            ((TextView) findViewById(R.id.profile_user_id)).setText("ID : " + userId);
            ((TextView) findViewById(R.id.profile_birth_year)).setText("Birth year : " + birthYear);
            ((TextView) findViewById(R.id.profile_gender)).setText("Gender : " + gender);

            if (!PreferenceHelper.getBoolean(PrefKeys.PREF_KEY_BUZZSCREEN_USER_PROFILE_SET_DONE, false)) {
                // when updating to new version
                BuzzScreenHost.getUserProfile()
                        .setUserId(userId)
                        .setBirthYear(birthYear)
                        .setGender(gender)
                        .sync();
                PreferenceHelper.putBoolean(PrefKeys.PREF_KEY_BUZZSCREEN_USER_PROFILE_SET_DONE, true);
            }
        } else {
            layoutProfile.setVisibility(View.GONE);
            layoutController.setVisibility(View.GONE);
        }
    }
}
