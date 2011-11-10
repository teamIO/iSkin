package me.thehutch.iskin;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.hero.Hero;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;


public class iSkin extends JavaPlugin {

    protected Heroes heroes = null;
    protected FileConfiguration config;
    
    @Override
    public void onDisable() {
        this.saveConfig();
        System.out.println("iSkin has been disabled");
    }

    @Override
    public void onEnable() {
        
        this.setupConfig();
        this.setupHeroes();
        this.getCommand("iskin").setExecutor(new CmdExecutor(this));
        
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, new PlayerListener () {
            
            @Override
            public void onPlayerJoin(PlayerJoinEvent ev) {
                String pl = ev.getPlayer().getName();
 
                if (!config.contains("Players." + pl)) {
                    config.set("Players." + pl + ".url", "http://s3.amazonaws.com/MinecraftSkins/Notch.png");
                    System.out.println(pl + " has been added to the config");
                    saveConfig();
                }

                // If priority == players then update the player on joining
                if (config.getString("Priority").equals("players")) {
                    updatePlayerSkin(ev.getPlayer().getName());
                }
                
                
                // If priority == groups then update that players skin with that of his groups
                else if (config.getString("Priority").equals("groups")) {
                    
                    Set<String> groups = config.getConfigurationSection("Groups").getKeys(false);
                    for (String g : groups) {
                        if (ev.getPlayer().hasPermission("iskin.group." + g)) {
                            SpoutManager.getAppearanceManager().setGlobalSkin(ev.getPlayer(), config.getString("Groups." + g + ".url"));
                        }
                    }
                }
                
                
                // If priority == heroes then update that players skin with that of his heroclass
                else if (config.getString("Priority").equals("heroes")) {
                    
                    Set<String> heroClasses = config.getConfigurationSection("Heroes").getKeys(false);
                    Hero h = heroes.getHeroManager().getHero(ev.getPlayer());
                    for(String hc : heroClasses) {
                        if (h.getHeroClass().getName().equals(hc)) {
                            SpoutManager.getAppearanceManager().setGlobalSkin(ev.getPlayer(), config.getString("Heroes." + hc + ".url"));
                        }
                    }
                }
                else {
                    config.set("Priority", "players");
                    saveConfig();
                }
            }
        }, Event.Priority.Normal, this);
        System.out.println("[iSkin] has been enabled");
    }
    
    private void setupConfig() {

        config = this.getConfig();
        config.options().header(" iSkin configuration file ");
        config.addDefault("Priority", "players");
        config.addDefault("Groups override players", true);
        config.addDefault("Players.testplayer.url", "http://s3.amazonaws.com/MinecraftSkins/Notch.png");
        config.addDefault("Groups.testgroup.url", "http://s3.amazonaws.com/MinecraftSkins/Notch.png");
        config.addDefault("Heroes.testclass.url", "http://s3.amazonaws.com/MinecraftSkins/Notch.png");
        config.options().copyDefaults(true);
        saveConfig();
    }
    
    
    public boolean setupHeroes() {
        Plugin p = this.getServer().getPluginManager().getPlugin("Heroes");
        if (p !=null) {
            heroes = (Heroes) p;
            // if plugin "Heroes" exists then Cast the heroes plugin over to heroes
        }
        return false;
    }
    
    public Heroes getHeroes() {
        return heroes;
        //returns the heroes plugin
    }
    
    public String getPlayerSkin(String p) {
        return this.config.getString("Players." + p + ".url");
    }
    
    public String getGroupSkin(String group) {
        return this.config.getString("Groups." + group + ".url");
    }
    
    public String getHeroesSkin(String hc) {
        return this.config.getString("Heroes." + hc + ".url");
    }
    
    // Check for people with multiple skins
    public void checkForMultipleSkins() {

        Set<String> playerKey = this.config.getConfigurationSection("Players").getKeys(false);
        Set<String> groupKey = this.config.getConfigurationSection("Groups").getKeys(false);
        Set<String> heroKey = this.config.getConfigurationSection("Heroes").getKeys(false);
        Player[] players = this.getServer().getOnlinePlayers();

        for (Player Opl : players) {
        for (String gK : groupKey) {
            if ((Opl.getPlayer().hasPermission("iskin.group." + gK)) && (!Opl.isOp() && !Opl.hasPermission("*"))) {
                for (String p : playerKey) {
                    if (p.contains(Opl.getName())) {
                        playerKey.remove(p);
                        this.saveConfig();
                        }
                    }
                }
            }
        }
        if (this.getHeroes() !=null) {
            for (String hK : heroKey) {
            for (String pK : playerKey) {
                if (hK.contains(pK)) {
                    playerKey.remove(pK);
                    this.saveConfig();
                    }
                } 
            }
        }   
    }

    
    // Updates a players skin
    public void updatePlayerSkin(String p) {
        this.checkForMultipleSkins();
        String url = this.getPlayerSkin(p);
        HumanEntity pl = this.getServer().getPlayer(p);
        SpoutManager.getAppearanceManager().setGlobalSkin(pl, url);
    }
    
    // Updates a groups skin
    public void updateGroupSkin() {
        
        Set<String> groups = this.config.getConfigurationSection("Groups").getKeys(false);
        Player[] pl = this.getServer().getOnlinePlayers();
        
        
        for (Player p : pl) {
            for (String g : groups) {
                if (p.hasPermission("iskin.group." + g)) {
                        this.saveConfig();
                        this.checkForMultipleSkins();
                        String url = this.getGroupSkin(g);
                        SpoutManager.getAppearanceManager().setGlobalSkin((HumanEntity)p, url);
                }
            }
        }
    }
    
    // Updates a HeroClass skin
    public void updateHeroSkin() {
        if (this.getHeroes() !=null) {
            this.checkForMultipleSkins();
            Collection<Hero> allHeroes = this.getHeroes().getHeroManager().getHeroes();
            Set<String> heroClasses = this.config.getConfigurationSection("Heroes").getKeys(false);
            for (Hero hero : allHeroes) {
            for (String hc : heroClasses) {
                if (hero.getHeroClass().getName().equals(hc)) {
                    String url = this.getHeroesSkin(hc);
                    SpoutManager.getAppearanceManager().setGlobalSkin((HumanEntity)hero.getPlayer(), url);
                }
            }
        }
    }
}

    public HashSet<OfflinePlayer> getAllExistingPlayers() {
        
        List<World> worlds = this.getServer().getWorlds();
        HashSet<OfflinePlayer> allPlayers = new HashSet<OfflinePlayer>();
        
            for (World w : worlds) {
                File f = new File(w.getName() + File.separator + "players" + File.separator);
                
            for (File playerFile: f.listFiles()) {
                allPlayers.add(this.getServer().getOfflinePlayer(playerFile.getName().substring(0 , playerFile.getName().length()-4)));
            }
        }
            return allPlayers;
    }
    
    
    // Help messages
    public void helpMessage1(CommandSender cs) {
	cs.sendMessage(ChatColor.GOLD+"-----------iSkin-----------");
	cs.sendMessage(ChatColor.GREEN + "/iskin reload");
	cs.sendMessage(ChatColor.GREEN + "/iskin help <page>");
	cs.sendMessage(ChatColor.GREEN + "/iskin setself <url>");
	cs.sendMessage(ChatColor.GREEN + "/iskin setself minecraft <playername>");
	cs.sendMessage(ChatColor.GREEN + "/iskin setplayer <playername> <url>");
	cs.sendMessage(ChatColor.GREEN + "/iskin setplayer <playername> minecraft <character>");
	cs.sendMessage(ChatColor.GOLD + "<> = enter details here");
	cs.sendMessage(ChatColor.GOLD + "Page 1 of 2");
    }
    
    public void helpMessage2(CommandSender cs) {
	cs.sendMessage(ChatColor.GOLD+"-----------iSkin-----------");
	cs.sendMessage(ChatColor.GREEN + "/iskin setgroup <groupname> <url>");
	cs.sendMessage(ChatColor.GREEN + "/iskin setgroup <groupname> minecraft <character>");
	cs.sendMessage(ChatColor.GREEN + "/iskin sethero <HeroClass> <url>");
	cs.sendMessage(ChatColor.GREEN + "/iskin sethero <HeroClass> minecraft <character>");
	cs.sendMessage(ChatColor.GOLD + "<> = enter details here");
	cs.sendMessage(ChatColor.GOLD + "Page 2 of 2");
    }
}
