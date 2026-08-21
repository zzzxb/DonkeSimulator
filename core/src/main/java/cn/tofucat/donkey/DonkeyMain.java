package cn.tofucat.donkey;

import cn.tofucat.donkey.config.Config;
import cn.tofucat.donkey.config.ConfigManager;
import cn.tofucat.donkey.manager.ScreenManager;
import cn.tofucat.donkey.screen.GameScreen;
import cn.tofucat.donkey.screen.LogoScreen;
import cn.tofucat.donkey.utils.Log;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class DonkeyMain extends Game {
    Screen screen;

    @Override
    public void create() {
        ConfigManager.init();
        Log.setLogLevel(Config.getString("log.level", "info"));
        ScreenManager.init(this);
        if (Config.getBoolean("opening.logo", true)) {
            screen = new LogoScreen();
        } else {
            screen = new GameScreen();
        }
        ScreenManager.setScreen(screen);
    }
}
