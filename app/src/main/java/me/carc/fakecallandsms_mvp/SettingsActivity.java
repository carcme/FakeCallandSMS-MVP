package me.carc.fakecallandsms_mvp;

import android.content.Intent;
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

    public static boolean langChanged;

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
        if(langChanged) {
            Intent intent = getIntent();
            intent.putExtra("lang", true);
            setResult(RESULT_OK, intent);

        }
        super.onBackPressed();
    }

}
