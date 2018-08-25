package com.sandy_rock_studios.macbookair.tictactoe.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sandy_rock_studios.macbookair.tictactoe.R;
import com.sandy_rock_studios.macbookair.tictactoe.interfaces.UserCreationHandler;
import com.sandy_rock_studios.macbookair.tictactoe.util.User;
import com.sandy_rock_studios.macbookair.tictactoe.util.tasks.PostUserTask;

public class CreatePlayerActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText myDisplayNameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_player);
        myDisplayNameField = findViewById(R.id.create_player_input);
        findViewById(R.id.create_player_button).setOnClickListener(this);
    }

    private void createUser(){
        new PostUserTask(myDisplayNameField.getText().toString(), new UserCreationHandler() {
            @Override
            public void handleUser(User user) {
                if(user != null){
                    saveID(user.getID());
                    redirectToMain();
                }else{
                    makeToast("User with same display name already exists. Try again!");
                }
            }
        }).execute();
    }

    private void saveID(String userID){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", userID);
        editor.commit();
        Log.w("GOOD", "saved " + userID);
    }

    private void makeToast(String displayName){
        Context context = getApplicationContext();
        CharSequence text = displayName;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void redirectToMain(){
        Intent intent = new Intent(CreatePlayerActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.create_player_button:
                createUser();
                break;
        }
    }
}
