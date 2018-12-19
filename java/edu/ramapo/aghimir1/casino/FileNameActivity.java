package edu.ramapo.aghimir1.casino;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileNameActivity extends AppCompatActivity {

    /* *********************************************
    Symbolic constants
    ********************************************* */
    public static final String NEW_TOURNAMENT = "edu.ramapo.aghimir1.casino.NewTournamentObject";
    //Create a static tournament instance here
    public static Tournament newTournament;

    /**
     The function that gets called when this activity is created
     @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_name);
    }

    /**
     Called when the user taps the Start Game button. This function tries to open the
     file the user specified. Shows an error toast if the file does not exists
     @param view, a View object
     */
    public void startGame(View view){
        EditText textInput = (EditText) findViewById(R.id.fileNameEditText);
        String fileName = textInput.getText().toString();

        fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + fileName;

        File file = new File(fileName);


        if( !file.isFile() ){
            Context context = getApplicationContext();
            CharSequence text = "Invalid file name!";

            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

//         Start GameActivity but only after you have initialized tournament
        newTournament = new Tournament( fileName );

        startNewGame();
    }

    /**
     This function starts GameActivity and passes the tournament object as an intent
     to GameActivity
     */
    private void startNewGame(){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(NEW_TOURNAMENT, newTournament);
        startActivity(intent);
    }
}
