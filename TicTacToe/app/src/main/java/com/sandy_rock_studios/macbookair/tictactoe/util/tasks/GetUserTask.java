package com.sandy_rock_studios.macbookair.tictactoe.util.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.sandy_rock_studios.macbookair.tictactoe.interfaces.GameCreationHandler;
import com.sandy_rock_studios.macbookair.tictactoe.interfaces.UserCreationHandler;
import com.sandy_rock_studios.macbookair.tictactoe.util.Game;
import com.sandy_rock_studios.macbookair.tictactoe.util.User;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetUserTask extends AsyncTask<String, Void, User>{
    private static final String ID_QUERY_URL = "http://murmuring-escarpment-67851.herokuapp.com/players/id/";
    private static final String DISPLAY_NAME_QUERY_URL = "http://murmuring-escarpment-67851.herokuapp.com/players/displayName/";
    private String myID;
    private String myDisplayName;
    private UserCreationHandler myHandler;
    public GetUserTask(String userID, String displayName, UserCreationHandler handler){
        myID = userID;
        myDisplayName = displayName;
        myHandler = handler;
    }
    private Exception exception;
    @Override
    protected User doInBackground(String... strings) {
        String urlString;
        if(myID != null){
            urlString = ID_QUERY_URL + myID;
        }else if(myDisplayName != null){
            urlString = DISPLAY_NAME_QUERY_URL + myDisplayName;
        }else{
            return null;
        }
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
            return new User(new JSONObject(response));
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

    protected void onPostExecute(User user) {
        if(exception != null){
            Log.w("BAD", "Still an issue!");
        }
        myHandler.handleUser(user);
    }
}
