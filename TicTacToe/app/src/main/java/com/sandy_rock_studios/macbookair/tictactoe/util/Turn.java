package com.sandy_rock_studios.macbookair.tictactoe.util;

import android.util.Log;

import org.json.JSONObject;

public class Turn {
    private int myRow;
    private int myCol;
    private String myPlayer;
    public Turn(JSONObject turnResponse){
        parseJSON(turnResponse);
        Log.w("BAD","MAde it past parse json in game!");
    }

    private void parseJSON(JSONObject response){
        Log.w("BAD","Here!");
        Log.w("BAD",response.toString());
        myPlayer = response.optString("taken_by_player");
        JSONObject position = response.optJSONObject("position");
        boolean bad = position == null;
        Log.w("BAD","" + bad);
        myRow = position.optInt("row");
        myCol = position.optInt("col");
    }

    public int getRow(){
        return myRow;
    }

    public int getCol(){
        return myCol;
    }

    public String getPlayer(){
        return myPlayer;
    }
}
