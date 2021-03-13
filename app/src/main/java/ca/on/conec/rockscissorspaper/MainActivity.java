/**
 * FileName : MainActivity.java
 * Purpose
 * Revision History :
 *      2021.01.30 Create Project and implement enum and funtions
 *      2021.02.11 Modify specific style.
 */
package ca.on.conec.rockscissorspaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class MainActivity extends AppCompatActivity {

    // Initial Button
    private Button btnRock , btnScissor, btnPaper;
    private ImageView imgMine, imgComputer;
    private TextView txtResult;
    RockScissorPaper usrResult , comResult = null;
    int clickedBtnId , computerResultId;


    private boolean creatingActivity = false;
    private boolean saveState;
    private boolean isDarkAppTheme;

    private SharedPreferences sharedPref;

    /**
     * RockScissorPaper Enum
     */
    private enum RockScissorPaper {
        ROCK(0), SCISSOR(1), PAPER(2);

        private int value;
        private RockScissorPaper(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static int getRandomValue() {
            Random random = new Random();
            return random.nextInt(values().length);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        creatingActivity = true;

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        setAppTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setBtnAssign();
    }

    private void setAppTheme() {
        isDarkAppTheme = sharedPref.getBoolean("darkAppTheme", false);

        if(isDarkAppTheme) {
            Log.i("INFO" , "DARK");
            setTheme(R.style.RockScissorsPaperDark);
        } else {
            setTheme(R.style.RockScissorsPaper);
        }
    }

    private void setBtnAssign() {
        // Button ID Link
        btnRock = findViewById(R.id.btnRock);
        btnScissor = findViewById(R.id.btnScissors);
        btnPaper = findViewById(R.id.btnPaper);

        // ImageView ID Link
        imgMine = findViewById(R.id.imgMine);
        imgComputer = findViewById(R.id.imgComputer);

        // TextVIew ID Link
        txtResult = findViewById(R.id.txtResult);

        btnRock.setOnClickListener(buttonListener);
        btnScissor.setOnClickListener(buttonListener);
        btnPaper.setOnClickListener(buttonListener);
    }


    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickedBtnId = v.getId();
            makeResultForMine(clickedBtnId);

            computerResultId = RockScissorPaper.getRandomValue();
            makeResultForComputer(computerResultId);

            if(usrResult == comResult) {
                // TIE
                txtResult.setText(R.string.txt_result_tie);
            } else if((usrResult == RockScissorPaper.ROCK && comResult == RockScissorPaper.SCISSOR) ||
                    (usrResult == RockScissorPaper.SCISSOR && comResult == RockScissorPaper.PAPER) ||
                    (usrResult == RockScissorPaper.PAPER && comResult == RockScissorPaper.ROCK))  {
                // WIN
                txtResult.setText(R.string.txt_result_win);
            } else {
                // LOSE
                txtResult.setText(R.string.txt_result_lose);
            }
         }
    };

    /**
     * method name : makeResultForMine.
     * purpose : when the click the button or rotate screen, display my result
     * @param id
     */
    private void makeResultForMine(int id) {
        if(id != -1) {
            switch (id) {
                case R.id.btnRock:
                    imgMine.setImageResource(R.drawable.ic_rock);
                    usrResult = RockScissorPaper.ROCK;
                    break;
                case R.id.btnScissors:
                    imgMine.setImageResource(R.drawable.ic_scissors);
                    usrResult = RockScissorPaper.SCISSOR;
                    break;
                case R.id.btnPaper:
                    imgMine.setImageResource(R.drawable.ic_paper);
                    usrResult = RockScissorPaper.PAPER;
                    break;
            }

            if (imgMine.getVisibility() == View.INVISIBLE) {
                imgMine.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * method name : makeResultForComputer
     * purpose : when the click the button or rotate screen, display computer's result
     * @param computerResultId
     */
    private void makeResultForComputer(int computerResultId) {
        if(computerResultId != -1 ) {
            comResult = RockScissorPaper.values()[computerResultId];
            // Get randomly computer's result
            switch (comResult) {
                case ROCK:
                    imgComputer.setImageResource(R.drawable.ic_rock);
                    break;
                case SCISSOR:
                    imgComputer.setImageResource(R.drawable.ic_scissors);
                    break;
                case PAPER:
                    imgComputer.setImageResource(R.drawable.ic_paper);
                    break;
            }

            if (imgComputer.getVisibility() == View.INVISIBLE) {
                imgComputer.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * Purpose : When the rotate and then execute life cycle of pause , save the data.
     */
    @Override
    protected void onPause() {
        Editor editor = sharedPref.edit();
        editor.putString("resultTxt" , txtResult.getText().toString());
        editor.putInt("clickedBtnId", clickedBtnId);
        editor.putInt("computerResultId", computerResultId);
        editor.commit();


        super.onPause();
    }

    /**
     * Purpose : When the rotate and then execute life cycle of resume, display the data
     */
    @Override
    protected void onResume() {

        setAppTheme();
        super.onResume();


        setContentView(R.layout.activity_main);
        setBtnAssign();

        creatingActivity = false;

        saveState = sharedPref.getBoolean("saveOnClose", false);

        makeResultForMine(sharedPref.getInt("clickedBtnId" , -1));
        makeResultForComputer(sharedPref.getInt("computerResultId" , -1));
        txtResult.setText(sharedPref.getString("resultTxt" , "Choose rock, paper or scissors."));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rock_scissors_paper_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {

            case R.id.menuSettings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.menuStatistics:
                startActivity(new Intent(getApplicationContext(), StatsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}