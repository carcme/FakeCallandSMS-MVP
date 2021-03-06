package me.carc.fakecallandsms_mvp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.fabric.sdk.android.services.common.Crash;
import me.carc.fakecallandsms_mvp.BuildConfig;
import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.TinyDB;
import me.carc.fakecallandsms_mvp.common.utils.Common;
import me.carc.fakecallandsms_mvp.common.utils.NotificationUtils;
import me.carc.fakecallandsms_mvp.common.utils.U;
import me.carc.fakecallandsms_mvp.common.utils.ViewUtils;

public class SettingsFragment extends Fragment {
    private static final String TAG = SettingsFragment.class.getName();
    public static final String TAG_ID = "SettingsFragment";

    private static final int PENDING_INTENT_SMS_RINGTONE = 6001;
    private static final int NOTIFICATION_CHANGE = 6002;

    public static final String PREF_FINISH_ACTIVITY_AFTER_CALL_SET = "PREF_FINISH_ACTIVITY_AFTER_CALL_SET";

    public static final int PERMISSION_STORAGE_RESULT = 1501;
    private static final int VOICE_FILE_SELECT = 1002;

    @BindView(R.id.smsAppResetBtn) Button smsAppResetBtn;
    @BindView(R.id.voiceFileInput) Button voiceFileInput;
    @BindView(R.id.smsRingtone) Button smsRingtone;
    @BindView(R.id.closeAfterCallSetSw) Switch closeAfterCallSetSw;
    @BindView(R.id.hideLauncherIconSW) Switch hideLauncherIconSW;
    @BindView(R.id.resetSmsAppSW) Switch resetSmsAppSW;
    @BindView(R.id.quickTimeInput) TextView quickTimeInput;
    @BindView(R.id.launchCodeInput) TextView launchCodeInput;
    @BindView(R.id.callDurationInput) TextView callDurationInput;

    private View rootView;

    private void showDlg(String title, String text, final TextView view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle(title);

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.alert_layout, (ViewGroup) getView(), false);
        ((TextView) viewInflated.findViewById(R.id.text)).setText(text);
        final EditText input = viewInflated.findViewById(R.id.input);
        builder.setView(viewInflated);
        input.setHint(view.getText());

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(numberSanityCheck(input.getText().toString())) {
                    view.setText(input.getText().toString());
                    updatePref(view);
                }
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

    private boolean numberSanityCheck(String strNummber) {
        try {
            int ignored = Integer.valueOf(strNummber);
            return true;
        } catch (NumberFormatException nfe) {
            Toast.makeText(getActivity(), "That number is too large or just plain invalid :/", Toast.LENGTH_SHORT).show();
        }
        return false;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.settings_fragment, container, false);
        ButterKnife.bind(this, rootView);
        setRetainInstance(true);

        setDefaultValues();

