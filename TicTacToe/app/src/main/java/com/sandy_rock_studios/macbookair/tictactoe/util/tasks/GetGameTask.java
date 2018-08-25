package com.sandy_rock_studios.macbookair.tictactoe.util.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.sandy_rock_studios.macbookair.tictactoe.interfaces.GameCreationHandler;
import com.sandy_rock_studios.macbookair.tictactoe.util.Game;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetGameTask extends AsyncTask<String, Void, Game> {
    private static final String QUERY_URL = "http://murmuring-escarpment-67851.herokuapp.com/games/";
    private String myID;
    private GameCreationHandler myHandler;
    public GetGameTask(String gameID, GameCreationHandler handler){
        myID = gameID;
        myHandler = handler;
    }
    private Exception exception;
    @Override
    protected Game doInBackground(String... strings) {
        String urlString = QUERY_URL + myID;
        BufferedReader reader=null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            String response = sb.toString();
            return new Game(new JSONObject(response));
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
}
