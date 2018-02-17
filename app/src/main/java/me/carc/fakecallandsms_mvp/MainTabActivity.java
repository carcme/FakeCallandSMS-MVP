package me.carc.fakecallandsms_mvp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Random;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.cketti.library.changelog.ChangeLog;
import me.carc.fakecallandsms_mvp.adapter.TabbedPagerAdapter;
import me.carc.fakecallandsms_mvp.alarm.AlarmHelper;
import me.carc.fakecallandsms_mvp.app.App;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.CallLogUtil;
import me.carc.fakecallandsms_mvp.common.TinyDB;
import me.carc.fakecallandsms_mvp.common.utils.Common;
import me.carc.fakecallandsms_mvp.common.utils.U;
import me.carc.fakecallandsms_mvp.db.AppDatabase;
import me.carc.fakecallandsms_mvp.fragments.CarcFragment;
import me.carc.fakecallandsms_mvp.fragments.FakeCallFragment;
import me.carc.fakecallandsms_mvp.fragments.FakeSmsFragment;
import me.carc.fakecallandsms_mvp.fragments.PendingFragment;
import me.carc.fakecallandsms_mvp.fragments.SettingsFragment;
import me.carc.fakecallandsms_mvp.model.FakeContact;

public class MainTabActivity extends Base implements
        FakeCallFragment.FakeCallListener,
        FakeSmsFragment.FakeSmsListener {

    private static final String TAG = MainTabActivity.class.getName();
    private static final String EXTRA_RESTART_STR = "EXTRA_RESTART_STR";
    public static final int PERMISSION_CONTACT_RESULT = 1500;
    public static final int INTENT_SMS_PACKAGE = 101;
    private FirebaseAnalytics mFirebaseAnalytics;

    TinyDB tinyDb;
    FakeContact fakeContact;

    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.container)
    ViewPager mViewPager;
    @BindView(R.id.fabMain)
    FloatingActionButton fabMain;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * SMS Callbacks
     */

    @Override
    public void onSmsContact(String name, String number, String image) {
        if(fakeContact == null) {
            Answers.getInstance().logCustom(new CustomEvent("onSmsContact:: FakeContact is null"));
            fakeContact = new FakeContact();
        }
        fakeContact.setName(name);
        fakeContact.setNumber(number);
        fakeContact.setImage(image);
    }

    @Override
    public void onSmsMessage(String msg) {
        fakeContact.setSmsMsg(msg);
    }

    @Override
    public void onSmsType(String location) {
        fakeContact.setSmsType(location);
    }

    @Override
    public void onSmsTime(long time) {
        fakeContact.setTime(time);
    }


    /**
     * Call Callbacks
     */

    @Override
    public void onSetContactDetails(String name, String number, String image) {
        if(fakeContact == null) {
            Answers.getInstance().logCustom(new CustomEvent("onSetContactDetails:: FakeContact is null"));
            fakeContact = new FakeContact();
        }
        fakeContact.setName(name);
        fakeContact.setNumber(number);
        fakeContact.setImage(image);
    }

    @Override
    public void onSetDate(long time) {
        fakeContact.setDate(time);
    }

    @Override
    public void onSetTime(long time) {
        fakeContact.setTime(time);
    }

    @Override
    public void onSetRingtone(Uri ringtone) {
        fakeContact.setRingtone(ringtone.toString());
    }

    @Override
    public void onSetVibrate(boolean vibrate) {
        fakeContact.setVibrate(vibrate);
    }

    @Override
    public void onSetCallLogs(boolean log) {
        fakeContact.setUseCallLogs(log);
    }

    @Override
    public void onSetCallType(int type) {
        fakeContact.setCallType(type);
    }

    @OnClick(R.id.fabMain)
    public void startSend() {
        Log.i(TAG, "startSend: "+fakeContact.debug());

        switch (mViewPager.getCurrentItem()) {
            case 0:

                if (fakeContact.getCallType() == C.CALL_INCOMING) {
                    String constant = tinyDb.getString(C.PREF_MAX_CALL_DURATION, String.valueOf(C.MAX_CALL_DURATION_DEFAULT));
                    fakeContact.setDuration(TextUtils.isEmpty(constant) ? C.MAX_CALL_DURATION_DEFAULT : Integer.valueOf(constant));

                    addCallToDb();

                    Bundle params = new Bundle();
                    params.putString("ACTION", "generate Telephone Call");
                    mFirebaseAnalytics.logEvent("App_Usage", params);

                } else {
                    generateCallLog();
                }

                break;

            case 1:

                if (Common.isEmpty(fakeContact.getNumber()) || Common.isEmpty(fakeContact.getSmsMsg())) {
                    Snackbar snackbar = Snackbar.make(fabMain, "Please check you have entered a message and selected a contact", Snackbar.LENGTH_LONG).setAction("Action", null);
                    View view = snackbar.getView();
                    view.setBackgroundColor(ContextCompat.getColor(this, R.color.accent));
                    snackbar.show();

                } else {
                    String defaultSMSPackage = Telephony.Sms.getDefaultSmsPackage(MainTabActivity.this);
                    if (defaultSMSPackage != null && defaultSMSPackage.equals(getPackageName())) {
                        writeSms();
                    } else {
                        switchSmSPackage();
                    }
                }
                break;

            default:
                Snackbar snackbar = Snackbar.make(fabMain, R.string.wrong_screen_error_msg, Snackbar.LENGTH_LONG).setAction("Action", null);
                View view = snackbar.getView();
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.accent));
                snackbar.show();
        }
    }

    private void generateCallLog() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            Random r = new Random();
            int min = 4000;
            int max = 30000;
            int duration = r.nextInt(max - min + 1) + min;

            CallLogUtil.addCallToLog(
                    getApplicationContext(),
                    Common.isEmpty(fakeContact.getName()) ? "Unknown" : fakeContact.getName(),
                    Common.isEmpty(fakeContact.getNumber()) ? "Unknown" : fakeContact.getNumber(),
                    duration,
                    fakeContact.getCallType(),
                    fakeContact.getTime() != 0 ? fakeContact.getTime() : System.currentTimeMillis());

            Snackbar snackbar = Snackbar.make(fabMain, "Call added to call history", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.BLACK)
                    .setAction("Show", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent showCallLog = new Intent();
                            showCallLog.setAction(Intent.ACTION_VIEW);
                            showCallLog.setType(CallLog.Calls.CONTENT_TYPE);
                            startActivity(showCallLog);
                        }
                    });
            View view = snackbar.getView();
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.gcc_green_1));
            snackbar.show();

            Bundle params = new Bundle();
            params.putString("ACTION", "generate Call Log");
            mFirebaseAnalytics.logEvent("App_Usage", params);

        } else {
            final String[] LOCATION_PERMISSIONS = {Manifest.permission.WRITE_CALL_LOG};
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, FakeCallFragment.PERMISSION_WRITE_CALL_LOG_RESULT);
        }
    }

    private void addCallToDb() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = ((App) getApplicationContext()).getDB();

                fakeContact.setIndex(U.currentTimeInteger(fakeContact.getTime()));
                fakeContact.setDatabaseType("CALL (RECEIVE)");

                if(fakeContact.getTime() > 0)
                    db.fakeContactDao().insert(fakeContact);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addToPendingFragment(fakeContact);
                        AlarmHelper.getInstance().setCallAlarmActivity(fakeContact);
                        restartActivity(R.string.fake_call_started);
                    }
                });
            }
        });
    }

    private void addSmsToDb() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = ((App) getApplicationContext()).getDB();

                fakeContact.setIndex(U.currentTimeInteger(fakeContact.getTime()));
                fakeContact.setDatabaseType("SMS (RECEIVE)");
                AlarmHelper.getInstance().setSmsAlarmActivity(fakeContact);

                db.fakeContactDao().insert(fakeContact);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addToPendingFragment(fakeContact);
                        restartActivity(R.string.sms_scheduled);
                    }
                });
            }
        });
    }

    private void restartActivity(@StringRes int stringRes) {
        finish();
        if(!TinyDB.getTinyDB().getBoolean(C.PREF_FINISH_ACTIVITY_AFTER_CALL_SET)) {
            Intent intent = new Intent(MainTabActivity.this, MainTabActivity.class);
            intent.putExtra(EXTRA_RESTART_STR, stringRes);
            startActivity(intent);
        }
    }

    private void switchSmSPackage() {
        // Save the default sms package
        if(Telephony.Sms.getDefaultSmsPackage(this) != null)
            tinyDb.putString(C.SMS_DEFAULT_PACKAGE_KEY, Telephony.Sms.getDefaultSmsPackage(this));

        //Change the default sms app to my app
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
        startActivityForResult(intent, INTENT_SMS_PACKAGE);
    }


    //Write to the default sms app
    private void writeSms() {
        Bundle params = new Bundle();
        params.putString("ACTION", "Send SMS");
        mFirebaseAnalytics.logEvent("App_Usage", params);

        addSmsToDb();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        fakeContact = new FakeContact();
        tinyDb = new TinyDB(this);

        if (C.HAS_M)
            accessAllowed();
        else
            init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().hasExtra(EXTRA_RESTART_STR)) {
            getIntent().removeExtra(EXTRA_RESTART_STR);
            int stringId = getIntent().getIntExtra(EXTRA_RESTART_STR, R.string.fake_call_started);

            if(fabMain == null)
                Log.e(TAG, "onResume: Butterknife didn't work... fabMain is null");
            else {
                Snackbar snackbar = Snackbar.make(fabMain, stringId, Snackbar.LENGTH_LONG);
                View view = snackbar.getView();
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.gcc_green_1));
                snackbar.show();
            }
        }

        ChangeLog cl = new ChangeLog(this);
        if (cl.isFirstRun()) {
            cl.getLogDialog().show();
        }
