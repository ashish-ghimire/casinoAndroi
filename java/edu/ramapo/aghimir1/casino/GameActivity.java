package edu.ramapo.aghimir1.casino;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

import static edu.ramapo.aghimir1.casino.Strategy.BUILD;
import static edu.ramapo.aghimir1.casino.Strategy.CAPTURE;
import static edu.ramapo.aghimir1.casino.Strategy.TRAIL;

public class GameActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    /* *********************************************
    Symbolic constants
    ********************************************* */
    public static final String ROUND_INFO = "com.aghimir1.ramaFood.EventTitle";
    public static final String HUMAN_FINAL_TOURNAMENT_SCORE = "edu.ramapo.aghimir1.casino.HumanFinalTournamentScore";
    public static final String COMPUTER_FINAL_TOURNAMENT_SCORE = "edu.ramapo.aghimir1.casino.ComputerFinalTournamentScore";
    public static final String TOURNAMENT_WINNER = "edu.ramapo.aghimir1.casino.TournamentWinner";

    private static final int TABLE_MULTIBUILD_OFFSET = 100;
    private static final int TABLE_SINGLE_BUILD_OFFSET = 200;
    private static final int TABLE_LOOSE_CARD_OFFSET = 300;
    private static final int HAND_CARD_OFFSET = 400;
    private static final int INVALID_HAND_CARD_INDEX = Input.INVALID_INDEX;

    /* *********************************************
    Class member variables
    ********************************************* */
    private ArrayList<Integer> indexOfLooseTableCards;
    private ArrayList<Integer> indexOfSingleBuilds;
    private ArrayList<Integer> indexOfMultiBuilds;
    private int handCardIndex;
    private Round newRound;
    private Tournament newTournament;
    private boolean isTournamentSerialized;

    /**
     Gets called when this activity is created
     @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        initializeTournament(intent);

        if( !newTournament.isSerialized() )
            newRound.setTheRoundUp();

        refreshView();
    }

    /**
     Used to initialize the tournament instance used in this class
     @param intent - an Intent object from which tournament object passed
     from the activity that started this activity can be extracted
     */
    private void initializeTournament(Intent intent){
        // To retrieve object in second Activity
        newTournament = (Tournament) getIntent().getSerializableExtra( FileNameActivity.NEW_TOURNAMENT );
        newRound = newTournament.getCurrentRound();
    }

    /**
     Refreshes the view based to reflect any changes made to the model classes
     */
    private void refreshView(){
        newRound = newTournament.getCurrentRound();
        initializePossibleInputs();
        setUpTable();
        setUpHand();

        // Set up text boxes
        setUpTextBox( R.id.textViewTurn, "Turn: " + newRound.getWhoIsPlaying().getPlayerType() );
        setUpTextBox( R.id.textViewRoundNumber, "Round Number: " + newRound.getRoundCount() );

        setUpScores();
    }

    /**
     Intializes the objects that may be used as Input for the players
     */
    private void initializePossibleInputs(){
        indexOfLooseTableCards = new ArrayList<>();
        indexOfSingleBuilds = new ArrayList<>();
        indexOfMultiBuilds = new ArrayList<>();
        handCardIndex = INVALID_HAND_CARD_INDEX;
    }


    /**
     Called when user clicks floating action button. Shows pop up menu with move options
     for the player making the move
     @param - v, a View object
     */
    public void showPopup(View v){
        PopupMenu menu = new PopupMenu(this, v);
        menu.setOnMenuItemClickListener(this);
        menu.inflate(R.menu.popup_menu);
        menu.show();
    }

    /**
     Called when user clicks any of the buttons in the pop up menu. The buttons
     in the menu are used to make moves
     @param - item, a MenuItem object represnting what move to make.
     @return true if the user clicks any of the buttons. false otherwise
     */
    @Override
    public boolean onMenuItemClick(MenuItem item){
        switch (item.getItemId() ){
            case R.id.menuBuild:
                makeMove(BUILD);
                return true;
            case R.id.menuCapture:
                makeMove(CAPTURE);
                return true;
            case R.id.menuTrail:
                makeMove(TRAIL);
                return true;
        }
        return false;
    }

    /**
     Called when the user taps the Save imagebutton
     @param view, a View object
     */
    public void saveGame(View view){
        newRound.saveGame();

        // Exit the game after saving
        endApplication(view);
    }

    /**
     Called when user clicks the quit image button. A red imagebutton with quit symbol.
     This function ends this activity, exits the game and redirects the screen to the device's
     home window
     @param - v, a View object
     */

    public void endApplication( View view ) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }


    /**
     Called when the user taps the help imagebutton
     @param view, a View object
     */
    public void help(View view){
        String helpString = newRound.help();

        final Snackbar snackbar = Snackbar.make(findViewById(R.id.constraintLayouthelpSection), helpString, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        View snackbarView = snackbar.getView();
        TextView tv= snackbarView.findViewById(android.support.design.R.id.snackbar_text); //
        tv.setMaxLines(3);

        snackbar.show();
    }

    /**
     Called when user clicks the automove button. Imagebutton with computer icon. USed
     to make move for the computer player
     @param - v, a View object
     */
    public void autoMove(View v){
        initializePossibleInputs();
        makeMove( Input.INVALID_INDEX );
    }

    /**
     Called when user clicks the reset move button.
     @param - v, a View object
     */
    public void resetMoves(View view){
        initializePossibleInputs();
    }

    /**
     Called when user clicks the Game State button. Used to open a dialog box to display
     the basic state of the game like both players' piles, both players' hands and the deck for
     round
     @param - v, a View object
     */
    public void displayGameState( View view ){
        //Dialog
        final Dialog d = new Dialog(GameActivity.this);
        d.setTitle("Game State: ");
        d.setContentView(R.layout.game_state_layout);

        // Display deck cards
        addCardImagesToLayout( (LinearLayout) d.findViewById( R.id.deckLinear), newRound.getDeck().getDeckCards() );

        // Display pile and hand for human
        addCardImagesToLayout( (LinearLayout) d.findViewById( R.id.humanPileLinear), newRound.getHuman().getPile() );
        addCardImagesToLayout( (LinearLayout) d.findViewById( R.id.humanHandLinear), newRound.getHuman().getHand() );

        // Display pile and hand for computer
        addCardImagesToLayout( (LinearLayout) d.findViewById( R.id.computerPileLinear), newRound.getComputer().getPile() );
        addCardImagesToLayout( (LinearLayout) d.findViewById( R.id.computerHandLinear), newRound.getComputer().getHand() );

        d.show();
    }

    /**
     Sets up the table section of the view
     */
    private void setUpTable(){
        LinearLayout tableView = (LinearLayout) findViewById(R.id.linearLayoutTable);
        tableView.removeAllViews();
        Table gameTable = newTournament.getCurrentRound().getTable();

        setUpMultiBuildView(tableView, gameTable);
        setUpSingleBuildView(tableView, gameTable);
        setUpCardLooseCardButtons(gameTable.getLooseCards(), TABLE_LOOSE_CARD_OFFSET, tableView);
    }

    /**
     Sets up view for multibuilds on the table
     @param row- a Linearlayout to which the function adds newly created views
     @param gameTable - a data obtained from the model. This data is used to
     set the content for the view
     */
    private void setUpMultiBuildView(LinearLayout row, Table gameTable){

        FlexboxLayout flexboxLayout = null;

        int count = 0;
        for(MultiBuild m: gameTable.getMultiBuilds() ){
            ArrayList<Card> allCardsInMulti = new ArrayList<>();

            for(Build b: m.getMultiBuild() )
                allCardsInMulti.addAll(b.getBuildCards());

            FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFlexShrink(1);
            flexboxLayout = new FlexboxLayout(this);// FlexboxLayout) findViewById(R.id.flexbox_layout);
            flexboxLayout.setLayoutParams(layoutParams);

            addCardsToFlex(flexboxLayout, allCardsInMulti );

            flexboxLayout.setId(count + TABLE_MULTIBUILD_OFFSET);

            flexboxLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Code here executes on main thread after user presses button
                    recordMultiBuild(v);
                }
            });

            flexboxLayout.addView(setDescription(this,m.getOwner() + " multi-build. Sum = " + m.getNumericValue()));
            flexboxLayout.setPadding(20, 0, 20, 0);
            row.addView(flexboxLayout);

            count++;
        }
    }

    /**
     Sets up view for single builds on the table
     @param row- a Linearlayout to which the function adds newly created views
     @param gameTable - a data obtained from the model. This data is used to
     set the content for the view
     */
    private void setUpSingleBuildView(LinearLayout row, Table gameTable){

        FlexboxLayout flexboxLayout = null;

        int count = 0;
        for(Build b: gameTable.getSingleBuilds() ){

            FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFlexShrink(1);
            flexboxLayout = new FlexboxLayout(this);// FlexboxLayout) findViewById(R.id.flexbox_layout);
            flexboxLayout.setLayoutParams(layoutParams);

            addCardsToFlex(flexboxLayout, b.getBuildCards() );

            flexboxLayout.setId(count + TABLE_SINGLE_BUILD_OFFSET);
            count++;

            flexboxLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Code here executes on main thread after user presses button
                    recordSingleBuild(v);
                }
            });

            flexboxLayout.addView(setDescription(this,b.getOwner() + " build. Sum = " + b.getNumericValue() ));
            flexboxLayout.setPadding(20, 0, 20, 0);
            row.addView(flexboxLayout);
        }
    }

    /**
     set the content for a textview and return it
     @param text- String object that becomes the text view's text
     @param context
     @return recently created textview
     */

    private TextView setDescription(Context context, String text){
        TextView description = new TextView(this);
        description.setBackgroundColor(Color.BLACK);
        description.setClickable(false);
        description.setFocusable(false);
        description.setText(text);
        description.setTextColor(Color.WHITE);
        description.setLines(2);
        description.setMaxWidth(120);
        description.setGravity(Gravity.CENTER);
        return description;
    }

    /**
     Sets up a layout with some cards
     */

    private void addCardsToFlex(FlexboxLayout layout, ArrayList<Card> cards){
        int index = 0;

        for(Card card: cards ){
            ImageButton button = new ImageButton(this);

            String cardImg = card.toString().toLowerCase();
            int resId = getResources().getIdentifier( cardImg, "drawable", getPackageName());
            button.setBackgroundResource(resId);
            layout.addView(button);

            View view = layout.getFlexItemAt(index);
            FlexboxLayout.LayoutParams lp = (FlexboxLayout.LayoutParams) view.getLayoutParams();
            view.setLayoutParams(lp);

            // Setting setClickable and setFocusable to false will trigger parent's on clickListener when you touch
            // these textviews
            button.setClickable(false);
            button.setFocusable(false);

            index++;
        }
    }


    /**
     Responds to any button clicks for the buttons that represent view
     for a single build on the table
     @param view- the view of this activity
     */
    public void recordSingleBuild ( View view ){
        int singleBuildIndex = view.getId() - TABLE_SINGLE_BUILD_OFFSET;
        indexOfSingleBuilds.add(singleBuildIndex);
    }

    /**
     Responds to any button clicks for the buttons that represent view
     for a multi build on the table
     @param view- the view of this activity
     */
    public void recordMultiBuild( View view ){
        int multiBuildIndex = view.getId() - TABLE_MULTIBUILD_OFFSET;
        indexOfMultiBuilds.add(multiBuildIndex);
    }

    /**
     Responds to any button clicks for the buttons that represent view
     for a loose card on the table
     @param view- the view of this activity
     */
    public void recordTableLooseCard( View view ){
        int tableLooseCardIndex = view.getId() - TABLE_LOOSE_CARD_OFFSET;
        indexOfLooseTableCards.add(tableLooseCardIndex);
    }

    /**
     Responds to any button clicks for the buttons that represent view
     for a player's hand
     @param view- the view of this activity
     */
    public void recordHandCard(View view){
        int index = view.getId() - HAND_CARD_OFFSET;
        handCardIndex = index;
    }

    /**
     Sets up view for a player's hand
     */
    private void setUpHand(){
        ArrayList<Card> handCards = newTournament.getCurrentRound().getWhoIsPlaying().getHand();
        LinearLayout handView = (LinearLayout) findViewById(R.id.handLinearLayout);
        handView.removeAllViews();

        int count = 0;
        for(Card c: handCards){
            ImageButton button = new ImageButton(this);
            button.setId(count + HAND_CARD_OFFSET);

            // Setting appropriate image for the card
            String cardImg = c.toString().toLowerCase();
            int resId = getResources().getIdentifier( cardImg, "drawable", getPackageName());
            button.setBackgroundResource(resId);
            count++;

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Code here executes on main thread after user presses button
                    recordHandCard(v);
                }
            });

            handView.addView(button);
        }
    }

    /**
     Sets up view for players' tournament scores
     */
    private void setUpScores(){
        setUpTextBox( R.id.textViewHumanTournamentScore, "Human Tournament Score: " + newRound.getHumanTournamentScore());
        setUpTextBox(  R.id.textViewComputerTournamentScore, "Computer Tournament Score: " + newRound.getComputerTournamentScore() );
    }

    /**
     Sets text for a textview based on the textview's id
     @param id, an integer value that suggests which value to set the text for
     @param text- A String value that suggests what should the text of the textview be
     */
    private void setUpTextBox(int id, String text){
        TextView textView = (TextView) findViewById(id);
        textView.setText(text);
    }

    /**
     Sets text for a textview based on the textview's id
     @param cardList, a list of loose cards. This list is used to set the content for
     the view
     @param offset- an integer variable used to set the id for Imagebutton views created here
     @param row - a Linear layout upon which views created in this function are attached to
     */
    private void setUpCardLooseCardButtons(ArrayList<Card> cardList, int offset, LinearLayout row){
        int count = 0;

        for(Card c: cardList){
            ImageButton button = new ImageButton(this);
            button.setId(count + offset);

            String cardImg = c.toString().toLowerCase();
            int resId = getResources().getIdentifier( cardImg, "drawable", getPackageName());
            button.setBackgroundResource(resId);
            count++;

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Code here executes on main thread after user presses button
                    recordTableLooseCard(v);
                }
            });

            row.addView(button);
        }
    }

    /**The function below returns a button size that adjusts appropriately with adjustments to screen resolution
     @return - an integer that denotes a button size
     */
    private int getButtonSize(){
        int density= getResources().getDisplayMetrics().densityDpi;
        int size =150;
        switch(density)
        {
            case DisplayMetrics.DENSITY_LOW:
                size = 100;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                size = 100;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                size = 150;
                break;
        }

        return size;
    }


    /**
     Called when the user taps either Build, Capture or Trail buttons. This function
     calls a model function to execute the moves the user chose to execute
     @param action, an integer that represents what action (Build, capture or trail)
     the user selected
     */
    private void makeMove(int action){

        Input input = new Input(indexOfLooseTableCards, indexOfSingleBuilds, indexOfMultiBuilds, handCardIndex, action, "");
        Player currentPlayer = newRound.getWhoIsPlaying();
        currentPlayer.setInput(input);

        boolean legitMoveMade = newRound.makeAMove( currentPlayer );
        if( !legitMoveMade ){
            showToast("Invalid Move!");
//            showMoveSummary();
            refreshView();
            return;
        }

        showMoveSummary();
        newRound.continueTheRound();

        if( newRound.roundOver() )
            openDialog();

        refreshView();
    }

    /**
     This function shows displays summary for the move the player made
     */
    private void showMoveSummary(){
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.mainPageConstraintGameActivity), newRound.getMoveSummary(), Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        View snackbarView = snackbar.getView();
        TextView tv= snackbarView.findViewById(android.support.design.R.id.snackbar_text); //
        tv.setMaxLines(3);

        LinearLayout linearLayout = findViewById(R.id.bottomPageLinear);
        TextView temp = new TextView(this);
        temp.setText(newRound.getMoveSummary() + "\n");
        linearLayout.addView(temp);

        snackbar.show();
    }

    /**
     This function opens and sets a dialog box that displays information about end of a game round
     */
    private void openDialog(){
        //Dialog
        final Dialog dialog = new Dialog(GameActivity.this);
        dialog.setTitle("Round Over: ");
        dialog.setContentView(R.layout.layout_dialog);

        // Display pile cards
        addCardImagesToLayout( (LinearLayout) dialog.findViewById( R.id.humanPileLinearLayout), newRound.getHuman().getPile() );
        addCardImagesToLayout( (LinearLayout) dialog.findViewById( R.id.compPileLinearLayout), newRound.getComputer().getPile() );

        Human human = newRound.getHuman();
        Computer computer = newRound.getComputer();

        //Set up text boxes
        setUpTextBoxDialog( R.id.humanRoundScore, "Human Round Score: " + human.getScore(), dialog);
        setUpTextBoxDialog( R.id.computerRoundScore, "Computer Round Score: " + computer.getScore(), dialog);
        setUpTextBoxDialog( R.id.humanPileCount, "Human Pile Count: " + human.getPile().size(), dialog);
        setUpTextBoxDialog( R.id.computerPileCount, "Computer Pile Count: " + computer.getPile().size() + "", dialog);
        setUpTextBoxDialog( R.id.numSpadesHuman, "Num Spades Human: " + human.getNumSpades(), dialog);
        setUpTextBoxDialog( R.id.numSpadesComputer, "Num Spades Computer: " + computer.getNumSpades(), dialog);
        setUpTextBoxDialog( R.id.tournamentScoreHuman, "Tournament Score Human: " + ( human.getScore() + newRound.getHumanTournamentScore() ), dialog );
        setUpTextBoxDialog( R.id.tournamentScoreComputer, "Tournament Score Computer: " + ( computer.getScore() + newRound.getComputerTournamentScore() ), dialog );
        setUpTextBoxDialog( R.id.roundWinner, "Round Winner: " + newRound.getWinner(), dialog);

        dialog.setCanceledOnTouchOutside(false);

        Button okay = dialog.findViewById(R.id.okayButton);
        //on click listener to OK button
        okay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //close the dialog
                startNewRound();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     This function opens GameOVerActivity if the tournament is over. Else sets up a new round based
     on the model
     */
    private void startNewRound(){
        // Then start a new round through tournament class
        if( !newTournament.startNewRound() ){
            endTournament();
        }
        else{
            newRound = newTournament.getCurrentRound();
            newRound.setTheRoundUp();
            refreshView();
        }
    }

    /**
     This function opens GameOverActivity and passes about some game state to the activity
     based on information obtained from the model class
     */
    private void endTournament(){
        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra( HUMAN_FINAL_TOURNAMENT_SCORE, newTournament.gethumanPlayer().getScore() + "" );
        intent.putExtra( COMPUTER_FINAL_TOURNAMENT_SCORE, newTournament.getComputerPlayer().getScore() + "" );
        intent.putExtra( TOURNAMENT_WINNER, newTournament.getResult() );
        startActivity(intent);
    }

    /**
     This function adds imageviews to a linear layout
    @param - layout, a linear layout to which dynamically created image views are added to
     @param - cards, an Arraylist of card objects used to set the content for dynamically
     created imageviews
     */
    private void addCardImagesToLayout( LinearLayout layout, ArrayList <Card> cards){
        for (Card i: cards ){
            ImageView pileCard = new ImageView(this);
            // Set image view size and margins
            String cardImg = i.toString().toLowerCase();
            int resId = getResources().getIdentifier( cardImg, "drawable", getPackageName());
            pileCard.setBackgroundResource(resId);
            layout.addView(pileCard);
        }
    }

    /**
     This function is used to set up content of textboxes inside the dialog box created in this activity
     @param - id, of the textbox to set content on
     @param - d, a Dialog box object used to identify the dialog box
     @param - text- a String variable whose value is used to set the content for textboxes in this
     function
     */
    private void setUpTextBoxDialog(int id, String text, Dialog d){
        TextView textView = (TextView) d.findViewById(id);
        textView.setText(text);
    }

    /**
     This function shows a toast if player made an invalid moove
     @param -text, a CharSequence whose content is used to set the text for the toast
     */
    private void showToast(CharSequence text){
        Context context = getApplicationContext();

        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
