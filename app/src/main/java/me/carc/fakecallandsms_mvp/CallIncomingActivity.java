package me.carc.fakecallandsms_mvp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.frakbot.glowpadbackport.GlowPadView;

import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.fakecallandsms_mvp.alarm.CarcWakeLockHolder;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.CallLogUtil;
import me.carc.fakecallandsms_mvp.common.TinyDB;
import me.carc.fakecallandsms_mvp.common.utils.Common;
import me.carc.fakecallandsms_mvp.common.utils.U;

public class CallIncomingActivity extends Base {

    private static final String TAG = C.DEBUG + U.getTag();

    private TinyDB tinyDb;

    private Ringtone mRingtone;
    private MediaPlayer voicePlayer;

    private long duration;

    private final Handler pingHandler = new Handler();
    private final Handler hangupHandler = new Handler();
    private final Handler callTimerhandler = new Handler();

    @BindView(R.id.callInfoLayout)
    RelativeLayout callInfoLayout;

    @BindView(R.id.callStatus)
    TextView callStatus;

    @BindView(R.id.btnEndCall)
    ImageButton btnEndCall;

    @BindView(R.id.incomingCallWidget)
    GlowPadView glowPad;

    @BindView(R.id.unknown_image)
    ImageView imageUnknown;

    @BindView(R.id.contact_image)
    ImageView imageContact;

    @BindView(R.id.callDuration)
    TextView callDuration;

    @BindView(R.id.callerName)
    TextView callerName;

    @BindView(R.id.phoneNumber)
    TextView callerNumber;


    @OnClick(R.id.btnEndCall)
    void onEndCallBtn() {
        btnEndCall.setImageResource(R.drawable.ic_lockscreen_decline_normal_layer);

        callStatus.setText(R.string.call_ending);

        logCalls(CallLog.Calls.INCOMING_TYPE);
        stopNotifications(C.CALL_END);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CarcWakeLockHolder wl = new CarcWakeLockHolder(this);
        wl.aquireWakeLock();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.fakecall_incoming_layout);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d(TAG, "onCreate: No Contact Info Receieved");
            finish();
        }

        tinyDb = TinyDB.getTinyDB();
        if (tinyDb == null)
            tinyDb = new TinyDB(this);

        checkCallOngoing();
        setupGlowPad();
        retrieveContactInfo();
        playIncomingNotification();
        setCallScreenColor();
