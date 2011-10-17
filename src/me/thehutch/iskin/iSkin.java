package me.thehutch.iskin;

import com.herocraftonline.dev.heroes.Heroes;
import java.io.File;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;



public class iSkin extends JavaPlugin {

    FileConfiguration config;
    File file = new File(this.getDataFolder() + File.separator + "config.yml");
    
    @Override
    public void onDisable() {
        System.out.println("iSkin has been disabled");
    }

    @Override
    public void onEnable() {
        
        this.setupConfig();
        this.setupHeroes();
        this.getServer().getPluginManager();
        this.getCommand("iskin").setExecutor(new CmdExecutor(this));
        System.out.println("[iSkin] has been enabled");
    }
    
    public void setupConfig() {
        
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }

        config = this.getConfig();
        config.options().header(" iSkin configuration file ");

        config.addDefault("Players.testplayer.url", "http://www.minecraft.net/skin/Notch.png");
        config.addDefault("Groups.testgroup.url", "http://www.minecraft.net/skin/Notch.png");
        config.addDefault("Enable Heroes support", true);
        config.addDefault("Heroes.testclass.url", "http://www.minecraft.net/skin/Notch.png");

        config.options().copyDefaults(true);
        saveConfig();
    }
    
    public void load() {
        try {
            this.config.load(this.getDataFolder() + File.separator + "config.yml");
        } catch (Exception ex) {
            ex.printStackTrace();
    }
} 
    
    
    public Heroes heroes = null;
    public boolean setupHeroes() {
        
        Plugin p = this.getServer().getPluginManager().getPlugin("Heroes");
        
        if (p !=null) {
            heroes = (Heroes) p;
            // if plugin "Heroes" does exist then Cast the heroes plugin over to heroes
        }
        return false;
    }
    
    
    public Heroes getHeroes() {
        return heroes;
        //returns the heroes plugin
    }
    
    
    public String getPlayerSkin(Player p) {
        
        ConfigurationSection section = config.getConfigurationSection("Groups");
        Set<String> groups = section.getKeys(false);
        
        if (groups == null || groups.size() < 1) {
            return null;
        }
        
        for (int x=0 ; x<groups.size() ; x++) {
        }
        Iterator<String> url = groups.iterator();
        
        if((p.hasPermission("icape.group." + url.toString())) && (!p.hasPermission("*"))) {
            return url.toString();
        }
        
        return null;
    }
    
    // how long you gonne be?
    //a few minutes,figured it out :)
    public void updateSkin(SpoutPlayer sp) {
        
        String url = config.getString("Players." + sp.getName() + ".url", "http://www.minecraft.net/skin/" + sp.getName() + ".png");
        
            if (url.equalsIgnoreCase("http://www.minecraft.net/skin/" + sp.getName() + ".png")) {
                saveConfig(); // save the default value
                
            if (this.getPlayerSkin((Player)sp) !=null && !url.equals("http://www.minecraft.net/skin/" + sp.getName() + ".png")) {
                url = getPlayerSkin(sp);

            SpoutManager.getAppearanceManager().setGlobalSkin((HumanEntity)sp, url);
            }
        }
    }
    
    
    public void updateHero(Player p) {
        
        if (this.heroes == null) {
            return;
        }
        
        boolean cfg = config.getBoolean("Enabled Heroes support", true);
        
        if (cfg == false) {
            return;
        }
        else {
            
            String classname = this.heroes.getHeroManager().getHero(p).getHeroClass().getName();
            
            String url = this.config.getString("Heroes." + classname + ".url", "http://www.minecraft.net/skin/" + p + "Notch.png");
            SpoutManager.getAppearanceManager().setGlobalSkin((HumanEntity)p, url);
        }
        
        
    }
    
    
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
