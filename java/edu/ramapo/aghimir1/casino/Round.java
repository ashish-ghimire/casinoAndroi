package edu.ramapo.aghimir1.casino;

import android.os.Environment;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static edu.ramapo.aghimir1.casino.Strategy.BUILD;
import static edu.ramapo.aghimir1.casino.Strategy.CAPTURE;
import static edu.ramapo.aghimir1.casino.Strategy.TRAIL;

public class Round implements Serializable {

    /* *********************************************
    Symbolic constants
    ********************************************* */
    //If turn is true, it is human player's turn. If false, it is computer player's turn
    private static boolean m_turn = true;
    private static int roundCount = 0;

    /* *********************************************
    Class member variables
    ********************************************* */
    private Deck m_deckForTheRound;
    private Table m_table;
    private Player m_playerWhoCapturedLast;
    private Computer m_computerPlayer;
    private Human m_humanPlayer;
    private int m_humanTournamentScore;
    private int m_computerTournamentScore;
    private String m_winner;
    private StringBuilder m_move_summary;

    /* *********************************************
    Constructors
    ********************************************* */
    /**
     @param humanTournamentScore - An integer variable containing human player's tournament score
     @param computerTournamentScore - An integer variable containing computer player's tournament score
     */
    Round(int humanTournamentScore, int computerTournamentScore){
        m_playerWhoCapturedLast = null;
        //m_deckForTheRound = new Deck(true, "populateTheDeck.txt");
        m_deckForTheRound = new Deck();
        m_computerPlayer = new Computer();
        m_humanPlayer = new Human();
        m_table = new Table();
        m_humanTournamentScore = humanTournamentScore;
        m_computerTournamentScore = computerTournamentScore;
        m_winner = "It's a tie!";
        m_move_summary = new StringBuilder("");
    }

    /**
     * Intialize the class's member variables
     @param deck - A Deck object for one round
     @param capturedLast - A reference to a player (human or computer). Determines who captured last
     in the Round being initialized
     @param compPlayer - A Computer object which serves as the Computer player for one round
     @param humanPlayer - A human object which serves as the human player for one round
     @param humanTournamentScore - An integer variable containing human player's tournament score
     @param computerTournamentScore - An integer variable containing computer player's tournament score
     @param table - A Table object that serves as the table for one round
     @param round_count - An integer used to what round number the game is playing
     @param nextPlayer - A String variable that could be "Human or "Comuter". USed to determine
     who plays next in the round
     */
    Round(Deck deck, Table table, Player capturedLast, Computer compPlayer, Human humanPlayer, int round_count, String nextPlayer, int humanTournamentScore, int computerTournamentScore){
        m_table = table;
        m_playerWhoCapturedLast = capturedLast;
        m_computerPlayer = compPlayer;
        m_humanPlayer = humanPlayer;
        roundCount = round_count;
        m_deckForTheRound = deck;
        setTurn(nextPlayer);
        m_humanTournamentScore = humanTournamentScore;
        m_computerTournamentScore = computerTournamentScore;
        m_winner = "It's a tie!";
        m_move_summary = new StringBuilder("");
    }

    /* *********************************************
    Selectors
    ********************************************* */

    /**
     Selector that returns the human object of the round
     @return returns the human object of the round
     */
    public Human getHuman() { return m_humanPlayer; }

    /**
     Selector that returns the computer object of the round
     @return returns the computer object of the round
     */
    public Computer getComputer() { return m_computerPlayer; }

    /**
     Selector that returns the table object of the round
     @return returns the table object of the round
     */
    public Table getTable() { return m_table; }

    /**
     Selector that returns the deck object of the round
     @return returns the deck object of the round
     */
    public Deck getDeck() { return m_deckForTheRound; }

    /**
     Selector that lets the caller know who is playing
     @return true if it's human turn in the round. false otherwise
     */
    public boolean getTurn(){ return m_turn; }

    /**
     Selector that lets the caller know human player's tournament score
     @return an integer that is human player's tournament score
     */
    public int getHumanTournamentScore(){ return m_humanTournamentScore; }

    /**
     Selector that lets the caller know computer player's tournament score
     @return an integer that is computer player's tournament score
     */
    public int getComputerTournamentScore() { return m_computerTournamentScore; }

    /**
     Selector
     @return an integer that is the current round number (how many rounds have
     already played in a tournament)
     */
    public int getRoundCount() { return roundCount; }

    /**
     Get the player whose turn is to play
     @return human object is it is human's turn. Computer object if it
     is Computer's turn
     */
    public Player getWhoIsPlaying() {
        if (m_turn)
            return m_humanPlayer;
        return m_computerPlayer;
    }

    /**
     Selector
     @return result indicating the result of the round. Either one of the two players
     wins a round or it ends in a tie
     */
    public String getWinner() {
        return m_winner;
    }

    /**
     Summary of all the mode a player made in one turn
     @return a string with Summary of all the mode a player made in one turn
     */
    public String getMoveSummary() {
        return m_move_summary.toString();
    }

    /**
     Sets which player plays next
     @param - string. If string is "Human," human player will play next. Else
     computer player will play next
     */
    private void setTurn(String whoseTurn){
        if( whoseTurn.equalsIgnoreCase(m_humanPlayer.getPlayerType()) )
            m_turn = true;
        else
            m_turn = false;
    }

    /* *********************************************
     Main function that could be used for testing
    ********************************************* */
    public static void main(String [] args){

    }

    /**
     Perform a toss to figure out the first player in a new round based
     on user's toss choice
     @param - string that contains human's toss choice (Heads or Tails)
     computer player will play next
     */
    public String toss(String headsOrTails){
        Random rand = new Random();
        int toss = rand.nextInt(1);
        int humanPlayersTossChoice = headsOrTails.equalsIgnoreCase("Heads") ? 0: 1;
        String tossResult = toss == 0?"Heads": "Tails";

        if(toss == humanPlayersTossChoice){
            m_turn = true;
        }
        else {
            m_turn = false;
        }

        return tossResult;
    }


    /**
     Function that deals 4 cards first to human player, then to computer player,
     then adds 4 cards to the table as loose table cards
     */
    public void setTheRoundUp(){
        //Initially, deal first four cards to the human player
        dealCards(m_humanPlayer, 4);

        //Then, deal the next four cards to the computer player
        dealCards(m_computerPlayer, 4);

        // Place the next four cards in the deck face up on the table
        for(int i = 0; i < 4; i++){
            Card c = m_deckForTheRound.getNextCard();
            m_table.addALooseCard(c);
        }
    }

