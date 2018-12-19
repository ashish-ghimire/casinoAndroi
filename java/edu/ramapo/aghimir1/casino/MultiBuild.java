package edu.ramapo.aghimir1.casino;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Formatter;

public class MultiBuild implements Serializable {

    /* *********************************************
    Class member variables
    ********************************************* */
    private static final int INVALID_MULTIBUILD_NUMERIC_VALUE = -1;
    private ArrayList<Build> m_multiBuild;

    // A build must be owned by either a computer or a human player
    // If the build is owned by a human player, owner will be "human"
    // Else, owner will be "computer
    private String m_owner;


    /* *********************************************
    Constructors
    ********************************************* */
    public MultiBuild(){
        m_multiBuild = new ArrayList<Build>();
    }

    public MultiBuild(ArrayList<Build> multiBuilds, String owner){
        m_multiBuild = (ArrayList) multiBuilds.clone();
        m_owner = owner;
    }

    /* *********************************************
     Mutators
    ********************************************* */

    /**
     A mutator that adds a build to a multibuild
     @param build, a Build object
     */
    public void addToMultiBuild(Build build){
        m_multiBuild.add(build);
    }

    /**
     A mutator that sets the owner of a build
     @param ownerName, a String variable is used to set an owner's value
     */
    public void setOwner(String ownerName){
        m_owner = ownerName;
    }

    /* *********************************************
     Selectors
    ********************************************* */

    /**
     A selector that returns lets the caller know who owns a build
     @return a String value representing the owner of a build
     */
    public String getOwner() {
        return m_owner;
    }

    /**
     A selector that returns a collection of all the builds that constitute a multibuild
     @return an ArrayList of all builds in a multibuild
     */
    public ArrayList <Build> getMultiBuild() {
        return (ArrayList) m_multiBuild.clone();
    }

    /**
     A function that returns the size of a multibuild
     @return an integer value that denotes the number of cards in a multibuild
     */
    public int getMultiBuildSize(){
        int numCardsInAMultiBuild = 0;

        for(Build b: m_multiBuild)
            numCardsInAMultiBuild += b.getBuildSize();

        return numCardsInAMultiBuild;
    }

    /**
     A selector that returns the numeric value of a multibuild
     @return an integer representing the the numeric value of a multibuild
     */
    public int getNumericValue(){
        if(m_multiBuild.isEmpty() )
            return INVALID_MULTIBUILD_NUMERIC_VALUE ;   //Invalid
        
        return m_multiBuild.get(0).getNumericValue();
    }

    /* *********************************************
    Main method that can be used for debugging
    ********************************************* */
    public static void main(String [] args){
    }


    /* *********************************************
     Utility functions
    ********************************************* */

    /**
     An overloaded method that helps to cast a multibuild to a string.
     @return a string representation of a multibuild.
     */
    public String toString(){
        StringBuilder multiBuildStr = new StringBuilder("[ ");
        for(int i = 0; i < m_multiBuild.size(); i++){
            multiBuildStr.append(m_multiBuild.get(i).toString() );
        }
        multiBuildStr.append("] ");
        return multiBuildStr.toString();
    }

    /**
     A utility function that writes a multibuild as a string to a file
     @param f, a Formattter variable that represents which file to write to
     */
    void writeToFile(Formatter f){
        f.format("%s", this.toString() );
    }
}
