package edu.ramapo.aghimir1.casino;

import java.io.Serializable;

public class Computer extends Player implements Serializable {
    /* *********************************************
    Constructor that simply calls the parent's constructor
   ********************************************* */
    public Computer(){
        super();
    }

    /**
     A selector that returns a computer player's input
     @param moveStrategy- a Strategy object that is used to determine a player's input
     for the game
     @return an Input object that can be used to execute moves for a player
     */
    public Input getInput(Strategy moveStrategy){
        return moveStrategy.getInput();
    }

    /**
     A selector that returns the player type
     @return a String representing the player type
     */
    public String getPlayerType() { return "Computer"; }

    /**
     A selector that gets help to execute computer players' moves
     @param moveStrategy- a Strategy object that can be used to get instructions
     on what move the player should execute
     @return a String that is a description of the moves the Computer player should take
     */
    public String getHelp(Strategy moveStrategy) { return ""; }
}
