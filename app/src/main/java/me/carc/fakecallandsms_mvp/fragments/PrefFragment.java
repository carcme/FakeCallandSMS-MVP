package me.carc.fakecallandsms_mvp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;

import com.codemybrainsout.ratingdialog.SimpleDialog;

import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.common.utils.U;

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
            U.emailFeedbackForm(getActivity());
            return true;
        }
    };

    private Preference.OnPreferenceClickListener ratePrefClick = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            U.showRatingForm(getActivity());
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