    /**
     Function that deals certain number of cards to certain player
     @param playerToDealCardTo - what player the function should deal cards to
     @param numCardsToDeal - an integer. how many cards the function should deal to
     a player
     */
    private void dealCards(Player playerToDealCardTo, int numCardsToDeal){
        for(int i = 0; i < numCardsToDeal; i++){
            Card c = m_deckForTheRound.getNextCard();
            playerToDealCardTo.addToHand(c);
        }
    }

    /**
     Function that makes moves (build, capture or trail) based on a player's game input data
     for a player's turn
     @param aPlayer- player that is making the move
     @return  - true if the player made a valid move. false if invalid move
     */
    public boolean makeAMove(Player aPlayer){
        Player opponent = getOpponentPlayer(aPlayer);
        m_move_summary = new StringBuilder("");

        Strategy moveStrategy = new Strategy(m_table, aPlayer.getHand(), opponent.getBuild(), opponent.getMultiBuild(), aPlayer.getAllHoldCards() );
        Input playersInput = aPlayer.getInput( moveStrategy );

        // Make sure the user has selected a legitimate card from his hand
        int handCardIndex = playersInput.getHandCardIndex();
        if(Input.INVALID_INDEX == handCardIndex )
            return false;

        m_move_summary.append("The " + aPlayer.getPlayerType() + " player chose to play " + aPlayer.getHand().get(handCardIndex).getStringRepresentation() + " to ");

        boolean validCapture = false,
                validBuild = false,
                validTrail = false;

        switch ( playersInput.getAction() ){
            case BUILD:
                validBuild = build(aPlayer, moveStrategy);
                break;
            case CAPTURE:
                validCapture = capture(aPlayer, moveStrategy);
                break;
            case TRAIL:
                validTrail = trail(aPlayer, moveStrategy);
                break;
        }

        boolean validMove = validCapture || validTrail || validBuild;

        if( validMove ) {
            m_turn = !m_turn; //Change turn
            m_move_summary.append( playersInput.getRationale() );
        }

        return validMove;
    }

    /**
     Returns the opponent player based on aPlayer
     @param aPlayer- player whose opponent needs to be figured out
     @return the Round's human member variable. Else the round's computer player
     */
    private Player getOpponentPlayer(Player aPlayer){
        if(aPlayer == m_computerPlayer)
            return m_humanPlayer;
        return m_computerPlayer;
    }

    /**
     Save game stats to a text file
     */
    public void saveGame(){
        ArrayList <MultiBuild> tableMultiBuilds = m_table.getMultiBuilds();
        ArrayList <Build> tableSingleBuilds = m_table.getSingleBuilds();

        String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + "savedGame.txt";

        try {
            Formatter f = new Formatter( fileName );
            f.format("%s %d %s", "Round: ", roundCount, "\n");
            savePlayerStats(m_computerPlayer, f, m_computerTournamentScore);
            savePlayerStats(m_humanPlayer, f, m_humanTournamentScore);

            f.format("%s", "\nTable: ");
            m_table.writeTableToFile( f );
            f.format("%c", '\n');

            // Write all table multi builds to file
            for (MultiBuild i: tableMultiBuilds){
                f.format("%s", "\nBuild Owner: ");
                i.writeToFile(f);
                f.format("%c %s", ' ', i.getOwner() );
            }

            // Write all table single builds to file
            for (Build i: tableSingleBuilds ){
                f.format("%s", "\nBuild Owner: ");
                i.writeToFile(f);
                f.format("%c %s", ' ', i.getOwner() );
            }

            if(m_playerWhoCapturedLast != null)
                f.format("%s %s", "\n\nLast Capturer:", m_playerWhoCapturedLast.getPlayerType() );

            f.format( "%s", "\n\nDeck: " );
            m_deckForTheRound.writeDeckCardsToFile(f);
            f.format("%s %s", "\n\nNext Player:", getWhoIsPlaying().getPlayerType() );

            f.close();
        } catch (Exception e) {
            System.out.println("Error");
        }
    }


    /**
     Save information(hand, pile, score) about a player to a file
     @param aPlayer- player whose information is saved into a file
     @param f - a Formatter object that indicates where in a file the function
     should write player data to
     @param playerTournamentScore - an integer variable indicating a player's tournament score
     */
    private void savePlayerStats(Player aPlayer, Formatter f, int playerTournamentScore ){

        f.format("%s", "\n" + aPlayer.getPlayerType() + ":\n");
        f.format("%s %d %s", "\tScore:", playerTournamentScore, "\n" );
        f.format("%s", "\tHand: ");
        aPlayer.writeHandToFile( f );
        f.format("%s", "\tPile: ");
        aPlayer.writePileToFile( f );
    }


    /**
     Get help for a player to make the optimal move for a turn
     @return - a String containing instructions for current player to make
     optimal move in a turn
     */
    public String help() {
        Player player = getWhoIsPlaying();
        Player opponent = getOpponentPlayer(player);

        Strategy moveStrategy = new Strategy(m_table, player.getHand(), opponent.getBuild(), opponent.getMultiBuild(), player.getAllHoldCards() );

        return player.getHelp(moveStrategy);
    }

    /**
     Execute capture action for a player. A player can capture multibuilds,
     single builds or sets (set of cards or loose cards)
     @param aPlayer - player who is trying to capture
     @param moveStrategy - strategy player may use to help him capture
     @return - true if the player successfully executed at least one
     type of capture. false otherwise
     */
    private boolean capture(Player aPlayer, Strategy moveStrategy){
        Input playersInput = aPlayer.getInput(moveStrategy);
        final int handCardIndex = playersInput.getHandCardIndex();
        final Card handCard = aPlayer.getHand().get(handCardIndex);
        boolean captureMulti = false;
        boolean captureSingle = false;
        boolean captureLooseCards = false;
        boolean validCapture;

        // Figure out if the user can capture multibuild
        if( !playersInput.getIndexOfMultiBuilds().isEmpty() ) {
            if( canCaptureMultiBuilds(aPlayer, playersInput) ){
                // Capture multibuild
                m_move_summary.append(" capture the following multibuilds: ");
                captureMulti = captureMultiBuild(aPlayer, moveStrategy);
            }
        }

        // Figure out if the user can capture singleBuild
        if( !playersInput.getIndexOfSingleBuilds().isEmpty() ) {
            if( canCaptureSingleBuilds(aPlayer, playersInput) ){
                // Capture single build
                m_move_summary.append(" capture the following single builds: ");
                captureSingle =  captureSingleBuild(aPlayer, moveStrategy);
            }
        }

        if( !playersInput.getIndexOfLooseTableCards().isEmpty() )
            captureLooseCards = captureSets(aPlayer, moveStrategy);

        validCapture = captureMulti || captureSingle || captureLooseCards;
        if(validCapture){
            aPlayer.addToPile(handCard);
            aPlayer.removeHoldCard( handCardIndex );
            aPlayer.removeFromHand( handCardIndex );
            m_playerWhoCapturedLast = aPlayer;
        }

        return validCapture;
    }

