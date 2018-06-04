package me.carc.fakecallandsms_mvp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
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
import me.carc.fakecallandsms_mvp.common.ImageSize;
import me.carc.fakecallandsms_mvp.common.TinyDB;
import me.carc.fakecallandsms_mvp.common.utils.U;
import me.carc.fakecallandsms_mvp.db.AppDatabase;
import me.carc.fakecallandsms_mvp.fragments.CarcFragment;
import me.carc.fakecallandsms_mvp.fragments.FakeCallFragment;
import me.carc.fakecallandsms_mvp.fragments.FakeSmsFragment;
import me.carc.fakecallandsms_mvp.fragments.ImageSourceFragment;
import me.carc.fakecallandsms_mvp.fragments.PendingFragment;
import me.carc.fakecallandsms_mvp.fragments.SettingsFragment;
import me.carc.fakecallandsms_mvp.fragments.cloud.CloudGridActivity;
import me.carc.fakecallandsms_mvp.fragments.cloud.data.model.Photo;
import me.carc.fakecallandsms_mvp.model.FakeContact;

public class MainTabActivity extends Base implements
        FakeCallFragment.FakeCallListener,
        FakeSmsFragment.FakeSmsListener,
        ImageSourceFragment.OnSourceSelectedListener {

    private static final String TAG = MainTabActivity.class.getName();

    private static final String EXTRA_RESTART_STR = "EXTRA_RESTART_STR";
    public static final int PERMISSION_CONTACT_RESULT = 1500;
    public static final int PERMISSION_STORAGE_RESULT = 1501;
    public static final int PERMISSION_CAMERA_RESULT = 1502;
    public static final int PERMISSION_WRITE_CALL_LOG_RESULT = 1503;


    public static final int REQ_SWITCH_SMS_PACKAGE = 101;
    public static final int REQUEST_CLOUD   = 656;
    public static final int REQUEST_CAMERA  = 657;
    public static final int REQUEST_GALLERY = 658;
    public static final int UNSPLASH_ERROR  = 659;

    TinyDB tinyDb;
    private FakeContact fakeContact;

    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.container) ViewPager mViewPager;
    @BindView(R.id.fabMain) FloatingActionButton fabMain;
    @Nullable @BindView(R.id.toolbar) Toolbar toolbar;

    private WeakReference<ImageView> mAttachmentImageRef;
    private WeakReference<ContentLoadingProgressBar> mAttachmentProgressRef;

    /**
     * SMS Callbacks
     */

    @Override
    public void onSmsContact(String name, String number, String image) {
        if (fakeContact == null) {
            if (BuildConfig.USE_CRASHLYTICS)
                Answers.getInstance().logCustom(new CustomEvent("onSmsContact:: FakeContact is null"));
            fakeContact = new FakeContact();
        }
        fakeContact.setName(name);
        fakeContact.setNumber(number);
        fakeContact.setImage(image);
    }

    @Override
    public void onMmsSubject(String subject) {
        fakeContact.setMmsSubject(subject);
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

    @Override
    public void attachmentReference(ImageView imageButton, ContentLoadingProgressBar progressBar) {
        mAttachmentImageRef = new WeakReference<>(imageButton);
        mAttachmentProgressRef = new WeakReference<>(progressBar);
        fabMain.hide();
    }


    /**
     * Call Callbacks
     */

    @Override
    public void onSetContactDetails(String name, String number, String image) {
        if (fakeContact == null) {
            if (BuildConfig.USE_CRASHLYTICS)
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
        Log.i(TAG, "startSend: " + fakeContact.debug());

        switch (mViewPager.getCurrentItem()) {
            case 0:
                if (fakeContact.getCallType() == C.CALL_INCOMING) {
                    String constant = tinyDb.getString(C.PREF_MAX_CALL_DURATION, String.valueOf(C.MAX_CALL_DURATION_DEFAULT));
                    fakeContact.setDuration(TextUtils.isEmpty(constant) ? C.MAX_CALL_DURATION_DEFAULT : Integer.valueOf(constant));

                    addCallToDb();

                    if(BuildConfig.USE_CRASHLYTICS)
                        Answers.getInstance().logCustom(new CustomEvent("Start Incoming Call"));
                } else {
                    generateCallLog();
                }

                break;

            case 1:
                boolean invalidMsg = TextUtils.isEmpty(fakeContact.getSmsMsg()) &&
                        TextUtils.isEmpty(fakeContact.getMmsSubject()) &&
                        TextUtils.isEmpty(fakeContact.getAttachmentPath());

                if (TextUtils.isEmpty(fakeContact.getNumber()) || invalidMsg) {
                    Snackbar snackbar = Snackbar.make(fabMain, R.string.error_sms_params, Snackbar.LENGTH_LONG).setAction("Action", null);
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
                Snackbar snackbar = Snackbar.make(fabMain, R.string.error_wrong_screen, Snackbar.LENGTH_LONG).setAction("Action", null);
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
                    TextUtils.isEmpty(fakeContact.getName()) ? getString(R.string.shared_string_unknown) : fakeContact.getName(),
                    TextUtils.isEmpty(fakeContact.getNumber()) ? getString(R.string.shared_string_unknown) : fakeContact.getNumber(),
                    duration,
                    fakeContact.getCallType(),
                    fakeContact.getTime() != 0 ? fakeContact.getTime() : System.currentTimeMillis());

            Snackbar snackbar = Snackbar.make(fabMain, R.string.added_call_history, Snackbar.LENGTH_LONG)
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

            if(BuildConfig.USE_CRASHLYTICS)
                Answers.getInstance().logCustom(new CustomEvent("Generate Call Log"));

        } else {
            final String[] LOCATION_PERMISSIONS = {Manifest.permission.WRITE_CALL_LOG};
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, PERMISSION_WRITE_CALL_LOG_RESULT);
        }
    }

    private void addCallToDb() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = ((App) getApplicationContext()).getDB();

                fakeContact.setIndex(U.currentTimeInteger(fakeContact.getTime()));
                fakeContact.setDatabaseType("CALL (RECEIVE)");

                if (fakeContact.getTime() > 0)
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
                fakeContact.setDatabaseType(fakeContact.isMMS() ? "MMS (RECEIVE)" : "SMS (RECEIVE)");
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
        if (!TinyDB.getTinyDB().getBoolean(SettingsFragment.PREF_FINISH_ACTIVITY_AFTER_CALL_SET)) {
            Intent intent = new Intent(MainTabActivity.this, MainTabActivity.class);
            intent.putExtra(EXTRA_RESTART_STR, stringRes);
            startActivity(intent);
        }
    }

    private void switchSmSPackage() {
        // Save the default sms package
        if (Telephony.Sms.getDefaultSmsPackage(this) != null)
            tinyDb.putString(C.SMS_DEFAULT_PACKAGE_KEY, Telephony.Sms.getDefaultSmsPackage(this));

        //Change the default sms app to my app
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
        startActivityForResult(intent, REQ_SWITCH_SMS_PACKAGE);
    }


    //Write to the default sms app
    private void writeSms() {
        if(BuildConfig.USE_CRASHLYTICS)
            Answers.getInstance().logCustom(new CustomEvent("Send SMS"));
        addSmsToDb();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);

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

            if (fabMain == null)
                Log.e(TAG, "onResume: Butterknife didn't work... fabMain is null");
            else {
                Snackbar snackbar = Snackbar.make(fabMain, stringId, Snackbar.LENGTH_LONG);
                View view = snackbar.getView();
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.gcc_green_1));
                snackbar.show();
            }
        }
    }

    /**
     * Initialise the view - may require permissions first
     */
    private void init() {
        setSupportActionBar(toolbar);
        setupViewPager();
        showRatingDialog();

        ChangeLog cl = new ChangeLog(this);
        if (cl.isFirstRun())  cl.getFullLogDialog().show();
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
                .positiveButtonTextColor(R.color.gcc_red_1)
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
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS))
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

            case PERMISSION_STORAGE_RESULT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImageSourceFragment f = (ImageSourceFragment) getSupportFragmentManager().findFragmentByTag(ImageSourceFragment.TAG_ID);
                    f.flyOut(ImageSourceFragment.IMAGE_GALLERY);
                }
                break;

            case PERMISSION_CAMERA_RESULT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImageSourceFragment f = (ImageSourceFragment) getSupportFragmentManager().findFragmentByTag(ImageSourceFragment.TAG_ID);
                    f.flyOut(ImageSourceFragment.IMAGE_CAMERA);
                }
                break;

            case PERMISSION_CONTACT_RESULT:
                init();
                break;

            case PERMISSION_WRITE_CALL_LOG_RESULT:
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
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: " + requestCode);


        if (resultCode == Activity.RESULT_OK) {
            getSupportFragmentManager().popBackStack();
            fabMain.show();
        }

        switch (requestCode) {
            case REQ_SWITCH_SMS_PACKAGE:
                if (resultCode == Activity.RESULT_OK) {
                    final String myPackageName = getPackageName();
                    if (Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
                        //Write to the default sms app
                        writeSms();
                    }
                }
                break;

            case REQUEST_CLOUD:
                if (resultCode == Activity.RESULT_OK) {
                    final Photo photo = data.getParcelableExtra(CloudGridActivity.EXTRA_SELECTED_CLOUD_PHOTO);
                    mAttachmentProgressRef.get().show();
                    Glide.with(this)
                            .load(photo.getPhotoUrl(ImageSize.SCREEN[1]))  // use screen height instead of width
                            .asBitmap()
                            .error(R.drawable.ic_warning)
                            .into(new BitmapImageViewTarget(mAttachmentImageRef.get()) {
                                @Override
                                public void onResourceReady(Bitmap bitmap, @Nullable GlideAnimation anim) {
                                    super.onResourceReady(bitmap, anim);

                                    mAttachmentProgressRef.get().hide();

                                    String picturePath = TinyDB.getTinyDB().putImage(getFilesDir().toString(), photo.filename, bitmap);
                                    fakeContact.setAttachmentPath(picturePath);

                                    // TODO: 31/05/2018 test this when internet is working again!!

/*
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                        byte[] b = baos.toByteArray();
                                        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                                        TinyDB.getTinyDB().putString(ref + C.SP_CROPIMAGE, encodedImage);
*/
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                    super.onLoadFailed(e, errorDrawable);
                                    mAttachmentProgressRef.get().hide();
                                    showWarningDialog("Image load error", "The image download failed. Please check your network connection");
                                }
                            });
                } else if (resultCode == UNSPLASH_ERROR) {
                    Toast.makeText(MainTabActivity.this, "Bit of a problem getting the image", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_GALLERY:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Glide.with(this)
                                .load(data.getData())
                                .centerCrop()
                                .into(mAttachmentImageRef.get());

                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        assert selectedImage != null;
                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        assert cursor != null;
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        fakeContact.setAttachmentPath(picturePath);
                        cursor.close();
                    }
                }
                break;

            case REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    File dir = new File(getFilesDir() + "/images");
                    if (dir.exists()) {
                        File file = new File(dir, C.FILE_TMP);
                        if (file.exists() && file.length() > 0) {
                            Toast.makeText(MainTabActivity.this, file.toString(), Toast.LENGTH_SHORT).show();

                            Glide.with(this)
                                    .load(file.toString())
                                    .centerCrop()
                                    .into(mAttachmentImageRef.get());

                            fakeContact.setAttachmentPath(file.toString());
                            getSupportFragmentManager().popBackStack();

                        } else
                            Log.d(TAG, "onActivityResult: ");
                    }
                }
                break;
        }

    }

    @Override
    public void onBackPressed() {

        ImageSourceFragment f = (ImageSourceFragment) getSupportFragmentManager().findFragmentByTag(ImageSourceFragment.TAG_ID);

        if (f != null)
            f.flyOut(ImageSourceFragment.IMAGE_NONE);
        else
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

    @Override
    public void onSourceSelected(int src) {
        switch (src) {
            case ImageSourceFragment.IMAGE_NONE:
                getSupportFragmentManager().popBackStack();
                fabMain.show();
                break;

            case ImageSourceFragment.IMAGE_CLOUD:
                startActivityForResult(new Intent(MainTabActivity.this, CloudGridActivity.class), REQUEST_CLOUD);
                if (BuildConfig.USE_CRASHLYTICS)
                    Answers.getInstance().logCustom(new CustomEvent("MMS Cloud Image"));
                break;

            case ImageSourceFragment.IMAGE_CAMERA:
                Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = generateImagePath(C.FILE_TMP);

                if (file != null) {
                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
                        Log.i("camera", "This device has camera!");
                    else
                        Log.i("camera", "CAMERA NOT AVAILABLE!");

                    try {
                        if (C.HAS_N) {
                            Uri apkURI = FileProvider.getUriForFile(this, getString(R.string.file_provider), file);
                            chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, apkURI);
                        } else
                            chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

                        chooserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        if (chooserIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(chooserIntent, REQUEST_CAMERA);

                            if (BuildConfig.USE_CRASHLYTICS)
                                Answers.getInstance().logCustom(new CustomEvent("MMS Camera Image"));
                        } else
                            showAlertDialog("No Camera Found", "Can not use this feature");


                    } catch (IllegalArgumentException e) {
                        Log.e("File Selector", "The selected file can't be shared: " + file.toString());
                    }
                } else
                    Toast.makeText(this, "Could not create an image file for the camera! ", Toast.LENGTH_SHORT).show();
                break;

            case ImageSourceFragment.IMAGE_GALLERY:
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && !Environment.getExternalStorageState().equals(Environment.MEDIA_CHECKING)) {

                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, REQUEST_GALLERY);

                    if (BuildConfig.USE_CRASHLYTICS)
                        Answers.getInstance().logCustom(new CustomEvent("MMS Gallery Image"));
                } else {
                    Toast.makeText(this, R.string.no_media, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public File generateImagePath(String fileName) {
        try {
            boolean created = false;
            // Get the files/ subdirectory of internal storage
            File privateRootDir = getFilesDir();
            // Get the files/images subdirectory;
            File imagesDir = new File(privateRootDir, "images");
            if (!imagesDir.exists())
                created = imagesDir.mkdirs();

            File file = new File(imagesDir, fileName);
            if (!file.exists())
                created = file.createNewFile();

            Log.d(TAG, "generateImagePath: " + created);
            return file;

        } catch (IOException ex) {
            Log.d(TAG, "generateImagePath: " + ex.getMessage());
            return null;
        }
    }
}
