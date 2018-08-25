package com.sandy_rock_studios.macbookair.tictactoe.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sandy_rock_studios.macbookair.tictactoe.R;
import com.sandy_rock_studios.macbookair.tictactoe.interfaces.ConditionHandler;
import com.sandy_rock_studios.macbookair.tictactoe.interfaces.GameCreationHandler;
import com.sandy_rock_studios.macbookair.tictactoe.util.Game;
import com.sandy_rock_studios.macbookair.tictactoe.util.GameBoard;
import com.sandy_rock_studios.macbookair.tictactoe.util.ResultAnalyzer;
import com.sandy_rock_studios.macbookair.tictactoe.util.Turn;
import com.sandy_rock_studios.macbookair.tictactoe.util.tasks.GetGameTask;
import com.sandy_rock_studios.macbookair.tictactoe.util.tasks.PostGameResultTask;
import com.sandy_rock_studios.macbookair.tictactoe.util.tasks.PostTurnTask;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GameFragment extends Fragment implements View.OnClickListener{
    private static final String GAME_ID_PARAM = "param1";
    private static final String FIRST_MARKER = "X";
    private static final String SECOND_MARKER = "O";

    private String myGameID;
    private String myPlayerID;
    private boolean myTurn;
    private String myMarker, myOpponentMarker;
    private GameBoard myGameBoard;

    private TextView myTurnLabel;
    private View myView;

    private OnFragmentInteractionListener myListener;

    public GameFragment() {
        // Required empty public constructor
    }

    public static GameFragment newInstance(String gameID) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putString(GAME_ID_PARAM, gameID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            myGameID = getArguments().getString(GAME_ID_PARAM);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            myPlayerID = sharedPreferences.getString("user_id", null);
            new GetGameTask(myGameID, new GameCreationHandler() {
                @Override
                public void handleGame(Game game) {
                    if(game != null){
                        myGameBoard = game.getGameBoard();
                        if(myPlayerID.equals(game.getUserID())){
                            yourTurn();
                            myMarker = FIRST_MARKER;
                            myOpponentMarker = SECOND_MARKER;
                        }else{
                            myTurn = false;
                            myMarker = SECOND_MARKER;
                            myOpponentMarker = FIRST_MARKER;
                            waitForOpponent();
                        }
                    }
                }
            }).execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_game, container, false);
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                Button button = myView.findViewById(getResources().getIdentifier("b" + i + j, "id", getContext().getPackageName()));
                button.setOnClickListener(this);
            }
        }
        myTurnLabel = myView.findViewById(R.id.turn_text);
        return myView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            myListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        myListener = null;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if(id == getButtonID(row, col)){
                    takeTurn(row, col);
                }
            }
        }
    }

    private int getButtonID(int row, int col){
        return getResources().getIdentifier("b" + row + col, "id", getContext().getPackageName());
    }

    private void takeTurn(int row, int col){
        if(myTurn && myGameBoard.canGo(row, col)){
            myTurn = false;
            new PostTurnTask(myGameID, myPlayerID, row, col, new GameCreationHandler() {
                @Override
                public void handleGame(Game game) {
                    updateGameBoard(game.getTurns());
                    boolean gameOver = attemptToFinishGame(game);
                    if(!gameOver){
                        waitForOpponent();
                    }
                }
            }).execute();
        }
    }

    private void yourTurn(){
        myTurn = true;
        myTurnLabel.setText("Your turn.");
    }

    private void waitForOpponent(){
        myTurnLabel.setText("Waiting for opponent...");
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
                                public void handleGame(final Game game) {
                                    if(game != null && game.getTurns().size() > 0 && !game.getTurns().get(0).getPlayer().equals(myPlayerID)) {
                                        updateGameBoard(game.getTurns());
                                        boolean gameOver = attemptToFinishGame(game);
                                        if(!gameOver){
                                            yourTurn();
                                        }
                                        timer.cancel();
                                        timer.purge();
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

    private void updateGameBoard(List<Turn> turns){
        myGameBoard = new GameBoard(turns);
        if(turns.size() > 0){
            Turn mostRecentTurn = turns.get(0);
            Button buttonClicked = myView.findViewById(getButtonID(mostRecentTurn.getRow(), mostRecentTurn.getCol()));
            if(mostRecentTurn.getPlayer().equals(myPlayerID)){
                buttonClicked.setText(myMarker);
            }else{
                buttonClicked.setText(myOpponentMarker);
            }
        }
    }

    private boolean attemptToFinishGame(Game game){
        ResultAnalyzer analyzer = new ResultAnalyzer(game.getGameBoard());
        String result = analyzer.getResult();
        if(result.equals(ResultAnalyzer.NOT_YET_FINISHED)){
            return false;
        }
        if(result.equals(ResultAnalyzer.DRAW)){
            postGameResult(PostGameResultTask.DRAW, "It's a draw!");
            return true;
        }else if(analyzer.getWinner().equals(myPlayerID)){
            postGameResult(PostGameResultTask.WIN, "You win!");
            return true;
        }else{
            postGameResult(PostGameResultTask.LOSS, "You lose!");
            return true;
        }
    }

    private void postGameResult(String result, final String resultDescription){
        new PostGameResultTask(myPlayerID, result, new ConditionHandler() {
            @Override
            public void handle(boolean condition) {
                if(!condition){
                    makeToast("Error saving result to game server!");
                }
                myListener.onGameFinished(resultDescription);
            }
        }).execute();
    }

    public interface OnFragmentInteractionListener {
        void onGameFinished(String resultString);
    }

    private void makeToast(String displayName){
        Context context = getContext();
        CharSequence text = displayName;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
