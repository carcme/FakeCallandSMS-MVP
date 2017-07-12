package me.carc.fakecallandsms_mvp.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.TinyDB;
import me.carc.fakecallandsms_mvp.common.utils.Algorithms;
import me.carc.fakecallandsms_mvp.common.utils.U;


public class SettingsFragment extends Fragment {


    private static final int FILE_SELECT = 1002;

    @BindView(R.id.smsAppResetBtn)
    Button smsAppResetBtn;

    @BindView(R.id.voiceFileInput)
    Button voiceFileInput;

    @BindView(R.id.hideLauncherIconSW)
    Switch hideLauncherIconSW;

    @BindView(R.id.resetSmsAppSW)
    Switch resetSmsAppSW;

    @BindView(R.id.quickTimeInput)
    TextView quickTimeInput;

    @BindView(R.id.launchCodeInput)
    TextView launchCodeInput;

    @BindView(R.id.callDurationInput)
    TextView callDurationInput;


    private View rootView;

    private void showDlg(String title, String text, final TextView view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);

        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.alert_layout, (ViewGroup) getView(), false);
        // Set up the input
        ((TextView) viewInflated.findViewById(R.id.text)).setText(text);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        input.setHint(view.getText());

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                view.setText(input.getText().toString());
                updatePref(view);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void updatePref(View v) {

        TinyDB db = TinyDB.getTinyDB();

        switch (v.getId()) {
            case R.id.callDurationInput:
                db.putString(C.PREF_MAX_CALL_DURATION, callDurationInput.getText().toString());
                break;

            case R.id.quickTimeInput:
                db.putString(C.PREF_QUICK_TIME, quickTimeInput.getText().toString());
                break;

            case R.id.launchCodeInput:
                db.putString(C.PREF_DIAL_LAUNCHER, launchCodeInput.getText().toString());
                break;
        }
    }


    @OnClick(R.id.callDurationInput)
    void setCallDuration() {
        showDlg("Call Duration", "Set the maximum duration of an incoming call (Seconds)", callDurationInput);
    }


    @OnClick(R.id.quickTimeInput)
    void setQuickTime() {
        showDlg("Call Duration", "Set the quick start time (Minutes)", quickTimeInput);
    }


    @OnClick(R.id.launchCodeInput)
    void setLaunchCode() {
        showDlg("Call Duration", "Start app from dialer with this number", launchCodeInput);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.settings_fragment, container, false);
        ButterKnife.bind(this, rootView);

        setDefaultValues();

        return rootView;
    }

    private void setDefaultValues() {

        TinyDB db = TinyDB.getTinyDB();
        int constant;
        String defaultValue;

        // DIAL PAD CODE
        String code = db.getString(C.PREF_DIAL_LAUNCHER);
        if (Algorithms.isEmpty(code)) {
            launchCodeInput.setHint(C.DIAL_PAD_LAUNCH_DEF_CODE);
        } else {
            launchCodeInput.setEnabled(true);
            launchCodeInput.setText(code);
        }

        // VOICE FILE
        voiceFileInput.setText(db.getString(C.PREF_BACKGROUND_VOICE_DISPLAY, getString(R.string.voice)));

        /// CALL DURATION
        constant = C.MAX_CALL_DURATION_DEFAULT;
        defaultValue = String.valueOf(constant);
        callDurationInput.setText(db.getString(C.PREF_MAX_CALL_DURATION, defaultValue));

        // QUICK TIME
        constant = C.QUICK_TIME_DEFAULT;
        defaultValue = String.valueOf(constant);
        quickTimeInput.setText(db.getString(C.PREF_QUICK_TIME, defaultValue));


        // Reset default app on exit
        resetSmsAppSW.setChecked(db.getBoolean(C.PREF_RESET_SMS_ON_EXIT));


        // HIDE LAUNCHER
        hideLauncherIconSW.setChecked(db.getBoolean(C.PREF_SHOW_LAUNCHER_ICON));
    }


    @OnClick(R.id.smsAppResetBtn)
    void resetSMSApp() {
        if (Telephony.Sms.getDefaultSmsPackage(getActivity()).equals(getActivity().getPackageName())) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                    TinyDB.getTinyDB().getString(C.SMS_DEFAULT_PACKAGE_KEY));
            startActivity(intent);
        }
    }


    @OnClick(R.id.voiceFileInput)
    void attachVoiceFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, FILE_SELECT);
    }


    @OnClick(R.id.hideLauncherIconSW)
    void hideLaunchIcon() {

        boolean hideIcon = hideLauncherIconSW.isChecked();

        if (hideIcon) {
            showLauncher(PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
            Snackbar.make(rootView, "A launcher restart may be required for this to take effect", Toast.LENGTH_SHORT).show();
        } else {
            showLauncher(PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
        }

        TinyDB.getTinyDB().putBoolean(C.PREF_SHOW_LAUNCHER_ICON, hideIcon);

    }


    @OnClick(R.id.resetSmsAppSW)
    void resetSmsAppOnExit() {

        boolean resetOnExit = resetSmsAppSW.isChecked();

        if(resetOnExit)
            Snackbar.make(rootView, "Scheduled messages CAN NOT be added to the default SMS app", Toast.LENGTH_SHORT).show();

        TinyDB.getTinyDB().putBoolean(C.PREF_RESET_SMS_ON_EXIT, resetOnExit);
    }


    private void showLauncher(int state) {
        final ComponentName LAUNCHER_COMPONENT_NAME = new ComponentName("me.carc.fakecallandsms_mvp",
                "me.carc.fakecallandsms_mvp.Launcher");
        PackageManager packageManager = getActivity().getPackageManager();
        packageManager.setComponentEnabledSetting(LAUNCHER_COMPONENT_NAME, state, PackageManager.DONT_KILL_APP);
    }


    @OnTextChanged(R.id.quickTimeInput)
    void onQuickTimeTextChanged(CharSequence msg, int start) {
//        TinyDB.getTinyDB().putString(C.PREF_QUICK_TIME, msg.toString());
    }


    @OnTextChanged(R.id.launchCodeInput)
    void onLaunchCodeTextChanged(CharSequence msg, int start) {
        TinyDB.getTinyDB().putString(C.PREF_DIAL_LAUNCHER, msg.toString());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case FILE_SELECT:
                if (resultCode == Activity.RESULT_OK) {
                    String path = U.getPath(getActivity(), Uri.parse(data.getDataString()));

                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(path);

                    String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

                    if(Algorithms.isEmpty(artist) && Algorithms.isEmpty(title))
                        voiceFileInput.setText(U.fileNameFromUrl(path));
                    else
                        voiceFileInput.setText(artist + " | " + title);

                    TinyDB.getTinyDB().putString(C.PREF_BACKGROUND_VOICE_DISPLAY, voiceFileInput.getText().toString());
                    TinyDB.getTinyDB().putString(C.PREF_BACKGROUND_VOICE, path);

                }
                break;
        }
    }
}