package com.sandy_rock_studios.macbookair.tictactoe.util.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.sandy_rock_studios.macbookair.tictactoe.interfaces.ConditionHandler;
import com.sandy_rock_studios.macbookair.tictactoe.interfaces.GameCreationHandler;
import com.sandy_rock_studios.macbookair.tictactoe.util.Game;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteGameTask extends AsyncTask<String, Void, Boolean>{
    private static final String QUERY_URL = "http://murmuring-escarpment-67851.herokuapp.com/games/";
    private String myID;
    private ConditionHandler myHandler;
    public DeleteGameTask(String gameID, ConditionHandler handler){
        myID = gameID;
        myHandler = handler;
    }
    private Exception exception;
    @Override
    protected Boolean doInBackground(String... strings) {
        String urlString = QUERY_URL + myID;
        BufferedReader reader=null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            return urlConnection.getResponseCode() == 200;
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
        return false;
    }

    protected void onPostExecute(Boolean condition) {
        if(exception != null){
            Log.w("BAD", "Still an issue!");
        }
        myHandler.handle(condition);
    }
}
