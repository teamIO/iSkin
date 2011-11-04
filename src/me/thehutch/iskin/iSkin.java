package me.thehutch.iskin;

import com.herocraftonline.dev.heroes.Heroes;
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
        
        try {
            this.checkForMultipleSkins();
        } catch(Exception ex) {
            System.out.println(ex.getCause());
            
        }
        this.setupConfig();
        this.setupHeroes();
        this.getServer().getPluginManager();
        this.getCommand("iskin").setExecutor(new CmdExecutor(this));
        System.out.println("[iSkin] has been enabled");
    }
    
    private void setupConfig() {

        config = this.getConfig();
        config.options().header(" iSkin configuration file ");
        config.addDefault("Players.testplayer.url", "http://www.minecraft.net/skin/Notch.png");
        config.addDefault("Groups.testgroup.url", "http://www.minecraft.net/skin/Notch.png");
        config.addDefault("Heroes.testclass.url", "http://www.minecraft.net/skin/Notch.png");
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
    
    public String getHeroesSkin(String hero) {
        return this.config.getString("Heroes." + hero + ".url");
    }
    
    public void checkForMultipleSkins() {

        Set<String> playerKey = this.config.getConfigurationSection("Players").getKeys(false);
        Set<String> groupKey = this.config.getConfigurationSection("Groups").getKeys(false);
        Set<String> heroKey = this.config.getConfigurationSection("Heroes").getKeys(false);
        HashSet<OfflinePlayer> allPlayers = this.getAllExistingPlayers();

        for (OfflinePlayer Opl : allPlayers) {
        for (String gK : groupKey) {
            if (Opl.getPlayer().hasPermission("iskin.group." + gK)) {
                for (String p : playerKey) {
                    if (p.contains(Opl.getName())) {
                        playerKey.remove(p);
                        this.saveConfig();
                        }
                    }
                }
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
}
    
    // Updates a okayers skin
    public void updatePlayerSkin(String p) {
        this.saveConfig();
        this.checkForMultipleSkins();
        String url = this.getPlayerSkin(p);
        SpoutManager.getAppearanceManager().setGlobalSkin((HumanEntity)getServer().getPlayer(p), url);
    }
    
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
    
    public void updateHeroSkin() {
        Collection<Hero> allHeroes = this.getHeroes().getHeroManager().getHeroes();
        for (Hero hero : allHeroes) {
            
            this.saveConfig();
            this.checkForMultipleSkins();
            String url = this.getHeroesSkin(hero.toString());
            SpoutManager.getAppearanceManager().setGlobalSkin((HumanEntity)hero, url);
            }
        }
    
    

    
    public void updateHero(Player p) {
        
        if (this.getHeroes() == null) {
        }
        else {
            
        boolean cfg = config.getBoolean("Enabled Heroes support", true);
        String classname = this.getHeroes().getHeroManager().getHero(p).getHeroClass().getName();
        String url = this.config.getString("Heroes." + classname + ".url");
        SpoutManager.getAppearanceManager().setGlobalSkin((HumanEntity)p, url);
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
	cs.sendMessage(ChatColor.GREEN+"/iskin reload");
	cs.sendMessage(ChatColor.GREEN+"/iskin help <page>");
	cs.sendMessage(ChatColor.GREEN+"/iskin setself <url>");
	cs.sendMessage(ChatColor.GREEN+"/iskin setself minecraft <playername>");
	cs.sendMessage(ChatColor.GOLD+"<> = enter details here");
	cs.sendMessage(ChatColor.GOLD+"Page 1 of 2");
    }
    
    public void helpMessage2(CommandSender cs) {
	cs.sendMessage(ChatColor.GOLD+"-----------iSkin-----------");
	cs.sendMessage(ChatColor.GREEN+"/iskin setplayer <playername> <url>");
	cs.sendMessage(ChatColor.GREEN+"/iskin setplayer <playername> minecraft <character>");
	cs.sendMessage(ChatColor.GREEN+"/iskin setgroup <groupname> <url>");
	cs.sendMessage(ChatColor.GREEN+"/iskin setgroup <groupname> minecraft <character>");
	cs.sendMessage(ChatColor.GOLD+"Page 2 of 2");
    }
}
