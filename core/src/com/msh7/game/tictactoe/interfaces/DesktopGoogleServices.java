package com.msh7.game.tictactoe.interfaces;

import com.msh7.game.tictactoe.TicTacToeGame;

public class DesktopGoogleServices implements IGoogleServices {
    TicTacToeGame game;

    @Override
    public void SignIn() {
        System.out.println("signin GS");
    }

    @Override
    public void SignOut() {
        System.out.println("sigout GS");
    }

    @Override
    public boolean isSignedIn() {
        return false;
    }

    public void QuickGame() {
        System.out.println("quickGame");
    }

    public void initMatch() {
        System.out.println("initMatch");
    }

    public void setGame(TicTacToeGame game) {
        this.game = game;
    }

    @Override
    public void sendMove(int row, int col) {

    }

    @Override
    public void showBannerAd(boolean show) {

    }

    @Override
    public void showFullScreenAd() {

    }

    @Override
    public void quitGame() {

    }

    @Override
    public void rate() {

    }

    @Override
    public void quickGame() {

    }
}
