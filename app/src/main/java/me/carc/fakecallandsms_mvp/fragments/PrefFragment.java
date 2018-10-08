package me.carc.fakecallandsms_mvp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codemybrainsout.ratingdialog.SimpleDialog;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;

import de.cketti.library.changelog.ChangeLog;
import me.carc.fakecallandsms_mvp.BuildConfig;
import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.common.utils.AndroidUtils;
import me.carc.fakecallandsms_mvp.common.utils.U;

/**
 * Menu settings
 * Created by bamptonm on 7/11/17.
 */

public class PrefFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // FEEDBACK listeners
        final Preference feedbackPref = findPreference("feedback");
        feedbackPref.setOnPreferenceClickListener(feedbackPrefClick);

        // RATE listeners
        final Preference ratePref = findPreference("rate");
        ratePref.setOnPreferenceClickListener(ratePrefClick);

        // SHARE listeners
        final Preference sharePref = findPreference("share");
        sharePref.setOnPreferenceClickListener(sharePrefClick);

        // ABOUT listeners
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
        aboutPref.setSummary(String.format(getString(R.string.app_version), AndroidUtils.getVersion()));

        // ABOUT listeners
        final Preference changelog = findPreference("changelog");
        changelog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ChangeLog cl = new ChangeLog(getActivity());
                cl.getFullLogDialog().show();
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        final ListView lv = v.findViewById(android.R.id.list);
        lv.setDivider(new ColorDrawable(Color.TRANSPARENT));
        return v;
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

    private Preference.OnPreferenceClickListener sharePrefClick = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, getString(R.string.settings_title_share)));

            if(BuildConfig.USE_CRASHLYTICS) Answers.getInstance().logShare(new ShareEvent());

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
