package fr.leomelki.loupgarou.classes.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ArenaConfig {

    private final String arenaName;

    private final FileConfiguration config;

    public ArenaConfig(String arenaName, FileConfiguration config) {

        this.arenaName = arenaName;

        this.config = config;

    }

    public String getArenaName() {

        return arenaName;

    }

    public int getRoleCount(String roleName) {

        return config.getInt("arenas." + arenaName + ".roles." + roleName, 0);

    }

    public List<String> getSpawns() {

        return config.getStringList("arenas." + arenaName + ".spawns");

    }

    public String getLobbyLocation() {

        return config.getString("arenas." + arenaName + ".lobby");

    }

}