//        else if(BuildConfig.DEBUG) cl.getLogDialog().show();
    }

    /**
     * Initialise the view - may require permissions first
     */
    private void init() {
        setSupportActionBar(toolbar);
        setupViewPager();
        showRatingDialog();
    }

    private void showRatingDialog() {
        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .icon(ContextCompat.getDrawable(this, R.mipmap.ic_launcher))
                .session(7)
                .threshold(3)
                .title(getString(R.string.rating_dialog_experience))
                .titleTextColor(R.color.black)
                .positiveButtonText("Not Now")
                .negativeButtonText("Never")
                .positiveButtonTextColor(R.color.white)
                .negativeButtonTextColor(R.color.grey_500)
                .formTitle("Submit Feedback")
                .formHint(getString(R.string.rating_dialog_suggestions))
                .formSubmitText("Submit")
                .formCancelText("Cancel")
                .ratingBarColor(R.color.rating_star)
                .onThresholdFailed(new RatingDialog.Builder.RatingThresholdFailedListener() {
                    @Override
                    public void onThresholdFailed(RatingDialog ratingDialog, float rating, boolean thresholdCleared) {
                        feedbackForm();
                        ratingDialog.dismiss();
                    }
                })
                .onRatingChanged(new RatingDialog.Builder.RatingDialogListener() {
                    @Override
                    public void onRatingSelected(float rating, boolean thresholdCleared) {

                    }
                })
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {

                    }
                }).build();

        ratingDialog.show();
    }


    private void feedbackForm() {
        U.emailFeedbackForm(this);
    }

    /**
     * Check permissions previously granted
     */
    private void accessAllowed() {
        if (PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS))
            init();
        else
            requestPermissions();
    }


    /**
     * Request permissions if required
     */
    private void requestPermissions() {
        final String[] LOCATION_PERMISSIONS = {Manifest.permission.READ_CONTACTS};
        ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, PERMISSION_CONTACT_RESULT);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_CONTACT_RESULT:
                init();
                break;

            case FakeCallFragment.PERMISSION_WRITE_CALL_LOG_RESULT:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        generateCallLog();
                    } else {
                        Snackbar snackbar = Snackbar.make(fabMain, "Permission required to add to Call Logs", Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.BLACK)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        generateCallLog();
                                    }
                                });
                        View view = snackbar.getView();
                        view.setBackgroundColor(ContextCompat.getColor(this, R.color.accent));
                        snackbar.show();
                    }
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void addToPendingFragment(FakeContact contact) {
        PendingFragment pending = (PendingFragment) ((TabbedPagerAdapter) mViewPager.getAdapter()).getItem(2);
        pending.addNewPending(contact);
    }

    /**
     * Setup the pager
     */
    private void setupViewPager() {
        TabbedPagerAdapter mSectionsPagerAdapter = new TabbedPagerAdapter(getSupportFragmentManager());

        mSectionsPagerAdapter.addFragment(new FakeCallFragment(), "Call");
        mSectionsPagerAdapter.addFragment(new FakeSmsFragment(), "SMS");
        mSectionsPagerAdapter.addFragment(new PendingFragment(), "Schedule");
        mSectionsPagerAdapter.addFragment(new SettingsFragment(), "Settings");
        mSectionsPagerAdapter.addFragment(new CarcFragment(), "Extras");

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(onPagerChangedListener);
    }


    private ViewPager.SimpleOnPageChangeListener onPagerChangedListener = new ViewPager.SimpleOnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            int pos = mViewPager.getCurrentItem();
            if (pos == 0 || pos == 1)
                fabMain.show();
            else
                fabMain.hide();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
        }

        public void onPageSelected(int pos) {
            super.onPageSelected(pos);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_tab, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.action_start:
                startSend();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: " + requestCode);

        if (requestCode == INTENT_SMS_PACKAGE && resultCode == RESULT_OK) {

            final String myPackageName = getPackageName();
            if (Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {

                //Write to the default sms app
                writeSms();
            }
        } else
            super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (TinyDB.getTinyDB().getBoolean(C.PREF_RESET_SMS_ON_EXIT)) {
            String defaultSMS = TinyDB.getTinyDB().getString(C.SMS_DEFAULT_PACKAGE_KEY);
            if (defaultSMS != null && Telephony.Sms.getDefaultSmsPackage(this).equals(getPackageName())) {
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSMS);
                startActivity(intent);
            }
        }
        super.onDestroy();
    }
}
