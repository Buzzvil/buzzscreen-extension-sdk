package com.buzzvil.sample_client;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buzzvil.buzzscreen.extension.BuzzScreenClient;
import com.buzzvil.buzzscreen.sdk.BuzzScreen;
import com.buzzvil.buzzscreen.sdk.UserProfile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Views
    private View topLayout;
    private ProgressBar pbLoading;
    private ImageView ivStatus;
    private TextView tvTitle;
    private TextView tvMessage;

    private View errorLayout;
    private View switchLayout;
    private View profileLayout;
    private Button btnSwitchOn;
    private Button btnSwitchOff;

    private int snoozeSelectedPosition = 0;

    private AlertDialog dialog;

    private BuzzScreenClient buzzScreenClient = new BuzzScreenClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init Toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // init views
        topLayout = (RelativeLayout) findViewById(R.id.main_top_layout);
        pbLoading = (ProgressBar) findViewById(R.id.main_top_loading);
        ivStatus = (ImageView) findViewById(R.id.main_top_status);
        tvTitle = (TextView) findViewById(R.id.main_title);
        tvMessage = (TextView) findViewById(R.id.main_message);

        errorLayout = findViewById(R.id.main_error_layout);
        switchLayout = findViewById(R.id.main_switch_layout);
        profileLayout = findViewById(R.id.main_profile_layout);
        btnSwitchOn = (Button) findViewById(R.id.main_switch_on);
        btnSwitchOff = (Button) findViewById(R.id.main_switch_off);

        btnSwitchOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (App.isTermAgree()) {
                    BuzzScreen.getInstance().activate();
                    updateSwitchLayout();
                } else {
                    showTermAgreeDialog();
                }
            }
        });

        btnSwitchOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSnoozeSettings();
            }
        });

        findViewById(R.id.main_go_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchApp(App.MAIN_APP_PACKAGE);
            }
        });

        BuzzScreen.getInstance().launch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLoading();
        buzzScreenClient.checkAvailability(new BuzzScreenClient.OnCheckAvailabilityListener() {
            @Override
            public void onAvailable() {
                showSwitchLayout();
            }

            @Override
            public void onError(BuzzScreenClient.CheckAvailabilityError error) {
                switch (error) {
                    case HOST_APP_NOT_INSTALLED:
                        showErrorLayout(
                                R.string.main_error_install_title,
                                R.string.main_error_install_message,
                                R.string.main_error_install_button,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        launchMainAppMarket();
                                    }
                                });
                        break;
                    case HOST_NOT_SUPPORTED_VERSION:
                        showErrorLayout(
                                R.string.main_error_update_title,
                                R.string.main_error_update_message,
                                R.string.main_error_update_button,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        launchMainAppMarket();
                                    }
                                });
                        break;
                    case NOT_ENOUGH_USER_INFO:
                        showErrorLayout(
                                R.string.main_error_profile_title,
                                R.string.main_error_profile_message,
                                R.string.main_error_profile_button,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (TextUtils.isEmpty(App.DEEP_LINK_ONBOARDING)) {
                                            launchApp(App.MAIN_APP_PACKAGE);
                                        } else {
                                            launchLink(App.DEEP_LINK_ONBOARDING);
                                        }
                                    }
                                });
                        break;
                    case UNKNOWN_ERROR:
                        showErrorLayout(
                                R.string.main_error_unknown_title,
                                R.string.main_error_unknown_message,
                                R.string.main_error_unknown_button,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        finish();
                                    }
                                });
                        break;
                }
            }
        });
    }

    private void showLoading() {
        tvTitle.setText(R.string.main_loading);
        switchLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        profileLayout.setVisibility(View.GONE);
        topLayout.setBackgroundColor(getResources().getColor(R.color.topDark));
        pbLoading.setVisibility(View.VISIBLE);
    }

    private void showErrorLayout(int titleResId, int messageResId, int btnResId, View.OnClickListener buttonClickListener) {
        pbLoading.setVisibility(View.GONE);
        ivStatus.setImageResource(R.drawable.ic_circle_warning);
        errorLayout.setVisibility(View.VISIBLE);
        tvTitle.setText(titleResId);
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setText(messageResId);
        ((Button) findViewById(R.id.main_error_button)).setText(btnResId);
        ((Button) findViewById(R.id.main_error_button)).setOnClickListener(buttonClickListener);
    }

    private void showSwitchLayout() {
        pbLoading.setVisibility(View.GONE);
        tvMessage.setVisibility(View.VISIBLE);
        switchLayout.setVisibility(View.VISIBLE);
        updateSwitchLayout();
        profileLayout.setVisibility(View.VISIBLE);
        updateProfileLayout();
    }

    private void updateSwitchLayout() {
        if (BuzzScreen.getInstance().isActivated() && !BuzzScreen.getInstance().isSnoozed()) {
            ivStatus.setImageResource(R.drawable.ic_circle_check);
            topLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btnSwitchOn.setVisibility(View.GONE);
            btnSwitchOff.setVisibility(View.VISIBLE);
            tvTitle.setText(R.string.main_switch_on_status);
        } else {
            ivStatus.setImageResource(R.drawable.ic_circle_x);
            topLayout.setBackgroundColor(getResources().getColor(R.color.topDark));
            btnSwitchOn.setVisibility(View.VISIBLE);
            btnSwitchOff.setVisibility(View.GONE);
            tvTitle.setText(R.string.main_switch_off_status);
        }
        updateSnoozeMessage();
    }

    private void updateProfileLayout() {
        UserProfile userProfile = BuzzScreen.getInstance().getUserProfile();
        ((TextView) findViewById(R.id.profile_user_id)).setText("ID : " + userProfile.getUserId());
        ((TextView) findViewById(R.id.profile_birth_year)).setText("Birth year : " + userProfile.getBirthYear());
        ((TextView) findViewById(R.id.profile_gender)).setText("Gender : " + userProfile.getGender());
        ((TextView) findViewById(R.id.profile_region)).setText("Region : " + userProfile.getRegion());
    }

    @Override
    protected void onPause() {
        super.onPause();
        buzzScreenClient.pause();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void showSnoozeSettings() {
        snoozeSelectedPosition = 0;
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.main_snooze_title);
        adb.setSingleChoiceItems(R.array.snooze_strings, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                snoozeSelectedPosition = i;
            }
        });
        adb.setNegativeButton(android.R.string.cancel, null);
        adb.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (snoozeSelectedPosition) {
                    case 0:
                        BuzzScreen.getInstance().snooze(60 * 60 * 2);
                        break;
                    case 1:
                        BuzzScreen.getInstance().snooze(60 * 60 * 6);
                        break;
                    case 2:
                        BuzzScreen.getInstance().snooze(60 * 60 * 24);
                        break;
                    case 3:
                        BuzzScreen.getInstance().deactivate();
                        break;
                }
                updateSwitchLayout();
            }
        });
        adb.show();
    }

    // TODO localization
    private void updateSnoozeMessage() {
        if (BuzzScreen.getInstance().isSnoozed()) {
            int snoozeTo = BuzzScreen.getInstance().getSnoozeTo();
            Date dtSnoozeTo = new Date(snoozeTo * 1000L);
            String time = new SimpleDateFormat("MMM d일 h:mm aa", Locale.getDefault()).format(dtSnoozeTo);
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(time + "에 켜집니다");
        } else {
            tvMessage.setVisibility(View.GONE);
        }
    }

    private void launchMainAppMarket() {
        try {
            launchLink("market://details?id=" + App.MAIN_APP_PACKAGE);
        } catch (ActivityNotFoundException e) {
            launchLink("https://play.google.com/store/apps/details?id=" + App.MAIN_APP_PACKAGE);
        }
    }

    private void launchApp(String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void launchLink(String actionLink) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(actionLink));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showTermAgreeDialog() {
        dialog = new AlertDialog.Builder(MainActivity.this)
                .setMessage("잠금화면 이용약관 동의")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        App.setTermAgree(true);
                        BuzzScreen.getInstance().activate();
                        updateSwitchLayout();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create();
        dialog.show();
    }
}
