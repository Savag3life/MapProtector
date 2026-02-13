package not.savage.signed.commands;

import net.kyori.adventure.text.Component;
import not.savage.signed.SignedMaps;
import not.savage.signed.util.KyoriString;
import not.savage.signed.util.Time;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record SignCommand(SignedMaps plugin) implements CommandExecutor {

    public SignCommand(final SignedMaps plugin) {
        this.plugin = plugin;
        plugin.getCommand("sign-map").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can sign maps.");
            return true;
        }

        if (!sender.hasPermission("signed.sign")) {
            sender.sendMessage(KyoriString.of(plugin.getConfig().getString("messages.no_permission")).color());
            return true;
        }

        final Player player = (Player) sender;
        final ItemStack toSign = player.getInventory().getItemInMainHand();
        if (toSign == null || toSign.getType() != Material.FILLED_MAP) {
            player.sendMessage(KyoriString.of(plugin.getConfig().getString("messages.not_map")).color());
            return true;
        }

        if (toSign.getItemMeta().getPersistentDataContainer().has(SignedMaps.SIGNED_BY, PersistentDataType.STRING)) {
            player.sendMessage(KyoriString.of(plugin.getConfig().getString("messages.already_signed")).color());
            return true;
        }

        final long signedAt = System.currentTimeMillis();
        toSign.editMeta(meta -> {
            meta.getPersistentDataContainer().set(SignedMaps.SIGNED_BY, PersistentDataType.STRING, player.getName());
            meta.getPersistentDataContainer().set(SignedMaps.SIGNED_BY_ID, PersistentDataType.STRING, player.getUniqueId().toString());
            meta.getPersistentDataContainer().set(SignedMaps.SIGNED_AT, PersistentDataType.LONG, signedAt);
        });

        if (plugin.getConfig().getBoolean("item.enchanted")) {
            toSign.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1);
            toSign.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (plugin.getConfig().getString("item.display_name") != null && !plugin.getConfig().getString("item.display_name").isEmpty()) {
            toSign.editMeta(meta -> {
                meta.displayName(KyoriString.of(plugin.getConfig().getString("item.display_name")).color());
            });
        }

        if (plugin.getConfig().getStringList("item.signature") != null &&
                !plugin.getConfig().getStringList("item.signature").isEmpty()) {
            toSign.editMeta(meta -> {
                List<Component> currentLore = meta.lore() == null ? new ArrayList<>() : new ArrayList<>(meta.lore());
                plugin.getConfig().getStringList("item.signature").forEach(line -> {
                    line = line.replace("%name%", player.getName());
                    line = line.replace("%date%", Time.formatSince(signedAt));
                    currentLore.add(KyoriString.of(line).color());
                });
                meta.lore(currentLore);
            });
        }

        player.sendMessage(KyoriString.of(plugin.getConfig().getString("messages.signed")).color());
        return true;
    }
}