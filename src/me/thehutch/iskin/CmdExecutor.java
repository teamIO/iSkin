package me.thehutch.iskin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdExecutor implements CommandExecutor {

	private iSkin plugin;
	public CmdExecutor(iSkin instance) {
            plugin = instance;
        }
	
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String aliases, String[] args) {
		
		if(cmd.getName().equalsIgnoreCase("iskin") && args.length !=0) {

		if (args[0].equalsIgnoreCase("reload") && args.length == 1) {
			
                    if (!cs.hasPermission("iskin.reload")) {
                        cs.sendMessage(ChatColor.RED + "You do not have permission");
                        return false;
                    }
                        plugin.reloadConfig();
                        plugin.saveConfig();
                        for(Player p : plugin.getServer().getOnlinePlayers()) {
                            plugin.updatePlayerSkin(p.getName());
                        }
                        plugin.updateGroupSkin();
                        cs.sendMessage(ChatColor.GREEN + "Reload complete");
                        return true;
                    }

		/*
		 * help command
		 */
		else if (args[0].equalsIgnoreCase("help")) {
                    
                    if (!cs.hasPermission("iskin.help")) {
                        cs.sendMessage(ChatColor.RED + "You do not have permission");
                        return false;
                    }
			
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
                    }
                    else {
                        return false; 
                    }
                } 
		
		
		
		/*
		 * setself command
		 */
		else if (args[0].equalsIgnoreCase("setself")) {
			
                    if (!cs.hasPermission("iskin.setself")) {
                        cs.sendMessage(ChatColor.RED + "You do not have permission");
                        return false;
                    }
                    
                        if (!(cs instanceof Player)) {
                            cs.sendMessage("Player is required for /iskin setself");
                            cs.sendMessage("non-players can not have skins.");
                            return false;
                        }
                      
                        // With 'minecraft' version:
			if (args.length == 3) {
                            String url = args[2];
                            if (!url.contains("http://")) {
                                if (args[1].equalsIgnoreCase("minecraft")) {
                                    url = "http://s3.amazonaws.com/MinecraftSkins/" + args[2] + ".png";
                                }
                            }
                            if (!url.endsWith(".png")) {
                                cs.sendMessage(ChatColor.RED + "URL must end with .png");
                                return false;
                            }
                            else {
                                plugin.getConfig().set("Players." + cs.getName(), url);
                                plugin.saveConfig();
                                plugin.updatePlayerSkin(cs.getName());
                                cs.sendMessage(ChatColor.GOLD + "Your skin has now changed!");
                                    return true;
                            }
                        }
                        
                        // Without 'minecraft' version:
                        else if (args.length == 2) {
                            String url = args[1];
                            
                            if (!url.endsWith(".png")) {
                                cs.sendMessage(ChatColor.RED + "URL must end with .png");
                                return false; 
                            }
                            else {
                                plugin.getConfig().set("Players." + cs.getName(), url);
                                plugin.saveConfig();
                                plugin.updatePlayerSkin(cs.getName());
                                cs.sendMessage(ChatColor.GOLD + "Your skin has now changed");
                            }
                        }
                    }


		/*
		 * Setplayer command
		 */
		else if (args[0].equalsIgnoreCase("setplayer")) {
                    
                    if (!cs.hasPermission("iskin.setplayer")) {
                        cs.sendMessage(ChatColor.RED + "You do not have permission");
                        return false;
                    }
                    
                    // With 'minecraft' version:
                    if (args.length == 4) {
                        
                        try {
                            Player p = plugin.getServer().getPlayer(args[1]);
                            String url = args[3];
                            
                            if (!url.contains("http://")) {
                                if (args[2].equalsIgnoreCase("minecraft")) {
                                    url = "http://s3.amazonaws.com/MinecraftSkins/" + args[3] + ".png";                                    
                                }
                            }
                            
                            if (!url.endsWith(".png")) {
                                cs.sendMessage(ChatColor.RED + "URL must end with .png");
                            }
                            
                            plugin.getConfig().set("Players." + p.getName(), url);
                            plugin.saveConfig();
                            plugin.updatePlayerSkin(p.getName());
                            cs.sendMessage(ChatColor.GOLD + p.getName() + "'s name has now changed");
                            return true;
                            
                        } catch (Exception ex) {
                            cs.sendMessage("Invalid Player!");
                            return false;
                        }
                    } 
                    
                    // Without 'minecraft' version:
                    else if (args.length == 3) {
                        try {
                            Player p = plugin.getServer().getPlayer(args[1]);
                            String url = args[3]; 
                            
                            if (!url.endsWith(".png")) {
                                cs.sendMessage(ChatColor.RED + "URL must end with .png");
                                return false;
                            }
                            
                            plugin.getConfig().set("Players." + p, url);
                            plugin.saveConfig();
                            plugin.updatePlayerSkin(p.getName());
                            cs.sendMessage(ChatColor.GOLD + p.getName() + " now has a new skin!");
                            return true;
                            
                        } catch (Exception ex) {
                            cs.sendMessage("Invalid Player!");
                            return false;
                        }
                    }
                    else {
                        return false;
                    }
		}
                


		/*
		 * setgroup command
		 */
		else if (args[0].equalsIgnoreCase("setgroup")) {
			
                    if (!cs.hasPermission("iskin.setgroup")) {
                        cs.sendMessage(ChatColor.RED + "You do not have permission");
                        return false;
                    }
                    
                    // With 'minecraft' version:
                    if (args.length == 4) {
                        
                            String url = args[3];
                            String group = args[1];
                            
                            if (!url.contains("http://")) {
                                if (args[2].equalsIgnoreCase("minecraft")) {
                                    url = "http://s3.amazonaws.com/MinecraftSkins/" + args[3];
                                }
                            }
                            if (!url.endsWith(".png")) {
                                cs.sendMessage(ChatColor.RED + "URL must end with .png");
                                return false;
                            }
                            
                            plugin.getConfig().set("Groups." + group, url);
                            plugin.saveConfig();
                            plugin.updateGroupSkin();
                            cs.sendMessage(ChatColor.GOLD + group + " has had its skin changed!");
                            return true;
                            
                        } 

                    // Without 'minecraft' version:
                    else if (args.length == 3) {

                        String url = args[2];
                        String group = args[1];
                        
                        if (!url.endsWith(".png")) {
                            cs.sendMessage(ChatColor.RED + "URL must end with .png");
                            return false;
                        }

                        plugin.getConfig().set("Groups." + group, url);
                        plugin.saveConfig();
                        plugin.updateGroupSkin();
                        cs.sendMessage(ChatColor.GOLD + group + " has had its skin changed!");
                        return true;
                    }
                    else {
                        return false;
                    }
                }

            /*
             * SetHero Command
             */  
            else if (args[0].equalsIgnoreCase("setHero")) {
                
                    if (!cs.hasPermission("iskin.sethero")) {
                        cs.sendMessage(ChatColor.RED + "You do not have permission");
                        return false;
                    }
                
                // With 'minecraft' version:
                if (args.length == 4) {
                    
                    String url = args[3];
                    String heroClass = args[1];
                    
                    if (!url.contains("http://")) {
                        if (args[2].equalsIgnoreCase("minecraft")) {
                            url = "http://s3.amazonaws.com/MinecraftSkins/" + args[3] + ".png";
                        }
                    }
                    if (!url.endsWith(".png")) {
                        cs.sendMessage(ChatColor.RED + "URL must end with .png");
                        return false;
                    }
                    
                    plugin.getConfig().set("Heroes." + heroClass, url);
                    plugin.saveConfig();
                    plugin.updateHeroSkin();
                    cs.sendMessage(ChatColor.GOLD + "The " + heroClass + "'s class now has a new skin");
                    return true;
                    
                }
                
                // Without 'minecraft' version:
                else if (args.length == 3) {
                    
                    String url = args[2];
                    String heroClass = args[1];
                    
                    if (!url.endsWith(".png")) {
                        cs.sendMessage(ChatColor.RED + "URL must end with .png");
                        return false;
                    }
                    
                    plugin.getConfig().set("Heroes." + heroClass, url);
                    plugin.saveConfig();
                    plugin.updateHeroSkin();
                    cs.sendMessage(ChatColor.GOLD + "The " + heroClass + "'s class now has a new skin");
                    return true;

                }
                else {
                    return false;
                }
            }
            plugin.helpMessage1(cs);
            return false;
        }
        return false;
    }
}
