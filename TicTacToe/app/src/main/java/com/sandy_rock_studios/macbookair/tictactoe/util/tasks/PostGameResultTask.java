package com.sandy_rock_studios.macbookair.tictactoe.util.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.sandy_rock_studios.macbookair.tictactoe.interfaces.ConditionHandler;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostGameResultTask extends AsyncTask<String, Void, Boolean>{
    private static final String QUERY_URL = "http://murmuring-escarpment-67851.herokuapp.com/players/addResult/";
    public static final String LOSS = "lose";
    public static final String WIN = "win";
    public static final String DRAW = "draw";

    private String myUserID;
    private String myResult;
    private ConditionHandler myHandler;
    public PostGameResultTask(String userID, String result, ConditionHandler handler){
        myUserID = userID;
        myResult = result;
        myHandler = handler;
    }
    private Exception exception;
    @Override
    protected Boolean doInBackground(String... strings) {
        String urlString = QUERY_URL + myUserID + "/" + myResult;
        BufferedReader reader=null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
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

    protected void onPostExecute(Boolean success) {
        if(exception != null){
            Log.w("BAD", "Still an issue!");
        }
        myHandler.handle(success);
    }
}
