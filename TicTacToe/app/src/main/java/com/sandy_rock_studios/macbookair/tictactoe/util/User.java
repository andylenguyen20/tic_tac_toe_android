package com.sandy_rock_studios.macbookair.tictactoe.util;

import org.json.JSONObject;

public class User {
    private int myWins;
    private int myLosses;
    private int myDraws;
    private String myID;
    private String myDisplayName;
    public User(JSONObject userResponse){
        parseJSON(userResponse);
    }

    private void parseJSON(JSONObject response){
        myID = response.optString("_id");
        myDisplayName = response.optString("display_name", "-");
        myWins = response.optInt("wins", 0);
        myLosses = response.optInt("losses", 0);
        myDraws = response.optInt("draws", 0);
    }

    public int getWins(){
        return myWins;
    }

    public int getDraws(){
        return myDraws;
    }

    public int getLosses(){
        return myLosses;
    }

    public String getID(){
        return myID;
    }

    public String getDisplayName(){
        return myDisplayName;
    }

}
