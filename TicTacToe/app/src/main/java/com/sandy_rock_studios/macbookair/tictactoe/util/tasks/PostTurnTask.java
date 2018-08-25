package com.sandy_rock_studios.macbookair.tictactoe.util.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.sandy_rock_studios.macbookair.tictactoe.interfaces.GameCreationHandler;
import com.sandy_rock_studios.macbookair.tictactoe.util.Game;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostTurnTask extends AsyncTask<String, Void, Game> {
    private static final String QUERY_URL = "http://murmuring-escarpment-67851.herokuapp.com/games/";
    private String myGameID;
    private JSONObject myRequestBody;
    private GameCreationHandler myHandler;
    public PostTurnTask(String gameID, String userID, int row, int col, GameCreationHandler handler){
        myGameID = gameID;
        myRequestBody = makeRequestBody(userID, row, col);
        myHandler = handler;
    }
    private Exception exception;
    @Override
    protected Game doInBackground(String... strings) {
        String urlString = QUERY_URL + myGameID + "/turns";

        BufferedReader reader=null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            DataOutputStream writer = new DataOutputStream(urlConnection.getOutputStream());
            boolean bad = myRequestBody == null;
            Log.w("BAD", bad + "");
            Log.w("BAD", myRequestBody.toString());
            writer.writeBytes(myRequestBody.toString());
            writer.flush();
            writer.close();

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            String response = sb.toString();
            Log.w("BAD", response);
            Game game = new Game(new JSONObject(response));
            Log.w("BAD", "made it here!");
            return game;
        }catch (Exception e) {
            this.exception = e;
            Log.w("BAD", e.toString());
        }finally {
            try {
                reader.close();
            } catch(Exception ex) {
                this.exception = ex;
            }
        }
        return null;
    }

    protected void onPostExecute(Game game) {
        if(exception != null){
            Log.w("BAD", "Still an issue!");
        }
        myHandler.handleGame(game);
    }

    private JSONObject makeRequestBody(String userID, int row, int col){
        try {
            JSONObject position = new JSONObject();
            position.put("row", row);
            position.put("col", col);
            JSONObject body = new JSONObject();
            body.put("taken_by_player", userID);
            body.put("position", position);
            return body;
        } catch (JSONException e) {
        }
        return null;
    }
}
