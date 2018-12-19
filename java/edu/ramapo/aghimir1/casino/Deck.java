package edu.ramapo.aghimir1.casino;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Formatter;

public class Deck implements Serializable {
    /* *********************************************
    Class member variables
    ********************************************* */
    private ArrayList<Card> m_cardsInDeck;
    private int m_nextCardIndex;

    /* *********************************************
    Constructors
    ********************************************* */
    public Deck() {
        m_nextCardIndex = 0;
        m_cardsInDeck = new ArrayList<Card>();
        populateTheDeck();
        shuffle();
    }

    public Deck(ArrayList<Card> deckCards){
        m_cardsInDeck = (ArrayList)deckCards.clone();
        m_nextCardIndex = 0;
    }

    /* *********************************************
    Selectors
    ********************************************* */

    /**
     An accessor that can only be used to deal one deck card at a time
     @return the next card in the deck that
     */
    public Card getNextCard(){
        Card toReturn = null;
        if( !isEmpty() ){
            toReturn = m_cardsInDeck.get(m_nextCardIndex);
            m_nextCardIndex++;
        }
        return toReturn;
    }

    /**
     An accessor that returns all the cards in the deck that have not yet been dealt
     @return a collection of Cards
     */
    public ArrayList<Card> getDeckCards(){
        ArrayList<Card> deckCards = new ArrayList<>();
        deckCards.addAll( m_cardsInDeck.subList(m_nextCardIndex, m_cardsInDeck.size() ));
        return deckCards;
    }

    /* *********************************************
    Main function that can be used for testing
    ********************************************* */
    public static  void main(String [] args){
    }

    /* *********************************************
    Utility functions used to initialize the deck
    ********************************************* */

    /**
     Generates cards populates the deck with the generated cards
     */
    public void populateTheDeck(){
        ArrayList<String> cardStrings = new ArrayList<String>();
        generateCards(cardStrings, Card.SPADE);
        generateCards(cardStrings, Card.CLUB);
        generateCards(cardStrings, Card.HEART);
        generateCards(cardStrings, Card.DIAMOND);

        for(String i : cardStrings){
            Card newCard = new Card(i.charAt(0), i.charAt(1));
            m_cardsInDeck.add(newCard);
        }
    }

    /**
     Generates cards for the deck
     */
    private void generateCards(ArrayList<String> cardStrings, char suite){
        for(int i = 2; i < 10; i++)
            cardStrings.add("" + suite + i );

        cardStrings.add("" + suite + Card.ACE);
        cardStrings.add("" + suite + Card.FACE_FOR_10);
        cardStrings.add("" + suite + Card.JACK);
        cardStrings.add("" + suite + Card.QUEEN);
        cardStrings.add("" + suite + Card.KING);
    }


    /**
     Shuffles a collection, in the context of this class, shuffles the card in the deck
     */
    private void shuffle(){
        Collections.shuffle(m_cardsInDeck);
    }

    /* *********************************************
    Other Utility functions
    ********************************************* */

    /**
     A utility function that writes a deck as a string to a file
     @param f, a Formattter variable that represents which file to write to
     */
    public void writeDeckCardsToFile( Formatter f ){
        for(int i = m_nextCardIndex; i < m_cardsInDeck.size(); i++){
            m_cardsInDeck.get(i).writeCardToFile(f);
            f.format("%c", ' ');
        }
    }

    /**
     determines if the deck is empty
     @return true if the deck has at least one card. false otherwise
     */
    public boolean isEmpty(){
        if(m_nextCardIndex >= m_cardsInDeck.size())
            return true;

        return false;
    }
}
