package ca.on.conec.rockscissorspaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

public class StatsActivity extends AppCompatActivity {

    private boolean isDarkAppTheme;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        setAppTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setAppTheme() {
        isDarkAppTheme = sharedPref.getBoolean("darkAppTheme", false);

        if(isDarkAppTheme) {
            setTheme(R.style.RockScissorsPaperDark);
        } else {
            setTheme(R.style.RockScissorsPaper);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean rtnValue = true;
        switch(item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            default :
                rtnValue =  super.onOptionsItemSelected(item);
                break;
        }

        return rtnValue;
    }


}