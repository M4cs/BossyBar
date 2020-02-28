package us.updat.bossybar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class BBTabCompleter implements TabCompleter {
	
	BBPlugin plugin;
	
	public BBTabCompleter(BBPlugin instance) {
		plugin = instance;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender,  Command cmd, String alias,  String[] args) {
		if (args != null && args.length > 0) {
			if (args.length == 1) {
				List<String> cmdOptions = new ArrayList<String>();
				cmdOptions.add("add");
				cmdOptions.add("colors");
				cmdOptions.add("help");
				cmdOptions.add("list");
				cmdOptions.add("remove");
				cmdOptions.add("rm");
				cmdOptions.add("?");
				List<String> finalOptions = parseFinalOptions(args[0], cmdOptions);
				return finalOptions;
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("add")) {
					List<String> finalOptions = parseFinalOptions(args[1], plugin.lookupColors);
					return finalOptions;
				} else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("rm")) {
					List<String> options = new ArrayList<String>();
					if (plugin.barMap.size() > 0) {
						for (Entry<String, BossBar> ent : plugin.barMap.entrySet())  {
							options.add(ent.getKey());
						}
						List<String> finalOptions = parseFinalOptions(args[1], options);
						return finalOptions;
					} else {
						options.add("No Bars Yet!");
						return options;
					}
				} else {
					return new ArrayList<String>();
				}
			} else if (args.length == 3) {
				List<String> option = new ArrayList<String>();
				if (plugin.barMap.containsKey(args[2])) {
					option.clear();
					option.add("" + ChatColor.RED + "This ID is Taken!");
					return option;
				}
				option.add("ID Name");
				return option;
			} else if (args.length == 4) {
				return new ArrayList<String>();
			}
		}
		return new ArrayList<String>();
	}

	public List<String> parseFinalOptions(String arg, List<String> cmdOptions) {
		List<String> finalOptions = new ArrayList<String>();
		for (String option : cmdOptions) {
			if (option.startsWith(arg)) {
				finalOptions.add(option);
			}
		}
		return finalOptions;
	}
	
}
