package com.sandy_rock_studios.macbookair.tictactoe.activities.loading;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sandy_rock_studios.macbookair.tictactoe.R;
import com.sandy_rock_studios.macbookair.tictactoe.activities.GameActivity;
import com.sandy_rock_studios.macbookair.tictactoe.activities.MainActivity;
import com.sandy_rock_studios.macbookair.tictactoe.interfaces.GameCreationHandler;
import com.sandy_rock_studios.macbookair.tictactoe.interfaces.UserCreationHandler;
import com.sandy_rock_studios.macbookair.tictactoe.util.Game;
import com.sandy_rock_studios.macbookair.tictactoe.util.User;
import com.sandy_rock_studios.macbookair.tictactoe.util.tasks.GetUserTask;
import com.sandy_rock_studios.macbookair.tictactoe.util.tasks.JoinGameTask;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingCustomOpponentGameActivity extends AppCompatActivity implements Loader, View.OnClickListener {
    private boolean matchMade;
    private String myID;
    private String myOpponentDisplayName;
    private String myGameID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_custom_opponent_game);
        findViewById(R.id.cancel_match_finding_button).setOnClickListener(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        myID = sharedPreferences.getString("user_id", null);
        myOpponentDisplayName = sharedPreferences.getString("pref_custom_opponent_name", "INVALID");
        lookForMatch();
    }

    @Override
    public void lookForMatch() {
        new GetUserTask(null, myOpponentDisplayName, new UserCreationHandler() {
            @Override
            public void handleUser(User user) {
                if(user == null){
                    makeToast("Specified opponent does not exist!");
                    return;
                }
                repeatedlyAttemptToJoinGame(user.getID());
            }
        }).execute();

    }

    private void repeatedlyAttemptToJoinGame(final String opponentID){
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        final TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new JoinGameTask(myID, new GameCreationHandler() {
                                @Override
                                public void handleGame(Game game) {
                                    if(game == null){
                                        makeToast("Opponent hasn't started game yet! Trying again");
                                        return;
                                    }
                                    myGameID = game.getGameID();
                                    startGame(myGameID);
                                }
                            }).execute(opponentID);
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000); //execute in every 50000 ms
    }

    private void makeToast(String displayName){
        Context context = getApplicationContext();
        CharSequence text = displayName;
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onCancel() {
        Intent intent = new Intent(LoadingCustomOpponentGameActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!matchMade){
            onCancel();
        }
    }

    @Override
    public void startGame(String gameID) {
        matchMade = true;
        Intent intent = new Intent(LoadingCustomOpponentGameActivity.this, GameActivity.class);
        Bundle b = new Bundle();
        b.putCharArray("game_id", gameID.toCharArray());
        intent.putExtras(b);
        startActivity(intent);
        finish();
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
