package com.sandy_rock_studios.macbookair.tictactoe.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Turn> myTurns;
    private String myUserID;
    private String myOpponentID;
    private String myGameID;
    private GameBoard myGameBoard;

    public Game(JSONObject gameResponse){
        myTurns = new ArrayList<>();
        parseJSON(gameResponse);
        Log.w("BAD","MAde it past parse json in game!");
        myGameBoard = new GameBoard(myTurns);
    }

    public List<Turn> getTurns() {
        return myTurns;
    }

    public String getUserID() {
        return myUserID;
    }

    public String getGameID() {
        return myGameID;
    }

    public String getOpponentID() {
        return myOpponentID;
    }

    public GameBoard getGameBoard() {
        return myGameBoard;
    }

    private void parseJSON(JSONObject response){
        JSONArray turns = response.optJSONArray("turns");
        if(turns != null){
            for(int i = 0; i < turns.length(); i++){
                JSONObject turn = turns.optJSONObject(i);
                myTurns.add(new Turn(turn));
            }
        }
        myOpponentID = response.optString("opponent");
        myGameID = response.optString("_id");
        myUserID = response.optString("created_by");
    }
}