//        showNotification();
    }


    private void showNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);

        String mContact = getIntent().hasExtra(C.NAME) ? getIntent().getStringExtra(C.NAME) : "";
        String mNumber = getIntent().hasExtra(C.NUMBER) ? getIntent().getStringExtra(C.NUMBER) : "";

        if (Common.isEmpty(mContact) && Common.isEmpty(mNumber))
            mContact = getString(R.string.unknown_caller);

        nBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        nBuilder.setSmallIcon(R.drawable.ic_call, 1);
        nBuilder.setContentTitle("Incoming Call");
        nBuilder.setContentText(!Common.isEmpty(mContact) ? mContact : mNumber);
        nBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        nBuilder.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        nBuilder.setOnlyAlertOnce(true);

        nm.notify(2002, nBuilder.build());
    }

    /**
     * Randomise the colors shown on the answer screen - correspond to the google contacts images
     */
    private void setCallScreenColor() {
        TypedArray colors = getResources().obtainTypedArray(R.array.array_GoogleContactsColors);

        int color, min = 0;
        int max = colors.length();

        // Generate random color
        Random r = new Random();
        color = r.nextInt(max - min + 1) + min;

        callInfoLayout.setBackgroundColor(ContextCompat.getColor(this, colors.getResourceId(color, R.color.gcc_orange_2)));
    }

    /**
     * Create the GlowPad ringer answer display
     */
    private void setupGlowPad() {
        pingHandler.postDelayed(pingDisplay, C.PING_TIMEOUT);
        glowPad.setOnTriggerListener(new GlowPadView.OnTriggerListener() {
            @Override
            public void onGrabbed(View v, int handle) { /*Do nothing*/ }

            @Override
            public void onReleased(View v, int handle) {
                glowPad.ping();
            }

            @Override
            public void onTrigger(View v, int target) {

                pingHandler.removeCallbacks(pingDisplay);
                hangupHandler.removeCallbacks(hangup);

                if (target == 0) {
                    glowPad.setVisibility(View.GONE);
                    btnEndCall.setVisibility(View.VISIBLE);
                    stopNotifications(C.CALL_ACCEPT);
                } else if (target == 2) {

                    stopNotifications(C.CALL_REJECT);
                    logCalls(CallLog.Calls.INCOMING_TYPE);
                }
                glowPad.reset(true);
            }

            @Override
            public void onGrabbedStateChange(View v, int handle) { /*Do nothing*/ }

            @Override
            public void onFinishFinalAnimation() {
                glowPad.ping();
            }
        });
    }


    private Runnable pingDisplay = new Runnable() {

        @Override
        public void run() {
            glowPad.ping();
            pingHandler.postDelayed(this, C.PING_TIMEOUT);
        }
    };


    private Runnable hangup = new Runnable() {

        @Override
        public void run() {
            logCalls(CallLog.Calls.MISSED_TYPE);
            stopNotifications(C.CALL_END);
        }
    };


    /**
     * Get the contact details and display them on the screen
     */
    private void retrieveContactInfo() {
        String photoUri = getIntent().getStringExtra(C.IMAGE);

        if (photoUri != null) {
            imageContact.setVisibility(View.VISIBLE);

            imageContact.setImageURI(Uri.parse(photoUri));

        } else {
            imageUnknown.setVisibility(View.VISIBLE);

            DisplayMetrics display = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(display);

            switch (display.densityDpi) {
                case DisplayMetrics.DENSITY_LOW:
                    imageUnknown.getLayoutParams().width = 75;
                    imageUnknown.getLayoutParams().height = 75;
                    break;
                case DisplayMetrics.DENSITY_MEDIUM:
                    imageUnknown.getLayoutParams().width = 100;
                    imageUnknown.getLayoutParams().height = 100;
                    break;
                case DisplayMetrics.DENSITY_HIGH:
                    imageUnknown.getLayoutParams().width = 150;
                    imageUnknown.getLayoutParams().height = 150;
                    break;
            }
        }

        // Build the incoming caller info string
        String name = getIntent().getStringExtra(C.NAME);
        String number = getIntent().getStringExtra(C.NUMBER);

        if (!Common.isEmpty(name))
            callerName.setText(name);
        else
            callerName.setText(R.string.unknown_caller);

        if (!Common.isEmpty(number))
            callerNumber.setText(number);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * Check if there is an ongoing or pending real call
     *
     * @return Ongoing or pending call is present
     */
    private boolean checkCallOngoing() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE;
    }


    /**
     * Play the ring tone depending on vibrate options. If no ringtone is selected, use the
     * default system ringtone (if there is no vibrate option selected)
     */
    private void playIncomingNotification() {
        Vibrator vibrator = null;
        if (getIntent().getBooleanExtra(C.VIBRATE, false)) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(C.VIBRATE_PATTERN, 0);
        }

        String guiRingtone = getIntent().getStringExtra(C.RINGTONE);
        try {
            // Ringtone not set and vibrate not ticked
            if (guiRingtone == null) {
                if (vibrator == null) {
                    // Nothing selected - play the default system ringtone
                    Uri defaultRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
                    mRingtone = RingtoneManager.getRingtone(this, defaultRintoneUri);
                    if (mRingtone == null) {
                        String uriTone = tinyDb.getString(C.PREF_RING_TONE);
                        mRingtone = RingtoneManager.getRingtone(this, Uri.parse(uriTone));
                    }

                    mRingtone.play();
                }
            } else {
                // User selected a ringtone - play it
                mRingtone = RingtoneManager.getRingtone(this, Uri.parse(guiRingtone));
                mRingtone.play();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Have you selected a ring tone?", Toast.LENGTH_LONG).show();
        }

        int constant = C.QUICK_TIME_DEFAULT;
        String defaultValue = String.valueOf(constant);
        tinyDb.getString(C.PREF_QUICK_TIME, defaultValue);

        hangupHandler.postDelayed(hangup, getIntent().getLongExtra(C.DURATION, C.MAX_CALL_DURATION_DEFAULT * C.MINUTE_MILLIS));
    }


    /**
     * Stop all notifications - switch on the user path of the call
     *
     * @param type Type of notification to stop
     */
    private void stopNotifications(int type) {
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null)
            vibrator.cancel();

        if (mRingtone != null)
            mRingtone.stop();

        switch (type) {
            case C.CALL_ACCEPT:
                duration = System.currentTimeMillis();
                startVoice();
                break;
            case C.CALL_REJECT:
                returnToMain();
                break;
            case C.CALL_END:
                returnToMain();
                break;
            default:
                returnToMain();
        }
    }


    private void startVoice() {
        String voice = tinyDb.getString(C.PREF_BACKGROUND_VOICE);

        if (!Common.isEmpty(voice)) {

            Uri voiceURI = Uri.parse(voice);

            voicePlayer = new MediaPlayer();

            try {
                voicePlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                voicePlayer.setDataSource(this, voiceURI);
                voicePlayer.prepareAsync();
                voicePlayer.setLooping(true);

                voicePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        duration = 0;
        callTimerhandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                long min = (duration % 3600) / 60;
                long seconds = duration % 60;
                String dur = String.format(Locale.getDefault(), "%02d:%02d", min, seconds);
                duration++;
                callDuration.setText(dur);
                callTimerhandler.postDelayed(this, 1000);
            }
        }, 10);
    }


    private void stopVoice() {

        if (voicePlayer != null && voicePlayer.isPlaying()) {
            voicePlayer.stop();
        }

    }

    private void returnToMain() {

        stopVoice();

        // Clear the wake lock - used to keep screen on while fake call occuring
        CarcWakeLockHolder.getInstance().releaseWakeLock();

        // Show the launch Icon on home screen
        showLaunchIcon();

        // Close this screen
        int SPLASH_DISPLAY_LENGTH = 1500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(CallIncomingActivity.this, MainTabActivity.class);
                finish();
                startActivity(intent);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }


    @Override
    public void onDetachedFromWindow() {
        stopVoice();

        super.onDetachedFromWindow();
    }

    /**
     * Log the calls
     *
     * @param callType MISSED_TYPE or INCOMING_TYPE
     */
    private void logCalls(int callType) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        CallLogUtil.addCallToLog(
                getApplicationContext(),
                getIntent().getStringExtra(C.NAME),
                getIntent().getStringExtra(C.NUMBER),
                duration,
                callType,
                System.currentTimeMillis());
    }
}
