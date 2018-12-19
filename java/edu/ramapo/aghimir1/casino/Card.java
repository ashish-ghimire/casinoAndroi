package edu.ramapo.aghimir1.casino;

import java.io.Serializable;
import java.util.Formatter;

public class Card implements Serializable {

    /* *********************************************
    Symbolic constants that represent certain face values a card may have
    ********************************************* */
    public static final char FACE_FOR_10 = 'X';
    public static final char JACK = 'J';
    public static final char QUEEN = 'Q';
    public static final char KING = 'K';
    public static final char ACE = 'A';

    /* *********************************************
    Symbolic constants for suite characters for cards
    ********************************************* */
    public static final char SPADE = 'S';
    public static final char CLUB = 'C';
    public static final char DIAMOND = 'D';
    public static final char HEART = 'H';

    /* *********************************************
    Symbolic constants for face value for cards
    ********************************************* */
    private static final int ASCIICodeFor2 = 50;
    private static final int ASCIICodeFor9 = 57;

    /* *********************************************
    Class member variables
    ********************************************* */
    //m_suiteCharacter can be either S (Spade), C (Club), D(Diamnond), H(Heart)
    private char m_suiteCharacter;

    // m_faceValue can be 1-9/X/J/Q/K/A
    private char m_faceValue;

    /* *********************************************
    Constructors
    ********************************************* */
    public Card(){
        m_faceValue = ' ';
        m_suiteCharacter = ' ';
    }

    public Card(char suite, char face){
        m_suiteCharacter = suite;
        m_faceValue = face;
    }

    /* *********************************************
    Selectors
    ********************************************* */

    /**
     A selector that returns the numeric value of a card
     @param aceIs14, a boolean character that determines if the
     value of Ace card should be 14 or not
     @return an integer value representing the numeric value of a card
     */
    public int getNumericValue(boolean aceIs14) {
        int numericValue = 0;

        switch(m_faceValue){
            case ACE:
                if(aceIs14)
                    numericValue = 14;
                else
                    numericValue = 1;
                break;
            case FACE_FOR_10: numericValue = 10;
                break;
            case JACK: numericValue = 11;
                break;
            case QUEEN: numericValue = 12;
                break;
            case KING: numericValue = 13;
                break;
            case '2': numericValue = 2;
                break;
            case '3': numericValue = 3;
                break;
            case '4': numericValue = 4;
                break;
            case '5': numericValue = 5;
                break;
            case '6': numericValue = 6;
                break;
            case '7': numericValue = 7;
                break;
            case '8': numericValue = 8;
                break;
            case '9': numericValue = 9;
                break;
        }

        return numericValue;
    }

    /**
     An overloaded method that calls getNumericValue(boolean aceIs14) with
     a default argument of false
     @return an integer value representing the numeric value of a card
     */
    public int getNumericValue(){
        return getNumericValue(false);
    }

    /**
     A selector that returns the suite character of a card
     @return a char value that represents the suite character of a card
     */
    public char getSuiteCharacter() { return m_suiteCharacter; }

    /**
     A selector that returns the face character of a card
     @return a char value that represents the face character of a card
     */
    public char getFaceValue() { return m_faceValue; }

    /* *********************************************
    Mutators
    ********************************************* */

    /**
     A mutator that verifies and sets the suite character of a card
     @param suite, a char character that could represent suite character of a card
     @return true if the suite character is valid. false otherwise
     */
    public boolean setSuiteCharacter(char suite){
        if( !(suite == SPADE || suite == CLUB || suite == DIAMOND || suite == HEART) )
            return false;

        m_suiteCharacter = suite;
        return true;
    }

    /**
     A mutator that verifies and sets the face character of a card
     @param face, a char parameter that could represent face character of a card
     @return true if the face character is valid. false otherwise
     */
    public boolean setFaceCharacter(char face){
        if( !( (face >= ASCIICodeFor2 && face <= ASCIICodeFor9) || face == FACE_FOR_10 || face == JACK || face == QUEEN || face == KING || face == ACE) )
            return false;

        m_faceValue = face;
        return true;
    }

    /* *********************************************
    Main method for debugging
    ********************************************* */
    public static void main(String[] args){
    }

    /* *********************************************
    Utility functions
    ********************************************* */

    /**
     A utility function that writes a card as a string to a file
     @param f, a Formattter variable that represents which file to write to
     */
    void writeCardToFile(Formatter f){
        f.format("%s", "" + m_suiteCharacter + m_faceValue);
    }

    /**
     A utility function that determines the weight of a card based on the
     game specifications.
     @return an integer value representing the weight (value) of a card
     */
    public int getCardWeight(){
        int weight = 0;

        if(m_suiteCharacter == DIAMOND && m_faceValue == FACE_FOR_10)
            weight += 10;

        if(m_suiteCharacter == SPADE && m_faceValue == '2')
            weight += 8;

        if(m_faceValue == ACE)
            weight += 5;

        if(m_suiteCharacter == SPADE)
            weight += 2;

        return weight;
    }

    /**
     A utility function that describes a Card in detail.
     @return String description of a card. For instance,
     card HA would be written as "Ace of Hearts"
     */
    public final String getStringRepresentation(){
        String representation = "";
        String face = "";
        String suite = "";

        switch(m_faceValue){
            case ACE:
                face = "ace";
                break;
            case KING:
                face = "king";
                break;
            case QUEEN:
                face = "queen";
                break;
            case JACK:
                face = "jack";
                break;
            case FACE_FOR_10:
                face += "10";
                break;
            default:
                face += m_faceValue;
                break;
        }

        switch(m_suiteCharacter){
            case SPADE:
                suite = "Spades";
                break;
            case DIAMOND:
                suite = "Diamonds";
                break;
            case HEART:
                suite = "Hearts";
                break;
            case CLUB:
                suite = "Clubs";
                break;
        }

        return face + " of " + suite;
    }

    /**
     An overloaded method that helps to cast a card to a string.
     @return a string representation of a card.
     */
    public String toString(){
        return "" + m_suiteCharacter + m_faceValue;
    }
}
