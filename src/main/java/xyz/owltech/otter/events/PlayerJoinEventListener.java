package xyz.owltech.otter.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.owltech.otter.OtterAntiVPN;
import xyz.owltech.otter.profile.Profile;
import xyz.owltech.otter.utils.ChatUtils;
import xyz.owltech.otter.utils.Settings;

public class PlayerJoinEventListener implements Listener {

    @EventHandler
    public void onJoin(PlayerLoginEvent event) {
        OtterAntiVPN.getInstance().getExecutorService().execute(() -> {
            boolean vpn = OtterAntiVPN.getInstance().getOtterApi().check(event.getAddress().getHostAddress());
            if (vpn) {

                final String message = ChatUtils.colour(Settings.ALERT_MESSAGE.replaceAll("%player%", event.getPlayer().getName()));
                if (Settings.ALERTS_ENABLED) {
                    Bukkit.getConsoleSender().sendMessage(message);

                    OtterAntiVPN.getInstance().getProfileManager().toList().stream().filter(Profile::isAlerts).forEach(profile -> {
                        Player player = Bukkit.getPlayer(profile.getUuid());
                        if (player != null) {
                            player.sendMessage(message);
                        }
                    });

                }

                if (Settings.KICK_PLAYERS
                        && !(Settings.OP_BYPASS && event.getPlayer().isOp())
                        && !event.getPlayer().hasPermission("otter.bypass")) {

                    Bukkit.getScheduler().runTask(OtterAntiVPN.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            event.getPlayer().kickPlayer(ChatUtils.colour(Settings.KICK_MESSAGE));
                        }
                    });

                }
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        OtterAntiVPN.getInstance().getProfileManager().add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        OtterAntiVPN.getInstance().getProfileManager().remove(event.getPlayer().getUniqueId());
    }

}
