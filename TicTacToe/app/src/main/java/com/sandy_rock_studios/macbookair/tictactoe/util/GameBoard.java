package com.sandy_rock_studios.macbookair.tictactoe.util;

import java.util.List;

public class GameBoard {
    public static final String NO_PLAYER = "NO_PLAYER_TAG";
    private String[][] myBoard;
    public GameBoard(List<Turn> turns){
        myBoard = new String[3][3];
        applyTurns(turns);
    }

    private void applyTurns(List<Turn> turns){
        for(Turn turn : turns){
            myBoard[turn.getRow()][turn.getCol()] = turn.getPlayer();
        }
        for(int row = 0; row < myBoard.length; row++){
            for(int col = 0; col < myBoard[0].length; col++){
                if(myBoard[row][col] == null){
                    myBoard[row][col] = NO_PLAYER;
                }
            }
        }
    }

    public String[][] getBoard(){
        return myBoard;
    }

    public boolean canGo(int row, int col){
        return myBoard[row][col].equals(NO_PLAYER);
    }
}
