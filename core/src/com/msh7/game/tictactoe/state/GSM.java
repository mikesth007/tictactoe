package com.msh7.game.tictactoe.state;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.msh7.game.tictactoe.TicTacToeGame;
import com.msh7.game.tictactoe.interfaces.IGoogleServices;

import java.util.LinkedList;

/**
 * Manages States in a Stack structure.
 */
public class GSM {
    private IGoogleServices googleServices;
    private LinkedList<State> states;
    private TicTacToeGame game;

    public GSM(TicTacToeGame game) {
        this.game = game;
        this.googleServices = game.googleServices;
        states = new LinkedList<State>();
    }

    public void update(float dt) {
        states.peek().update(dt);
    }

    public void render(SpriteBatch sb) {
        states.peek().render(sb);
    }

    public void pop() {
        states.pop();
    }

    public void push(State s) {
        states.push(s);
    }

    public void set(State s) {
        states.pop();
        states.push(s);
    }

    public IGoogleServices getGoogleServices() {
        return googleServices;
    }

    public TicTacToeGame getGame() {
        return game;
    }

    public void moveReceived(int row, int col) {
        if (states.size() > 0) {
            for (State state : states) {
                if (state instanceof MultiPlayerGameState) {
                    ((MultiPlayerGameState) state).opponentMove(row, col);
                }
            }
        }
    }

    public void playerLeft() {
        if (states.size() > 0) {
            for (State state : states) {
                if (state instanceof MultiPlayerGameState) {
                    ((MultiPlayerGameState) state).playerLeft();
                }
            }
        }
    }

    public void messageReceived(int messageId) {
        if (states.size() > 0) {
            for (State state : states) {
                if (state instanceof MultiPlayerGameState) {
                    ((MultiPlayerGameState) state).messageReceived(messageId);
                }
            }
        }
    }
}
