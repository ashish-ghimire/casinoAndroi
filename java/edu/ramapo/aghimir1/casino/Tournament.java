package edu.ramapo.aghimir1.casino;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tournament implements Serializable {

    /* *********************************************
    Symbolic constants
    ********************************************* */
    private static final int THRESHOLD_MAX_SCORE = 21;

    /* *********************************************
    Class member variables
    ********************************************* */
    private Computer  m_computerPlayer;
    private Human m_humanPlayer;
    private Round m_currentRound;
    private boolean m_isSerialized = false;
    private String m_tournamentResult;

    /* *********************************************
    Constructors
    ********************************************* */
    public Tournament(){
        m_computerPlayer = new Computer();
        m_humanPlayer = new Human();
        m_currentRound = new Round(0, 0);
        m_tournamentResult = "";
    }

    public Tournament(String serializationFile) {
        m_computerPlayer = new Computer();
        m_humanPlayer = new Human();
        loadTheTournament(serializationFile);
        m_isSerialized = true;
    }


    /* *********************************************
    Selectors
    ********************************************* */

    /**
     Selector for the Round object in this class
     @return An instance of the current round
     */
    public Round getCurrentRound(){
        return m_currentRound;
    }

    /**
     Selector for the human object in this class
     @return An instance of the tournament's human player
     */
    public Human gethumanPlayer(){
        return m_humanPlayer;
    }

    /**
     Selector for the human object in this class
     @return An instance of the tournament's human player
     */
    public Computer getComputerPlayer(){
        return m_computerPlayer;
    }

    /**
     Selector for knowing the tournament's winner
     @return "The tournament ended in a tie" if tie.
     "The human player won the tournament" if human player wins the tournament
     "The computer player won the tournament" if computer player wins the tournament
     */

    public String getResult() { return m_tournamentResult; }


    /**
     A tournament can be played with or without serialization. This function returns
     if the tournament is serialized
     @return boolean value. true if tournament is serialized. false otherwise
     */
    public boolean isSerialized() {
        return m_isSerialized;
    }


    /* *********************************************
    Mutators
    ********************************************* */

    /**
     Selector for the human object in this class
     @param computerPlayer - A Computer object used to set the value of the tournament's computer player
     */
    public void setcomputerPlayer(Computer computerPlayer) {
        this.m_computerPlayer = computerPlayer;
    }

    /**
     Selector for the human object in this class
     @param humanPlayer - A human object used to set the value of the tournament's human player
     */
    public void sethumanPlayer(Human humanPlayer) {

        this.m_humanPlayer = humanPlayer;
    }

    /**
     Selector for the round object in this class
     @param currentRound - A round object used to set the value of the tournament's current round
     */
    public void setcurrentRound(Round currentRound) {

        this.m_currentRound = currentRound;
    }

    /* *********************************************
     Main function that could be used for testing
    ********************************************* */

    public static void main( String [] args ){
    }

    /* *********************************************
     Utility Functions for parsing game data from a file
    ********************************************* */

    /**
     This function sets up everything required for serialization. It calls other methods to parse
     game data from a file, sets up relevant objects like the Computer and Human objects, Round object,
     Table object and everything else required for a serialized game to run properly
     @param fileName - A round object used to set the value of the tournament's current round
     */
    private void loadTheTournament(String fileName) {
        int roundNumber = 555;
        int computerScore = 555;
        int humanScore = 555;
        String nextPlayer = "";
        ArrayList <String> humanHand = new ArrayList<>();
        ArrayList <String> humanPile = new ArrayList<>();
        ArrayList <String> computerHand = new ArrayList<>();
        ArrayList <String> computerPile = new ArrayList<>();
        ArrayList <ArrayList<String>> computersSingleBuilds = new ArrayList<>();
        ArrayList < ArrayList <ArrayList <String> > > computersMultiBuilds = new ArrayList<>();
        ArrayList <ArrayList <String> > humansSingleBuilds = new ArrayList<>();
        ArrayList <ArrayList <ArrayList <String> > > humansMultiBuilds = new ArrayList<>();
        ArrayList <ArrayList <String> > tableSingleBuilds = new ArrayList<>();
        ArrayList <ArrayList <ArrayList <String> > > tableMultiBuilds = new ArrayList<>();
        ArrayList <String> tableLooseCards = new ArrayList<>();;
        ArrayList <String> deckCards = new ArrayList<>();
        String playerWhoCapturedLast = "";

        try {
            File f = new File(fileName);

            Scanner sc = new Scanner(f);

            while(sc.hasNext()) {
                String oneLine = sc.nextLine();

                if( oneLine.contains("Round") )
                    roundNumber = parseNumber(oneLine);

                if( oneLine.contains("Last") )
                    playerWhoCapturedLast = parseNextString(oneLine);

                if( oneLine.contains("Next") )
                    nextPlayer = parseNextString(oneLine);

                if( oneLine.contains("Deck") )
                    deckCards = parseGroupOfString(oneLine);

                if( oneLine.contains(m_computerPlayer.getPlayerType() + ":") )
                    computerScore = parsePlayerData(sc, computerHand, computerPile);

                if( oneLine.contains(m_humanPlayer.getPlayerType() + ":") )
                    humanScore = parsePlayerData(sc, humanHand, humanPile);

                if( oneLine.contains("Table") )
                    parseTable( oneLine, tableMultiBuilds, tableSingleBuilds, tableLooseCards );

                if( oneLine.contains("Build Owner") )
                    parsePlayerBuilds( oneLine, humansMultiBuilds, humansSingleBuilds, computersMultiBuilds, computersSingleBuilds );
            }
            sc.close();
        }
        catch (FileNotFoundException e){
            System.out.println("Error");
        }

        // Set the tournament players' scores
        m_computerPlayer.setScore(computerScore);
        m_humanPlayer.setScore(humanScore);

        // Create table
        Table  table = new Table();
        Human  humanPlayer = new Human();
        Computer  computerPlayer = new Computer();
        Player whoCapturedLast = null;

        createPlayer(humanPlayer, humanHand, humanPile, humansSingleBuilds, humansMultiBuilds, table, "Human");
        createPlayer(computerPlayer, computerHand, computerPile, computersSingleBuilds, computersMultiBuilds, table, "Computer");

        if( playerWhoCapturedLast.equalsIgnoreCase( humanPlayer.getPlayerType() ) )
            whoCapturedLast = humanPlayer;
        else if( playerWhoCapturedLast.equalsIgnoreCase(computerPlayer.getPlayerType()))
            whoCapturedLast = computerPlayer;

        addLooseCards(tableLooseCards, table);

        // Create Deck
        ArrayList <Card> cardsInADeck = new ArrayList<>();
        createDeck(cardsInADeck, deckCards);
        Deck deck = new Deck(cardsInADeck);

        //First Round after serialization
        m_currentRound = new Round (deck, table, whoCapturedLast, computerPlayer, humanPlayer, roundNumber, nextPlayer, m_humanPlayer.getScore(), m_computerPlayer.getScore() );
    }

    /**
     Parses a number from oneLine of text
     @param oneLine - A String text from which a number is parsed
     */
    private int parseNumber(String oneLine){
        String numberOnly= oneLine.replaceAll("[^0-9]", "");
        return Integer.parseInt(numberOnly);
    }

    /**
     Finds and returns the last word character from a line of text
     @param oneLine - A String text from which a last word is parsed
     */
    private String parseNextString(String oneLine){
        String [] words = oneLine.split(" ");
        return words[words.length - 1];
    }

    /**
     Parses every word except the first word from a line of text
     @param oneLine - A String text from which words are parsed
     @return an ArrayList of all the words except the first word in a text
     */
    private ArrayList<String> parseGroupOfString(String oneLine){
        ArrayList<String> deck = new ArrayList<>();
        String [] words = oneLine.split(" ");

        for(int i = 1; i < words.length; i++){
            if( words[i].equals("\\s") || words[i].equals("") || words[i].length() < 2 )
                continue;

            deck.add(words[i]);
        }

        return deck;
    }

    /**
     Parses every word except the first word from a line of text
     @param inFile - A Scanner object used to read the contents of a file
     @param playerHand - An ArrayList of Strings where each string represents a card in a players hand
     @param playerPile -  An ArrayList of Strings where each string represents a card in a players pile
     @return an integer representing score of a player. Error if integer can't be found
     */
    private int parsePlayerData(Scanner inFile, ArrayList <String> playerHand, ArrayList <String> playerPile){

        String playerScoreLine = inFile.nextLine();
        int playerScore = parseNumber(playerScoreLine);

        String playerHandLine = inFile.nextLine();
        playerHandLine = playerHandLine.replace("Hand:", "");
        playerHand.addAll( parseGroup(playerHandLine) );

        String playerPileLine = inFile.nextLine();
        playerPileLine = playerPileLine.replace("Pile:", "");
        playerPile.addAll( parseGroup(playerPileLine) );

        return  playerScore;
    }

    /**
     Parses data required for a Table object from a line of text
     @param oneLine - A String text from which table data is parsed
     @param tableMultiBuilds - An ArrayList of ArrayList of ArrayList of String. String represents raw data
     for a card, The innermost Arraylist represents cards to form a single build.
     @param tableSingleBuilds -  ArrayList of ArrayList of String where each String represents raw data
     for a card, The innermost Arraylist represents cards to form a single build.
     @param tableLooseCards - An Arraylist of Strings where string contains raw data to create a card
     */
    private void parseTable(String oneLine, ArrayList <ArrayList <ArrayList <String> > > tableMultiBuilds, ArrayList <ArrayList <String> > tableSingleBuilds, ArrayList <String> tableLooseCards){
        oneLine = oneLine.replace("Table", "");

        int index;

        parseMulti1( tableMultiBuilds, oneLine );
        index = oneLine.lastIndexOf(" ]");
        if( -1 != index ){
            oneLine = oneLine.substring(index + 1);
        }

        parseSingle1( tableSingleBuilds, oneLine );

        index = oneLine.lastIndexOf("]");
        if( -1 != index ){
            oneLine = oneLine.substring(index);
            oneLine = oneLine.replace("]", "");
        }

        tableLooseCards.addAll( parseGroup( oneLine ) );
    }

    /**
     Parses data required for a Table object from a line of text
     @param oneLine - A String text from which player build data is parsed
     @param humansMultiBuilds - An ArrayList of ArrayList of ArrayList of String. String represents raw data
     for a card, The innermost Arraylist represents cards to form a single build for a human
     @param humansSingleBuilds -  ArrayList of ArrayList of String where each String represents raw data
     for a card, The innermost Arraylist represents cards to form a single build for a human
     @param computersMultiBuilds -  An ArrayList of ArrayList of ArrayList of String. String represents raw data
     for a card, The innermost Arraylist represents cards to form a single build for a computer
     @param computersSingleBuilds -  ArrayList of ArrayList of String where each String represents raw data
     for a card, The innermost Arraylist represents cards to form a single build for a computer
     */
    private void parsePlayerBuilds( String oneLine, ArrayList <ArrayList <ArrayList <String> > > humansMultiBuilds, ArrayList <ArrayList <String> > humansSingleBuilds, ArrayList < ArrayList <ArrayList <String> > > computersMultiBuilds, ArrayList <ArrayList<String>> computersSingleBuilds  ){
        oneLine = oneLine.replace("Build Owner:", "");
        if( oneLine.contains("[ ") ){    //Parse multibuild
            if( oneLine.contains( m_humanPlayer.getPlayerType() ) )
                parseMulti1( humansMultiBuilds, oneLine);
            else
                parseMulti1( computersMultiBuilds, oneLine );
        }
        else{ // Parse single build
            if( oneLine.contains(m_humanPlayer.getPlayerType() ) )
                parseSingle1( humansSingleBuilds, oneLine);
            else
                parseSingle1( computersSingleBuilds, oneLine);
        }
    }

    /**
     Parses all raw data required to create multibuilds from one line of text
     @param toParse - A String text from which multi build data
     @param tableMultiBuilds - An ArrayList of ArrayList of ArrayList of String. String represents raw data
     for a card, The innermost Arraylist represents cards to form a single build for a table
     */
    private void parseMulti1( ArrayList <ArrayList <ArrayList <String> > > tableMultiBuilds, String toParse ){
        int index = toParse.indexOf("[ ");
        if( -1 == index )
            return;

        int start = index;
        toParse = toParse.substring(start);

        while( true ){
            ArrayList <ArrayList <String> > oneMulti = new ArrayList<>();

            start = 0;
            index = toParse.indexOf(" ]");
            if( -1 == index || toParse.length()  < 2 )
                return;

            int end = index + 1;
            String oneMultiStr = toParse.substring(start + 2, end - 1);

            //For one multi, parse all the single builds. Store one multi
            parseSingle1(oneMulti, oneMultiStr); //Change parse single builds function
            if( oneMulti.isEmpty() )
                return;

            tableMultiBuilds.add(oneMulti);
            String remaining = toParse.substring(end);

            index = remaining.indexOf(" ]");
            if( -1 == index || remaining.length() < 2 )
                return;

            toParse = remaining.substring(2);
        }
    }

    /**
     Parses all raw data required to create multibuilds from one line of text
     @param toParse - A String text from which multi build data
     @param allSingleBuilds - An ArrayList of ArrayList of String. String represents raw data
     for a card, The innermost Arraylist represents cards to form a single build for a table.
     The outermost ArrayList is a combination of all raw single builds
     */
    private void parseSingle1( ArrayList <ArrayList <String> > allSingleBuilds, String toParse){
        int index = toParse.indexOf("[");
        if( -1 == index )
            return;

        int start = index;
        toParse = toParse.substring(start);

        while( true ){
            start = 0;
            //Find 1st occuring index of "]". If not found, return
            index = toParse.indexOf("]");
            if( -1 == index || toParse.length()  < 2)
                return;

            int end = index;

            ArrayList <String> oneSingleBuild = new ArrayList<>();

            String oneSingleStr = toParse.substring(start + 1, end);

            // For one single build, parse all the loose card strings. Then, store oneSingleBuild
            oneSingleBuild = parseGroup(oneSingleStr);

            if( oneSingleBuild.isEmpty() )
                return;

            allSingleBuilds.add(oneSingleBuild);

            String remaining = toParse.substring( end );

            // Find 1st occuring index of "]" in remaining. If not found, return
            index = remaining.indexOf("]");

            if( -1 == index || remaining.length() < 2 )
                return;

            toParse = remaining.substring(2);
        }
    }

    /**
     Parses all raw data required to create multibuilds from one line of text
     @param oneLine - A String text from which group of words are parse
     @return an Arraylist containing all the words that the function parses
     */
    private ArrayList<String> parseGroup(String oneLine){

        ArrayList<String> group = new ArrayList<>();
        String [] words = oneLine.split(" ");

        for(String i: words){
            if( i.equals("\\s") || i.equals("") || i.length() < 2 )
                continue;

            group.add( i );
        }

        return group;
    }

    /**
     Sets properties of a player object
     @param player - A player object whose properties need to be set
     @param playerHand - An ArrayList that contains raw data to create a player's hand
     @param playerPile - An ArrayList that contains raw data to create a player's pile
     @param playersSingleBuilds - ArrayList of ArrayList of String where each String represents raw data
     for a card, The innermost Arraylist represents cards to form a single build for a player
     @param playersMultiBuilds - ArrayList of ArrayList of String where each String represents raw data
     for a card, The innermost Arraylist represents cards to form a single build for a player
     for a card, The innermost Arraylist represents cards to form a single build for a player.
     Contains raw data needed to create players' multibuilds
     @param table - a Table object to which the function adds the newly created builds and multibuilds
     @param playerType - A string variable that tells what type of player(Human or computer) the player is
     */
    private void createPlayer(Player  player,  ArrayList <String>  playerHand,  ArrayList <String>  playerPile,  ArrayList <ArrayList <String> >  playersSingleBuilds,  ArrayList <ArrayList <ArrayList <String> > >  playersMultiBuilds, Table  table, String playerType){
        ArrayList <Card> hand = new ArrayList<>();
        ArrayList <Card> pile = new ArrayList<>();
        ArrayList <Build> singleBuilds = new ArrayList<>();
        ArrayList <MultiBuild> multiBuilds = new ArrayList<>();
        createCardsFromStrings(playerHand, hand);
        createCardsFromStrings(playerPile, pile);
        createSingleBuilds(playersSingleBuilds, singleBuilds, playerType);
        createMultiBuilds(playersMultiBuilds, multiBuilds, playerType);

        player.setPile(pile);
        player.setHand(hand);

        for(Build b: singleBuilds)
            player.addABuild(b);

        for(MultiBuild m: multiBuilds)
            player.addAMultiBuild(m);

        addBuildsToTable(singleBuilds, multiBuilds, table);
    }

    /**
     Create a collection of Card objects from a collection of string containing raw card information
     @param stringOfCards - an arraylist that contains raw card data required to create a card
     @param newCards - an arraylist that contains card objects
     */
    private void createCardsFromStrings( ArrayList <String>  stringOfCards, ArrayList<Card> newCards){

        char possibleSuite,
                possibleFace;

        for(String i: stringOfCards){
            possibleSuite = i.charAt(0);
            possibleFace = i.charAt(1);
            Card newCard = new Card(possibleSuite, possibleFace);
            newCards.add(newCard);
        }
    }
    /**
     Create a collection of Single builds from a collection that contains raw data about cards in a single
     build
     @param stringOfSingleBuilds - an arraylist that contains that contains raw data about cards in a single
     build
     @param singleBuilds - an arraylist that contains newly created Build objects
     @param playerType - a String variable used to set the owner of the build
     */
    private void createSingleBuilds( ArrayList <ArrayList <String> > stringOfSingleBuilds, ArrayList <Build> singleBuilds, String playerType){
        for(int i = 0; i < stringOfSingleBuilds.size(); i++){
            ArrayList <Card> cards = new ArrayList<>();
            createCardsFromStrings(stringOfSingleBuilds.get(i), cards);
            int buildValue = getNumericValueOfAGroupOfCards(cards);
            Build  newSingleBuild = new Build( cards, playerType, buildValue );
            singleBuilds.add(newSingleBuild);
        }
    }

    /**
     Compute and return sum of numeric values of a group of cards
     @param cards - A collection of card objects
     @return an integer representing the sum of numeric value of cards in the parameter
     */
    private int getNumericValueOfAGroupOfCards(ArrayList<Card > cards){
        int value = 0;

        for( Card i: cards )
            value += ( i.getNumericValue() );

        return value;
    }

    /**
     Compute and return sum of numeric values of a group of cards
     @param stringOfplayersMultiBuilds - A multidimesnsional arraylist that contains string data to cra
     @return an integer representing the sum of numeric value of cards in the parameter
     */
    private void createMultiBuilds( ArrayList <ArrayList <ArrayList <String> > >  stringOfplayersMultiBuilds, ArrayList <MultiBuild>  multiBuilds,  String playerType){
        for(int i = 0; i < stringOfplayersMultiBuilds.size(); i++){
            ArrayList <Build> groupOfBuilds = new ArrayList<>();
            createSingleBuilds(stringOfplayersMultiBuilds.get(i), groupOfBuilds, playerType);
            MultiBuild newMultiBuild = new MultiBuild(groupOfBuilds, playerType);
            multiBuilds.add(newMultiBuild);
        }
    }

    /**
     Add a builds and mutlbuilds obtained through serialization to the table
     @param singleBuilds - A collection of Build objects which the function adds to the Table object
     @param table- a Table object to which Builds and multibuilds are added to
     @param multibuilds - A collection of multibuilds which the function adds to the table
     @return an integer representing the sum of numeric value of cards in the parameter
     */
    private void addBuildsToTable( ArrayList <Build>  singleBuilds,  ArrayList <MultiBuild> multibuilds, Table table){
        //Add single builds to table
        for(Build i: singleBuilds)
            table.addABuild( i );

        //Add multi builds to table
        for(MultiBuild i: multibuilds)
            table.addAMultiBuild( i );
    }

    /**
     Create and add card objects to a Table object
     @param tableLooseCards - A collection of Strings where each string contains raw data(face and suite character
     to form a card
     @param table- a Table object to which Builds and multibuilds are added to
     */
    private void addLooseCards( ArrayList <String> tableLooseCards, Table  table){

        for(String i : tableLooseCards){
            char suite = i.charAt(0);
            char face = i.charAt(1);
            Card  newCard = new Card(suite, face);
            table.addALooseCard(newCard);
        }
    }

    /**
     Create a group of card objects from a group of strings containing suite and face info about a card
     @param deckCards- A collection of Strings where each string contains raw data(face and suite character
     to form a card
     @param cardsInADeck- a collection that contains newly created card objects by this function
     */
    private void createDeck( ArrayList <Card> cardsInADeck, ArrayList <String> deckCards ){

        for(String i: deckCards){
            char suite = i.charAt(0);
            char face = i.charAt(1);
            Card  card = new Card(suite, face);
            cardsInADeck.add(card);
        }
    }

    /* *********************************************
     Other non-parsing utility functions
    ********************************************* */

    /**
     Lets the caller know if new round object is initilaized by this function
     @return false if the tournament is over. true otherwise.
     */
    public boolean startNewRound(){
        updatePlayerScores( m_currentRound.getHuman().getScore(), m_currentRound.getComputer().getScore() );

        if( tournamentOver() ){
            // Determine the winner
            determineTheWinner();
            return false;
        }

        m_currentRound = new Round(this.m_humanPlayer.getScore(), this.m_computerPlayer.getScore());
        return true;
    }

    // After each round is over, update the player scores here
    private void updatePlayerScores( int humanScore, int computerScore){
        // After each round is over, update the player scores here
        m_humanPlayer.updateScore( humanScore );
        m_computerPlayer.updateScore( computerScore );
    }

    /**
     Lets the caller know if the tournament is over
     @return false if the tournament is over. true otherwise.
     */
    public boolean tournamentOver(){
        if( m_humanPlayer.getScore() >= THRESHOLD_MAX_SCORE || m_computerPlayer.getScore() >= THRESHOLD_MAX_SCORE )
            return true;
        return false;
    }

    /**
     Sets the winner of the tournament by checking which player got the greater score.
     The tournament ends in a tie if both player have same score after tournament completion
     */
    private void determineTheWinner(){
        if(m_humanPlayer.getScore() == m_computerPlayer.getScore() )
            m_tournamentResult = "The tournament ended in a tie";
        else if(m_humanPlayer.getScore() > m_computerPlayer.getScore())
            m_tournamentResult = "The human player won the tournament";
        else
            m_tournamentResult =  "The computer player won the tournament";
    }
}
