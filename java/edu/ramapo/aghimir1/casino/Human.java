package edu.ramapo.aghimir1.casino;

import java.io.Serializable;

public class Human extends Player implements Serializable {

    /* *********************************************
    Constructor that simply calls the parent's constructor
   ********************************************* */
    public Human() {
        super();
    }

    /* *********************************************
    Selectors
   ********************************************* */

    /**
     A selector that returns the player type
     @return a String representing the player type
     */
    public String getPlayerType() {
        return "Human";
    }

    /**
     A selector that returns a human player's input
     @param moveStrategy- a Strategy object that can be used to determine a player's input
     for the game
     @return an Input object that can be used to execute moves for a player
     */
    public Input getInput(Strategy moveStrategy){
        return m_input;
    }

    /**
     A selector that gets help to execute human players' moves
     @param moveStrategy- a Strategy object that can be used to get instructions
     on what move the player should execute
     @return a String that is a description of the moves the Human player should take
     */
    public String getHelp(Strategy moveStrategy) {
        return "The player should " + moveStrategy.summarizeMove();
    }
}
