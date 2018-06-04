package me.carc.fakecallandsms_mvp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.crashlytics.android.answers.Answers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.services.common.Crash;
import me.carc.fakecallandsms_mvp.BuildConfig;
import me.carc.fakecallandsms_mvp.MainTabActivity;
import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.TinyDB;
import me.carc.fakecallandsms_mvp.common.utils.CalendarHelper;
import me.carc.fakecallandsms_mvp.common.utils.U;
import me.carc.fakecallandsms_mvp.common.utils.ViewUtils;

/**
 * Fake Call Module
 *
 * Created by bamptonm on 7/3/17.
 */

public class FakeCallFragment extends Fragment {

    private static final String TAG = FakeCallFragment.class.getName();
    public static final String TAG_ID = "FakeCallFragment";

    public static final int PENDING_INTENT_RINGTONE = 6000;

    public interface FakeCallListener {
        void onSetContactDetails(String contactName, String number, String image);
        void onSetRingtone(Uri ringtone);
        void onSetVibrate(boolean vibrate);
        void onSetCallLogs(boolean vibrate);
        void onSetDate(long time);
        void onSetCallType(int type);
        void onSetTime(long time);
    }

    private FakeCallListener callListener;

    final Calendar calendarInst = Calendar.getInstance();
    Calendar calPicker = Calendar.getInstance();

    @BindView(R.id.contactBtn) Button contactBtn;
    @BindView(R.id.ringtoneBtn) Button ringtoneBtn;
    @BindView(R.id.toggleVibrate) ToggleButton toggleVibrate;
    @BindView(R.id.toggleLogs) ToggleButton toggleLogs;
    @BindView(R.id.dateBtn) Button dateBtn;
    @BindView(R.id.timeBtn) Button timeBtn;
    @BindView(R.id.quickTimeBtn) ToggleButton quickTimeBtn;
    @BindView(R.id.callTypeRadioGroup) RadioGroup callTypeRadioGroup;


