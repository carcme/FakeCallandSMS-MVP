package me.carc.fakecallandsms_mvp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.codemybrainsout.ratingdialog.FeedbackDialog;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.codemybrainsout.ratingdialog.SimpleDialog;

import me.carc.fakecallandsms_mvp.R;

/**
 * Created by bamptonm on 7/11/17.
 */

public class PrefFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // Click listeners
        final Preference feedbackPref = findPreference("feedback");
        feedbackPref.setOnPreferenceClickListener(feedbackPrefClick);

        final Preference ratePref = findPreference("rate");
        ratePref.setOnPreferenceClickListener(ratePrefClick);

        final Preference aboutPref = findPreference("about");
        aboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                new SimpleDialog.Builder(getActivity())
                        .title("Open Source Libraries")
                        .message(getString(R.string.lib_thanks))
                        .build()
                        .show();
                return false;
            }
        });
    }


    private Preference.OnPreferenceClickListener feedbackPrefClick = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            final FeedbackDialog fbDialog = new FeedbackDialog.Builder(getActivity())
                    .titleTextColor(R.color.black)
                    .formTitle(getString(R.string.feedback_request_title))
                    .formHint(getString(R.string.feedback_request_hint))
                    .formSubmitText(getString(R.string.feedback_request_send))
                    .formCancelText(getString(R.string.feedback_request_cancel))

                    .positiveButtonTextColor(R.color.colorAccent)
                    .positiveButtonBackgroundColor(R.drawable.button_selector_positive)

                    .negativeButtonTextColor(R.color.colorPrimaryDark)
                    .negativeButtonBackgroundColor(R.drawable.button_selector_negative)

                    .onFeedbackFormSumbit(new FeedbackDialog.Builder.FeedbackDialogFormListener() {
                        @Override
                        public void onFormSubmitted(String feedback) {

                            Intent Email = new Intent(Intent.ACTION_SEND);
                            Email.setType("text/email");
                            Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"carcmedev@gmail.com"});
                            Email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                            Email.putExtra(Intent.EXTRA_TEXT, feedback);
                            startActivity(Intent.createChooser(Email, getString(R.string.feedbackChooserTitle)));
                        }

                        @Override
                        public void onFormCancel() { /* EMPTY */}
                    }).build();

            fbDialog.show();
            return true;
        }
    };

    private Preference.OnPreferenceClickListener ratePrefClick = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            final RatingDialog ratingDialog = new RatingDialog.Builder(getActivity())
                    .icon(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_launcher))
                    .threshold(3)
                    .title(getString(R.string.ratings_request_title))
                    .titleTextColor(R.color.black)
                    .formTitle(getString(R.string.feedback_request_title))
                    .formHint(getString(R.string.feedback_request_hint))
                    .formSubmitText(getString(R.string.feedback_request_send))
                    .formCancelText(getString(R.string.feedback_request_cancel))
                    .ratingBarColor(R.color.colorAccent)

                    .positiveButtonTextColor(R.color.colorAccent)
                    .positiveButtonBackgroundColor(R.drawable.button_selector_positive)

                    .negativeButtonTextColor(R.color.colorPrimaryDark)
                    .negativeButtonBackgroundColor(R.drawable.button_selector_negative)
                    .onThresholdCleared(new RatingDialog.Builder.RatingThresholdClearedListener() {
                        @Override
                        public void onThresholdCleared(RatingDialog dlg, float rating, boolean thresholdCleared) {

                            final Uri marketUri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
                            } catch (android.content.ActivityNotFoundException ex) {

                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Error")
                                        .setMessage("Couldn't find PlayStore on this device")
                                        .show();
                            }
                            dlg.dismiss();
                        }
                    })

                    .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                        @Override
                        public void onFormSubmitted(String feedback) { }
                    }).build();

            ratingDialog.show();

            return true;
        }
    };

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    private void showAlert(Context ctx) {
        new AlertDialog.Builder(ctx)
                .setTitle("About")
                .setMessage("Coming soon... Want to give credit to the various libraries used here")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}