    /**
     Determines if a player can capture multibuilds with the input they selected
     @param aPlayer - player who is trying to capture
     @param playersInput - an Input object containing info about the moves player
     wants to make
     @return - true if the player successfully can capture all multibuilds they selected.
     false otherwise
     */

    private boolean canCaptureMultiBuilds(Player aPlayer, Input playersInput){
        if( playersInput.getIndexOfMultiBuilds().isEmpty() )
            return false;

        int handCardIndex = playersInput.getHandCardIndex();
        Card handCard = aPlayer.getHand().get(handCardIndex);

        for( int i: playersInput.getIndexOfMultiBuilds() ){
            MultiBuild toCheck = m_table.getMultiBuilds().get(i);
            if( !(toCheck.getNumericValue() == handCard.getNumericValue(true) ) )
                return false;
        }
        return true;
    }

    /**
     Enable a player to capture multibuilds with the input they selected
     @param aPlayer - player who is trying to capture
     @param moveStrategy - strategy player may use to help him capture
     @return - true if the player successfully can capture at least one multibuild
     they selected. false otherwise
     */

    private boolean captureMultiBuild(Player aPlayer, Strategy moveStrategy){
        if( m_table.getMultiBuilds().isEmpty() )
            return false;

        Input playerInput = aPlayer.getInput(moveStrategy);
        ArrayList <Integer> tableMultiBuildIndices = playerInput.getIndexOfMultiBuilds();

        if(tableMultiBuildIndices.size() > m_table.getMultiBuilds().size())
            return false;

        int numMultiBuildsCaptured = 0;
        int previousIndex = 0;
        boolean holdCard = false;

        for(int i = 0; i < tableMultiBuildIndices.size(); i++){
            int tableMultiBuildIndex = tableMultiBuildIndices.get(i);

            if(tableMultiBuildIndex > previousIndex)
                tableMultiBuildIndex -= 1;

            MultiBuild selectedMultiBuild = m_table.getMultiBuilds().get( tableMultiBuildIndex );

            // Check if the card that the player is trying to play is a hold card
            holdCard = aPlayer.isHoldCard( playerInput.getHandCardIndex() );
            if(holdCard && !aPlayer.hasAGivenMultiBuild(selectedMultiBuild) )
                break;

            int buildValue = selectedMultiBuild.getNumericValue();
            int cardNumericValue = aPlayer.getHand().get(playerInput.getHandCardIndex()).getNumericValue(true);

            if(cardNumericValue == buildValue){
                // Add the cards in the multi build to the players pile
                aPlayer.addToPile(selectedMultiBuild);

                // Remove the reference the table has to the multi build
                m_table.removeAMultiBuild(tableMultiBuildIndex);

                // Remove the reference that the player has to the multi build
                if(aPlayer.getPlayerType().equalsIgnoreCase( selectedMultiBuild.getOwner() ))
                    aPlayer.removeMultiBuild(selectedMultiBuild);
                else
                    getOpponentPlayer(aPlayer).removeMultiBuild(selectedMultiBuild);

                numMultiBuildsCaptured++;
                m_move_summary.append(selectedMultiBuild.toString() + ", ");
            }
            else
                break;
            previousIndex = tableMultiBuildIndex;
        }

        if(numMultiBuildsCaptured > 0)
            return true;

        return false;
    }

    /**
     Determines if a player can capture single builds with the input they selected
     @param aPlayer - player who is trying to capture
     @param playersInput - an Input object containing info about the moves player
     wants to make
     @return - true if the player successfully can capture all single builds they selected.
     false otherwise
     */

    private boolean canCaptureSingleBuilds(Player aPlayer, Input playersInput ){
        if( playersInput.getIndexOfSingleBuilds().isEmpty() )
            return false;

        int handCardIndex = playersInput.getHandCardIndex();
        Card handCard = aPlayer.getHand().get(handCardIndex);

        for( int i: playersInput.getIndexOfSingleBuilds() ){
            Build toCheck = m_table.getSingleBuilds().get(i);
            if( !(toCheck.getNumericValue() == handCard.getNumericValue(true) ) )
                return false;
        }
        return true;
    }

    /**
     Enable a player to capture single builds with the input they selected
     @param aPlayer - player who is trying to capture
     @param moveStrategy - strategy player may use to help him capture
     @return - true if the player successfully can capture at least one single build
     they selected. false otherwise
     */

    private boolean captureSingleBuild(Player aPlayer, Strategy moveStrategy){
        if( m_table.getSingleBuilds().isEmpty() )
            return false;

        Input playersInput = aPlayer.getInput(moveStrategy);
        ArrayList<Integer> tableBuildIndices = playersInput.getIndexOfSingleBuilds();
        if(tableBuildIndices.size() > m_table.getSingleBuilds().size())
            return false;

        int numBuildsCaptured = 0;
        int previousIndex = Integer.MAX_VALUE;
        boolean holdCard =false;

        for(int i = 0; i < tableBuildIndices.size(); i++){
            int tableBuildIndex = tableBuildIndices.get(i);

            if(tableBuildIndex > previousIndex)
                tableBuildIndex -= 1;

            Build selectedBuild = m_table.getSingleBuilds().get( tableBuildIndex );

            // Check if the card that the player is trying to play is a hold card
            holdCard = aPlayer.isHoldCard( playersInput.getHandCardIndex() );
            if(holdCard && !aPlayer.hasAGivenSingleBuild(selectedBuild) )
                break;

            int buildValue = selectedBuild.getNumericValue();
            int cardNumericValue = aPlayer.getHand().get(playersInput.getHandCardIndex()).getNumericValue(true); //true means the value of ace should be 14

            if(cardNumericValue == buildValue) // The player can capture the build
            {
                //Add the cards in the build to the player's pile
                aPlayer.addToPile(selectedBuild);

                // Remove the reference the table has to the build
                m_table.removeABuild(tableBuildIndex);

                // Remove the reference that the player has to the build
                if(aPlayer.getPlayerType().equalsIgnoreCase( selectedBuild.getOwner() ) )
                    aPlayer.removeBuild(selectedBuild);
                else
                    getOpponentPlayer(aPlayer).removeBuild(selectedBuild);

                numBuildsCaptured++;
                m_move_summary.append(selectedBuild.toString() + ", ");
            }
            else
                break;
            previousIndex = tableBuildIndex;
        }

        if(numBuildsCaptured > 0)
            return true;

        return false;
    }

