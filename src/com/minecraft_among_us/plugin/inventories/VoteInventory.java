package com.minecraft_among_us.plugin.inventories;

import com.minecraft_among_us.plugin.Plugin;
import com.minecraft_among_us.plugin.game.AmongUsPlayer;
import com.minecraft_among_us.plugin.game.Color;
import com.minecraft_among_us.plugin.game.Game;
import com.minecraft_among_us.plugin.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Vote inventory class.
 */
public class VoteInventory extends BaseInventory {

    private final AmongUsPlayer auCaller;

    /**
     * Creates a new vote inventory.
     *
     * @param auPlayer Linked player
     * @param auCaller Player who called the vote
     */
    public VoteInventory(AmongUsPlayer auPlayer, AmongUsPlayer auCaller) {
        super(auPlayer);
        this.auCaller = auCaller;
    }

    @Override
    public Inventory create() {
        Game game = Game.getInstance();
        List<AmongUsPlayer> auPlayers = new ArrayList<>(game.getPlayers());
        Collections.sort(auPlayers);
        boolean hasVoted = game.getCurrentVoteSystem().hasVoted(this.auPlayer);
        Inventory inventory = Bukkit.createInventory(null, 54, "Vote");

        int i = 0;
        for (AmongUsPlayer auPlayer : auPlayers) {
            Player player = (Player) auPlayer.toBukkitPlayer();

            ItemStack colorItem = new ItemStack(auPlayer.getColor().wool);
            ItemMeta colorItemMeta = colorItem.getItemMeta();
            colorItemMeta.setDisplayName(auPlayer.getColor().code + player.getName() + " (" + auPlayer.getColor().code + auPlayer.getColor().name + ')');
            colorItem.setItemMeta(colorItemMeta);

            ItemStack headItem;
            if (auPlayer.getHat() == null) {
                headItem = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta headItemMeta = (SkullMeta) headItem.getItemMeta();
                headItemMeta.setOwningPlayer(player);
                headItemMeta.setDisplayName(auPlayer.getColor().code + player.getName());
                headItem.setItemMeta(headItemMeta);
            } else {
                headItem = auPlayer.getHat();
                ItemMeta headItemMeta = headItem.getItemMeta();
                headItemMeta.setDisplayName(auPlayer.getColor().code + player.getName());
                headItem.setItemMeta(headItemMeta);
            }
            
            ItemStack voteItem;
            if (auPlayer.isAlive()) {
                if (hasVoted) {
                    voteItem = new ItemStack(Material.FILLED_MAP);
                } else {
                    voteItem = new ItemStack(Material.PAPER);
                }
                ItemMeta voteItemMeta = voteItem.getItemMeta();
                voteItemMeta.setDisplayName("Vote for this player");
                voteItemMeta.setLore(Arrays.asList(hasVoted ? "§cYou have already voted" : "§aClick to vote for this player"));
                voteItem.setItemMeta(voteItemMeta);
            } else {
                voteItem = new ItemStack(Material.BARRIER);
                ItemMeta voteItemMeta = voteItem.getItemMeta();
                voteItemMeta.setDisplayName("§cThis player is dead");
                voteItem.setItemMeta(voteItemMeta);
            }

            if (auPlayer.equals(this.auCaller)) {
                ItemStack callerItem = new ItemStack(Material.REDSTONE_TORCH);
                ItemMeta callerItemMeta = callerItem.getItemMeta();
                callerItemMeta.setDisplayName("This player is the vote caller");
                callerItem.setItemMeta(callerItemMeta);
                inventory.setItem(i + 3, callerItem);
            }

            inventory.setItem(i, colorItem);
            inventory.setItem(i + 1, headItem);
            inventory.setItem(i + 2, voteItem);
            i += 9;
        }

        ItemStack skipItem;
        if (hasVoted) {
            skipItem = new ItemStack(Material.FILLED_MAP);
        } else {
            skipItem = new ItemStack(Material.TRIPWIRE_HOOK);
        }
        ItemMeta skipItemMeta = skipItem.getItemMeta();
        skipItemMeta.setDisplayName("§7Skip vote");
        skipItemMeta.setLore(Arrays.asList(hasVoted ? "§cYou have already voted" : "§aClick to skip your vote"));
        skipItem.setItemMeta(skipItemMeta);
        inventory.setItem(49, skipItem);

        ItemStack separatorItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta separatorItemMeta = separatorItem.getItemMeta();
        separatorItemMeta.setDisplayName("-");
        separatorItem.setItemMeta(separatorItemMeta);
        inventory.setItem(4, separatorItem);
        inventory.setItem(13, separatorItem);
        inventory.setItem(22, separatorItem);
        inventory.setItem(31, separatorItem);
        inventory.setItem(40, separatorItem);
        inventory.setItem(45, separatorItem);
        inventory.setItem(46, separatorItem);
        inventory.setItem(47, separatorItem);
        inventory.setItem(48, separatorItem);
        inventory.setItem(50, separatorItem);
        inventory.setItem(51, separatorItem);
        inventory.setItem(52, separatorItem);
        inventory.setItem(53, separatorItem);

        return inventory;
    }


    /**
     * Listener subclass.
     */
    public static class Listener implements org.bukkit.event.Listener {

        /**
         * Event triggered when a player opens the vote inventory.
         *
         * @param e Event
         */
        @EventHandler
        public void onOpenVote(PlayerInteractEvent e) {
            Game game = Game.getInstance();
            if (game.getState().equals(GameState.VOTE_PROGRESS) && e.getHand().equals(EquipmentSlot.HAND) && e.getItem() != null && e.getItem().getType().equals(Material.PAPER)) {
                Player player = e.getPlayer();
                player.openInventory(new VoteInventory(AmongUsPlayer.getPlayer(player.getUniqueId()), game.getCurrentVoteSystem().getCaller()).create());
            }
        }

        /**
         * Event triggered when a player interacts with the vote inventory.
         *
         * @param e Event
         */
        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getView().getTitle().equals("Vote")) {
                e.setCancelled(true);
                if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                    Player player = (Player) e.getWhoClicked();
                    AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                    ItemStack currentItem = e.getCurrentItem();
                    if (currentItem != null) {
                        Game game = Game.getInstance();
                        Material currentMaterial = currentItem.getType();
                        if (currentMaterial.equals(Material.TRIPWIRE_HOOK)) {
                            player.closeInventory();
                            game.getCurrentVoteSystem().vote(auPlayer, null);
                            Bukkit.broadcastMessage(Plugin.getPluginNameChat() + auPlayer.getColor().code + player.getName() + " §rvoted");
                        } else if (currentMaterial.equals(Material.PAPER)) {
                            Color color = Color.getColorByWool(e.getClickedInventory().getItem(e.getSlot() - 2).getType());
                            AmongUsPlayer auVoted = AmongUsPlayer.getPlayerByColor(color);
                            player.closeInventory();
                            game.getCurrentVoteSystem().vote(auPlayer, auVoted);
                            Bukkit.broadcastMessage(Plugin.getPluginNameChat() + auPlayer.getColor().code + player.getName() + " §rvoted");
                        }
                    }
                }
            }
        }
    }
}
