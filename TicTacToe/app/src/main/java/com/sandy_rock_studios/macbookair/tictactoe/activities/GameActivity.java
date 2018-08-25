package com.sandy_rock_studios.macbookair.tictactoe.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.sandy_rock_studios.macbookair.tictactoe.R;
import com.sandy_rock_studios.macbookair.tictactoe.fragments.GameFragment;
import com.sandy_rock_studios.macbookair.tictactoe.fragments.GameOverFragment;
import com.sandy_rock_studios.macbookair.tictactoe.interfaces.GameCreationHandler;
import com.sandy_rock_studios.macbookair.tictactoe.interfaces.UserCreationHandler;
import com.sandy_rock_studios.macbookair.tictactoe.util.Game;
import com.sandy_rock_studios.macbookair.tictactoe.util.User;
import com.sandy_rock_studios.macbookair.tictactoe.util.tasks.GetGameTask;
import com.sandy_rock_studios.macbookair.tictactoe.util.tasks.GetUserTask;

public class GameActivity extends AppCompatActivity implements GameFragment.OnFragmentInteractionListener, GameOverFragment.OnFragmentInteractionListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            char[] gameIDArray = bundle.getCharArray("game_id");
            if(gameIDArray == null){
                makeToast("Error connecting to game!");
                onHomeClicked();
            }else{
                String gameID = new String(gameIDArray);
                displayPlayerInformation(gameID);
                displayGameFragment(gameID);
            }
        } else{
            makeToast("Error connecting to game!");
            onHomeClicked();
        }
    }

    private void makeToast(String displayName){
        Context context = getApplicationContext();
        CharSequence text = displayName;
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void displayPlayerInformation(String gameID){
        new GetGameTask(gameID, new GameCreationHandler() {
            @Override
            public void handleGame(Game game) {
                if(game != null){
                    String opponentID = game.getOpponentID();
                    String creatorID = game.getUserID();
                    changeVersusFieldName(creatorID, opponentID);
                }
            }
        }).execute();
    }

    private void changeVersusFieldName(String creatorID, final String opponentID){
        new GetUserTask(creatorID, null, new UserCreationHandler() {
            @Override
            public void handleUser(final User user1) {
                if(user1 != null){
                    new GetUserTask(opponentID, null, new UserCreationHandler() {
                        @Override
                        public void handleUser(User user2) {
                            if(user2 != null){
                                ((TextView)findViewById(R.id.matchup_text)).setText(user1.getDisplayName() + " vs. " + user2.getDisplayName());
                            }
                        }
                    }).execute();
                }
            }
        }).execute();
    }

    private void displayGameFragment(String gameID){
        GameFragment fragment = GameFragment.newInstance(gameID);
        displayFragment(fragment);
    }

    private void displayGameOverFragment(String result) {
        GameOverFragment fragment = GameOverFragment.newInstance(result);
        displayFragment(fragment);
    }

    private void displayFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.game_fragment_container, fragment).addToBackStack(null).commit();
    }

    private void closeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.game_fragment_container);
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment).commit();
        }
    }

    @Override
    public void onGameFinished(String resultString) {
        closeFragment();
        displayGameOverFragment(resultString);
    }

    @Override
    public void onHomeClicked() {
        closeFragment();
        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNewGameClicked() {
        closeFragment();
        Intent intent = new Intent(GameActivity.this, MatchMakingActivity.class);
        startActivity(intent);
        finish();
    }
}
