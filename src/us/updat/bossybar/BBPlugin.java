package us.updat.bossybar;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public class BBPlugin extends JavaPlugin implements CommandExecutor, Listener {
	
	HashMap<String, BossBar> barMap = new HashMap<String, BossBar>();
	List<String> lookupColors = new ArrayList<String>();
	FileConfiguration config;
	
	@Override
	public void onEnable() {
		int pluginId = 6613;
		@SuppressWarnings("unused")
		MetricsLite metrics = new MetricsLite(this, pluginId);
		String[] colorsAvailable = new String[] {"blue", "green", "pink",
				"purple", "red", "white", "yellow"};
		for (String color : colorsAvailable) {
			lookupColors.add(color);
		}
		this.getCommand("bossybar").setExecutor(this);
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getCommand("bossybar").setTabCompleter(new BBTabCompleter(this));
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		for (Entry<String, BossBar> ent : barMap.entrySet()) {
			if (!(ent.getValue().getPlayers().contains(e.getPlayer()))) {
				ent.getValue().addPlayer(e.getPlayer());
				ent.getValue().setVisible(true);
				ent.getValue().setProgress(1.0);
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("bossybar")) {
			if (args != null && args.length > 0) {
				if (args[0].equalsIgnoreCase("add")) {
					if (args.length < 4) {
						sender.sendMessage("[BossyBar] Missing Arguments! Usage: bossybar add <color> <id> <text>");
						return true;
					} else {
						if (barMap.containsKey(args[2])) {
							sender.sendMessage("" + ChatColor.RED + "[BossyBar] That ID Exists!");
							return true;
						}
						ArrayList<String> newArgs = new ArrayList<String>();
						for (String arg : args) {
							newArgs.add(arg);
						}
						String colorArg = args[1].toLowerCase();
						BarColor color = getColor(colorArg);
						BossBar bar;
						if (args.length == 4) {
							bar = createColoredBossBar(args[2], args[3], color);
						} else {
							bar = createColoredBossBar(args[2], String.join(" ", newArgs.subList(3, args.length)).replaceAll("&", "§"), color);
						}
						barMap.put(args[2], bar);
						sender.sendMessage("" + ChatColor.GREEN + "[BossyBar] Created BossBar!");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("list")) {
					List<String> availableBars = new ArrayList<String>();
					if (barMap != null && barMap.size() > 0) {
						for (Entry<String, BossBar> ent : barMap.entrySet()) {
							availableBars.add(ent.getKey());
						}
						sender.sendMessage("" + ChatColor.GOLD + "[BossyBar] Available Bars: " + String.join(",", availableBars));
						return true;
					}
					sender.sendMessage("" + ChatColor.RED + "[BossyBar] No Bars Available!");
					return true;
				} else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("rm")) {
					if (args.length < 2) {
						sender.sendMessage("" + ChatColor.RED + "[BossyBar] Missing ID for Bar! Usage: /bossybar rm/remove <id>");
						return true;
					} else {
						if (removeBossBar(args[1])) {
							sender.sendMessage("" + ChatColor.GREEN + "[BossyBar] Bar " + args[1] + " Removed!");
							return true;
						} else {
							sender.sendMessage("" + ChatColor.RED + "[BossyBar] Bar " + args[1] + " Could Not Be Found!");
							return true;
						}
					}
				} else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
					String gold = "" + ChatColor.BOLD + ChatColor.GOLD;
					ChatColor reset = ChatColor.RESET;
					sender.sendMessage("" + gold + "====== BossyBar Help Menu ===== \n" +
										"/bossybar ?/help -" + reset + " Display this menu. \n" +
										"\n" + gold + "/bossybar add <id> <text> [color] -" + reset + "Add a custom boss bar. \n" +
										"\n" + gold + "/bossybar colors -" + reset + " List available colors for bars. \n" +
										"\n" + gold + "/bossybar list -" + reset + " List active bars. \n" +
										"\n" + gold + "/bossybar rm/remove <id> -" + reset + " Remove an active bar.");
					return true;
				} else if (args[0].equalsIgnoreCase("colors")) {
					sender.sendMessage("" + ChatColor.RED + "[BossyBar] Error. That is not a valid color. The valid colors are: " + ChatColor.WHITE + "white, " + 
							ChatColor.BLUE + "blue, " + ChatColor.GREEN + "green, " + ChatColor.LIGHT_PURPLE + "pink, " + ChatColor.DARK_PURPLE + "purple, " +
							ChatColor.YELLOW + "yellow, " + ChatColor.DARK_RED + "red.");
					return true;
				} else {
					sender.sendMessage("" + ChatColor.RED + "[BossyBar] Unknown Command! Run \"/bossybar ?\" for commands");
					return true;
				}
			}
			sender.sendMessage("" + ChatColor.RED + "[BossyBar] Missing Arguments! Use \"/bossybar ?\" to see commands.");
			return true;
		}
		return true;
		
	}
	
	public BarColor getColor(String color) {
		BarColor c;
		switch (color) {
			case "blue":
				c = BarColor.BLUE;
				break;
			case "red":
				c = BarColor.RED;
				break;
			case "white":
				c = BarColor.WHITE;
				break;
			case "yellow":
				c = BarColor.YELLOW;
				break;
			case "purple":
				c = BarColor.PURPLE;
				break;
			case "green":
				c = BarColor.GREEN;
				break;
			case "pink":
				c = BarColor.PINK;
				break;
			default:
				c = BarColor.WHITE;
		}
		return c;
	}
	
	public BossBar createColoredBossBar(String id, String title, BarColor color) {
		BarFlag[] barFlags = new BarFlag[0];
		BossBar bar = Bukkit.getServer().createBossBar(title, color, BarStyle.SOLID, barFlags);
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			bar.addPlayer(player);
		}
		bar.setVisible(true);
		bar.setProgress(1.0);
		return bar;
	}
	
	public boolean removeBossBar(String id) {
		if (barMap != null && barMap.size() > 0) {
			for (Entry<String, BossBar> ent : barMap.entrySet()) {
				if (ent.getKey().equalsIgnoreCase(id)) {
					ent.getValue().setVisible(false);
					ent.getValue();
					return true;
				}
			}
		}
		
		return false;
	}
	
}
