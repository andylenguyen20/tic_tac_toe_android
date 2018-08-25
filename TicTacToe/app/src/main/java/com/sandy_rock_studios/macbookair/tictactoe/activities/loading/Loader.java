package com.sandy_rock_studios.macbookair.tictactoe.activities.loading;

public interface Loader {
    void lookForMatch();

    void startGame(String gameID);

    void onCancel();
}
