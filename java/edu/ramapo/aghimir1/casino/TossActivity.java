package edu.ramapo.aghimir1.casino;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TossActivity extends AppCompatActivity {

    /* *********************************************
    Symbolic constants
    ********************************************* */
    public static final String NEW_TOURNAMENT = "edu.ramapo.aghimir1.casino.NewTournamentObject";
    public static Tournament newTournament;
    public static Round newRound;
    private static boolean tossButtonClicked = false;

    /**
     The function that gets called when this activity is created
     @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toss);

        newTournament = new Tournament();
        newRound = newTournament.getCurrentRound();
    }

    /**
     Called when the user taps the Start Game button. This function starts
     GameActivity if toss has already happened
     @param v, a View object
     */
    public void startNewGame(View v){
        if(tossButtonClicked){
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(NEW_TOURNAMENT, newTournament);
            startActivity(intent);
        }
    }

    /**
     Called when user taps the toss button. This function updates the Activity's
     textviews to reflect the results of the toss based on human player's toss choice
     @param view, a View object
     */

    public void toss(View view) {
        tossButtonClicked = true;

        // Disable the toggle feature of the toggle button
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        String headsOrTails = toggle.getText().toString();
        toggle.setClickable(false);

        String tossResult = newRound.toss(headsOrTails);

        String tossResultText = "It's ";
        tossResultText += tossResult.equalsIgnoreCase("Heads")? "Heads": "Tails";
        TextView tossResultView = (TextView) findViewById(R.id.tossResultView);
        tossResultView.setText(tossResultText);

        TextView firstPlayer = (TextView) findViewById(R.id.firstPlayer);
        String whoPlaysFirst = newRound.getTurn()? "Human": "Computer";
        whoPlaysFirst += " plays first";
        firstPlayer.setText(whoPlaysFirst);

        TextView secondPlayer = (TextView) findViewById(R.id.secondPlayer);
        String whoPlaysSecond = newRound.getTurn()? "Computer": "Human";
        whoPlaysSecond += " plays second";
        secondPlayer.setText(whoPlaysSecond);
    }
}
