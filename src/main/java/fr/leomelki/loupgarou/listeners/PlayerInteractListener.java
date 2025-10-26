package fr.leomelki.loupgarou.listeners;

import fr.leomelki.loupgarou.MainLg;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.classes.config.ArenaConfig;
import fr.leomelki.loupgarou.roles.Role;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Constructor;
import java.util.*;

public class PlayerInteractListener implements Listener {

    @Getter
    private Map<String, Constructor<? extends Role>> roles = new HashMap<>();

    private Map<UUID, Integer> rolesGestionJoueursPage = new HashMap<>();

    public PlayerInteractListener(Map<String, Constructor<? extends Role>> roles) {

        this.roles = roles;

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();

        ItemStack it = e.getItem();

        if(it == null) return;

        if(it.getType().equals(Material.CHEST) && it.getItemMeta().getDisplayName().equalsIgnoreCase("§6§lRôles")) {

            e.setCancelled(true);

            if(!p.hasPermission("loupgarou.admin")) return;

            guiRole(p);

            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.2f);

        } else if(it.getType() == Material.GOLD_NUGGET && it.getItemMeta().getDisplayName().equalsIgnoreCase("§2§lStart")) {

            e.setCancelled(true);

            if(!p.hasPermission("loupgarou.admin")) return;

            Bukkit.dispatchCommand(p, "lg start " + p.getName());

            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.3f);

        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();

        Inventory inv = e.getInventory();

        ItemStack it = e.getCurrentItem();

        if(it == null) return;

        if((it.getType() == Material.CHEST && it.getItemMeta().getDisplayName().equalsIgnoreCase("§6§lRôles")) ||
                (it.getType() == Material.GOLD_NUGGET && it.getItemMeta().getDisplayName().equalsIgnoreCase("§2§lStart"))) {

            e.setCancelled(true);

        }

