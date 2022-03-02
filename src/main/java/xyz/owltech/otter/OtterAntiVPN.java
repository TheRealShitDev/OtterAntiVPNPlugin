package xyz.owltech.otter;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.owltech.otter.api.OtterApi;
import xyz.owltech.otter.commands.OtterCommand;
import xyz.owltech.otter.events.PlayerJoinEventListener;
import xyz.owltech.otter.profile.ProfileManager;
import xyz.owltech.otter.utils.Settings;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class OtterAntiVPN extends JavaPlugin {

    @Getter private static OtterAntiVPN instance;

    private OtterApi otterApi;
    private ExecutorService executorService;
    private ProfileManager profileManager;

    @Override
    public void onEnable() {
        instance = this;
        profileManager = new ProfileManager();
        executorService = Executors.newSingleThreadExecutor();
        otterApi = new OtterApi();

        saveResource("config.yml", false);
        loadConfigSettings();

        Bukkit.getPluginManager().registerEvents(new PlayerJoinEventListener(), this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            profileManager.add(player.getUniqueId());
        }

        getCommand("otter").setExecutor(new OtterCommand());
    }

    @Override
    public void onDisable() {

    }

    public void loadConfigSettings() {
        reloadConfig();
        Settings.LICENSE = getConfig().getString("license");

        Settings.KICK_MESSAGE = getConfig().getString("kicks.message");
        Settings.KICK_PLAYERS = getConfig().getBoolean("kicks.enabled");
        Settings.OP_BYPASS = getConfig().getBoolean("kicks.op-bypass");

        Settings.ALERTS_ENABLED = getConfig().getBoolean("alerts.enabled");
        Settings.ALERT_MESSAGE = getConfig().getString("alerts.message");
    }

}