        return rootView;
    }

    private void setDefaultValues() {

        TinyDB db = TinyDB.getTinyDB();
        String defaultValue;

        // DIAL PAD CODE
        String code = db.getString(C.PREF_DIAL_LAUNCHER);
        if (TextUtils.isEmpty(code)) {
            launchCodeInput.setHint(C.DIAL_PAD_LAUNCH_DEF_CODE);
        } else {
            launchCodeInput.setEnabled(true);
            launchCodeInput.setText(code);
        }

        // VOICE FILE
        voiceFileInput.setText(db.getString(C.PREF_BACKGROUND_VOICE_DISPLAY, getString(R.string.voice)));
        if(!voiceFileInput.getText().equals(getString(R.string.voice)))
            ViewUtils.setButtonDrawableColor(getActivity(), voiceFileInput, R.color.controlSet, 1);

        // SMS RINGTONE
        smsRingtone.setText(db.getString(C.PREF_SMS_RING_TONE_DISPLAY, getString(R.string.select_sms_ringtone)));
        if(!smsRingtone.getText().equals(getString(R.string.select_sms_ringtone)))
            ViewUtils.setButtonDrawableColor(getActivity(), smsRingtone, R.color.controlSet, 1);

        /// CALL DURATION
        defaultValue = String.valueOf(C.MAX_CALL_DURATION_DEFAULT);
        callDurationInput.setText(db.getString(C.PREF_MAX_CALL_DURATION, defaultValue));

        // QUICK TIME
        defaultValue = String.valueOf(C.QUICK_TIME_DEFAULT);
        quickTimeInput.setText(db.getString(C.PREF_QUICK_TIME, defaultValue));


        // Reset default app on exit
        resetSmsAppSW.setChecked(db.getBoolean(C.PREF_RESET_SMS_ON_EXIT));

        // auto close app
        closeAfterCallSetSw.setChecked(db.getBoolean(PREF_FINISH_ACTIVITY_AFTER_CALL_SET));


        // HIDE LAUNCHER
        hideLauncherIconSW.setChecked(db.getBoolean(C.PREF_SHOW_LAUNCHER_ICON));
    }


    @OnClick(R.id.clearVoiceFile)
    void clearVoiceToneFile() {
        voiceFileInput.setText(getString(R.string.voice));
        TinyDB.getTinyDB().remove(C.PREF_BACKGROUND_VOICE_DISPLAY);
        TinyDB.getTinyDB().remove(C.PREF_BACKGROUND_VOICE);
        ViewUtils.setButtonDrawableColor(getActivity(), voiceFileInput, android.R.color.black, 1);
    }

    @OnClick(R.id.clearSmsFile)
    void clearSmsRingtone() {
        smsRingtone.setText(getString(R.string.select_sms_ringtone));
        TinyDB.getTinyDB().remove(C.PREF_SMS_RING_TONE_DISPLAY);
        TinyDB.getTinyDB().remove(C.PREF_SMS_RING_TONE);
        ViewUtils.setButtonDrawableColor(getActivity(), smsRingtone, android.R.color.black, 1);
    }


    @OnClick(R.id.smsAppResetBtn)
    void resetSMSApp() {
        String defaultSMS = TinyDB.getTinyDB().getString(C.SMS_DEFAULT_PACKAGE_KEY);
        String currentSMS = Telephony.Sms.getDefaultSmsPackage(getActivity());

        if (!TextUtils.isEmpty(defaultSMS) && !TextUtils.isEmpty(currentSMS) && currentSMS.equals(Objects.requireNonNull(getActivity()).getPackageName())) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSMS);
            startActivity(intent);
        }
    }


    @OnClick(R.id.voiceFileInput)
    void attachVoiceFile() {
        if(requestPermissions())
            pickVoiceFile();
    }

    private void pickVoiceFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select an Audio File"), VOICE_FILE_SELECT);
