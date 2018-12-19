package edu.ramapo.aghimir1.casino;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HomePageActivity extends AppCompatActivity {

    /**
     The function that gets called when this activity is created
     @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
    }

    /**
     Called when the user taps the New Game button. This function starts
     TossActivity
     @param view, a View object
     */

    public void toss(View view) {
        Intent intent = new Intent(this, TossActivity.class);
        startActivity(intent);
    }

    /**
     Called when the user taps the Load Game button. This function starts
     FileNameActivity
     @param view, a View object
     */

    public void loadGame(View view){
        Intent intent = new Intent(this, FileNameActivity.class);
        startActivity(intent);
    }
}
