package com.msh7.game.tictactoe.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.msh7.game.tictactoe.Assets;
import com.msh7.game.tictactoe.Image;
import com.msh7.game.tictactoe.Label;
import com.msh7.game.tictactoe.TextButton;
import com.msh7.game.tictactoe.TicTacToeGame;
import com.msh7.game.tictactoe.ai.ComputeAIPlayer;
import com.msh7.game.tictactoe.util.CellType;
import com.msh7.game.tictactoe.util.Grid;
import com.msh7.game.tictactoe.util.GridListener;

import java.util.Random;

public class GameState extends State {
    private final boolean soundMode;
    private GameStateEnum currentState;
    private boolean isHard = false;

    private Sprite background;

    private Grid grid;
    private CellType currentType;
    private TextButton playAgain;
    private TextButton mainMenu;
    private Label gameResult;
    private Label winnerLabel;
    private Texture gameOverBg;
    private float gameOverBgHeight;
    private ComputeAIPlayer aiPlayer;

    private Texture cellX, cellO;
    private BitmapFont font;
    private final GlyphLayout glyphLayout = new GlyphLayout();

    private boolean isMyTurn;

    private int rows, cols;
    private Image menu;
    private Image play;
    private Image settings;

    private float startingYGameOverBg = 64;

    public GameState(GSM gsm, final boolean isHard) {
        super(gsm);
        this.isHard = isHard;
        currentState = GameStateEnum.PLAYING;

        isMyTurn = new Random(234324234).nextBoolean();
        currentType = isMyTurn ? CellType.X : CellType.O;

        soundMode = Gdx.app.getPreferences(TicTacToeGame.MY_PREFS).getBoolean("sound", true);
        cellX = Assets.getInstance().manager.get(Assets.cell_x);
        cellO = Assets.getInstance().manager.get(Assets.cell_o);
        font = Assets.getInstance().manager.get(Assets.subtitle_font);
        Texture backgroundTex = Assets.getInstance().manager.get(Assets.background);
        backgroundTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        background = new Sprite(backgroundTex);
        gameOverBg = Assets.getInstance().manager.get(Assets.grid_bg);
        float bh = background.getHeight();
        float gh = TicTacToeGame.HEIGHT;
        float bw = background.getWidth();
        float gw = TicTacToeGame.WIDTH;
        float scale;
        if (bh < gh) {
            scale = gh / bh;
        } else if (bw < gw) {
            scale = gw / bw;
        } else if (gh < bh) {
            scale = bh / gh;
        } else if (gw < bw) {
            scale = bw / gw;
        } else
            scale = 1;

        background.setScale(scale);

        rows = 3;
        cols = 3;
        grid = new Grid(rows, cols);
        grid.setListener(new GridListener() {
            @Override
            public void gameWon(int clickedRow, int clickedCol) {
                System.out.println("WON");
                moveTimer = 0f;
                gameOver(2);
            }

            @Override
            public void gameDraw(int clickedRow, int clickedCol) {
                System.out.println("DRAW");
                moveTimer = 0f;
                gameOver(1);
            }

            @Override
            public void keepPlaying(int clickedRow, int clickedCol) {
                currentType = currentType == CellType.X ? CellType.O : CellType.X;
                isMyTurn = !isMyTurn;
                moveTimer = 0f;
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void checkWinner(int clickedRow, int clickedCol, CellType cellType) {
            }
        });

        aiPlayer = new ComputeAIPlayer(rows, cols);
        aiPlayer.setSeed(currentType == CellType.X ? CellType.O : CellType.X);

        Gdx.input.setInputProcessor(this);
    }

    private void makeComputerMove() {
        System.out.println("COMPUTER TIME");
        CellType[][] myInt = new CellType[3][3];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                myInt[row][col] = grid.getGrid()[row][col].getType();
            }
        }
        int[] move = aiPlayer.move(myInt, isHard);
        System.out.println("MOVE :: " + move[0] + " and " + move[1]);
        grid.move(move[0], move[1], currentType);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.draw(background, 0, 0);
        if (grid != null) {
            grid.render(batch);
            grid.update(Gdx.graphics.getDeltaTime());
        }
        if (currentState != GameStateEnum.PLAYING) {
            drawGameOver(batch);
        }

