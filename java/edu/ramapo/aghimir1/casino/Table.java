package edu.ramapo.aghimir1.casino;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Formatter;

public class Table implements Serializable {

    /* *********************************************
    Class member variables
    ********************************************* */

    // Table is a collection of loose cards, builds and multi-builds
    ArrayList <Card> m_looseCards;
    ArrayList <Build> m_builds;
    ArrayList<MultiBuild> m_multiBuilds;

    /* *********************************************
    Constructors
    ********************************************* */
    public Table(){
        m_looseCards = new ArrayList<Card>();
        m_builds = new ArrayList<Build>();
        m_multiBuilds = new ArrayList<MultiBuild>();
    }

    public Table(ArrayList<Card> looseCards, ArrayList<Build> builds, ArrayList<MultiBuild> multibuilds){
        m_looseCards = (ArrayList) looseCards.clone();
        m_builds = (ArrayList) builds.clone();
        m_multiBuilds = (ArrayList) multibuilds;
    }

    /* *********************************************
    Selectors
   ********************************************* */
    /**
     A selector that returns a collection of all loose cards in the table
     @return an ArrayList of all table loose cards
     */
    public ArrayList <Card> getLooseCards() { return (ArrayList) m_looseCards.clone(); }

    /**
     A selector that returns a collection of all single builds in the table
     @return an ArrayList of all table single builds
     */
    public ArrayList <Build> getSingleBuilds() { return (ArrayList) m_builds.clone(); }

    /**
     A selector that returns a collection of all single multibuilds in the table
     @return an ArrayList of all table single multibuilds
     */
    public ArrayList <MultiBuild> getMultiBuilds() { return (ArrayList)m_multiBuilds.clone(); }

    /**
     A selector that returns a collection of all table loose cards with a certain numeric value
     @param numericValue, an integer that specifies the loose cards to look for
     @return an ArrayList of all table loose cards with value equal to the parameter
     */
    public ArrayList <Integer> getLooseCardsWithGivenNumericValue(int numericValue){
        ArrayList<Integer> indicesOfLooseCards = new ArrayList<Integer>();

        for(int i = 0; i < m_looseCards.size(); i++){
            Card card = m_looseCards.get(i);
            if(card.getNumericValue() == numericValue){
                indicesOfLooseCards.add(i);
            }
        }

        return (ArrayList)indicesOfLooseCards.clone();
    }

    /**
     A selector that returns a collection of all table single builds with a certain numeric value
     @param numericValue, an integer that specifies the single builds to look for
     @return an ArrayList of all table single builds with value equal to the parameter
     */
    public ArrayList <Integer> getSingleBuildsWithGivenNumericValue(int numericValue){
        ArrayList <Integer> indicesOfSingleBuilds = new ArrayList<Integer>();

        for(int i = 0; i < m_builds.size(); i++){
            if(numericValue == m_builds.get(i).getNumericValue() )
                indicesOfSingleBuilds.add(i);
        }

        return (ArrayList)indicesOfSingleBuilds.clone();
    }

    /**
     A selector that returns a collection of all table multi builds with a certain numeric value
     @param numericValue, an integer that specifies the multi builds to look for
     @return an ArrayList of all table multi builds with value equal to the parameter
     */
    public ArrayList <Integer> getMultiBuildsWithGivenNumericValue(int numericValue){
        ArrayList<Integer> indicesOfMultiBuilds = new ArrayList<Integer>();

        for(int i = 0; i < m_multiBuilds.size(); i++){
            if(numericValue == m_multiBuilds.get(i).getNumericValue() )
                indicesOfMultiBuilds.add(i);
        }

        return (ArrayList)indicesOfMultiBuilds.clone();
    }

    /**
     A selector that returns a numeric representation of all loose cards on the table
     @return an ArrayList of numeric value of all table loose cards
     */
    public ArrayList <Integer> getNumericValueOfLooseCards(){
        ArrayList <Integer> numericLooseCards = new ArrayList<Integer>();

        for(int i = 0; i < m_looseCards.size(); i++)
            numericLooseCards.add(m_looseCards.get(i).getNumericValue());

        return (ArrayList)numericLooseCards.clone();
    }

    /* *********************************************
    Mutators
   ********************************************* */
    /**
     Verifies and adds a card to a table
     @param c, a Card object to add to the table
     */
    public void addALooseCard(Card c){
        if(c != null)
            m_looseCards.add(c);
    }

    /**
     Adds a single build to a table
     @param b, a Build object to add to the table
     */
    public void addABuild(Build b){
        m_builds.add(b);
    }

    /**
     Adds a  multi build to a table
     @param m, a multi build object to add to the table
     */
    public void addAMultiBuild(MultiBuild m){
        m_multiBuilds.add(m);
    }

    /**
     A mutator that removes a loose card from a table
     @param looseCardIndex, an integer variable that represents the card at what index
     should be removed
     */
    public void removeALooseCard(int looseCardIndex){
        if(looseCardIndex < 0 || looseCardIndex >= m_looseCards.size())
            return;

        m_looseCards.remove(looseCardIndex);
    }

    /**
     A mutator that removes a build from a table
     @param index, an integer variable that represents the build at what index
     should be removed
     */
    public void removeABuild(int index){
        if(!( index < 0 || index >= m_builds.size() ) )
            m_builds.remove( index );
    }
    /**
     A mutator that removes a build from the table
     @param build, a Build object that should be removed
     */
    public void removeABuild(Build build){
        m_builds.remove( build );
    }

    /**
     A mutator that removes a multibuild from a table
     @param index, an integer variable that represents the multibuild at what index
     should be removed
     */
    public void removeAMultiBuild(int index){
        if(!( index < 0 || index >= m_multiBuilds.size() ) )
            m_multiBuilds.remove(index);
    }

    /* *********************************************
    Main function that may be used for debugging
    ********************************************* */
    public static void main(String [] args){
    }

    /* *********************************************
    Utility functions to write the table into a file
   ********************************************* */
    /**
     A utility function that writes table data as a string to a file
     @param f, a Formattter variable that represents which file to write to
     */
    void writeTableToFile(Formatter f){
        writeMultiBuildsToFile(f);
        writeSingleBuildsToFile(f);
        writeLooseCardsToFile(f);
    }

    /**
     A utility function that writes all table multibuilds as a string to a file
     @param f, a Formattter variable that represents which file to write to
     */
    void writeMultiBuildsToFile(Formatter f){
        for(MultiBuild i:  m_multiBuilds) {
            i.writeToFile(f);
        }
    }

    /**
     A utility function that writes all table single builds as a string to a file
     @param f, a Formattter variable that represents which file to write to
     */
    void writeSingleBuildsToFile(Formatter f){
        for(Build i: m_builds){
            i.writeToFile(f);
        }
    }

    /**
     A utility function that writes all table loose cards as a string to a file
     @param f, a Formattter variable that represents which file to write to
     */
    void writeLooseCardsToFile(Formatter f){
        for(Card i: m_looseCards){
            i.writeCardToFile(f);
            f.format("%c", ' ');
        }
    }
}
