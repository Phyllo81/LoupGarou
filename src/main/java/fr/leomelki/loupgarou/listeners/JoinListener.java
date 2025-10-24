package fr.leomelki.loupgarou.listeners;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.wrappers.WrappedChatComponent;

import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import fr.leomelki.loupgarou.MainLg;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent.Reason;

public class JoinListener implements Listener{
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		WrapperPlayServerScoreboardTeam myTeam = new WrapperPlayServerScoreboardTeam();
		myTeam.setName(p.getName());
		myTeam.setPrefix(WrappedChatComponent.fromText(""));
		myTeam.setPlayers(Arrays.asList(p.getName()));
		myTeam.setMode(0);
		boolean noSpec = p.getGameMode() != GameMode.SPECTATOR;
		for(Player player : Bukkit.getOnlinePlayers())
			if(player != p) {
				if(player.getGameMode() != GameMode.SPECTATOR)
					player.hidePlayer(p);
				WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
				team.setName(player.getName());
				team.setPrefix(WrappedChatComponent.fromText(""));
				team.setPlayers(Arrays.asList(player.getName()));
				team.setMode(0);
				
				team.sendPacket(p);
				myTeam.sendPacket(player);
			}
		p.setFoodLevel(20);
		if(e.getJoinMessage() == null || !e.getJoinMessage().equals("joinall"))
			p.getPlayer().setResourcePack("http://leomelki.fr/mcgames/ressourcepacks/v32/loup_garou.zip");
		else {
			LGPlayer lgp = LGPlayer.thePlayer(e.getPlayer());
			lgp.showView();
			lgp.join(MainLg.getInstance().getCurrentGame());

			if(p.hasPermission("loupgarou.admin")) {

				ItemStack role = new ItemStack(Material.CHEST);
				ItemMeta roleM = role.getItemMeta();
				roleM.setDisplayName("§6§lRôles");
				role.setItemMeta(roleM);

				p.getInventory().setItem(3, role);

				ItemStack start = new ItemStack(Material.GOLD_NUGGET);
				ItemMeta startM = start.getItemMeta();
				startM.setDisplayName("§2§lStart");
				start.setItemMeta(startM);

				p.getInventory().setItem(5, start);

			}

		}
		if(noSpec)
			p.setGameMode(GameMode.ADVENTURE);
		e.setJoinMessage("");
		p.removePotionEffect(PotionEffectType.JUMP);
		p.removePotionEffect(PotionEffectType.INVISIBILITY);
		p.setWalkSpeed(0.2f);
	}
	@EventHandler
	public void onResoucePack(PlayerResourcePackStatusEvent e) {
		if(e.getStatus() == Status.SUCCESSFULLY_LOADED) {
			Player p = e.getPlayer();
			LGPlayer lgp = LGPlayer.thePlayer(p);
			lgp.showView();
			lgp.join(MainLg.getInstance().getCurrentGame());

			if(p.hasPermission("loupgarou.admin")) {

				ItemStack role = new ItemStack(Material.CHEST);
				ItemMeta roleM = role.getItemMeta();
				roleM.setDisplayName("§6§lRôles");
				role.setItemMeta(roleM);

				p.getInventory().setItem(3, role);

				ItemStack start = new ItemStack(Material.GOLD_NUGGET);
				ItemMeta startM = start.getItemMeta();
				startM.setDisplayName("§2§lStart");
				start.setItemMeta(startM);

				p.getInventory().setItem(5, start);

			}

			p.setPlayerListHeaderFooter("§4§lLoup-Garou\n", "\n§8Produit par §e§lLeomelki§8, §e§lShytoos §8& §e§lPhyllo_");

		}else if(e.getStatus() == Status.DECLINED || e.getStatus() == Status.FAILED_DOWNLOAD)
			e.getPlayer().kickPlayer(MainLg.getPrefix()+"§cIl vous faut le resourcepack pour jouer ! ("+e.getStatus()+")");
	}
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		LGPlayer lgp = LGPlayer.thePlayer(p);
		if(lgp.getGame() != null) {
			lgp.leaveChat();
			if(lgp.getRole() != null && !lgp.isDead())
				lgp.getGame().kill(lgp, Reason.DISCONNECTED, true);
			lgp.getGame().getInGame().remove(lgp);
			lgp.getGame().checkLeave();
		}
		LGPlayer.removePlayer(p);
		lgp.remove();
	}
	
}