    /**
    Determines if a player can capture loose cards/ sets of cards  with the input they selected
    @param aPlayer - player who is trying to capture
    @param playersInput - an Input object containing info about the moves player
    wants to make
    @return - true if the player successfully can capture all loose cards/ sets of cards  they selected.
     false otherwise
     */

    private boolean canCaptureLooseCards( Player aPlayer, Input playersInput ){
        ArrayList<Integer> indexOfLooseCards = playersInput.getIndexOfLooseTableCards();

        if( indexOfLooseCards.isEmpty() )
            return false;

        int handCardIndex = playersInput.getHandCardIndex();
        Card handCard = aPlayer.getHand().get(handCardIndex);

        return canCaptureSets(playersInput, handCard.getNumericValue() ) || canCaptureSets(playersInput, handCard.getNumericValue(true) );
    }

    /**
     Determines if a player can capture sets of cards  with the input they selected
     @param target - sum of all set of cards should equal to this value's variables
     @param playersInput - an Input object containing info about the moves player
     wants to make
     @return - true if the player successfully can capture all sets of cards  they selected.
     false otherwise
     */
    private boolean canCaptureSets( Input playersInput, int target){
        ArrayList<Card> tableLooseCards = m_table.getLooseCards();
        ArrayList<Integer> looseCardsIndicesUserSelected = playersInput.getIndexOfLooseTableCards();

        int sum = 0;
        int index = 0;

        while(index < looseCardsIndicesUserSelected.size() ){
            Card c = tableLooseCards.get(looseCardsIndicesUserSelected.get(index));
            sum += c.getNumericValue();
            if(sum > target)
                return false;

            if(sum == target)
                sum = 0;

            index++;
        }

        if( sum != 0)
            return false;

        return true;
    }


    /**
     Enforces this rule: If the player wants to capture, the player must capture all loose cards
     whose value is equal to the numeric value of the played card
     @param handCard - the card the player played in his current turn
     @param indexOfLooseCardsUserSelected - The are the indices of the loose cards the player
     selected to capture
     @return - true if the player has selected to capture all loose cards
     whose value is equal to the value of the hand(played)card
     */
    private boolean capturesAllIndividualLooseCards( Card handCard, ArrayList<Integer> indexOfLooseCardsUserSelected ){

        if( handCard.getFaceValue() != handCard.ACE ){
            ArrayList<Integer> indicesOfLooseCardsWithGivenNumericValue = m_table.getLooseCardsWithGivenNumericValue( handCard.getNumericValue() );
            for(Integer i: indicesOfLooseCardsWithGivenNumericValue ){
                if( !indexOfLooseCardsUserSelected.contains(i) ){
                    return false;
                }

            }
        }

        return true;

    }

    /**
     Enable a player to capture loose cards with the input they selected
     @param aPlayer - player who is trying to capture
     @param moveStrategy - strategy player may use to help him capture
     @return - true if the player successfully can capture at least one multibuild
     they selected. false otherwise
     */

    private boolean captureLooseTableCards( Player aPlayer, Strategy moveStrategy ) {
        Input playersInput = aPlayer.getInput(moveStrategy);
        int handCardIndex = playersInput.getHandCardIndex();

        if( aPlayer.isHoldCard( handCardIndex ) )
            return false;

        ArrayList <Integer> tableCardsThatMustBeCaptured = playersInput.getIndexOfLooseTableCards();
        for(Integer i: tableCardsThatMustBeCaptured){
            Card c = m_table.getLooseCards().get(i);
            aPlayer.addToPile(c);
            m_move_summary.append( c.getStringRepresentation() + ", " );
        }

        if(tableCardsThatMustBeCaptured.size() > 0)
            m_move_summary.delete(m_move_summary.length() - 2, m_move_summary.length() );

        //Remove the captured cards from the table
        return removeTableCards(tableCardsThatMustBeCaptured);
    }

    /**
     Enable a player to capture sets of loose cards with the input they selected
     @param aPlayer - player who is trying to capture
     @param moveStrategy - strategy player may use to help him capture
     @return - true if the player successfully can capture at least one multibuild
     they selected. false otherwise
     */

    private boolean captureSets( Player aPlayer, Strategy moveStrategy ){
        boolean captured = false;

        // Check if the user has selected to capture all loose cards whose value is
        // equal to the value of handCard
        Input tempInput = aPlayer.getInput(moveStrategy);
        Card tempHandCard = aPlayer.getHand().get(tempInput.getHandCardIndex());

        if( !capturesAllIndividualLooseCards(tempHandCard, tempInput.getIndexOfLooseTableCards() ) ){
            return false;
        }

        moveStrategy.setTable( m_table );
        moveStrategy.recalculateSetOfLooseCards();

        Input playersInput = aPlayer.getInput( moveStrategy );
        ArrayList<Integer> setOfCardsToCapture = playersInput.getIndexOfLooseTableCards();

        while( !setOfCardsToCapture.isEmpty() ){

            // You must optimize the statements part of this if statement below
            if( canCaptureLooseCards( aPlayer, playersInput ) ){
                m_move_summary.append(" capture the following cards from the table: ");
                captureLooseTableCards( aPlayer, moveStrategy );
                captured = true;
            }

            aPlayer.setInput(new Input());

            moveStrategy.setTable(m_table);
            moveStrategy.recalculateSetOfLooseCards();
            playersInput = aPlayer.getInput( moveStrategy );
            setOfCardsToCapture = playersInput.getIndexOfLooseTableCards();
        }

        return captured;
    }

    /**
     Remove all loose cards in a collection from the table
     @param setOfCards - This arraylist contains indices of loose cards that should be
     removed from the table
     @return - true if at least one loose card was removed from the table
     */

