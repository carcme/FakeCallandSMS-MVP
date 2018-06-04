package me.carc.fakecallandsms_mvp.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.fabric.sdk.android.services.common.Crash;
import me.carc.fakecallandsms_mvp.BuildConfig;
import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.utils.AndroidUtils;
import me.carc.fakecallandsms_mvp.common.utils.CalendarHelper;
import me.carc.fakecallandsms_mvp.common.utils.U;
import me.carc.fakecallandsms_mvp.common.utils.ViewUtils;
import me.carc.fakecallandsms_mvp.widgets.CircularBitmapImageViewTarget;

import static android.app.Activity.RESULT_OK;

/**
 * Fake SMS module
 * <p>
 * Created by bamptonm on 7/3/17.
 */

public class FakeSmsFragment extends Fragment {

    private static final String TAG = FakeSmsFragment.class.getName();
    public static final String TAG_ID = "FakeSmsFragment";

    Calendar calPicker = Calendar.getInstance();

    @BindView(R.id.smsMsg) EditText smsMsg;
    @BindView(R.id.attachmentBtn) ImageView attachmentBtn;
    @BindView(R.id.attachmentLoading) ContentLoadingProgressBar attachmentLoading;

    @BindView(R.id.smsNumber) EditText smsNumber;
    @BindView(R.id.contactNameTV) TextView contactNameTV;
    @BindView(R.id.smsCounter) TextView smsCounter;
    @BindView(R.id.contactBtn) ImageView contactBtn;
    @BindView(R.id.toggleInbox) ToggleButton toggleInbox;
    @BindView(R.id.toggleSent) ToggleButton toggleSent;
    @BindView(R.id.toggleDraft) ToggleButton toggleDraft;
    @BindView(R.id.dateBtn) Button dateBtn;
    @BindView(R.id.timeBtn) Button timeBtn;

    @BindView(R.id.smsSendOpts) LinearLayout smsSendOpts;

    @BindView(R.id.mmsSubject) EditText mmsSubject;


    public DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calPicker.set(year, month, dayOfMonth);
            setDateButton();
            smsListener.onSmsTime(calPicker.getTime().getTime());
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
            // set the button
            timeBtn.setText(time);
            ViewUtils.setButtonDrawableColor(getActivity(), timeBtn, R.color.controlSet, 1);