    public DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calPicker.set(year, month, dayOfMonth);
            setDateButton();
            callListener.onSetTime(calPicker.getTime().getTime());
        }
    };

    public TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int min) {
            calPicker.set(calPicker.get(Calendar.YEAR), calPicker.get(Calendar.MONTH), calPicker.get(Calendar.DAY_OF_MONTH), hour, min);

            // reset the seconds to zero
            calPicker.set(Calendar.SECOND, 0);

            // format the time
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String time = sdf.format(calPicker.getTime());

            // reset quickTime
            quickTimeBtn.setChecked(false);
            ViewUtils.setButtonDrawableColor(getActivity(), quickTimeBtn, R.color.controlDisabled, 1);

            // set the button
            timeBtn.setText(time);
            ViewUtils.setButtonDrawableColor(getActivity(), timeBtn, R.color.controlSet, 1);

            // send time thru callback
            callListener.onSetTime(calPicker.getTime().getTime());
        }
    };


    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);

        try {
            callListener = ctx != null ? (FakeCallListener)ctx : (FakeCallListener)getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(TAG + " must implement FakeCallListener callbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fakecall_square_fragment, container, false);
        ButterKnife.bind(this, rootView);
        setRetainInstance(true);

        setDefaultValues();

        return rootView;
    }


    private void setDefaultValues() {
        TinyDB db = TinyDB.getTinyDB();

        String constant = db.getString(C.PREF_QUICK_TIME, String.valueOf(C.QUICK_TIME_DEFAULT));
        quickTimeBtn.setTextOn(String.format(getString(R.string.minutes), constant));

        // CALL LOGS ON BY DEFAULT
        ViewUtils.setButtonDrawableColor(getActivity(), toggleLogs, R.color.controlSet, 1);
    }


    @OnClick(R.id.contactBtn)
    void onContactSelectButton() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        if(U.isIntentAvailable(getActivity(), intent)) {
            intent.putExtra(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, true);
            startActivityForResult(intent, C.PICK_CONTACT);
        } else {
            try {
                intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, C.PICK_CONTACT);
            } catch (Exception e) {
                U.featureRequest(getActivity(), "Contact");
            }
        }
    }


    @OnClick(R.id.ringtoneBtn)
    void onRingtoneButton() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.select_ringtone));
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        startActivityForResult(intent, PENDING_INTENT_RINGTONE);
    }


    @OnClick(R.id.timeBtn)
    void selectTime() {
        new CalendarHelper(getActivity()).timePicker(timeListener);
    }


    @OnClick(R.id.dateBtn)
    void selectDate() {
        new CalendarHelper(getActivity()).datePicker(dateListener);
    }


    private void setDateButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
        String date = sdf.format(calPicker.getTime());
        dateBtn.setText(date);
        ViewUtils.setButtonDrawableColor(getActivity(), dateBtn, R.color.controlSet, 1);

        // reset quickTime
        quickTimeBtn.setChecked(false);
        ViewUtils.setButtonDrawableColor(getActivity(), quickTimeBtn, R.color.controlDisabled, 1);
    }


    @OnClick(R.id.quickTimeBtn)
    void setQuickTimeAdd() {

        if (quickTimeBtn.isChecked()) {
            ViewUtils.setButtonDrawableColor(getActivity(), quickTimeBtn, R.color.controlSet, 1);

            //Reset scheduler
            timeBtn.setText(R.string.time);
            ViewUtils.setButtonDrawableColor(getActivity(), timeBtn, R.color.controlDisabled, 1);

            dateBtn.setText(R.string.date);
            ViewUtils.setButtonDrawableColor(getActivity(), dateBtn, R.color.controlDisabled, 1);

            String quickTimeStr = TinyDB.getTinyDB().getString(C.PREF_QUICK_TIME, String.valueOf(C.QUICK_TIME_DEFAULT));
            if(TextUtils.isEmpty(quickTimeStr)) {
                TinyDB.getTinyDB().putString(C.PREF_QUICK_TIME, String.valueOf(C.QUICK_TIME_DEFAULT));
                quickTimeStr = String.valueOf(C.QUICK_TIME_DEFAULT);
            }
            long add7Mins = calendarInst.getTime().getTime() + (C.MINUTE_MILLIS * Integer.valueOf(quickTimeStr));
            callListener.onSetTime(add7Mins);
            callListener.onSetDate(0);
            quickTimeBtn.setTextOn(String.format(getString(R.string.minutes), quickTimeStr));
        } else {
            ViewUtils.setButtonDrawableColor(getActivity(), quickTimeBtn, R.color.controlDisabled, 1);
            callListener.onSetTime(0);
        }
    }

    @OnClick(R.id.toggleVibrate)
    void onToggleVibrate() {

        callListener.onSetVibrate(toggleVibrate.isChecked());

        if (toggleVibrate.isChecked()) {
            ViewUtils.setButtonDrawableColor(getActivity(), toggleVibrate, R.color.controlSet, 1);
        } else {
            ViewUtils.setButtonDrawableColor(getActivity(), toggleVibrate, R.color.controlDisabled, 1);
        }
    }

    @OnClick(R.id.toggleLogs)
    void onToggleLogs() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            callListener.onSetCallLogs(toggleLogs.isChecked());

            if (toggleLogs.isChecked()) {
                ViewUtils.setButtonDrawableColor(getActivity(), toggleLogs, R.color.controlSet, 1);
            } else {
                ViewUtils.setButtonDrawableColor(getActivity(), toggleLogs, R.color.controlDisabled, 1);
            }
        } else {
            final String[] LOCATION_PERMISSIONS = {Manifest.permission.WRITE_CALL_LOG};
            ActivityCompat.requestPermissions(getActivity(), LOCATION_PERMISSIONS, MainTabActivity.PERMISSION_WRITE_CALL_LOG_RESULT);
        }
    }


    @OnClick(R.id.incomingRadioButton)
    void onSetCallTypeIncoming() {
        callListener.onSetCallType(C.CALL_INCOMING);
    }


    @OnClick(R.id.outgoingRadioButton)
    void onSetCallTypeOutgoing() {
        callListener.onSetCallType(C.CALL_OUTGOING);
    }


    @OnClick(R.id.missedRadioButton)
    void onSetCallTypeMissed() {
        callListener.onSetCallType(C.CALL_MISSED);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MainTabActivity.PERMISSION_WRITE_CALL_LOG_RESULT:
                onToggleLogs();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "onActivityResult: " + requestCode);

        if (requestCode == C.PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                ContentResolver cr = getActivity().getContentResolver();
                Cursor cursor = cr.query(data.getData(), null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {

                    String name;
                    String number = "";
                    String image;

                    // get contact info
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    image = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI));

                    // get user number and image
                    if (hasPhone > 0) {  // should always be true!!
                        try {
                            number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        } catch (IllegalStateException ise) {
                            Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                            try {
                                if (cp != null && cp.moveToFirst()) {
                                    number = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    cp.close();
                                }
                            } catch (Exception e) {
                                cp.close();
                            }
                        }
                    }

                    contactBtn.setText(name);
                    ViewUtils.setButtonDrawableColor(getActivity(), contactBtn, R.color.controlSet, 1);

                    callListener.onSetContactDetails(name, number, image);
                    cursor.close();
                }
            }
        } else if (requestCode == PENDING_INTENT_RINGTONE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                if(uri != null) {
                    TinyDB.getTinyDB().putString(C.PREF_RING_TONE, uri.toString());
                    callListener.onSetRingtone(uri);
                    ringtoneBtn.setText(R.string.ringtone_set);
                    ViewUtils.setButtonDrawableColor(getActivity(), ringtoneBtn, R.color.controlSet, 1);
                } else {
                    Toast.makeText(getActivity(), "There was an error getting the ringtone path", Toast.LENGTH_SHORT).show();
                    if(BuildConfig.USE_CRASHLYTICS)
                        Answers.getInstance().onException(new Crash.LoggedException(TAG, "Uri path from intent failed"));
                }
            }
        }
    }
}