    private boolean removeTableCards( ArrayList<Integer> setOfCards){

        if(setOfCards.size() == 0)
            return false;

        int previousIndex = Integer.MAX_VALUE;

        for(int i = 0; i < setOfCards.size(); i++) {
            int index = setOfCards.get(i);

            if(index > previousIndex){
                m_table.removeALooseCard(index - i);
            }
            else{
                m_table.removeALooseCard(index);
            }
            previousIndex = index;
        }

        return true;
    }

    /**
     Enable a player to exercicse one of the build options: create a single build,
     create a multibuild or increase an opponent's build
     @param aPlayer - player who is trying to build
     @param moveStrategy - strategy player may use to help him capture
     @return - true if the player exerised any one of the build options successfully
     */

    private boolean build(Player aPlayer, Strategy moveStrategy){
        Input playersInput = aPlayer.getInput(moveStrategy);
        boolean legitBuild = false;

        boolean increaseOpponentsBuild = false;
        boolean createSingleBuild = false;

        // If the user wants to increase an opponent's builds, he can only extend
        // one build at a time
        if( playersInput.getIndexOfSingleBuilds().size() == 1  ){
            legitBuild = increaseBuildValue(aPlayer, moveStrategy);
        }

        if( !legitBuild ){
            if( !playersInput.getIndexOfLooseTableCards().isEmpty() )
                legitBuild = createSingleBuild(aPlayer, moveStrategy);

        }

        if( !legitBuild )
            legitBuild = createMultiBuild(aPlayer, moveStrategy);

        return legitBuild;

//        return increaseOpponentsBuild || createSingleBuild;
    }

    /**
     Enable a player to create a single build. If the player already has a
     single build or a multibuild whose numeric equal to that of the newly
     created single build, a multibuild is created instead and the newly
     created single build will be added to the multibuild
     @param aPlayer - player who is trying to build
     @param moveStrategy - strategy player may use to help him capture
     @return - true if the player exerised any one of the build options successfully
     */

    private boolean createSingleBuild(Player aPlayer, Strategy  moveStrategy){

        Input input = aPlayer.getInput(moveStrategy);
        int cardFromHandIndex = input.getHandCardIndex();

        if( aPlayer.isHoldCard(cardFromHandIndex) )
            return false;

        ArrayList <Integer> indexOfLooseCards = input.getIndexOfLooseTableCards();

        if( !verifySingleBuild(indexOfLooseCards, cardFromHandIndex, aPlayer) )
            return false;

        //If there is already an existing build whose numeric value is equal to that of the current build, create a multi build instead
        int sumOfCurrentBuild = sum(indexOfLooseCards) + aPlayer.getHand().get(cardFromHandIndex).getNumericValue();
        int oldBuildIndex = aPlayer.getBuildIndex(sumOfCurrentBuild);
        int oldMultiBuildIndex = aPlayer.getMultiBuildIndex(sumOfCurrentBuild);

        //MEaning the player already has a multibuild whose value == value of our new build
        if(oldMultiBuildIndex != aPlayer.getInvalidIndex() ){
            Build newBuild = getNewlyCreatedSingleBuild(indexOfLooseCards, cardFromHandIndex, aPlayer);
            aPlayer.getMultiBuild().get(oldMultiBuildIndex).addToMultiBuild(newBuild);
            aPlayer.setHoldCardsForMultiBuild( newBuild.getNumericValue() );
            m_move_summary.append(" create the following multibuild, " + aPlayer.getMultiBuild().get(oldMultiBuildIndex).toString() );
            return removeTableCards(indexOfLooseCards);
        }

        //If there is already an existing build whose numeric value is equal to that of the current build, create a multi build instead
        if(oldBuildIndex != aPlayer.getInvalidIndex()){
            Build  newBuild = getNewlyCreatedSingleBuild(indexOfLooseCards, cardFromHandIndex, aPlayer);
            successfulMultiBuild(aPlayer, cardFromHandIndex, oldBuildIndex, indexOfLooseCards, newBuild);
            return true;
        }

        // If the player does not have any single or multi build whose numeric value == that of the new potential build, just create a single build
        return successfulSingleBuild( indexOfLooseCards, cardFromHandIndex, aPlayer );
    }


    /**
     To check if the sum of numeric loose cards and played card
     is equal to the value of a card in the player's hand
     @param aPlayer - player who is trying to build
     @param cardFromHandIndex - an integer passed by value  that represents
     the index of a card in the player's hand
     @param indexOfLooseCards - table indices of all loose cards that will be used to
     create a build
     @return - true if the player exerised any one of the build options successfully
     */

    private boolean verifySingleBuild(ArrayList<Integer> indexOfLooseCards, int cardFromHandIndex, Player aPlayer){
        if( indexOfLooseCards.size() < 1)
            return false;

        int sumOfSelectedLooseCards = 0;

        for(int i = 0; i < indexOfLooseCards.size(); i++)
            sumOfSelectedLooseCards += ( m_table.getLooseCards().get(indexOfLooseCards.get(i)) ).getNumericValue();

        int sumOfLooseCardsAndCardFromHand = sumOfSelectedLooseCards + aPlayer.getHand().get(cardFromHandIndex).getNumericValue();

        return aPlayer.handHasACardWithAGivenNumericValue(cardFromHandIndex, sumOfLooseCardsAndCardFromHand, true );
    }

    /**
     To check if the sum of numeric loose cards and played card
     is equal to the value of a card in the player's hand
     @param a_ArrayList - contains integer of indices of
     loose cards on the table
     @return - an integer value that represents sum of numeric values
     of cards represented by indices in a_vector
     */

    private int sum(ArrayList <Integer> a_ArrayList){
        int sum1 = 0;
        ArrayList<Card> tableLooseCards = m_table.getLooseCards();

        for(int i = 0; i < a_ArrayList.size(); i++){
            int index = a_ArrayList.get(i);
            sum1 += tableLooseCards.get(index).getNumericValue();
        }
        return sum1;
    }

    /**
     To create a single build using the hand card and the loose cards
     the player selected
     @param indexOfLooseCards - contains integer of indices
     of loose cards on the table
     @param cardFromHandIndex - hand index of the card the player hand
     @param aPlayer - player who is trying to build
     @return - an integer value that represents sum of numeric values
     of cards represented by indices in a_vector
     */

