package survival.labs;

import org.bukkit.plugin.java.JavaPlugin;
import survival.labs.bot.Bot;
import survival.labs.spigot.events.JoinListener;

public class Main extends JavaPlugin {

	public Database db = new Database();
	public Bot bot;
	
	public void onEnable() {
		loadConfig();
		db.setup();

		getServer().getPluginManager().registerEvents(new JoinListener(db), this);

		bot = new Bot(db);
		bot.startBot();
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
