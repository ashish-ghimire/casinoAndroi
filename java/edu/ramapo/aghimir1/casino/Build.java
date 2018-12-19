package edu.ramapo.aghimir1.casino;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Formatter;

public class Build implements Serializable {

    /* *********************************************
    Class member variables
    ********************************************* */
    private ArrayList<Card> m_cardsInABuild;
    private String m_owner;
    private int m_numericValue;

    /* *********************************************
    Constructors
    ********************************************* */
    public Build(){
        m_numericValue = 0;
        m_cardsInABuild = new ArrayList<Card>();
    }

    public Build(ArrayList<Card> buildCards, String owner, int value){
        m_cardsInABuild = (ArrayList)buildCards.clone();
        m_owner = owner;
        m_numericValue = value;
    }

    /* *********************************************
    Selectors
   ********************************************* */
    /**
     A selector that returns lets the caller know who owns a build
     @return a String value representing the owner of a build
     */
    final String getOwner() {
        return m_owner;
    }

    /**
     A selector that returns a collection of all the cards that constitute a build
     @return an ArrayList of all cards in a build
     */
    ArrayList<Card> getBuildCards() {
        return (ArrayList)m_cardsInABuild.clone();
    }

    /**
     A function that returns the size of a build
     @return an integer value that denotes the number of cards in a build
     */
    public int getBuildSize() {
        return  m_cardsInABuild.size();
    }

    /**
     A selector that returns the numeric value of a build (sum of numeric value
     of all cards in a build)
     @return an integer representing the the numeric value of a build
     */
    public int getNumericValue() {
        if(m_cardsInABuild.size() == 1 && m_cardsInABuild.get(0).getFaceValue() == Card.ACE){
            // Build is composed of only one ace card. In such a case, value of ace is always 14
            return  m_numericValue + 13;
        }

        return m_numericValue;
    }

    /* *********************************************
    Mutators
   ********************************************* */
    /**
     A mutator that verifies and adds a card to a build
     @param aCard, a Card object
     */
    public void addCardToABuild(Card aCard){
        if(aCard == null)
            return;

        m_cardsInABuild.add(aCard);
        m_numericValue += aCard.getNumericValue();
    }

    /**
     A mutator that sets the owner of a build
     @param ownerName, a String variable is used to set an owner's value
     */
    public void setOwner(String ownerName){
        m_owner = ownerName;
    }

    /* *********************************************
    Main function that may be used for debugging
   ********************************************* */
    public static void main(String[] args){

    }


    /* *********************************************
    Utility functions
   ********************************************* */

    /**
     An overloaded method that helps to cast a Build to a string.
     @return a string representation of a build.
     */
    public String toString(){
        StringBuilder buildString = new StringBuilder("[");
        for(int i = 0; i < m_cardsInABuild.size(); i++){
            buildString.append( m_cardsInABuild.get(i).toString() );
            if(i < m_cardsInABuild.size() - 1)
                buildString.append(" ");
        }
        buildString.append("] ");
        return buildString.toString();
    }

    /**
     A utility function that writes a Build as a string to a file
     @param f, a Formattter variable that represents which file to write to
     */
    void writeToFile(Formatter f){
        f.format("%s", this.toString() );
    }
}