//        startActivityForResult(intent, FILE_SELECT);
    }



    @OnClick(R.id.closeAfterCallSetSw)
    void autoCloseApp() {
        boolean autoClose = closeAfterCallSetSw.isChecked();

        if(autoClose)
            Snackbar.make(rootView, "App will close when scheduling a new call", Toast.LENGTH_SHORT).show();

        TinyDB.getTinyDB().putBoolean(PREF_FINISH_ACTIVITY_AFTER_CALL_SET, autoClose);
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

        String packageName = Objects.requireNonNull(getActivity()).getPackageName();
        PackageManager packageManager = Objects.requireNonNull(getActivity()).getPackageManager();

        final ComponentName LAUNCHER_COMPONENT_NAME = new ComponentName(packageName,packageName + ".Launcher");
        final ComponentName INCOG_COMPONENT_NAME = new ComponentName(packageName,packageName + ".Incognito");

        if(state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            packageManager.setComponentEnabledSetting(LAUNCHER_COMPONENT_NAME, state, PackageManager.DONT_KILL_APP);
            packageManager.setComponentEnabledSetting(INCOG_COMPONENT_NAME, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        } else if(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            packageManager.setComponentEnabledSetting(LAUNCHER_COMPONENT_NAME, state, PackageManager.DONT_KILL_APP);
            packageManager.setComponentEnabledSetting(INCOG_COMPONENT_NAME, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
    }


    @OnTextChanged(R.id.quickTimeInput)
    void onQuickTimeTextChanged(CharSequence msg, int start) {
        if(msg.length() == 0 || Integer.valueOf(msg.toString()) == 0) {
            String currentValue = TinyDB.getTinyDB().getString(C.PREF_QUICK_TIME, String.valueOf(C.QUICK_TIME_DEFAULT));
            if(currentValue.equals("0"))
                currentValue = String.valueOf(C.QUICK_TIME_DEFAULT);
            quickTimeInput.setText(currentValue);
        } else
            TinyDB.getTinyDB().putString(C.PREF_QUICK_TIME, msg.toString());
    }

    @OnTextChanged(R.id.launchCodeInput)
    void onLaunchCodeTextChanged(CharSequence msg, int start) {
        TinyDB.getTinyDB().putString(C.PREF_DIAL_LAUNCHER, msg.toString());
    }


    @OnClick(R.id.smsRingtone)
    void onRingtoneButton() {
        if (C.HAS_O) {
            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, NotificationUtils.ANDROID_CHANNEL_ID);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, Objects.requireNonNull(getActivity()).getPackageName());
            startActivityForResult(intent, NOTIFICATION_CHANGE);
        } else {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.select_ringtone));
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
            startActivityForResult(intent, PENDING_INTENT_SMS_RINGTONE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "onActivityResult: " + requestCode);

        switch (requestCode) {
            case VOICE_FILE_SELECT:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    try {
                        String path = U.getPath(getActivity(), Uri.parse(data.getDataString()));

                        if (TextUtils.isEmpty(path)) {
                            Toast.makeText(getActivity(), "Can not read file path!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Uri uri = Uri.parse(data.getDataString());

                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

                        mmr.setDataSource(getActivity(), uri);

                        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

                        if (Common.isEmpty(artist) && Common.isEmpty(title))
                            voiceFileInput.setText(U.fileNameFromUrl(path));
                        else
                            voiceFileInput.setText(String.format(getString(R.string.artist_title_divider), artist, title));

                        TinyDB.getTinyDB().putString(C.PREF_BACKGROUND_VOICE_DISPLAY, voiceFileInput.getText().toString());
                        TinyDB.getTinyDB().putString(C.PREF_BACKGROUND_VOICE, path);

                        ViewUtils.setButtonDrawableColor(getActivity(), voiceFileInput, R.color.controlSet, 1);
                    } catch (RuntimeException rte ) {
                        Toast.makeText(getActivity(), "Encountered an error parsing the voice file. Please try a different file", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case PENDING_INTENT_SMS_RINGTONE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                        if (!showSmsRingtoneName(uri)) {
                            Toast.makeText(getActivity(), "There was an error getting the ringtone path", Toast.LENGTH_SHORT).show();
                            if(BuildConfig.USE_CRASHLYTICS)
                                Answers.getInstance().onException(new Crash.LoggedException(TAG, "Uri path from intent failed"));
                        }
                    }
                }
                break;
            case NOTIFICATION_CHANGE:
                if (C.HAS_O) {
                    NotificationUtils notificationUtils = new NotificationUtils(getActivity());
                    NotificationChannel channel = notificationUtils.getChannel();
                    showSmsRingtoneName(channel.getSound());
                }
                break;
        }
    }

    private boolean showSmsRingtoneName(Uri uri) {
        if(uri == null)
            return false;

        String smsToneDisplayText;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(getActivity(), uri);

            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

            if(TextUtils.isEmpty(artist)) {
                String filePath;
                if ("content".equals(uri.getScheme())) {
                    Cursor cursor = Objects.requireNonNull(getActivity()).getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();
                    filePath = cursor.getString(0);
                    cursor.close();
                } else {
                    filePath = uri.getPath();
                }

                smsToneDisplayText = String.format(getString(R.string.sms_ringtone), title != null ? title : U.fileNameFromUrl(filePath));
            } else
                smsToneDisplayText = String.format(getString(R.string.sms_ringtone), title + " by " + artist);

        } catch (Exception e) {
            smsToneDisplayText = String.format(getString(R.string.sms_ringtone), "Unknown Title");
        }

        TinyDB.getTinyDB().putString(C.PREF_SMS_RING_TONE, uri.toString());
        TinyDB.getTinyDB().putString(C.PREF_SMS_RING_TONE_DISPLAY, smsToneDisplayText);

        smsRingtone.setText(smsToneDisplayText);
        ViewUtils.setButtonDrawableColor(getActivity(), smsRingtone, R.color.controlSet, 1);
        return true;
    }

    private boolean requestPermissions() {
        if (C.HAS_M && PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            final String[] STORAGE_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(STORAGE_PERMISSIONS, PERMISSION_STORAGE_RESULT);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_STORAGE_RESULT:
                pickVoiceFile();
                break;

            default:
                Toast.makeText(getActivity(), "Audio will not be available during calls", Toast.LENGTH_SHORT).show();
        }
    }
}