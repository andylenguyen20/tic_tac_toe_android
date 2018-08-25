package com.sandy_rock_studios.macbookair.tictactoe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sandy_rock_studios.macbookair.tictactoe.R;
import com.sandy_rock_studios.macbookair.tictactoe.activities.loading.LoadingCustomOpponentGameActivity;
import com.sandy_rock_studios.macbookair.tictactoe.activities.loading.LoadingOwnGameActivity;
import com.sandy_rock_studios.macbookair.tictactoe.activities.loading.LoadingRandomGameActivity;
import com.sandy_rock_studios.macbookair.tictactoe.interfaces.UserCreationHandler;
import com.sandy_rock_studios.macbookair.tictactoe.util.User;
import com.sandy_rock_studios.macbookair.tictactoe.util.tasks.GetUserTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String userID = PreferenceManager.getDefaultSharedPreferences(this).getString("user_id", null);
        if(userID == null){
            redirectToCreatePlayer();
        }else{
            displayUserInformation(userID);
            findViewById(R.id.start_button).setOnClickListener(this);
        }
    }

    private void redirectToCreatePlayer(){
        Intent intent = new Intent(MainActivity.this, CreatePlayerActivity.class);
        startActivity(intent);
        finish();
    }

    private void redirectToMatchMaking(){
        Intent intent = new Intent(MainActivity.this, MatchMakingActivity.class);
        startActivity(intent);
        finish();
    }

    private void displayUserInformation(String userID){
        final TextView userNameText = findViewById(R.id.user_name_entry);
        final TextView winsText = findViewById(R.id.wins_entry);
        final TextView drawsText = findViewById(R.id.draws_entry);
        final TextView lossesText = findViewById(R.id.losses_entry);
        new GetUserTask(userID, null, new UserCreationHandler() {
            @Override
            public void handleUser(User user) {
                if(user == null){
                    redirectToCreatePlayer();
                }else{
                    userNameText.setText(user.getDisplayName());
                    winsText.setText("" + user.getWins());
                    drawsText.setText("" + user.getDraws());
                    lossesText.setText("" + user.getLosses());
                }
            }
        }).execute();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.start_button:
                redirectToMatchMaking();
                break;
        }
    }
}