    private Build getNewlyCreatedSingleBuild(ArrayList<Integer>  indexOfLooseCards, int cardFromHandIndex, Player aPlayer){
        //Create a single build
        Build  singleBuild = new Build();

        singleBuild.setOwner( aPlayer.getPlayerType() );

        // Add the card that the player played to the build
        Card  a = aPlayer.getHand().get(cardFromHandIndex);
        singleBuild.addCardToABuild(a);

        // Add the looseCards on the table to the build
        for(int i = 0; i < indexOfLooseCards.size(); i++){
            int looseCardIndex = indexOfLooseCards.get(i);
            Card  b = m_table.getLooseCards().get(looseCardIndex);
            singleBuild.addCardToABuild(b);
        }

        aPlayer.removeHoldCard(cardFromHandIndex);
        // Remove card from the player's hand
        aPlayer.removeFromHand(cardFromHandIndex);

        return singleBuild;
    }

    /**
     To set the member variables of a newly created multibuild
     @param indexOfLooseCards - contains integer of indices
     of loose cards on the table
     @param cardFromHandIndex - hand index of the card the player hand
     @param aPlayer - player who is trying to build
     */

    private void successfulMultiBuild(Player  aPlayer, int cardFromHandIndex, int oldBuildIndex, ArrayList <Integer>  indexOfLooseCards, Build  newBuild){
        //Create a multiBuild
        MultiBuild  m = new MultiBuild();
        m.addToMultiBuild(newBuild);
        Build  oldBuild = aPlayer.getBuild().get(oldBuildIndex);
        m.addToMultiBuild(oldBuild);

        //Set owner of the multiBuild
        m.setOwner(aPlayer.getPlayerType());

        //Remove old build reference from player
        aPlayer.removeBuild(oldBuildIndex);

        //Remove old build reference from table
        m_table.removeABuild(oldBuild);

        // Remove previous loose cards from the table
        removeTableCards(indexOfLooseCards);

        // Let player have a reference for the new multi build
        aPlayer.addAMultiBuild(m);

        // Let the table have a reference for the new multi build
        m_table.addAMultiBuild(m);

        m_move_summary.append(" create the following multi-build, " + m.toString() );
    }

    /**
     To set the member variables of a newly created single build
     @param indexOfLooseCards - contains integer of indices
     of loose cards on the table
     @param cardFromHandIndex - hand index of the card the player hand
     @param aPlayer - player who is trying to build
     @return - an integer value that represents sum of numeric values
     of cards represented by indices in a_vector
     */

    boolean successfulSingleBuild(ArrayList<Integer> indexOfLooseCards, int cardFromHandIndex, Player aPlayer){
        //Create a single build
        Build  singleBuild = getNewlyCreatedSingleBuild(indexOfLooseCards, cardFromHandIndex, aPlayer);

        // Add the single build to the table and to the player
        m_table.addABuild(singleBuild);
        aPlayer.addABuild(singleBuild);

        m_move_summary.append(" create the following single build: " + singleBuild.toString() );
        m_move_summary.append(" using loose cards ");
        for(Integer i: indexOfLooseCards){
            m_move_summary.append(m_table.getLooseCards().get(i).getStringRepresentation() + ", ");
        }

        //Remove previous loose cards from the table as they are not loose cards anymore
        return removeTableCards(indexOfLooseCards);
    }

    /**
     To create a multibuild if the user can do so
     @param aPlayer - player who is trying to create a multibuild
     @param moveStrategy - strategy player may use to help him capture
     @return - true if the player created a multibuild successfully
     */

    private boolean createMultiBuild(Player aPlayer, Strategy  moveStrategy){
        boolean createdMultiBuild = createMultiBuildWithSingleBuild(aPlayer, moveStrategy);

        if(!createdMultiBuild)
            createdMultiBuild = createMultiBuildWithMultiBuild(aPlayer, moveStrategy);

        return createdMultiBuild;
    }

    /**
     To create a multibuild by selecting one single build and 0 or more
     loose cards
     @param aPlayer - player who is trying to create a multibuild
     @param moveStrategy - strategy player may use to help him capture
     @return - true if the player created a multibuild successfully
     */

    private boolean createMultiBuildWithSingleBuild( Player aPlayer, Strategy  moveStrategy ){
        if( !aPlayer.hasSingleBuild() )
            return false;


        Input input = aPlayer.getInput(moveStrategy);
        ArrayList <Integer> buildIndices = input.getIndexOfSingleBuilds();
        if(buildIndices.size() != 1)
            return false;

        int buildIndex = buildIndices.get(0);
        int cardFromHandIndex = input.getHandCardIndex();

        if( aPlayer.isHoldCard(cardFromHandIndex) )
            return false;

        boolean aceIsFourteen = false;
        Card  playedCard = aPlayer.getHand().get(cardFromHandIndex);
        ArrayList<Integer> indexOfLooseCards = input.getIndexOfLooseTableCards();

        if( indexOfLooseCards.size() == 0 )
            aceIsFourteen = true;

        int sum = playedCard.getNumericValue(aceIsFourteen) + sum( indexOfLooseCards );
        int buildNumericValue = m_table.getSingleBuilds().get( buildIndex ).getNumericValue();

        if(sum == buildNumericValue){
            Build  newBuild = getNewlyCreatedSingleBuild(indexOfLooseCards, cardFromHandIndex, aPlayer);
            successfulMultiBuild(aPlayer, cardFromHandIndex, buildIndex, indexOfLooseCards, newBuild);
            return true;
        }

        return false;
    }

    /**
     To create a multibuild by selecting one multi build and 0 or more
     loose cards
     @param aPlayer - player who is trying to create a multibuild
     @param moveStrategy - strategy player may use to help him capture
     @return - true if the player created a multibuild successfully
     */
    private boolean createMultiBuildWithMultiBuild( Player aPlayer, Strategy  moveStrategy ){
        if( !aPlayer.hasMultiBuild() )
            return false;

        Input input = aPlayer.getInput(moveStrategy);
        ArrayList <Integer> multiBuildIndices = input.getIndexOfMultiBuilds();
        if(multiBuildIndices.size() != 1)
            return false;

        int multibuildIndex = multiBuildIndices.get(0);
        int cardFromHandIndex = input.getHandCardIndex();

        if( aPlayer.isHoldCard(cardFromHandIndex) )
            return false;

        boolean aceIsFourteen = false;
        Card  playedCard = aPlayer.getHand().get(cardFromHandIndex);
        ArrayList<Integer> indexOfLooseCards = input.getIndexOfLooseTableCards();
        if( indexOfLooseCards.size() == 0 )
            aceIsFourteen = true;

        int sum = playedCard.getNumericValue(aceIsFourteen) + sum( indexOfLooseCards );

        MultiBuild oldMulti = m_table.getMultiBuilds().get( multibuildIndex );
        int multibuildNumericValue = oldMulti.getNumericValue();

        if(sum == multibuildNumericValue){
            Build newBuild = getNewlyCreatedSingleBuild(indexOfLooseCards, cardFromHandIndex, aPlayer);
            oldMulti.addToMultiBuild(newBuild);
            aPlayer.setHoldCardsForMultiBuild( multibuildNumericValue );
            m_move_summary.append(" create the following multibuild " + oldMulti.toString() );

            return true;
        }

        return false;
    }

