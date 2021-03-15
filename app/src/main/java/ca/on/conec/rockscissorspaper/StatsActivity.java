package ca.on.conec.rockscissorspaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StatsActivity extends AppCompatActivity {

    private boolean isDarkAppTheme;

    private SharedPreferences sharedPref;

    TextView txtLastMinuteValue , txtAllRecordValue;
    Button btnReset;


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

        txtLastMinuteValue = findViewById(R.id.txtLastMinuteValue);
        txtAllRecordValue = findViewById(R.id.txtAllRecordValue);
        btnReset = findViewById(R.id.btnReset);

        getData();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RSPApplication) getApplication()).resetTable();
                getData();
            }
        });
    }

    public void getData() {
        // Get LastMinuteValue
        String lastMinValue = ((RSPApplication) getApplication()).getStatisticsPastMinute();
        txtLastMinuteValue.setText(lastMinValue);

        // Get All Record Result
        String allRecordValue = ((RSPApplication) getApplication()).getStatisticsAllTime();
        txtAllRecordValue.setText(allRecordValue);
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