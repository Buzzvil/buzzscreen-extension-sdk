package com.buzzvil.buzzscreen.sample_host;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
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

    private AlertDialog dialog;

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
                    int birthYear = PreferenceHelper.getInt(PrefKeys.PREF_KEY_USER_BIRTH_YEAR, 1985);
                    String gender = PreferenceHelper.getString(PrefKeys.PREF_KEY_USER_GENDER, UserProfile.USER_GENDER_MALE);
                    String region = PreferenceHelper.getString(PrefKeys.PREF_KEY_USER_REGION, "서울특별시 관악구");

                    BuzzScreenHost.getUserProfile()
                            .setUserId(userId)
                            .setBirthYear(birthYear)
                            .setGender(gender)
                            .setRegion(region)
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

        layoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = getLayoutInflater().inflate(R.layout.profile_dialog, null);
                final EditText etBirthYear = (EditText) dialogView.findViewById(R.id.profile_birth_year);
                final EditText etRegion = (EditText) dialogView.findViewById(R.id.profile_region);
                final RadioGroup rgGender = (RadioGroup) dialogView.findViewById(R.id.profile_gender);

                int birthYear = PreferenceHelper.getInt(PrefKeys.PREF_KEY_USER_BIRTH_YEAR, 1985);
                String gender = PreferenceHelper.getString(PrefKeys.PREF_KEY_USER_GENDER, UserProfile.USER_GENDER_MALE);
                String region = PreferenceHelper.getString(PrefKeys.PREF_KEY_USER_REGION, "서울특별시 관악구");
                etBirthYear.setText(String.valueOf(birthYear));
                etRegion.setText(region);
                if (gender.equals(UserProfile.USER_GENDER_MALE)) {
                    rgGender.check(R.id.profile_gender_male);
                } else {
                    rgGender.check(R.id.profile_gender_female);
                }

                dialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("프로필 변경")
                        .setView(dialogView)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int newBirthYear = Integer.valueOf(etBirthYear.getText().toString());
                                    String newRegion = etRegion.getText().toString();
                                    String newGender = rgGender.getCheckedRadioButtonId() == R.id.profile_gender_male ? UserProfile.USER_GENDER_MALE : UserProfile.USER_GENDER_FEMALE;

                                    app.updateProfile(newBirthYear, newGender, newRegion);
                                    updateProfileUi();

                                    // Sync UserProfile to L app
                                    BuzzScreenHost.getUserProfile()
                                            .setBirthYear(newBirthYear)
                                            .setGender(newGender)
                                            .setRegion(newRegion)
                                            .sync();
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "Invalid value", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .create();
                dialog.show();
            }
        });
    }

    private void updateProfileUi() {
        if (app.isLoggedIn()) {
            layoutProfile.setVisibility(View.VISIBLE);
            layoutController.setVisibility(View.VISIBLE);
            String userId = PreferenceHelper.getString(PrefKeys.PREF_KEY_USER_ID, "");
            int birthYear = PreferenceHelper.getInt(PrefKeys.PREF_KEY_USER_BIRTH_YEAR, 1985);
            String gender = PreferenceHelper.getString(PrefKeys.PREF_KEY_USER_GENDER, UserProfile.USER_GENDER_MALE);
            String region = PreferenceHelper.getString(PrefKeys.PREF_KEY_USER_REGION, "서울특별시 관악구");
            ((TextView) findViewById(R.id.profile_user_id)).setText("ID : " + userId);
            ((TextView) findViewById(R.id.profile_birth_year)).setText("Birth year : " + birthYear);
            ((TextView) findViewById(R.id.profile_gender)).setText("Gender : " + gender);
            ((TextView) findViewById(R.id.profile_region)).setText("Region : " + region);

            if (!PreferenceHelper.getBoolean(PrefKeys.PREF_KEY_BUZZSCREEN_USER_PROFILE_SET_DONE, false)) {
                // 옛 버전에서 새 버전으로 업데이트를 했을 경우 처리
                BuzzScreenHost.getUserProfile()
                        .setUserId(userId)
                        .setBirthYear(birthYear)
                        .setGender(gender)
                        .setRegion(region)
                        .sync();
                PreferenceHelper.putBoolean(PrefKeys.PREF_KEY_BUZZSCREEN_USER_PROFILE_SET_DONE, true);
            }
        } else {
            layoutProfile.setVisibility(View.GONE);
            layoutController.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProfileUi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