    /**
     To allow the user to increase an opponent's build if she can do so
     @param aPlayer - player who is trying to create a multibuild
     @param moveStrategy - strategy player may use to help him capture
     @return - true if the player legitimately increased an opponent's build
     false otherwise
     */

    private boolean increaseBuildValue(Player aPlayer, Strategy moveStrategy){
        //Check if the opponent has a single build. If not, return
        Player  opponent = getOpponentPlayer(aPlayer);

        if( !opponent.hasSingleBuild() )
            return false;

        Input input = aPlayer.getInput(moveStrategy);

        if( input.getIndexOfLooseTableCards().size() != 0 || input.getIndexOfMultiBuilds().size() != 0)
            return false;

        int cardFromHandIndex = input.getHandCardIndex();

        if( aPlayer.isHoldCard(cardFromHandIndex) )
            return false;

        int possibleBuildIndex = input.getIndexOfSingleBuilds().get(0);
        int buildIndex = getOpponentsBuildIndex(possibleBuildIndex, opponent);

        Build opponentBuild = opponent.getBuild().get(buildIndex);
        int buildSum = opponentBuild.getNumericValue();
        int cardFromHandValue = aPlayer.getHand().get(cardFromHandIndex).getNumericValue();

        if(aPlayer.handHasACardWithAGivenNumericValue(cardFromHandIndex, buildSum + cardFromHandValue, true)){ //Ace is treated as 14
            m_move_summary.append(" increase the value of the following build: " + opponentBuild.toString() );
            opponent.removeBuild(opponentBuild);
            opponentBuild.addCardToABuild(aPlayer.getHand().get(cardFromHandIndex));
            aPlayer.removeHoldCard(cardFromHandIndex);
            aPlayer.removeFromHand(cardFromHandIndex);
            opponentBuild.setOwner( aPlayer.getPlayerType() );
            aPlayer.addABuild(opponentBuild);
            return true;
        }

        return false;
    }

    /**
     To allow the user to increase an opponent's build if she can do so
     @param opponent - opponent the player currently playing
     @param possibleBuildIndex - strategy player may use to help him capture
     @return - true if the player legitimately increased an opponent's build
     false otherwise
     */

    private int getOpponentsBuildIndex( int possibleBuildIndex, Player opponent){
        Build tableBuild = m_table.getSingleBuilds().get(possibleBuildIndex);

        ArrayList<Build> opponentsBuilds = opponent.getBuild();

        for(int i = 0; i < opponentsBuilds.size(); i++){
            if(opponentsBuilds.get(i) == tableBuild)
                return i;
        }
        return 0;
    }

    /**
     To allow a player to trail
     @param aPlayer - player who is trying to trail
     @param moveStrategy - strategy player may use to help him trail
     @return - true if the player legitimately trailed
     false otherwise
     */

    private boolean trail(Player aPlayer, Strategy moveStrategy){
        int cardFromHandIndex = aPlayer.getInput(moveStrategy).getHandCardIndex();

        if( aPlayer.isHoldCard(cardFromHandIndex) )
            return false;

        if( hasIndividualCards( aPlayer, cardFromHandIndex) )
            return false;

        //  Trailing option is not available to the owner of a build
        // - since that player can play the card matching the build to capture it or work on a multiple build.
        if(aPlayer.hasSingleBuild() || aPlayer.hasMultiBuild() )
            return false;


        Card trailedCard = aPlayer.getHand().get(cardFromHandIndex);

        // The player can trail.
        // Add the played card to the table
        m_table.addALooseCard( trailedCard );
        m_move_summary.append(" trail" );

        // Remove the played card from the players hand
        aPlayer.removeFromHand(cardFromHandIndex);
        return true;
    }

    /**
     To check if the table has any loose cards whose value is equal
     to the card the player played
     @param aPlayer - player
     @param cardFromHandIndex - an integer passed by value. Represents
     the index of a card in a player's hand
     @return - true if the player legitimately trailed
     false otherwise
     */

    private boolean hasIndividualCards(Player  aPlayer, int cardFromHandIndex){
        Card cardFromPlayersHand = aPlayer.getHand().get(cardFromHandIndex);
        ArrayList <Card> looseCardsOnTable = m_table.getLooseCards();

        for(int i = 0; i < looseCardsOnTable.size(); i++){
            if(looseCardsOnTable.get(i).getNumericValue() == cardFromPlayersHand.getNumericValue() )
                return true;
        }
        return false;
    }

    /**
     Continue playing the round by dealing cards to the players
     */

    public void continueTheRound(){
        //Each time all the players have played all their cards, four more cards are dealt
        // first to the human player and then to the computer from the remaining deck
        // and the round continues.
        if( !( m_humanPlayer.getHand().isEmpty() && m_computerPlayer.getHand().isEmpty() ) )
            return;

        if(!roundOver() ){
            dealCards(m_humanPlayer, 4);
            dealCards(m_computerPlayer, 4);
        }
        else
            endRound();
    }


    /**
     Determine if the round has ended
     @return - true if the round is over. false otherwise
     */

    public boolean roundOver(){
        return m_deckForTheRound.isEmpty() && m_humanPlayer.getHand().isEmpty() && m_computerPlayer.getHand().isEmpty();
    }

    /**
     To successfully end the round by following these rules.
     The round ends when the players have played all the cards and the deck
     is empty. Any cards that remain on the table are taken by the last
     player that made a capture.The piles of both the players are printed
     at the end of the round.
     */

