package fr.leomelki.loupgarou.classes;

import fr.leomelki.loupgarou.MainLg;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LGGameManager {

    private final Map<String, LGGame> arenas = new HashMap<>();

    public void loadGames() {

        arenas.clear();

        ConfigurationSection section = MainLg.getInstance().getConfig().getConfigurationSection("arenas");

        if(section == null) return;

        for(String name : section.getKeys(false)) {

            ConfigurationSection arenaSection = section.getConfigurationSection(name);

            if(arenaSection == null) continue;

            int maxPlayers = arenaSection.getInt("maxPlayers", 8);

            LGGame game = new LGGame(name, maxPlayers);

            ConfigurationSection rolesSection = arenaSection.getConfigurationSection("roles");

            if(rolesSection != null) {

                for (String roleName : rolesSection.getKeys(false)) {

                    int count = rolesSection.getInt(roleName, 0);

                    game.getRoleDistribution().put(roleName, count);

                }

            }

            arenas.put(name, game);

            MainLg.getInstance().getLogger().info("✅ Arène chargée : " + name + " (" + maxPlayers + " joueurs)");

        }

    }

    public Map<String, LGGame> getMapArenas() {

        return arenas;

    }

    public LGGame getArena(String name) {

        return arenas.get(name);

    }

    public Collection<LGGame> getArenas() {

        return arenas.values();

    }

}
