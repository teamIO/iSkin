package me.thehutch.iskin;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

public class CmdExecutor implements CommandExecutor {

	public iSkin plugin;
	public CmdExecutor(iSkin instance) {
	plugin = instance;
}
	Logger log = Logger.getLogger("Minecraft");
	

	
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String aliases, String[] args) {
		
		if(cmd.getName().equalsIgnoreCase("iskin")) {
			if(args.length == 0) {
			return false;
			}

		if ((args[0].equalsIgnoreCase("reload")) && (cs.hasPermission("iskin.reload") || cs.isOp())) {
			
				log.info("initiating plugin reload...");
				plugin.config = plugin.getConfig();
				plugin.load();
				plugin.updateSkin((SpoutPlayer) cs);
				plugin.updateHero((SpoutPlayer) cs);
				log.info("reload successful");
				cs.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.GREEN+"iSkin"+ChatColor.DARK_RED+"] has been reloaded");

			return true;
			}
		
		/*
		 * help command
		 */
		else if ((args[0].equalsIgnoreCase("help")) && (cs.hasPermission("iskin.help") || cs.isOp())) {
			
			if (args.length == 1) {
                            plugin.helpMessage1(cs);
				return true;
			}
                        else if (args.length == 2) {
                            
			if (args[1].equalsIgnoreCase("1")) {
                            plugin.helpMessage1(cs);
			return true;
                        }
                        
                        else if (args[1].equalsIgnoreCase("2")) {
                            plugin.helpMessage2(cs);
                            return true;
                        }
                        else {
                            cs.sendMessage("How did you get here?");
                            return false;
                        }
                    }
                        else {
                            cs.sendMessage("How did you get here?");
                            return false; 
                        }
		}
		
		
		
		/*
		 * setself command
		 */
		else if ((args[0].equalsIgnoreCase("setself")) && (cs.hasPermission("iskin.setself") || cs.isOp())) {
			
			if (args.length < 2 || args.length > 4) {
				return false;
			}
                        
                        if (!(cs instanceof Player)) {
                            return false;
                        }
			
			Player player = (Player)cs;
			String url = args[1];
			
			if(!url.contains("http://") && args.length == 3) {
				if(args[1].equalsIgnoreCase("minecraft")) 
					url = "http://www.minecraft.net/skin/" + args[2] + ".png";
				}
			
                        
                        
			//Fetchs URL from MC.net
		if (!url.endsWith(".png")) {
			player.sendMessage(ChatColor.RED+"URL must end with .png");
			return false;
		} else {

		
		plugin.config.set("Playerlist." + player.getName() + ".url", url);
		plugin.saveConfig();
		plugin.updateSkin((SpoutPlayer) cs);
		plugin.updateHero((SpoutPlayer) cs);
		cs.sendMessage(ChatColor.GOLD + "Your skin has now changed!");
			return true;
                }
	}
		
		
		
		/*
		 * Setplayer command
		 */
		else if ((args[0].equalsIgnoreCase("setplayer") && (cs.hasPermission("iskin.setplayer") || cs.isOp()))) {
			
			if (args.length < 3 || args.length > 5) {
								return false;
			}
			Player player = (Player)cs;
			String playerName = args[1];
			String url = args[2];
			
			if(!url.contains("http://") && args.length == 4) {
				if(args[2].equalsIgnoreCase("Minecraft")) 
					url = "http://www.minecraft.net/skin/" + args[3] + ".png";
				}
			if (!url.endsWith(".png")) {
				player.sendMessage(ChatColor.RED+"URL must end with .png");
			return false;
			}

			plugin.config.set("Playerlist." + playerName + ".url", url);
			plugin.saveConfig();
			plugin.updateSkin((SpoutPlayer) cs);
			plugin.updateHero((SpoutPlayer) cs);
				cs.sendMessage(ChatColor.GOLD + playerName + " now has a new skin!");
			return true;
		}


		/*
		 * setgroup command
		 */
		else if ((args[0].equalsIgnoreCase("setgroup")) && (cs.hasPermission("iskin.setgroup") || cs.isOp())) {
			if (args.length < 3 || args.length > 5) {
				return false;
		}


			Player player = (Player)cs;
			String groupName = args[1];
			String url = args[2];

				if(!url.contains("http://") && args.length == 4) {
					if(args[2].equalsIgnoreCase("Minecraft")) 
						url = "http://www.minecraft.net/skin/" + args[3] + ".png";
				}

		if (!url.endsWith(".png")) {
			player.sendMessage(ChatColor.RED+"URL must end with .png");
				return false;
		}

			plugin.config.set("Grouplist."+ groupName + ".url", url);
			plugin.saveConfig();
			plugin.updateSkin((SpoutPlayer) cs);
			plugin.updateHero((SpoutPlayer) cs);
				cs.sendMessage(ChatColor.GOLD + groupName + " Group has been created");
			return true;
		}
	}
		return false;
    }	
}