        if(e.getView().getTitle().equals("Rôles")) {

            if(!p.hasPermission("loupgarou.admin")) return;

            e.setCancelled(true);

            int index = 0;

            Integer n = null;

            int page = rolesGestionJoueursPage.getOrDefault(p.getUniqueId(), 0);

            if(it.getType() == Material.STICK && e.getSlot() == 45 && page > 0) {

                rolesGestionJoueursPage.put(p.getUniqueId(), page - 1);

                guiRole(p);

                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.2f);

            } else if(it.getType() == Material.STICK && e.getSlot() == 53 && (page + 1) * 28 < getRoles().size()) {

                rolesGestionJoueursPage.put(p.getUniqueId(), page + 1);

                guiRole(p);

                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.2f);

            }/* else if(it.getType() == Material.GOLD_NUGGET && it.getItemMeta().getDisplayName().equalsIgnoreCase("§2§lValider")) {

                for(Player players : Bukkit.getOnlinePlayers())
                    Bukkit.getPluginManager().callEvent(new PlayerQuitEvent(players, "joinall"));

                for(Player players : Bukkit.getOnlinePlayers())
                    Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(players, "joinall"));

                p.closeInventory();

                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);

            }*/ else if(it.getType() == Material.EGG || it.getType() == Material.DRAGON_EGG) {

                if(e.isLeftClick()) {

                    for(String role : getRoles().keySet()) {

                        if(role.equals(e.getCurrentItem().getItemMeta().getDisplayName().replace("§r", ""))) {

                            LGPlayer lgp = LGPlayer.thePlayer(p);

                            ArenaConfig arena = lgp.getGame().getArenaConfig();

                            n = arena.getRoleCount(role);

                            Bukkit.dispatchCommand(p, "lg roles set " + index + " " + (n + 1));

                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.3f);

                            break;

                        }

                        index++;

                    }

                } else if(e.isRightClick()) {

                    for(String role : getRoles().keySet()) {

                        if(role.equals(e.getCurrentItem().getItemMeta().getDisplayName().replace("§r", ""))) {

                            LGPlayer lgp = LGPlayer.thePlayer(p);

                            ArenaConfig arena = lgp.getGame().getArenaConfig();

                            n = arena.getRoleCount(role);

                            if(n > 0) {

                                Bukkit.dispatchCommand(p, "lg roles set " + index + " " + (n - 1));

                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.8f);

                            }

                            break;

                        }

                        index++;

                    }

                }

                LGPlayer lgp = LGPlayer.thePlayer(p);

                ArenaConfig arena = lgp.getGame().getArenaConfig();

                int amount = arena.getRoleCount(it.getItemMeta().getDisplayName().replace("§r", ""));

                ItemStack roleItem;

                if(amount == 0) {

                    roleItem = new ItemStack(Material.EGG);

                } else {

                    roleItem = new ItemStack(Material.DRAGON_EGG);

                }

                roleItem.setAmount(Math.max(1, Math.min(64, amount)));

                ItemMeta roleM = roleItem.getItemMeta();
                roleM.setDisplayName("§r" + it.getItemMeta().getDisplayName());
                List<String> lore = new ArrayList<>();
                lore.add("§7Quantité : §e" + amount);
                lore.add("§8Clic gauche: +1 | clic droit: -1");
                roleM.setLore(lore);
                roleItem.setItemMeta(roleM);

                inv.setItem(e.getSlot(), roleItem);

            }

        }

    }

    private void guiRole(Player player) {

        Inventory gui = Bukkit.createInventory(null, 54, "Rôles");

        ItemStack glassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassPaneM = glassPane.getItemMeta();
        glassPaneM.setDisplayName("§1");
        glassPane.setItemMeta(glassPaneM);

        gui.setItem(45, glassPane);
        gui.setItem(46, glassPane.clone());
        gui.setItem(47, glassPane.clone());
        gui.setItem(48, glassPane.clone());
        gui.setItem(49, glassPane.clone());
        gui.setItem(50, glassPane.clone());
        gui.setItem(51, glassPane.clone());
        gui.setItem(52, glassPane.clone());
        gui.setItem(53, glassPane.clone());

        /*ItemStack valider = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta validerM = valider.getItemMeta();
        validerM.setDisplayName("§2§lValider");
        valider.setItemMeta(validerM);

        gui.setItem(49, valider);*/

        int[] slots = new int[45];

        for(int s = 0; s < 45; s++) slots[s] = s;

        int page = rolesGestionJoueursPage.getOrDefault(player.getUniqueId(), 0);
        int pageSize = slots.length;

        List<String> roleNames = new ArrayList<>(getRoles().keySet());

        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, getRoles().size());

        for(int i = startIndex; i < endIndex; i++) {

            int slotIndex = i - startIndex;

            String roleName = roleNames.get(i);

            LGPlayer lgp = LGPlayer.thePlayer(player);

            ArenaConfig arena = lgp.getGame().getArenaConfig();

            int amount = arena.getRoleCount(roleName);

            ItemStack roleItem;

            if(amount == 0) {

                roleItem = new ItemStack(Material.EGG);

            } else {

                roleItem = new ItemStack(Material.DRAGON_EGG);

            }

            roleItem.setAmount(Math.max(1, Math.min(64, amount)));

            ItemMeta roleM = roleItem.getItemMeta();
            roleM.setDisplayName("§r" + roleName);
            List<String> lore = new ArrayList<>();
            lore.add("§7Quantité : §e" + amount);
            lore.add("§8Clic gauche: +1 | clic droit: -1");
            roleM.setLore(lore);
            roleItem.setItemMeta(roleM);

            gui.setItem(slotIndex, roleItem);

        }

        if(page > 0) {

            ItemStack precedente = new ItemStack(Material.STICK);
            ItemMeta precedenteM = precedente.getItemMeta();
            precedenteM.setDisplayName("§7<-");
            precedente.setItemMeta(precedenteM);

            gui.setItem(45, precedente);

        }

        if(endIndex < getRoles().size()) {

            ItemStack suivante = new ItemStack(Material.STICK);
            ItemMeta suivanteM = suivante.getItemMeta();
            suivanteM.setDisplayName("§7->");
            suivante.setItemMeta(suivanteM);

            gui.setItem(53, suivante);

        }

        player.openInventory(gui);

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {

        Player p = e.getPlayer();

        ItemStack it = e.getItemDrop().getItemStack();

        if(p.hasPermission("loupgarou.admin")) {

            if((it.getType() == Material.CHEST && it.getItemMeta().getDisplayName().equalsIgnoreCase("§6§lRôles")) ||
                    (it.getType() == Material.GOLD_NUGGET && it.getItemMeta().getDisplayName().equalsIgnoreCase("§2§lStart"))) {

                e.setCancelled(true);

            }

        }

    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {

        e.setCancelled(true);

        e.setFoodLevel(20);

    }

}
