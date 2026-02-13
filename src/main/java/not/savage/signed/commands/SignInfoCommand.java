package not.savage.signed.commands;

import not.savage.signed.SignedMaps;
import not.savage.signed.util.KyoriString;
import not.savage.signed.util.Placeholder;
import not.savage.signed.util.Time;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public record SignInfoCommand(SignedMaps plugin) implements CommandExecutor {

    public SignInfoCommand(final SignedMaps plugin) {
        this.plugin = plugin;
        plugin.getCommand("signature-info").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!sender.hasPermission("signed.signinfo")) {
            sender.sendMessage(KyoriString.of(plugin.getConfig().getString("messages.no_permission")).color());
            return true;
        }

        final Player player = (Player) sender;
        final ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType() != Material.FILLED_MAP) {
            player.sendMessage(KyoriString.of(plugin.getConfig().getString("messages.not_map")).color());
            return true;
        }

        if (!hand.hasItemMeta() || !hand.getItemMeta().getPersistentDataContainer().has(SignedMaps.SIGNED_BY, PersistentDataType.STRING)) {
            player.sendMessage(KyoriString.of(plugin.getConfig().getString("messages.not_signed")).color());
            return true;
        }

        final String signedBy = hand.getItemMeta().getPersistentDataContainer().get(SignedMaps.SIGNED_BY, PersistentDataType.STRING);
        final long signedAt = hand.getItemMeta().getPersistentDataContainer().get(SignedMaps.SIGNED_AT, PersistentDataType.LONG);
        final String ago = Time.formatSince(signedAt);
        final String dateString = Time.dateString(signedAt);

        plugin.getConfig().getStringList("messages.sign_info").forEach(line -> {
                    player.sendMessage(KyoriString.of(line).replaceAndColor(
                            Placeholder.of("name", signedBy),
                            Placeholder.of("date", dateString),
                            Placeholder.of("ago", ago)
                    ));
                }
        );

        return true;
    }
}
