package survival.labs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import survival.labs.bot.Bot;
import survival.labs.commands.MapCommand;
import survival.labs.spigot.events.JoinListener;

public class Main extends JavaPlugin {

	public Database db = new Database();
	public Bot bot;
	
	public void onEnable() {
		loadConfig();
		FileConfiguration config = getConfig();
		db.setup(config);

		getServer().getPluginManager().registerEvents(new JoinListener(db), this);
		String mapURL = config.getString("Commands.MapURL");
		getCommand("map").setExecutor(new MapCommand(mapURL));

		bot = new Bot(db);
		bot.startBot(config);
	}
	
	public void onDisable() {
		bot.stopBot();
		db.disconnect();
	}
	
	private void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
}
