package not.savage.signed;

import lombok.Getter;
import not.savage.signed.commands.SignCommand;
import not.savage.signed.commands.SignInfoCommand;
import not.savage.signed.commands.SignMapsReload;
import not.savage.signed.listener.CopyProtection;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class SignedMaps extends JavaPlugin {

    public static NamespacedKey SIGNED_BY = new NamespacedKey("signed", "signed_by");
    public static NamespacedKey SIGNED_BY_ID = new NamespacedKey("signed", "signed_by_id");
    public static NamespacedKey SIGNED_AT = new NamespacedKey("signed", "signed_at");

    @Getter private CopyProtection listener;

    @Override
    public void onEnable() {
        getLogger().info("Loading SignedMaps...");
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        saveDefaultConfig();
        reloadConfig();

        new SignCommand(this);
        new SignInfoCommand(this);
        new SignMapsReload(this);

        listener = new CopyProtection(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Unloading SignedMaps...");
        listener.unregister();
    }
}
