package ca.on.conec.rockscissorspaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * FileName : MainActivity.java
 * Purpose
 * Revision History :
 *      2021.01.30 Create Project and implement enum and funtions
 *      2021.02.11 Modify specific style.
 */
public class MainActivity extends AppCompatActivity {

    // Initial Button
    private Button btnRock , btnScissor, btnPaper;
    private ImageView imgMine, imgComputer;
    private TextView txtResult;
    RockScissorPaper usrResult , comResult = null;
    int clickedBtnId = -1 , computerResultId = -1;


    private boolean creatingActivity = false;
    private boolean saveState;
    private boolean isDarkAppTheme;

    private SharedPreferences sharedPref;

//    private AppCompatActivity SELF = null;

    private final static int ANIMATION_DURATION = 1000;
    private final static int TIMER_DURATION = 3000;
//    private final static int SERVICE_TIMER_DURATION = 10000;
    private Timer clickTimer = null;
    /**
     * RockScissorPaper Enum
     */
    private enum RockScissorPaper {
        ROCK(0), SCISSOR(1), PAPER(2);

        private int value;
        RockScissorPaper(int value) {
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
//        SELF = this;

        Log.i("INFO" , "onCreate");

        creatingActivity = true;

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        setAppTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        /*
        Timer serviceTimer = new Timer(true);

        serviceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                startService(new Intent(getApplicationContext(), NotificationService.class));
            }
        } , SERVICE_TIMER_DURATION);
        */

    }

    /**
     *  setTheme using sharedPref
     */
    private void setAppTheme() {
        isDarkAppTheme = sharedPref.getBoolean("darkAppTheme", false);

        Log.i("INFO" , isDarkAppTheme + "");
        if(isDarkAppTheme) {
            Log.i("INFO" , "DarkAppTheme");
            setTheme(R.style.RockScissorsPaperDark);
        } else {
            Log.i("INFO" , "BasicAppTheme");
            setTheme(R.style.RockScissorsPaper);
        }
    }

    /**
     * Click Event Listener
     */
    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // When displaying result, prevent clicking a button

            btnRock.setEnabled(false);
            btnScissor.setEnabled(false);
            btnPaper.setEnabled(false);

            imgMine.setVisibility(View.INVISIBLE);
            imgComputer.setVisibility(View.INVISIBLE);

            clickedBtnId = v.getId();
            makeResultForMine(clickedBtnId);

            // Animation
            imgMine.setAlpha(0f);
            imgMine.animate().alpha(1f).setDuration(ANIMATION_DURATION).setListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                        computerResultId = RockScissorPaper.getRandomValue();
                        makeResultForComputer(computerResultId);

                        imgComputer.setAlpha(0f);
                        imgComputer.animate().alpha(1f).setDuration(ANIMATION_DURATION);

                        if(usrResult == comResult) {
                            // TIE
                            txtResult.setText(R.string.txt_result_tie);
                            ((RSPApplication) getApplication()).addResult(3);

                        } else if((usrResult == RockScissorPaper.ROCK && comResult == RockScissorPaper.SCISSOR) ||
                                (usrResult == RockScissorPaper.SCISSOR && comResult == RockScissorPaper.PAPER) ||
                                (usrResult == RockScissorPaper.PAPER && comResult == RockScissorPaper.ROCK))  {
                            // WIN
                            txtResult.setText(R.string.txt_result_win);
                            ((RSPApplication) getApplication()).addResult(1);
                        } else {
                            // LOSE
                            txtResult.setText(R.string.txt_result_lose);
                            ((RSPApplication) getApplication()).addResult(2);
                        }


                        // After display result, enable to click the button
                        new Handler(Looper.getMainLooper()).postDelayed(
                            new Runnable() {
                                public void run() {
                                    btnRock.setEnabled(true);
                                    btnScissor.setEnabled(true);
                                    btnPaper.setEnabled(true);
                                }
                            }, 1000);

                    }
                }
            );

            ///// If user has not started a new game 3 second

            if(clickTimer != null) {
                clickTimer.cancel();
            }

            clickTimer = new Timer(true);

            clickTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    clickTimer.cancel();
                    clickTimer = null;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.encourage_message , Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }, TIMER_DURATION);


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

        } else {
            imgMine.setImageResource(0);
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
        } else {
            imgComputer.setImageResource(0);
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

        Log.i("INFO" , "onResume");

        super.onResume();

        saveState = sharedPref.getBoolean("saveOnClose", false);

        if(saveState || !creatingActivity) {
            if(sharedPref.contains("clickedBtnId")) {
                makeResultForMine(sharedPref.getInt("clickedBtnId", -1));
            }
            if(sharedPref.contains("computerResultId")) {
                makeResultForComputer(sharedPref.getInt("computerResultId", -1));
            }
            txtResult.setText(sharedPref.getString("resultTxt", "Choose rock, paper or scissors."));
        }

        creatingActivity = false;

    }

    /**
     * Menu Option
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rock_scissors_paper_menu , menu);
        return true;
    }

    /**
     * When click the option menu
     * @param item
     * @return
     */
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

    /**
     * When onStop
     */
    @Override
    protected void onStop() {

        Log.d("INFO" , "onStop");

        startService(new Intent(getApplicationContext(), NotificationService.class));


        super.onStop();
    }
}