    public void endRound(){
        if(m_playerWhoCapturedLast != null){
            //Add remaining loose cards on the table to the m_playerWhoCapturedLast's pile
            ArrayList<Card> tableLooseCards = m_table.getLooseCards();
            ArrayList <Integer> temp = new ArrayList<>();
            for(int i = 0; i < tableLooseCards.size(); i++){
                Card  c = tableLooseCards.get(i);
                m_playerWhoCapturedLast.addToPile(c);
                temp.add(i);
            }
            removeTableCards(temp);

            //Add remaining cards in a build on the table to the m_playerWhoCapturedLast's pile
            ArrayList <Build> tableSingleBuilds = m_table.getSingleBuilds();
            for(int i = 0; i < tableSingleBuilds.size(); i++){
                Build  b = tableSingleBuilds.get(i);
                m_playerWhoCapturedLast.addToPile(b);
            }

            //Add remainging cards in a multi-build on the table to the m_playerWhoCapturedLast's pile
            ArrayList <MultiBuild> tableMultiBuilds = m_table.getMultiBuilds();
            for(int i = 0; i < tableMultiBuilds.size(); i++){
                MultiBuild  m = tableMultiBuilds.get(i);
                m_playerWhoCapturedLast.addToPile(m);
            }

            //Change turn based on plaer who captured last. The player that captured last in round x, will play first in round x + 1 if round x is not the last round
            setTurn(m_playerWhoCapturedLast.getPlayerType());
        }

        updateScore();
        determineTheWinner();
        roundCount++;
    }

    /**
     To update the score based on the game rules which are:
     When a round ends, the points earned by each player are calculated based
     on the cards in each player's pile:
         - The player with the most cards in the pile gets 3 points. In the event
         of a tie, neither player gets points.
        - The player with the most spades gets 1 point. In the event of a tie,
        neither player gets points.
        - The player with 10 of Diamonds gets 2 points.
        - The player with 2 of Spades gets 1 point.
        - Each player gets one point per Ace.
     */

    private void updateScore(){ //Based on the players' piles

        updateScoreBasedOnTheBiggestPile();
        updateScoreBasedOnMostSpades();
        updateScoreBasedOnTenOfDiamonds();
        updateScoreBasedOnTwoOfSpades();
        updateScoreBasedOnAces();
    }

    /**
     To update the score based on based on this rule:
        - The player with the most cards in the pile gets 3 points. In the event
        of a tie, neither player gets points.
     */

    private void updateScoreBasedOnTheBiggestPile(){
        if( m_computerPlayer.getPile().size() > m_humanPlayer.getPile().size() )
            m_computerPlayer.updateScore(3);
        else if( m_humanPlayer.getPile().size() > m_computerPlayer.getPile().size() )
            m_humanPlayer.updateScore(3);
    }

    /**
     To update the score based on based on this rule:
       -The player with the most spades gets 1 point. In the event of a tie,
       neither player gets points.
     */

    private void updateScoreBasedOnMostSpades() {
        int spadesCountHumanPlayer = 0;
        int spadesCountComputerPlayer = 0;

        ArrayList<Card>humanPlayerPile = m_humanPlayer . getPile();
        ArrayList<Card>computerPlayerPile = m_computerPlayer . getPile();

        for (int i = 0; i < humanPlayerPile.size(); i++) {
            Card  card = humanPlayerPile.get(i);
            if (card. getSuiteCharacter() == card.SPADE )
                spadesCountHumanPlayer += 1;
        }

        for (int j = 0; j < computerPlayerPile.size(); j++) {
            Card  card = computerPlayerPile.get(j);
            if (card. getSuiteCharacter() == card.SPADE )
                spadesCountComputerPlayer += 1;
        }

        if (spadesCountComputerPlayer > spadesCountHumanPlayer)
            m_computerPlayer . updateScore(1);
        else if (spadesCountHumanPlayer > spadesCountComputerPlayer)
            m_humanPlayer . updateScore(1);

        m_computerPlayer.setNumSpades(spadesCountComputerPlayer);
        m_humanPlayer.setNumSpades(spadesCountHumanPlayer);
    }

    /**
     To update the score based on based on this rule:
        -The player with 10 of Diamonds gets 2 points.
     */

    private void updateScoreBasedOnTenOfDiamonds(){

        Card  anyCard = m_humanPlayer.getPile().get(0);
        updatePointsBasedOnACard(anyCard.DIAMOND, anyCard.FACE_FOR_10, 2, m_humanPlayer );
        updatePointsBasedOnACard(anyCard.DIAMOND, anyCard.FACE_FOR_10, 2, m_computerPlayer );
    }

    /**
     To update the score based on a card and the number of points to
     add to the score

     @param aPlayer - player whose points needs to be updated
     @param face - reprsents face value of a card
     @param pts - represents the amount of points to add to a player's score
     @param suite - represents suite character of a card

     */

    private void updatePointsBasedOnACard(char suite, char face, int pts, Player  aPlayer){
        ArrayList <Card > playerPile = aPlayer.getPile();

        for(int i = 0; i < playerPile.size(); i++){
            Card  c = playerPile.get(i);
            if( c.getSuiteCharacter() == suite && c.getFaceValue() == face ){   // USe symbolic constant
                aPlayer.updateScore(pts);  //Use symbolic constant
                return;
            }
        }
    }

    /**
     To update the score based on based on this rule:
        -The player with 2 of Spades gets 1 point.
     */

    private void updateScoreBasedOnTwoOfSpades(){

        Card  anyCard = m_humanPlayer.getPile().get(0);
        updatePointsBasedOnACard(anyCard.SPADE, '2', 1, m_humanPlayer );
        updatePointsBasedOnACard(anyCard.SPADE, '2', 1, m_computerPlayer );
    }

    /**
     To update the score based on based on this rule:
         Each player gets one point per Ace.
     */

    private void updateScoreBasedOnAces(){
        ArrayList <Card > humanPlayerPile = m_humanPlayer.getPile();
        ArrayList <Card > computerPlayerPile = m_computerPlayer.getPile();

        for(int i = 0; i < humanPlayerPile.size(); i++){
            Card  c = humanPlayerPile.get(i);
            if(c.getFaceValue() == c.ACE )
                m_humanPlayer.updateScore(1);
        }

        for(int i = 0; i < computerPlayerPile.size(); i++){
            Card  c = computerPlayerPile.get(i);
            if(c.getFaceValue() == c.ACE )
                m_computerPlayer.updateScore(1);
        }
    }

    /**
     To look at the score of both the players and to figure out who
     won the round
     */

    public void determineTheWinner(){
        int humanPlayerScore = m_humanPlayer.getScore();
        int computerPlayerScore = m_computerPlayer.getScore();

        if(humanPlayerScore > computerPlayerScore)
            m_winner = m_humanPlayer.getPlayerType();
        else if(computerPlayerScore > humanPlayerScore)
            m_winner = m_computerPlayer.getPlayerType();
    }
}