        if (currentState == GameStateEnum.PLAYING) {
            glyphLayout.setText(font, isMyTurn ? "YOUR MOVE" : "COMPUTER'S MOVE");
            batch.setColor(TicTacToeGame.GRID_BG_COLOR_DARK);

            float total_width = glyphLayout.width + 24 + 32;

            batch.draw(gameOverBg, (TicTacToeGame.WIDTH - total_width) / 2 - 8, 48 - 16 - 8, total_width + 16 + 16, 48);
            font.setColor(Color.BLACK);
            font.draw(batch, glyphLayout, TicTacToeGame.WIDTH / 2 - glyphLayout.width / 2 - 16 - 8, 48 + glyphLayout.height / 2);
            //draw current Icon (X or O)
            batch.draw(currentType == CellType.X ? cellX : cellO, TicTacToeGame.WIDTH / 2 + glyphLayout.width / 2 + 8, 48 - 16, 32, 32);
        }
        batch.end();
    }

    @Override
    public void update(float dt) {
        if (!isMyTurn) {
            moveTimer += dt;

            if (moveTimer > 1f && currentState == GameStateEnum.PLAYING) {
                makeComputerMove();
                moveTimer = 0f;
            }
        } else
            moveTimer = 0f;
    }

    private void drawGameOver(SpriteBatch batch) {
        batch.setColor(TicTacToeGame.GAME_OVER_BG_COLOR);
        batch.draw(gameOverBg, 80, startingYGameOverBg - 16, TicTacToeGame.WIDTH - 160, gameOverBgHeight);
        batch.setColor(Color.WHITE);
        gameResult.render(batch);
        winnerLabel.render(batch);
        //playAgain.render(batch);
        //mainMenu.render(batch);
        play.render(batch);
        menu.render(batch);
        settings.render(batch);
    }

    private float moveTimer = 0f;

    private void reset() {
        currentState = GameStateEnum.PLAYING;
        grid.resetGame();
        isMyTurn = new Random(234324234).nextBoolean();
        Gdx.input.setInputProcessor(this);
    }

    public void gameOverOld(int gameOver) {
        String winnerText;
        switch (gameOver) {
            case 1:
                currentState = GameStateEnum.GAME_DRAWN;
                winnerText = "GAME DRAWN";
                break;
            case 2:
            default:
                if (isMyTurn) {
                    currentState = GameStateEnum.GAME_WON;
                    winnerText = "YOU WIN";
                } else {
                    currentState = GameStateEnum.GAME_LOST;
                    winnerText = "YOU LOST";
                }
        }
        gameOverBgHeight = 250;
        float y = TicTacToeGame.HEIGHT - gameOverBgHeight;
        y = y / 2;
        y = TicTacToeGame.HEIGHT - y;

        gameResult = new Label("GAME RESULT", Label.LabelType.TITLE, TicTacToeGame.WIDTH / 2, y - 30);
        gameResult.setColor(Color.WHITE);
        winnerLabel = new Label(winnerText, Label.LabelType.SUBTITLE, TicTacToeGame.WIDTH / 2, y - 60);
        winnerLabel.setColor(Color.WHITE);
        playAgain = new TextButton("Play Again?", TicTacToeGame.WIDTH / 2, y - 130);
        mainMenu = new TextButton("Main Menu", TicTacToeGame.WIDTH / 2, y - 200);

        if (soundMode)
            Assets.getInstance().playSound(currentState);
    }

    public void gameOver(int gameOver) {
        String winnerText;
        String gameResultText;
        switch (gameOver) {
            case 1:
                currentState = GameStateEnum.GAME_DRAWN;
                winnerText = "GAME DRAWN";
                gameResultText = "OUCH!!!";
                break;
            case 2:
            default:
                if (isMyTurn) {
                    currentState = GameStateEnum.GAME_WON;
                    winnerText = "YOU WON";
                    gameResultText = "YAY!!!";
                } else {
                    currentState = GameStateEnum.GAME_LOST;
                    winnerText = "YOU LOST";
                    gameResultText = "OOPS!!!";
                }
        }
        gameOverBgHeight = 136;

        Texture playTex = Assets.getInstance().manager.get(Assets.play);
        play = new Image(playTex, TicTacToeGame.WIDTH / 2 - playTex.getWidth() - 16, startingYGameOverBg + playTex.getHeight() / 2);
        menu = new Image(Assets.getInstance().manager.get(Assets.menu), TicTacToeGame.WIDTH / 2, startingYGameOverBg + playTex.getHeight() / 2);
        settings = new Image(Assets.getInstance().manager.get(Assets.settings), TicTacToeGame.WIDTH / 2 + playTex.getWidth() + 16, startingYGameOverBg + playTex.getHeight() / 2);

        float y = startingYGameOverBg + playTex.getHeight() + playTex.getHeight() / 2 + 12;
        winnerLabel = new Label(winnerText, Label.LabelType.SUBTITLE, TicTacToeGame.WIDTH / 2, y);
        winnerLabel.setColor(Color.WHITE);
        gameResult = new Label(gameResultText, Label.LabelType.TITLE, TicTacToeGame.WIDTH / 2, y + 24);
        gameResult.setColor(Color.WHITE);

        if (soundMode)
            Assets.getInstance().playSound(currentState);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int p, int b) {
        if (currentState == GameStateEnum.PLAYING && !isMyTurn)
            return true;
        unproject(m, cam);

        if (currentState == GameStateEnum.PLAYING) {
            grid.click(m.x, m.y, currentType);
            System.out.println("PLAYING");
        } else {
            System.out.println("GAMEOVER");
            if (play.contains(m.x, m.y)) {
                //GameState nextState = new GameState(gsm, isHard);
                //FadeTransitionState state = new FadeTransitionState(gsm, this, nextState);
                //gsm.set(state);

                if (soundMode)
                    Assets.getInstance().playClick();
                reset();
            } else if (menu.contains(m.x, m.y)) {
                if (soundMode)
                    Assets.getInstance().playClick();

                MenuState nextState = new MenuState(gsm);
                CheckeredTransitionState state = new CheckeredTransitionState(gsm, this, nextState);
                gsm.set(state);
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
