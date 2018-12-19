package edu.ramapo.aghimir1.casino;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {

    /**
     The function that gets called when this activity is created
     @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Intent intent = getIntent();
        setUpTextBoxes( intent );
    }

    /**
     This function sets the value of all almost all textboxes used in this activity
     @param intent, an Intent object that is used to extract values for the textboxes
     */

    private void setUpTextBoxes(Intent intent){

        TextView textView = (TextView) findViewById( R.id.gameOVerHumanTournament);
        String text = textView.getText() + intent.getStringExtra(GameActivity.HUMAN_FINAL_TOURNAMENT_SCORE );
        textView.setText(text);

        textView = (TextView) findViewById( R.id.gameOverComputerTournament );
        text = textView.getText() + intent.getStringExtra( GameActivity.COMPUTER_FINAL_TOURNAMENT_SCORE );
        textView.setText(text);

        textView = (TextView) findViewById( R.id.gameOverWinner );
        text = textView.getText() + intent.getStringExtra( GameActivity.TOURNAMENT_WINNER);
        textView.setText(text);
    }

    /**
     This function ends the activity and the enitire game. After execution, this method
     causes the app to exit and return view to the device's home screen
     @param view, a View variable
     */

    public void endApplication( View view ) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
