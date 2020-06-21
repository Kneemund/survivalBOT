package survival.labs.spigot.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import survival.labs.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class JoinListener implements Listener {

    private final Database db;

    public JoinListener(Database db) {
        this.db = db;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        try {
            ResultSet results = db.query(db.sqlCountByUUID(uuid.toString()));
            results.first();
            if(results.getInt(1) == 0) event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, ChatColor.RED + "You are not whitelisted.");
            results.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
