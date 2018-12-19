package edu.ramapo.aghimir1.casino;

import java.io.Serializable;
import java.util.ArrayList;

public class Input implements Serializable {
    /* *********************************************
    Symbolic constants
    ********************************************* */
    public static final int INVALID_INDEX = 5000;

    /* *********************************************
    Class member variables
    ********************************************* */
    private ArrayList<Integer> indexOfLooseTableCards;
    private ArrayList<Integer> indexOfSingleBuilds;
    private ArrayList<Integer> indexOfMultiBuilds;
    private int handCardIndex;
    private int m_action;
    private String m_rationale;

    /* *********************************************
    Constructors
    ********************************************* */

    public Input(){
        indexOfLooseTableCards = new ArrayList<>();
        indexOfSingleBuilds = new ArrayList<>();
        indexOfMultiBuilds = new ArrayList<>();
        handCardIndex = INVALID_INDEX;
        m_action = INVALID_INDEX;
        m_rationale = "";
    }

    public Input( ArrayList<Integer> looseTableCards, ArrayList<Integer> singleBuilds, ArrayList<Integer> multiBuilds, int handCard, int action, String rationale){
        indexOfLooseTableCards = (ArrayList)looseTableCards.clone();
        indexOfSingleBuilds = (ArrayList)singleBuilds.clone();
        indexOfMultiBuilds = (ArrayList)multiBuilds.clone();
        handCardIndex = handCard;
        m_action = action;
        m_rationale = rationale;
    }

     /* *********************************************
    Selectors
    ********************************************* */

    /**
     Selector
     @return returns the indices of loosecards that a player selected
     */

    public ArrayList<Integer> getIndexOfLooseTableCards() {
        return (ArrayList) indexOfLooseTableCards.clone();
    }

    /**
     Selector
     @return returns the indices of single builds that a player selected
     */

    public ArrayList<Integer> getIndexOfSingleBuilds() {
        return (ArrayList)indexOfSingleBuilds.clone();
    }

    /**
     Selector
     @return returns the action the user took. action is one of
     BUILD, CAPTURE, TRAIL
     */

    public int getAction() {
        return m_action;
    }

    /**
     Selector
     @return returns the reason which this class ecommends taking
     a move
     */

    public String getRationale() {
        return m_rationale;
    }

    /**
     Selector
     @return returns the indices of multi builds that a player selected

     */

    public ArrayList<Integer> getIndexOfMultiBuilds() {
        return (ArrayList)indexOfMultiBuilds.clone();
    }

    /**
     Selector
     @return returns the index of the card player chose to play     */

    public int getHandCardIndex() {
        return handCardIndex;
    }

    /* *********************************************
     Main function that could be used for testing
    ********************************************* */
    public static void main(String [] args){

    }
}
