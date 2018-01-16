package com.msh7.game.tictactoe.interfaces;

import com.msh7.game.tictactoe.TicTacToeGame;

public interface IGoogleServices {
    public void SignIn();

    public void SignOut();

    public boolean isSignedIn();

    public void QuickGame();

    public void initMatch();

    public void setGame(TicTacToeGame game);

    public void sendMove(int row, int col);

    public void showBannerAd(boolean show);

    public void showFullScreenAd();

    public void quitGame();

    public void rate();

    void quickGame();
}
