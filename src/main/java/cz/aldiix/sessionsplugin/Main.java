package cz.aldiix.sessionsplugin;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        System.out.println("Sessions enabled!");
        Config.init();
        Controller.init();
    }
}
