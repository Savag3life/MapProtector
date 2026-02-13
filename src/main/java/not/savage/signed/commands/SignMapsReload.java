package not.savage.signed.commands;

import not.savage.signed.SignedMaps;
import not.savage.signed.util.KyoriString;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public record SignMapsReload(SignedMaps plugin) implements CommandExecutor {

    public SignMapsReload(final SignedMaps plugin) {
        this.plugin = plugin;
        plugin.getCommand("sign-map-reload").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!sender.hasPermission("signed.sign")) {
            sender.sendMessage(KyoriString.of(plugin.getConfig().getString("messages.no_permission")).color());
            return true;
        }

        plugin.reloadConfig();
        sender.sendMessage(KyoriString.of("<green>Sign-Maps config reloaded.").color());
        return true;
    }
}
