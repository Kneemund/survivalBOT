package survival.labs.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MapCommand implements CommandExecutor {

    private final String mapURL;

    public MapCommand(String mapURL) {
        this.mapURL = mapURL;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        TextComponent component = new TextComponent();
        component.setText(ChatColor.GOLD + "Click " + ChatColor.RED +  "here" + ChatColor.GOLD + " to open the map.");
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, mapURL));

        Player player = (Player) sender;
        player.spigot().sendMessage(component);
        return true;
    }
}
