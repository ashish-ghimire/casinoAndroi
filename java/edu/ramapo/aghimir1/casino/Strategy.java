package edu.ramapo.aghimir1.casino;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Strategy implements Serializable {
    /* *********************************************
    Symbolic constants that represent the actions the computer player should take
    ********************************************* */
    public static final int BUILD = 1;
    public static final int CAPTURE = 2;
    public static final int TRAIL = 3;

    private boolean capturedLooseCards;
    private static final int CREATE_SINGLE_BUILD = 1;
    private static final int INCREASE_OPPONENTS_BUILD = 3;

    /* *********************************************
    Symbolic constants that represent the rationale for actions
    ********************************************* */
    private static final String CAPTURE_RATIONALE = " to capture as many cards as possible.";
    private static final String CREATE_BUILD_RATIONALE = " to possibly capture more cards in the future.";
    private static final String INCREASE_OPPONENTS_BUILD_RATIONALE = " to take over opponents build, to annoy your opponent and to possibly capture more cards in the future.";
    private static final String TRAIL_RATIONALE = " because the player can't capture or build";

    /* *********************************************
    Member variables
    ********************************************* */

    private int m_action; //Can be BUILD, CAPTURE or TRAIL
    private int m_cardToPlay; //This is the (hand) index of the optimal card that can be played. Index can be 0-3
    private String m_reasonForAction; // This explains why the Strategy class suggests the action it suggests
    private Table  m_gameTable;  // The class will base strategy for players based on this table
    private ArrayList <Card> m_hand;
    private ArrayList <Build> m_opponentsBuilds;
    private ArrayList <MultiBuild> m_opponentsMultiBuilds;
    private boolean m_capturedSingleBuild;
    private boolean m_capturedMultiBuild;
    private ArrayList <Integer> m_looseCardsToCapture;  //Indices of loose cards in the table that can be captured by the player
    private ArrayList <Integer> m_multiBuildsToCapture;
    private ArrayList <Integer> m_singleBuildsToCapture;
    private ArrayList <ArrayList<Integer> > m_setOfLooseCardsToCapture;
    private ArrayList <Integer> m_oneSetOfLooseCardsToCapture;
    private ArrayList <Integer> m_looseCardsForSingleBuild;
    private int m_buildOption;
    private int m_opponentsBuildToIncrease;
    private Map<Card,Integer> m_mapFromIndicesToValues;
    private Input moves;
    private ArrayList<Integer> m_dummySet;
    private ArrayList<Card> m_holdCards;

    /* *********************************************
    Constructors
    ********************************************* */
    public Strategy(){
        m_action = 0;
        m_cardToPlay = Input.INVALID_INDEX;
        m_reasonForAction = "";
        m_capturedSingleBuild = false;
        m_capturedMultiBuild = false;
        m_buildOption = 0;

        //Invalid value as opponents is unlikely to have 5000 single builds in one round
        m_opponentsBuildToIncrease = Input.INVALID_INDEX;
        m_hand = new ArrayList<>();
        m_opponentsBuilds = new ArrayList<>();
        m_opponentsMultiBuilds = new ArrayList<>();
        m_looseCardsToCapture = new ArrayList<>();
        m_multiBuildsToCapture = new ArrayList<>();
        m_singleBuildsToCapture = new ArrayList<>();
        m_oneSetOfLooseCardsToCapture = new ArrayList<>();
        m_looseCardsForSingleBuild = new ArrayList<>();
        m_mapFromIndicesToValues = new HashMap<>();
        m_dummySet = new ArrayList<>();

        // May need to initialize the variable below
        m_setOfLooseCardsToCapture = new ArrayList<>();
        m_holdCards = new ArrayList<>();
    }

    /**
     Initialize all member variables
     @param table - Table object used to initiliaze this class's Table
     member variable
     @param hand - a list of Card objects used to initiliaze m_hand
     @param opponentsBuilds - a list of Build objects used to iniliaze m_opponentsBuilds
     @param opponentsMultiBuilds - a list of MultiBuild objects used to iniliaze m_opponentsMultiBuilds
     */

    public Strategy(Table  table, ArrayList <Card> hand, ArrayList <Build> opponentsBuilds, ArrayList<MultiBuild> opponentsMultiBuilds, ArrayList<Card> holdCards){
        m_action = 0;
        m_cardToPlay = Input.INVALID_INDEX; // Invalid value
        m_reasonForAction = "";
        m_gameTable = table;
        m_hand = (ArrayList) hand.clone();
        m_opponentsBuilds = (ArrayList) opponentsBuilds.clone();
        m_opponentsMultiBuilds = (ArrayList)opponentsMultiBuilds.clone();
        m_capturedSingleBuild = false;
        m_capturedMultiBuild = false;
        m_buildOption = 0;

        //Invalid value as opponents is unlikely to have 1000 single builds in one round
        m_opponentsBuildToIncrease = Input.INVALID_INDEX;
        m_looseCardsForSingleBuild = new ArrayList<>();
        m_mapFromIndicesToValues = new HashMap<>();
        m_looseCardsToCapture = new ArrayList<>();
        m_multiBuildsToCapture = new ArrayList<>();
        m_singleBuildsToCapture = new ArrayList<>();
        m_oneSetOfLooseCardsToCapture = new ArrayList<>();
        m_dummySet = new ArrayList<>();

        // May need to initialize the variable below
        m_setOfLooseCardsToCapture = new ArrayList<>();
        m_holdCards = holdCards;
        capturedLooseCards = false;

        setMoveOption();
        initializeInput();
    }

    /* *********************************************
    Selectors
    ********************************************* */
    Input getInput() { return moves; }

    /* *********************************************
    Mutators
    ********************************************* */
    public void setTable(Table a_table) {
        m_gameTable = a_table;
    }

    /**
     This function decides the optimal move option for the player in a turn
     */
    private void setMoveOption(){

        if( canIncreaseOpponentsBuild() ){
            m_action = BUILD;
            return;
        }

        if( canCreateNewBuilds() ){
            m_action = BUILD;
            return;
        }

        if( capture() ){
            m_action = CAPTURE;
            return;
        }

        setUpTrail();
        m_action = TRAIL;
    }

    /**
     To initialize optimal input for a player based on optimal
     action
     */

    private void initializeInput(){
        // the line below is only for capture. For build, it should be different
        if(m_action == CAPTURE) {
            moves = new Input(m_dummySet, m_singleBuildsToCapture, m_multiBuildsToCapture, m_cardToPlay, m_action, CAPTURE_RATIONALE);
        }
        else if (m_action == BUILD && m_buildOption == CREATE_SINGLE_BUILD){
            // The line below is only for creating single build. It should be different for increasing opponent's build value
            moves = new Input(m_looseCardsForSingleBuild, new ArrayList<Integer>(), new ArrayList<Integer>(), m_cardToPlay, m_action, CREATE_BUILD_RATIONALE);
        }
        else if (m_action == BUILD && m_buildOption == INCREASE_OPPONENTS_BUILD){
            ArrayList<Integer> tempList = new ArrayList<>();
            tempList.add(m_opponentsBuildToIncrease);
            moves = new Input(new ArrayList<Integer>(), tempList, new ArrayList<Integer>(), m_cardToPlay, m_action, INCREASE_OPPONENTS_BUILD_RATIONALE );
        }
        else{
            moves = new Input(new ArrayList<Integer>(), new ArrayList<Integer>(), new ArrayList<Integer>(), m_cardToPlay, m_action, TRAIL_RATIONALE );
        }

        m_holdCards.clear();
    }

    /**
     To check if the player can capture any loose cards, single or multi
     builds or sets of cards     */

    private boolean capture(){
        int maxCapture = 0;
        ArrayList<Integer> individualLooseCardIndices; //Loose card indices based on the table
        ArrayList <Integer> multiBuildIndices;
        ArrayList <Integer> singleBuildIndices;
        ArrayList <Card> tableLooseCards = m_gameTable.getLooseCards();
        setUpMap( m_gameTable.getLooseCards() );

        for(int i = 0; i < m_hand.size(); i++){

            int possibleNumberOfCaptures = 0;
            Card handCard = m_hand.get(i);

            // Get individualooseCard Indices
            individualLooseCardIndices = m_gameTable.getLooseCardsWithGivenNumericValue( handCard.getNumericValue() );
            possibleNumberOfCaptures += individualLooseCardIndices.size();

            //Get set of loose cards ----- Need to implement this
            ArrayList<ArrayList <Integer> > setOfLooseCards = getCombinationsPart2( tableLooseCards, handCard.getNumericValue(true) ); //true means value of ace should be 14
            for(int k = 0; k < setOfLooseCards.size(); k++){
                possibleNumberOfCaptures += setOfLooseCards.get(k).size();
            }

            //Get multibuildIndices
            multiBuildIndices = m_gameTable.getMultiBuildsWithGivenNumericValue(handCard.getNumericValue(true));
            possibleNumberOfCaptures += possibleNumCardsCapturedInMultiBuild(multiBuildIndices);

            //Get singleBuildIndices
            singleBuildIndices = m_gameTable.getSingleBuildsWithGivenNumericValue(handCard.getNumericValue(true));
            possibleNumberOfCaptures += possibleNumCardsCapturedInSingleBuild(singleBuildIndices);

            if(possibleNumberOfCaptures > maxCapture){
                m_looseCardsToCapture = individualLooseCardIndices;
                m_multiBuildsToCapture = multiBuildIndices;
                m_singleBuildsToCapture = singleBuildIndices;
                m_cardToPlay = i;
                m_setOfLooseCardsToCapture = setOfLooseCards;
                addLooseCardIndices();
                maxCapture = possibleNumberOfCaptures;
            }

            if(!m_singleBuildsToCapture.isEmpty())
                sortBuildCapture();

            if(!m_multiBuildsToCapture.isEmpty())
                sortMultiBuildCapture();
        }

        if(maxCapture == 0)
            return false;
        return true;
    }

    /**
     To set up member variable, m_mapFromIndicesToValues which is
     an unordered map which is a map from a Card object to an integer value.
     */
    private void setUpMap(ArrayList <Card> a_vector){
        for(int i = 0; i < a_vector.size(); i++){
            m_mapFromIndicesToValues.put( a_vector.get(i), i );
        }
    }

    /**
     This function returns a set of set of integers, where each set is of size
     greater than 1.
     */

    ArrayList <ArrayList<Integer> > getCombinationsPart2(ArrayList<Card> originalListOfNumbers, int target){
        ArrayList <ArrayList<Integer> > setOfSetOfNumbers = new ArrayList<ArrayList<Integer>>();
        ArrayList <Card> partial = new ArrayList<>();
        ArrayList <Integer> partialIndices = new ArrayList<>();

        combos(originalListOfNumbers, partial, target, setOfSetOfNumbers, partialIndices);

        ArrayList<ArrayList <Integer> > newSet = new ArrayList<ArrayList<Integer>>();

        for(int i = 0; i < setOfSetOfNumbers.size(); i++){
            if(setOfSetOfNumbers.get(i).size() > 1)
                newSet.add(setOfSetOfNumbers.get(i));
        }

        return newSet;
    }

    /**
     This function populates a set of set of numbers, where the
     sum of numbers in each individual set == target as specified in the
     parameter
     */
    private void combos( ArrayList<Card>  originalListOfNumbers, ArrayList<Card> partial, int target, ArrayList <ArrayList<Integer> >  giantVector, ArrayList<Integer>  partialIndices){
        int sum1 = sum(partial);

        if(sum1 == target)
            giantVector.add(partialIndices);

        if(sum1 > target)
            return;

        for(int i = 0; i <originalListOfNumbers.size(); i++){
            Card  temp = originalListOfNumbers.get(i);
            int temp2 = m_mapFromIndicesToValues.get(temp);
            ArrayList<Card> remainingElements = new ArrayList<>();

            for(int j = i + 1; j < originalListOfNumbers.size(); j++){
                remainingElements.add(originalListOfNumbers.get(j));
            }

            ArrayList<Card> partial2 = (ArrayList) partial.clone();
            partial2.add(temp);

            ArrayList<Integer> partialIndices2 = (ArrayList) partialIndices.clone();
            partialIndices2.add(temp2);

            combos(remainingElements, partial2, target, giantVector, partialIndices2);
        }
    }

    /**
     To calculate a sum of numeric values of cards found in the parameter
     */

    private int sum(ArrayList<Card>  aVector){
        int sum = 0;
        for(int i = 0; i < aVector.size(); i++){
            sum += aVector.get(i).getNumericValue();
        }

        return sum;
    }

    /**
     To set indices of all loose cards and sets of cards that can be captured
     */

    private void addLooseCardIndices( ){
        m_dummySet.addAll( m_looseCardsToCapture );

        for(ArrayList<Integer> i : m_setOfLooseCardsToCapture){
            for(int j : i)
                m_dummySet.add( j );
        }
    }

    /**
     To calculate the number of cards in each multibuild represented by
     the multibuild index in the paramter and return the summation
     of number cards in all the multibuilds
     */

    int possibleNumCardsCapturedInMultiBuild(ArrayList<Integer> multiBuildIndices){
        int possibleNumberOfCaptures = 0;

        for(int i = 0; i < multiBuildIndices.size(); i++){
            int index = multiBuildIndices.get(i);
            MultiBuild  m = m_gameTable.getMultiBuilds().get(index);
            possibleNumberOfCaptures += (m.getMultiBuildSize());
        }

        return possibleNumberOfCaptures;
    }

    /**
     To calculate the number of cards in each single build represented by
     the single build index in the paramter and return the summation
     of number cards in all the single builds
     */    

    int possibleNumCardsCapturedInSingleBuild( ArrayList<Integer> singleBuildIndices){
        int possibleNumberOfCaptures = 0;

        for(int i = 0; i < singleBuildIndices.size(); i++){
            int index = singleBuildIndices.get(i);
            Build  b = m_gameTable.getSingleBuilds().get(index);
            possibleNumberOfCaptures += b.getBuildSize();
        }

        return possibleNumberOfCaptures;
    }

    /**
     To sort a member collection that contains indices of all single builds
     the player can capture in such a way such that the player's builds
     appear before her opponent's builds
     */

    private void sortBuildCapture(){
        ArrayList <Integer> sortedBuild = new ArrayList<>();
        ArrayList <Integer> oppBuildToCapture = new ArrayList<>();

        for(int i = 0; i < m_singleBuildsToCapture.size(); i++){
            int indexOfBuild = m_singleBuildsToCapture.get(i);
            Build buildToLook = m_gameTable.getSingleBuilds().get(indexOfBuild);

            if( !m_opponentsBuilds.contains(buildToLook) ) // Not found
                sortedBuild.add(indexOfBuild);
            else
                oppBuildToCapture.add(indexOfBuild);
        }

        for(int i = 0; i < oppBuildToCapture.size(); i++)
            sortedBuild.add(oppBuildToCapture.get(i));

        m_singleBuildsToCapture = (ArrayList) sortedBuild.clone();
    }

    /**
     To sort a member vector that contains indices of all multi builds
     the player can capture in such a way such that the player's multi
     build indices appear before her opponent's multi builds indices
     */

    private void sortMultiBuildCapture(){
        ArrayList <Integer> sortedBuild = new ArrayList<>();
        ArrayList <Integer> oppBuildToCapture = new ArrayList<>();

        for(int i = 0; i < m_multiBuildsToCapture.size(); i++){
            int indexOfBuild = m_multiBuildsToCapture.get(i);
            MultiBuild  buildToLook = m_gameTable.getMultiBuilds().get(indexOfBuild);

            if( !m_opponentsMultiBuilds.contains(buildToLook) ) // Not found
                sortedBuild.add(indexOfBuild);
            else
                oppBuildToCapture.add(indexOfBuild);
        }

        for(int i = 0; i < oppBuildToCapture.size(); i++)
            sortedBuild.add(oppBuildToCapture.get(i));

        m_multiBuildsToCapture = (ArrayList) sortedBuild.clone();
    }

    /**
     To check if the player can increase a single build that belongs
     to her opponent
     @return  true if the player can increase an opponent's build.
     false otherwise
     */
    boolean canIncreaseOpponentsBuild(){
        int highestNumOfCardsInABuild = 0;

        for(int i = 0; i < m_opponentsBuilds.size(); i++){
            Build  oppBuild = m_opponentsBuilds.get(i);
            int buildValue = oppBuild.getNumericValue();

            for(int j = 0; j < m_hand.size(); j++){    //j denotes the card that could potentially be incorporated into the increased build
                for(int k = 0; k < m_hand.size(); k++){  //k denotes the card whose value should equal the value of the increased build
                    if(k != j){

                        if ( m_holdCards.contains(m_hand.get(j) ) )
                            continue;

                        if(buildValue + m_hand.get(j).getNumericValue() == m_hand.get(k).getNumericValue(true) ){ //The player can potentially increase an opponents build
                            if( oppBuild.getBuildSize() > highestNumOfCardsInABuild ){ //Select the largest build
                                m_cardToPlay = j;
                                //Build index to increase == i
                                m_opponentsBuildToIncrease = i;
                                highestNumOfCardsInABuild = oppBuild.getBuildSize();
                                m_buildOption = INCREASE_OPPONENTS_BUILD;
                            }
                        }
                    }

                }
            }
        }
        if(highestNumOfCardsInABuild > 0) //MEaning there is at least one build (a build has more than one card) that the user can increase
            return true;

        return false;
    }

    /**
     To check if the player can create a single build
     @return true if the player can create a single build.
     false otherwise
     */
    private boolean canCreateNewBuilds(){
        ArrayList <Card> tableLooseCards = m_gameTable.getLooseCards();
        setUpMap(m_gameTable.getLooseCards());

        for(int playCard = 0; playCard < m_hand.size(); playCard++ ){
            for(int holdCard = 0; holdCard < m_hand.size(); holdCard++){
                if(playCard != holdCard){

                    if( m_holdCards.contains( m_hand.get(playCard) ) )
                        continue;

                    int target = m_hand.get(holdCard).getNumericValue(true) - m_hand.get(playCard).getNumericValue();
                    ArrayList< ArrayList <Integer> > multipleSetOfLooseCards = getCombinations(tableLooseCards, target);

                    //Compute biggest setOfLooseCards
                    ArrayList <Integer> biggestSetOfLooseCards = getLargestSet(multipleSetOfLooseCards);

                    if(biggestSetOfLooseCards.size() > m_looseCardsForSingleBuild.size()){
                        m_looseCardsForSingleBuild = biggestSetOfLooseCards;
                        m_cardToPlay = playCard;
                    }
                }
            }
        }

        if(m_looseCardsForSingleBuild.size() > 0 ){
            m_buildOption = CREATE_SINGLE_BUILD;
            return true;
        }

        return false;
    }

    /**
     This function returns a set of set of integers, where each element in each
     set represents an index of a card on the player's hand
     @param originalListOfNumbers -a list of Card objects
     @target - an integer variable that specifies what the cards
     in each individual set should add up to
     @return setOfSetOfNumbers - a list of list of int where each list of int
     contains a set of integer values. Each integer value is 
     an index of the card  
     */

    private ArrayList <ArrayList<Integer> > getCombinations( ArrayList<Card>  originalListOfNumbers, int target){
        ArrayList <ArrayList<Integer> > setOfSetOfNumbers = new ArrayList<>();
        ArrayList <Card> partial = new ArrayList<>();
        ArrayList <Integer> partialIndices = new ArrayList<>();

        combos(originalListOfNumbers, partial, target, setOfSetOfNumbers, partialIndices);

        return setOfSetOfNumbers;
    }


    /**
     Given a list of lists, this function finds the larges list
     @return the list with highest number of elements
     */
    private ArrayList <Integer> getLargestSet(ArrayList<ArrayList<Integer> > multipleSets){
        ArrayList <Integer> largest = new ArrayList<>();

        for(int i = 0; i < multipleSets.size(); i++){
            if(multipleSets.get(i).size() > largest.size())
                largest = multipleSets.get(i);
        }

        return largest;
    }

    /**
     To identify the correct card to choose for trail
     */

    private void setUpTrail(){
    /*
        Compute weight for every card in hand. The higher the weight, higher the value of the card.
        Trail the card with the lowest weight
    */
        int lowestWeight = Integer.MAX_VALUE;

        for(int i = 0; i < m_hand.size(); i++){
            Card c = m_hand.get(i);
            if(c.getCardWeight() < lowestWeight ){
                m_cardToPlay = i;
                lowestWeight = c.getCardWeight();
            }
        }
    }

    /**
     To recalculate indices of loose cards based on the table
     loose cards and the played card. This is necessary if the table
     is updated
     */
    public void recalculateSetOfLooseCards() {
            if (  !m_looseCardsToCapture.isEmpty() ) {
                moves = new Input(m_looseCardsToCapture, m_singleBuildsToCapture, m_multiBuildsToCapture, m_cardToPlay, m_action, CAPTURE_RATIONALE);
                m_looseCardsToCapture.clear();
                capturedLooseCards = true;
                return;
            }

        ArrayList<Card> tableLooseCards = m_gameTable.getLooseCards();
        setUpMap(m_gameTable.getLooseCards());

        ArrayList<ArrayList<Integer>> looseCardIndices = getCombinationsPart2(tableLooseCards, m_hand.get(m_cardToPlay).getNumericValue(true)); // true means ace value will be 14

        if (looseCardIndices.size() > 0)
            m_oneSetOfLooseCardsToCapture = looseCardIndices.get(0);
        else
            m_oneSetOfLooseCardsToCapture.clear();

        moves = new Input(m_oneSetOfLooseCardsToCapture, m_singleBuildsToCapture, m_multiBuildsToCapture, m_cardToPlay, m_action, CAPTURE_RATIONALE);

        //Whereas you can capture multibuilds, single builds or individual cards
        // all at once, you need to capture set of cards one set at a time
        // The three lines below remove a set once it has been captured
        if (m_setOfLooseCardsToCapture.size() > 0)   //Do you really need to do this?
            m_setOfLooseCardsToCapture.remove(0);
    }

    /**
     To compute indices of all sets of loose cards that can be captured
     @param target - Determines what the value of a set of loose cards should be
     @return a set of indices of all sets of cards that can be captured
     */

    public ArrayList<Integer> getSets( int target ){ //target is usually handcard.getNumericValue(true)
        // The list below won't be modified in this function
        final ArrayList<Card> tableLooseCards = m_gameTable.getLooseCards();

        // The list, constructSetsFrom will be modified in this function
        ArrayList<Card> constructSetsFrom = m_gameTable.getLooseCards();
        ArrayList<Integer> overallSet = new ArrayList<>();
        ArrayList<ArrayList <Integer> > setOfSets = new ArrayList<>();
        ArrayList<Card> overallCardList = new ArrayList<>();

        do{
            setUpMap(constructSetsFrom);
            setOfSets = getCombinationsPart2( constructSetsFrom, target);

            if( !setOfSets.isEmpty() ){
                ArrayList<Integer> oneSet = (ArrayList) setOfSets.get(0).clone();

                ArrayList<Card> tempCardList = new ArrayList<>();

                for(Integer i: oneSet)
                    tempCardList.add( constructSetsFrom.get(i) );

                overallCardList.addAll( tempCardList );
                constructSetsFrom.removeAll(tempCardList);
            }

        } while ( !setOfSets.isEmpty() );

        //Convert cards to table indices
        for(Card c: overallCardList)
            overallSet.add( tableLooseCards.indexOf(c) );

        return overallSet;
    }

    /**
     To summarize the entire move
     */

    public String summarizeMove(){

        StringBuilder summary = new StringBuilder();
        summary.append("play ");
        summary.append(m_hand.get(m_cardToPlay).getStringRepresentation() );
        summary.append( " to ");

        switch(m_action){
            case BUILD:
                summarizeBuild(summary);
                break;
            case CAPTURE:
                summarizeCapture(summary);
                break;
            case TRAIL:
                summary.append(" trail");
                summary.append(TRAIL_RATIONALE);
                break;
        }

        return summary.toString();
    }

    /**
     To summarize all the build actions taken
     */

    private void summarizeBuild(StringBuilder summary){
        if(m_buildOption == CREATE_SINGLE_BUILD){
            summary.append( " create a build using the following loose cards from table : ");

            for(int i = 0; i < m_looseCardsForSingleBuild.size(); i++){
                int tableLooseCardIndex = m_looseCardsForSingleBuild.get(i);
                summary.append( m_gameTable.getLooseCards().get(tableLooseCardIndex).getStringRepresentation() );
                summary.append(", ");
            }
            summary.append(CREATE_BUILD_RATIONALE);
        }
        else{
            summary.append( " increase the following build that belongs to your opponent: ");
            summary.append(m_opponentsBuilds.get(m_opponentsBuildToIncrease).toString() );
            summary.append( INCREASE_OPPONENTS_BUILD_RATIONALE );
        }
    }

    /**
     To summarize all the capture actions taken
     */
    private void summarizeCapture(StringBuilder summary){
        if( !m_looseCardsToCapture.isEmpty() ){
            summary.append( "capture the following loose cards from the table: ");
            for(int i = 0; i < m_looseCardsToCapture.size(); i++ ){
                int index = m_looseCardsToCapture.get(i);
                summary.append( m_gameTable.getLooseCards().get(index).getStringRepresentation() );
                summary.append(", ");
            }
        }

        ArrayList<Integer> capturableSets = getSets(m_hand.get(m_cardToPlay).getNumericValue(true));

        if( !capturableSets.isEmpty() ){
            for(Integer i: m_looseCardsToCapture){
                //Meaning there is a card that is in both individualLooseCards and in set of cards. This can only happen if there is an Ace on the
                // table that can be used to capture an individual loose card and a set of cards. It is user's choice what he wants to capture
                if( capturableSets.contains(i) ){
                    summary.append( " OR ");
                    break;
                }
            }

            summary.append( " capture the following sets of cards from the table: ");
            for(Integer index: capturableSets)
                summary.append(m_gameTable.getLooseCards().get(index).getStringRepresentation() + ",");
        }

        if(!m_singleBuildsToCapture.isEmpty()){
            summary.append( " capture the following single builds from the table: ");
            for(int i = 0; i < m_singleBuildsToCapture.size(); i++){
                int buildIndex = m_singleBuildsToCapture.get(i);
                summary.append(m_gameTable.getSingleBuilds().get(buildIndex).toString() );
                summary.append(", ");
            }
        }

        if(!m_multiBuildsToCapture.isEmpty() ){
            summary.append( " capture the following multi builds from the table: ");
            for(int i = 0; i < m_multiBuildsToCapture.size(); i++){
                int multiBuildIndex = m_multiBuildsToCapture.get(i);
                summary.append( m_gameTable.getMultiBuilds().get(multiBuildIndex).toString() );
                summary.append( ", " );
            }
        }
        summary.append( CAPTURE_RATIONALE );
    }
}
