package com.sandy_rock_studios.macbookair.tictactoe.activities.loading;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sandy_rock_studios.macbookair.tictactoe.R;
import com.sandy_rock_studios.macbookair.tictactoe.activities.GameActivity;
import com.sandy_rock_studios.macbookair.tictactoe.activities.MainActivity;
import com.sandy_rock_studios.macbookair.tictactoe.interfaces.ConditionHandler;
import com.sandy_rock_studios.macbookair.tictactoe.interfaces.GameCreationHandler;
import com.sandy_rock_studios.macbookair.tictactoe.util.Game;
import com.sandy_rock_studios.macbookair.tictactoe.util.tasks.DeleteGameTask;
import com.sandy_rock_studios.macbookair.tictactoe.util.tasks.GetGameTask;
import com.sandy_rock_studios.macbookair.tictactoe.util.tasks.JoinGameTask;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingRandomGameActivity extends AppCompatActivity implements Loader, View.OnClickListener {
    private String myID;
    private String myGameID;
    private boolean createdGame;
    private boolean matchMade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_random_game);
        findViewById(R.id.cancel_match_finding_button).setOnClickListener(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        myID = sharedPreferences.getString("user_id", null);
        lookForMatch();
    }

    @Override
    public void lookForMatch() {
        Log.w("GOOD", "joining open game");
        new JoinGameTask(myID, new GameCreationHandler() {
            @Override
            public void handleGame(Game game) {
                if(game == null){
                    makeToast("Trouble joining a game. Come back later!");
                    return;
                }
                myGameID = game.getGameID();
                if(game.getUserID().equals(myID)){
                    createdGame = true;
                }
                if(gameIsFull(game)){
                    startGame(game.getGameID());
                }else{
                    repeatedlyCheckIfSomeoneJoined();
                }
            }
        }).execute();
    }

    private void repeatedlyCheckIfSomeoneJoined(){
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        final TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new GetGameTask(myGameID, new GameCreationHandler() {
                                @Override
                                public void handleGame(Game game) {
                                    if(game == null) {
                                        makeToast("Trouble joining a game. Trying again!");
                                    }else if(!game.getOpponentID().equals("") && !matchMade){
                                        timer.cancel();
                                        timer.purge();
                                        startGame(myGameID);
                                    }
                                }
                            }).execute();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000); //execute in every 50000 ms
    }

    private boolean gameIsFull(Game game){
        return !game.getUserID().equals("") && !game.getOpponentID().equals("");
    }

    private void makeToast(String displayName){
        Context context = getApplicationContext();
        CharSequence text = displayName;
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void startGame(String gameID) {
        matchMade = true;
        Intent intent = new Intent(LoadingRandomGameActivity.this, GameActivity.class);
        Bundle b = new Bundle();
        b.putCharArray("game_id", gameID.toCharArray());
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCancel() {
        if(createdGame){
            new DeleteGameTask(myGameID, new ConditionHandler() {
                @Override
                public void handle(boolean condition) {
                    if(!condition){
                        Log.w("BAD", "Failed to delete game: " + myGameID);
                        Intent intent = new Intent(LoadingRandomGameActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }).execute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!matchMade){
            onCancel();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.cancel_match_finding_button:
                onCancel();
                break;
        }
    }
}
