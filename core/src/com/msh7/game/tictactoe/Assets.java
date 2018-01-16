package com.msh7.game.tictactoe;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.msh7.game.tictactoe.state.GameStateEnum;

public class Assets {

    public AssetManager manager = new AssetManager();
    private static Assets assets;

    public static final AssetDescriptor<Texture> background = new AssetDescriptor<Texture>("img/game_background_full.jpg", Texture.class);
    //public static final AssetDescriptor<Texture> background_light = new AssetDescriptor<Texture>("img/game_background.jpg", Texture.class);
    public static final AssetDescriptor<Texture> logo = new AssetDescriptor<Texture>("img/logo_try.png", Texture.class);
    public static final AssetDescriptor<Texture> pixel = new AssetDescriptor<Texture>("img/pixel.png", Texture.class);
    public static final AssetDescriptor<Texture> button = new AssetDescriptor<Texture>("img/button.png", Texture.class);
    public static final AssetDescriptor<Texture> grid_bg = new AssetDescriptor<Texture>("img/grid_bg.png", Texture.class);
    public static final AssetDescriptor<Texture> grid_cell = new AssetDescriptor<Texture>("img/grid_cell.png", Texture.class);
    public static final AssetDescriptor<Texture> game_logo = new AssetDescriptor<Texture>("img/game_logo.png", Texture.class);
    public static final AssetDescriptor<BitmapFont> designFont = new AssetDescriptor<BitmapFont>("fonts/24_design_font.fnt", BitmapFont.class);
    public static final AssetDescriptor<BitmapFont> font = new AssetDescriptor<BitmapFont>("fonts/font-export.fnt", BitmapFont.class);
    public static final AssetDescriptor<BitmapFont> subtitle_font = new AssetDescriptor<BitmapFont>("fonts/font-subtitle-export.fnt", BitmapFont.class);
    public static final AssetDescriptor<BitmapFont> title_font = new AssetDescriptor<BitmapFont>("fonts/font-subtitle-export.fnt", BitmapFont.class);
    public static final AssetDescriptor<Texture> cell_x = new AssetDescriptor<Texture>("img/icon_x.png", Texture.class);
    public static final AssetDescriptor<Texture> cell_o = new AssetDescriptor<Texture>("img/icon_o.png", Texture.class);
    public static final AssetDescriptor<Sound> beep = new AssetDescriptor<Sound>("sound/beep.ogg", Sound.class);
    public static final AssetDescriptor<Sound> click = new AssetDescriptor<Sound>("sound/button-click.ogg", Sound.class);
    public static final AssetDescriptor<Sound> win = new AssetDescriptor<Sound>("sound/win.ogg", Sound.class);
    public static final AssetDescriptor<Sound> lose = new AssetDescriptor<Sound>("sound/lose.ogg", Sound.class);

    //UI
    public static final AssetDescriptor<Texture> sound_on = new AssetDescriptor<Texture>("ui/sound_on.png", Texture.class);
    public static final AssetDescriptor<Texture> sound_off = new AssetDescriptor<Texture>("ui/sound_off.png", Texture.class);
    public static final AssetDescriptor<Texture> rate = new AssetDescriptor<Texture>("ui/rate.png", Texture.class);
    public static final AssetDescriptor<Texture> settings = new AssetDescriptor<Texture>("ui/settings.png", Texture.class);
    public static final AssetDescriptor<Texture> play = new AssetDescriptor<Texture>("ui/play.png", Texture.class);
    public static final AssetDescriptor<Texture> menu = new AssetDescriptor<Texture>("ui/menu.png", Texture.class);

    public static Assets getInstance() {
        if (assets == null)
            assets = new Assets();
        return assets;
    }

    public void load() {
        manager.load(background);
        //manager.load(background_light);
        manager.load(pixel);
        manager.load(logo);
        manager.load(game_logo);
        manager.load(designFont);
        manager.load(button);
        manager.load(grid_bg);
        manager.load(grid_cell);
        manager.load(font);
        manager.load(subtitle_font);
        manager.load(title_font);
        manager.load(cell_o);
        manager.load(cell_x);
        manager.load(beep);
        manager.load(click);
        manager.load(win);
        manager.load(lose);

        manager.load(sound_off);
        manager.load(sound_on);
        manager.load(rate);
        manager.load(settings);
        manager.load(play);
        manager.load(menu);
    }

    public void playClick() {
        manager.get(click).play();
    }

    public void playBeep() {
        manager.get(click).play();
    }

    public void playWin() {
        manager.get(win).play();
    }

    public void playLose() {
        manager.get(lose).play();
    }

    public void dispose() {
        manager.dispose();
    }

    public void playSound(GameStateEnum currentState) {
        switch (currentState) {
            case GAME_DRAWN:
            case GAME_WON:
                playWin();
                break;
            case GAME_LOST:
                playLose();
                break;
            default:
                playBeep();
        }
    }
}