            smsListener.onSmsTime(calPicker.getTime().getTime());
        }
    };


    public interface FakeSmsListener {
        void onSmsContact(String contactName, String number, String image);
        void onMmsSubject(String subject);
        void onSmsMessage(String msg);
        void onSmsType(String location);
        void onSmsTime(long time);
        void attachmentReference(ImageView imageButton, ContentLoadingProgressBar progressBar);
    }

    private FakeSmsListener smsListener;


    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);

        try {
            smsListener = ctx != null ? (FakeSmsListener) ctx : (FakeSmsListener) getActivity();
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
        View rootView = inflater.inflate(R.layout.fakesms_square_fragment, container, false);
        ButterKnife.bind(this, rootView);
        setRetainInstance(true);
        toggleInbox.setChecked(true);
        ViewUtils.setButtonDrawableColor(getActivity(), toggleInbox, R.color.controlSet, 1);

        smsListener.attachmentReference(attachmentBtn, attachmentLoading);

        return rootView;
    }


    @OnClick(R.id.dateBtn)
    void selectDate() {
        new CalendarHelper(getActivity()).datePicker(dateListener);
    }


    @OnClick(R.id.timeBtn)
    void selectTime() {
        new CalendarHelper(getActivity()).timePicker(timeListener);
    }


    private void setDateButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
        String date = sdf.format(calPicker.getTime());
        dateBtn.setText(date);
        ViewUtils.setButtonDrawableColor(getActivity(), dateBtn, R.color.controlSet, 1);
    }

    @OnTextChanged(R.id.mmsSubject)
    void onSubjectTextChanged(CharSequence subject, int start) {
        smsListener.onMmsSubject(subject.toString());
    }

    @OnTextChanged(R.id.smsMsg)
    void onMsgTextChanged(CharSequence msg) {

        int smsCount = msg.length() / 160;
        int charCount = msg.length() % 160;
        String output = smsCount + "/" + charCount;
        smsCounter.setText(output);

        smsListener.onSmsMessage(msg.toString());
    }

    @OnClick(R.id.attachmentBtn)
    void attachImage() {
        AndroidUtils.hideSoftKeyboard(getActivity(), attachmentBtn);

        ImageSourceFragment imageSrc = new ImageSourceFragment();
        this.getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, imageSrc, ImageSourceFragment.TAG_ID)
                .addToBackStack(ImageSourceFragment.TAG_ID)
                .commit();
    }

    @OnTextChanged(R.id.smsNumber)
    void onNumberTextChanged(CharSequence msg, int start) {

        // reset contact button and
//        contactName.setVisibility(View.GONE);
        if (start > 0)
            contactNameTV.setText("");
        //contactBtn.setText(R.string.contacts);
//        ViewUtils.setButtonDrawableColor(getActivity(), contactBtn, R.color.controlDisabled, 1);

        smsListener.onSmsContact("", msg.toString(), "");
    }


    @OnClick({R.id.contactBtn, R.id.contactNameTV})
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


    @OnClick(R.id.toggleInbox)
    void onToggleInbox() {

        if (toggleInbox.isChecked()) {
            smsListener.onSmsType(C.SMS_INBOX);
            ViewUtils.setButtonDrawableColor(getActivity(), toggleInbox, R.color.controlSet, 1);
        } else {
            ViewUtils.setButtonDrawableColor(getActivity(), toggleInbox, R.color.controlDisabled, 1);
        }

        toggleSent.setChecked(false);
        toggleDraft.setChecked(false);
        ViewUtils.setButtonDrawableColor(getActivity(), toggleSent, R.color.controlDisabled, 1);
        ViewUtils.setButtonDrawableColor(getActivity(), toggleDraft, R.color.controlDisabled, 1);
    }


    @OnClick(R.id.toggleSent)
    void onToggleSent() {

        if (toggleSent.isChecked()) {
            ViewUtils.setButtonDrawableColor(getActivity(), toggleSent, R.color.controlSet, 1);
            smsListener.onSmsType(C.SMS_SENT);
        } else {
            ViewUtils.setButtonDrawableColor(getActivity(), toggleSent, R.color.controlDisabled, 1);
        }
        toggleInbox.setChecked(false);
        toggleDraft.setChecked(false);
        ViewUtils.setButtonDrawableColor(getActivity(), toggleInbox, R.color.controlDisabled, 1);
        ViewUtils.setButtonDrawableColor(getActivity(), toggleDraft, R.color.controlDisabled, 1);
    }


    @OnClick(R.id.toggleDraft)
    void onToggleDraft() {

        if (toggleDraft.isChecked()) {
            ViewUtils.setButtonDrawableColor(getActivity(), toggleDraft, R.color.controlSet, 1);
            smsListener.onSmsType(C.SMS_DRAFT);
        } else {
            ViewUtils.setButtonDrawableColor(getActivity(), toggleDraft, R.color.controlDisabled, 1);
        }
        toggleInbox.setChecked(false);
        toggleSent.setChecked(false);
        ViewUtils.setButtonDrawableColor(getActivity(), toggleInbox, R.color.controlDisabled, 1);
        ViewUtils.setButtonDrawableColor(getActivity(), toggleSent, R.color.controlDisabled, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "onActivityResult: " + requestCode);

        if (requestCode == C.PICK_CONTACT && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Cursor cursor = getActivity().getContentResolver().query(data.getData(), null, null, null, null);
                if(cursor != null) {
                    if (cursor.moveToFirst()) {
                        // Fetch other Contact details to use
                        try {
                            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String image = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI));

                            if(!TextUtils.isEmpty(image)) {
                                Glide.with(this)
                                        .load(image)
                                        .asBitmap()
                                        .centerCrop()
                                        .error(R.drawable.ic_person)
                                        .into(new CircularBitmapImageViewTarget(getActivity(), contactBtn));
                           } else {
                                contactBtn.setImageResource(R.drawable.ic_person);
                                contactBtn.setScaleType(ImageView.ScaleType.CENTER);
                            }

                            // show the contact name
                            contactNameTV.setText(name);

                            // set the contact number
                            smsNumber.setText(number);
                            smsListener.onSmsContact(name, number, image);
                        } catch (IllegalStateException e) {
                            Toast.makeText(getActivity(), "This contact is not valid", Toast.LENGTH_SHORT).show();
                        }
                    }
                    cursor.close();
                } else {
                    Toast.makeText(getActivity(), "There was an error getting the Contact information", Toast.LENGTH_SHORT).show();
                    if(BuildConfig.USE_CRASHLYTICS)
                        Answers.getInstance().onException(new Crash.LoggedException(TAG, "cursor not NULL but can not moveToFirst()"));
                }
            }
        }
    }
}


