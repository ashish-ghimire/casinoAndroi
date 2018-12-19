package edu.ramapo.aghimir1.casino;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Player implements Serializable {

    /* *********************************************
    Class member variables that are protected
    ********************************************* */
    protected static final int MAX_CARDS_IN_A_HAND = 4;
    protected static final int INVALID_INDEX = 5000;
    protected int m_score;
    protected ArrayList <Card> m_pile;
    protected ArrayList <Card> m_hand;
    protected ArrayList <Build> m_build;
    protected ArrayList<MultiBuild> m_multiBuild;
    protected Input m_input;

    // When creating a build, there should be a card in your hand whose numeric
    // value equals the sum of numeric values of cards in a build. I call the
    // card to hold in your hand, the hold card. The multimap below maps
    // the sum of a build to one or more Cards whose numeric value equals to the sum (hold cards)
    protected Map<Integer, List<Card> > m_holdCardsForSingleBuilds;

    // The multimap below maps the sum of a build to one or more Cards whose
    // numeric value equals to the sum. Cards whose numeric value equals to the
    // sum are called hold cards
    protected Map <Integer, List<Card> > m_holdCardsForMultiBuilds;
    protected int m_numSpades;

    /* *********************************************
    Constructor
    ********************************************* */
    public Player(){
        m_score = 0;
        m_hand = new ArrayList<>();
        m_pile = new ArrayList<>();
        m_build = new ArrayList<>();
        m_multiBuild = new ArrayList<>();
        m_holdCardsForSingleBuilds = new HashMap<Integer, List<Card>>();
        m_holdCardsForMultiBuilds = new HashMap<Integer, List<Card>>();
        m_input = new Input();
        m_numSpades = 0;
    }

    /* *********************************************
     Selectors
    ********************************************* */

    /**
     A selector that returns a player's score
     @return an integer value that denotes a player's score
     */
     int getScore() {
         return m_score;
     }

    /**
     A selector that returns a collection of all the cards that constitute a player's pile
     @return an ArrayList of all cards in a player's pile
     */
     ArrayList <Card> getPile()  { return (ArrayList) m_pile.clone(); }

    /**
     A selector that returns a collection of all the cards that constitute a player's hand
     @return an ArrayList of all cards in a player's hand
     */
     ArrayList <Card> getHand()  { return (ArrayList) m_hand.clone(); }

    /**
     A selector that returns a collection of all the builds a player has
     @return an ArrayList of builds a player has
     */
     ArrayList <Build> getBuild() { return (ArrayList) m_build.clone(); }

    /**
     A selector that returns a collection of all the multibuilds a player has
     @return an ArrayList of all multibuilds a player has
     */
     ArrayList <MultiBuild> getMultiBuild() { return (ArrayList)m_multiBuild.clone(); }

    /**
     A selector that returns the number of spades a player has in her pile
     @return an integer value that denotes a player's spade count
     */
     int getNumSpades(){
         return m_numSpades;
     }

    /**
     A selector that returns a collection of all the hold cards a player has.
     When creating a build/ multibuild, there should be a card in your hand whose numeric
     value equals the sum of numeric values of cards in a build. I call the
     card to hold in your hand, the hold card.
     @return an ArrayList of all hold cards a player has
     */
    public ArrayList<Card> getAllHoldCards(){
        ArrayList<Card> allHoldCards = new ArrayList<>();

        allHoldCards.addAll( getHoldCards(m_holdCardsForSingleBuilds) );
        allHoldCards.addAll( getHoldCards(m_holdCardsForMultiBuilds) );

        return allHoldCards;
    }

    /**
     A selector that returns a collection of hold cards given a map
     @param a_map, a map of integer to lists. This map should have information
     about hold cards. An instance of a_map could look something like
     13 -> CK, HK
     @return an ArrayList of all hold cards a player has in a map
     */
    private ArrayList<Card> getHoldCards(Map <Integer, List<Card> > a_map){
        ArrayList<Card> holdCards = new ArrayList<>();

        if(a_map == null)
            return holdCards;

        for (List<Card> oneList : a_map.values() ) {
            if(oneList != null && oneList.size() == 1){
                holdCards.add( oneList.get(0) );
            }
        }

        return (ArrayList) holdCards.clone();
    }

    /* *********************************************
     Abstract methods that are also selectors
    ********************************************* */
    abstract Input getInput(Strategy moveStrategy);
    abstract String getHelp(Strategy moveStrategy);
    public abstract String getPlayerType();

    /* *********************************************
     Mutators
    ********************************************* */

    /**
     A mutator that updates a player's score
     @param pts, an integer variable that represents the amount of points to be
     added to a player's score
     @return true if the player's score was updated. false otherwise
     */
    public boolean updateScore(int pts){
        if(pts > 50)
            return false;

        m_score += pts;
        return true;
    }

    /**
     A mutator that sets a player's pile
     @param pile, an arraylist of cards which are used to set a player's pile
     */
    public void setPile(ArrayList <Card> pile) { m_pile = pile; }

    /**
     A mutator that sets a player's pile
     @param hand, an arraylist of cards which are used to set a player's hand
     */
    public void setHand(ArrayList <Card> hand) { m_hand = hand; }

    /**
     A mutator that sets a player's score
     @param pts, an integer variable that determines what score the player should have
     */
    public void setScore(int pts) {
        m_score = pts;
    }

    /**
     A mutator that sets a player's build collection
     @param build, an arraylist of builds which are used to set a player's build collection
     */
    public void setBuild(ArrayList <Build> build ) {
        m_build = build;
    }

    /**
     A mutator that sets a player's multibuild collection
     @param multi, an arraylist of multibuilds which are used to set a player's build collection
     */
    public void setMultiBuild(ArrayList <MultiBuild> multi) { m_multiBuild = multi; }

    /**
     A mutator that sets a player's spade Card count
     @param numSpades, an integer variable that determines the number of spades the player has
     */
    public void setNumSpades(int numSpades) {
        this.m_numSpades = numSpades;
    }

    /**
     A mutator that sets a player's input
     @param input, an Input object that contains information about the specific inputs for the game
     in order to make moves
     */
    public void setInput(Input input) { m_input = input; }

    /**
     A mutator that adds a card to the player's hand if the card is valid
     @param c, a card object
     @return true if the card was added to the hand. false otherwise
     */
    public boolean addToHand(Card  c){
        if( m_hand.size() >= MAX_CARDS_IN_A_HAND )
            return false;

        m_hand.add(c);
        return true;
    }

    /**
     A mutator that adds a card to the player's pile if the card is valid
     @param c, a card object
     @return true if the card was added to the pile. false otherwise
     */
    public boolean addToPile(Card  c){
        if(c == null)
            return false;

        m_pile.add(c);
        return true;
    }

    /**
     A mutator that adds a group of cards in a build to the player's pile
     @param b, a Build object
     @return true if all cards were added to the pile. false otherwise
     */
    public boolean addToPile(Build  b){
        if(b == null)
            return false;

        ArrayList <Card> cardsInTheSelectedBuild = b.getBuildCards();
        for(Card c: cardsInTheSelectedBuild)
            addToPile(c);

        return true;
    }

    /**
     A mutator that adds a group of cards in a muktibuild to the player's pile
     @param m, a MultiBuild object
     @return true if all cards were added to the pile. false otherwise
     */
    public boolean addToPile(MultiBuild  m){
        if( m == null)
            return false;

        ArrayList <Build> allBuilds = m.getMultiBuild();
        for(Build b: allBuilds )
            addToPile(b);

        return true;
    }

    /**
     A mutator that adds a build to a player's build collection, This makes the player
     the owner of the build
     @param b, a Build object
     @return true if build was successfully added. false othwerwise
     */
    public boolean addABuild(Build  b){
        if(b == null)
            return false;

        m_build.add(b);
        // When creating a build, there should be a card in your hand whose numeric
        // value equals the sum of numeric values of cards in a build. I call the
        // card to hold in your hand, the hold card. The function below configures
        // hold cards for a build
        setHoldCardsForSingleBuild( b.getNumericValue() );
        return true;
    }

    /**
     A mutator that adds a multibuild to a player's multibuild collection, This makes the player
     the owner of the multibuild
     @param m, a MultiBuild object
     @return true if multibuild was successfully added. false othwerwise
     */
    public boolean addAMultiBuild(MultiBuild  m){
        if(m == null)
            return false;

        m_multiBuild.add(m);
        setHoldCardsForMultiBuild( m.getNumericValue() );
        return true;
    }

    /**
     A mutator that removes a card from a players hand
     @param handIndex, an integer variable that represents the card at what index
     should be removed
     */
    public void removeFromHand(int handIndex){
        if(handIndex < 0 || handIndex > m_hand.size())
            return;

        m_hand.remove(handIndex);
    }

    /**
     A mutator that removes a build from the player's build collection
     @param index, an integer variable that represents the build at what index
     should be removed
     */
    public void removeBuild(int index){
        if(index < 0 || index > m_build.size())
            return;

        // Remove all map elements that match the build's numeric value
        m_holdCardsForSingleBuilds.remove( m_build.get(index).getNumericValue() );
        m_build.remove(index);
    }

    /**
     A mutator that removes a build from the player's build collection
     @param build, a Build object that should be removed
     */
    public void removeBuild(Build  build){
        if(build != null){
            m_holdCardsForSingleBuilds.remove( build.getNumericValue() );
            m_build.remove( build );
        }
    }

    /**
     A mutator that removes a multibuild from the player's multibuild collection
     @param multiBuild, a MultiBuild object that should be removed
     */
    public void removeMultiBuild(MultiBuild  multiBuild){
        m_holdCardsForMultiBuilds.remove( multiBuild.getNumericValue() );
        m_multiBuild.remove(multiBuild);
    }

    /**
     A mutator that removes all hold cards whose numeric value is equal to the numeric
     value of the card specified by the parameter
     @param cardFromHandIndex, an integer variable that represents the index of a card
     in a player's hand
     */
    public void removeHoldCard(int cardFromHandIndex){
        Card card = m_hand.get(cardFromHandIndex);
        int holdValue = card.getNumericValue();

        List<Card> holdCards = m_holdCardsForMultiBuilds.get(holdValue);
        if( !(holdCards == null) ){
            for(Card i: holdCards){
                if(i == card)
                    holdCards.remove(i);
            }
        }

        holdCards = m_holdCardsForSingleBuilds.get(holdValue);
        if(!(holdCards == null) ){
            for(Card i: holdCards){
                if(i == card)
                    holdCards.remove(i);
            }
        }
    }

    /* *********************************************
     Main function that may be used for debugging
    ********************************************* */
    public static void main(String [] args) {
    }

    /* *********************************************
     Utility functions
    ********************************************* */

    /**
     A function used to determine if the card represented by the parameter is a "hold card"
     When creating a build, there should be a card in your hand whose numeric
     value equals the sum of numeric values of cards in a build. I call the
     card to hold in your hand, the hold card.
     @param cardFromHandIndex, an integer variable that represents the index of a card
     in a player's hand
     */
    public boolean isHoldCard(int cardFromHandIndex){
        if( isHoldCard(cardFromHandIndex, m_holdCardsForSingleBuilds) || isHoldCard(cardFromHandIndex, m_holdCardsForMultiBuilds) )
            return true;

        return false;
    }

    /**
     A utility function that writes a Player's hand as string to a file
     @param f, a Formattter variable that represents which file to write to
     */
    void writeHandToFile(Formatter f){
        for(Card i: m_hand){
            i.writeCardToFile(f);
            f.format("%c", ' ');
        }
        f.format("%s", "\n");
    }

    /**
     A utility function that writes a Player's pile as string to a file
     @param f, a Formattter variable that represents which file to write to
     */
    void writePileToFile(Formatter f){
        for(Card i: m_pile){
            i.writeCardToFile(f);
            f.format("%c", ' ');
        }
        f.format("%s", "\n");
    }

    /**
     A utility function that lets the caller know if the player owns any single build
     @return true if the player owns at least one single build. false othwerwise
     */
    public boolean hasSingleBuild(){
        return !m_build.isEmpty();
    }

    /**
     A function that lets the caller know if the player owns any multi build
     @return true if the player owns at least one multi build. false othwerwise
     */
    public boolean hasMultiBuild(){
        return !m_multiBuild.isEmpty();
    }


    /**
     Lets the caller know if the player has the card specified in the parameter in his hand
     @param indexToAvoid, an integer value that tells which index to avoid while searching for
      a card in the player's hand
     @param requiredNumericValue, an integer that tells the function to search for a card with
     the value specified by this parameter
     @param aceIsFourteen, a boolean variable that determines if the value of an ace card should be
     fourteen while searching for it
     @return true if the player has the card the caller is looking for. false otherwise
     */
    public boolean handHasACardWithAGivenNumericValue(int indexToAvoid, int requiredNumericValue, boolean aceIsFourteen){
        for(int i = 0; i < m_hand.size(); i++){
            if(i != indexToAvoid){
                if( m_hand.get(i).getNumericValue(aceIsFourteen) == requiredNumericValue)
                    return true;
            }
        }

        return false;
    }

    /**
     Overloaded function that calls
     public boolean handHasACardWithAGivenNumericValue(int indexToAvoid, int requiredNumericValue, boolean aceIsFourteen)
     with default value of aceIsFourteen = false
     @param indexToAvoid, an integer value that tells which index to avoid while searching for
     a card in the player's hand
     @param requiredNumericValue, an integer that tells the function to search for a card with
     the value specified by this parameter
     @return true if the player has the card the caller is looking for. false otherwise
     */
    public boolean handHasACardWithAGivenNumericValue(int indexToAvoid, int requiredNumericValue){
        return handHasACardWithAGivenNumericValue(indexToAvoid, requiredNumericValue, false);
    }

    /**
     Returns the index of the build in the player's collection. Returns the index of the
     build whose numeric value is equal to the value specified by the parameter
     @param numericValue, an integer variable that tells the function to return the index
     of a build whose numeric value is equal to this parameter's value
     @return an integer reprsenting the relevant build index if a relevant build exists
     Error value otherwise equal to symbolic constant, INVALID_INDEX
     */
    public int getBuildIndex(int numericValue){
        for(int i = 0; i < m_build.size(); i++){
            if(m_build.get(i).getNumericValue() == numericValue)
                return i;
        }
        return INVALID_INDEX;
    }

    /**
     Returns the index of the multibuild in the player's collection. Returns the index of the
     multibuild whose numeric value is equal to the value specified by the parameter
     @param numericValue, an integer variable that tells the function to return the index
     of a multibuild whose numeric value is equal to this parameter's value
     @return an integer reprsenting the relevant multibuild index if a relevant multibuild exists
     Error value otherwise equal to symbolic constant, INVALID_INDEX
     */

    public int getMultiBuildIndex(int numericValue){
        for(int i = 0; i < m_multiBuild.size(); i++){
            if(m_multiBuild.get(i).getNumericValue() == numericValue )
               return i;
        }
        return INVALID_INDEX;
    }

    /**
     Lets the caller know if the player has a specific build
     @param build, a Build object that the caller is looking for
     @return true if the player has the build specified by the parameter. false otherwise
     */
    public boolean hasAGivenSingleBuild(Build build){
        return m_build.contains(build);
    }

    /**
     Lets the caller know if the player has a specific multibuild
     @param multibuild, a MultiBuild object that the caller is looking for
     @return true if the player has the multibuild specified by the parameter. false otherwise
     */
    public boolean hasAGivenMultiBuild(MultiBuild multibuild){
        return m_multiBuild.contains(multibuild);
    }

    /**
     A utility function that returns the invalid index specified by the Player class
     @return an integer value of the invalid index
     */
    public int getInvalidIndex() { return INVALID_INDEX; }

    /**
     When creating a build, there should be a card in your hand whose numeric
     value equals the sum of numeric values of cards in a build. I call the
     card to hold in your hand, the hold card. The function below configures
     hold cards for a single build
     @param buildSum- an integer that determines what should the value of
     the hold card be
     */
    private void setHoldCardsForSingleBuild(int buildSum){
        List<Card> tempList = new ArrayList<Card>();

        for(Card c : m_hand){
            if(c.getNumericValue(true) == buildSum)
                tempList.add(c);
        }

        if( !tempList.isEmpty() )
            m_holdCardsForSingleBuilds.put(buildSum, tempList);
    }

    /**
     When creating a multibuild, there should be a card in your hand whose numeric
     value equals the sum of numeric values of cards in a multibuild. I call the
     card to hold in your hand, the hold card. The function below configures
     hold cards for a multi build
     @param multiBuildSum- an integer value that determines what should the value of
     the hold card be
     */
    public void setHoldCardsForMultiBuild(int multiBuildSum){
        List<Card> tempList = new ArrayList<Card>();

        for(Card c : m_hand){
            if(c.getNumericValue(true) == multiBuildSum)
                tempList.add(c);
        }

        if( !tempList.isEmpty() )
            m_holdCardsForMultiBuilds.put(multiBuildSum, tempList);
    }

    /**
     When creating a multibuild, there should be a card in your hand whose numeric
     value equals the sum of numeric values of cards in a multibuild. I call the
     card to hold in your hand, the hold card. The function below configures
     hold cards for a multi build
     @param cardFromHandIndex - an integer passed by value that should be a vector index
     of one of the hand cards the player has (cards in m_hand)
     @param a_map - map that contains info about hold cards for the player
     @return true if the card represented by the index in the parameter is a hold card.
     false otherwise
     */
    private boolean isHoldCard(int cardFromHandIndex, Map <Integer, List<Card> > a_map){
        Card card = m_hand.get(cardFromHandIndex);

        // getNumericValue(true) indicates the value of an Ace card should be 14
        List<Card> tempList = a_map.get( card.getNumericValue(true) );

        if( tempList != null && tempList.size() == 1 )
            return true;

        return false;
    }
}
