package me.carc.fakecallandsms_mvp;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import me.carc.fakecallandsms_mvp.fragments.PrefFragment;

/**
 * Setting activity
 *
 * Created by bamptonm on 2/3/17.
 */

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setTitle(getString(R.string.action_settings));
        }

        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
