package com.msh7.game.tictactoe.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.msh7.game.tictactoe.TicTacToeGame;
import com.msh7.game.tictactoe.interfaces.DesktopGoogleServices;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = TicTacToeGame.WIDTH ;
        config.height = TicTacToeGame.HEIGHT;
        new LwjglApplication(new TicTacToeGame(new DesktopGoogleServices()), config);
    }
}
