package com.sandy_rock_studios.macbookair.tictactoe.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultAnalyzer {
    public static final String FINISHED = "Finished";
    public static final String DRAW = "Draw";
    public static final String NOT_YET_FINISHED = "NotYetFinished";
    private String myResult;
    private GameBoard myBoard;
    private String myWinner;
    public ResultAnalyzer(GameBoard board){
        myBoard = board;
        analyze();
    }

    private void analyze(){
        String[][] board = myBoard.getBoard();
        for(int row = 0; row < 3; row++){
            if(board[row][0].equals(board[row][1]) && board[row][0].equals(board[row][2]) && !board[row][0].equals(GameBoard.NO_PLAYER)){
                myWinner = board[row][0];
                myResult = FINISHED;
                return;
            }
        }
        for(int col = 0; col < 3; col++){
            if(board[0][col].equals(board[1][col]) && board[0][col].equals(board[2][col]) && !board[0][col].equals(GameBoard.NO_PLAYER)){
                myWinner = board[0][col];
                myResult = FINISHED;
                return;
            }
        }
        if(board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2]) && !board[0][0].equals(GameBoard.NO_PLAYER)){
            myWinner = board[0][0];
            myResult = FINISHED;
            return;
        }
        if(board[2][0].equals(board[1][1]) && board[0][2].equals(board[1][1]) && !board[2][0].equals(GameBoard.NO_PLAYER)){
            myWinner = board[0][0];
            myResult = FINISHED;
            return;
        }
        if(isFull(board)){
            myResult = DRAW;
        }else{
            myResult = NOT_YET_FINISHED;
        }
    }

    private boolean isFull(String[][] board){
        for(int row = 0; row < 3; row++){
            for(int col = 0; col < 3; col++){
                if(board[row][col].equals(GameBoard.NO_PLAYER)){
                    return false;
                }
            }
        }
        return true;
    }

    public String getResult() {
        return myResult;
    }

    public String getWinner(){
        if(myResult.equals(DRAW) || myResult.equals(NOT_YET_FINISHED)){
            return null;
        }
        return myWinner;
    }
}
