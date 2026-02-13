package not.savage.signed.listener;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import not.savage.signed.SignedMaps;
import not.savage.signed.util.KyoriString;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.CartographyInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class CopyProtection implements Listener {

    private final SignedMaps plugin;
    private final Cache<@NotNull UUID, Long> cooldowns = Caffeine.newBuilder()
            .expireAfterWrite(Duration.of(2, ChronoUnit.SECONDS))
            .maximumSize(500)
            .build();

    public CopyProtection(SignedMaps plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void protectCrafting(PrepareItemCraftEvent event) {
        Arrays.stream(((CraftingInventory) event.getView().getTopInventory()).getMatrix())
                .filter(Objects::nonNull)
                .filter(ItemStack::hasItemMeta)
                .filter(i -> !i.getPersistentDataContainer().isEmpty())
                .filter(i -> i.getPersistentDataContainer().has(SignedMaps.SIGNED_BY, PersistentDataType.STRING))
                .findAny().ifPresent(ignored -> {
                    event.getInventory().setResult(null);
                    event.getViewers().forEach(this::sendCopyMessage);
                });
    }

    @EventHandler
    public void protectSmithing(PrepareSmithingEvent event) {
        final ItemStack mineral = event.getInventory().getInputMineral();
        if (mineral != null && mineral.hasItemMeta() &&
                mineral.getPersistentDataContainer().has(SignedMaps.SIGNED_BY, PersistentDataType.STRING)) {
            event.setResult(null);
            event.getViewers().forEach(this::sendCopyMessage);
            return;
        }

        final ItemStack equipment = event.getInventory().getInputEquipment();
        if (equipment != null && equipment.hasItemMeta() &&
                equipment.getPersistentDataContainer().has(SignedMaps.SIGNED_BY, PersistentDataType.STRING)) {
            event.setResult(null);
            event.getViewers().forEach(this::sendCopyMessage);
            return;
        }

        final ItemStack template = event.getInventory().getInputTemplate();
        if (template != null && template.hasItemMeta() &&
                template.getPersistentDataContainer().has(SignedMaps.SIGNED_BY, PersistentDataType.STRING)) {
            event.setResult(null);
            event.getViewers().forEach(this::sendCopyMessage);
        }
    }

    public void sendCopyMessage(HumanEntity player) {
        if (cooldowns.getIfPresent(player.getUniqueId()) == null) {
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
            player.sendMessage(KyoriString.of(plugin.getConfig().getString("messages.cannot_copy")).color());
        }
    }

    @EventHandler
    public void protectAnvil(PrepareAnvilEvent event) {
        if (event.getInventory().getContents().length < 2) return;
        final ItemStack inputOne = event.getInventory().getFirstItem();
        if (inputOne != null && inputOne.hasItemMeta() && inputOne.getPersistentDataContainer().has(SignedMaps.SIGNED_BY, PersistentDataType.STRING)) {
            event.setResult(null);
            event.getViewers().forEach(this::sendCopyMessage);
            return;
        }

        final ItemStack inputTwo = event.getInventory().getSecondItem();
        if (inputTwo != null && inputTwo.hasItemMeta() && inputTwo.getPersistentDataContainer().has(SignedMaps.SIGNED_BY, PersistentDataType.STRING)) {
            event.setResult(null);
            event.getViewers().forEach(this::sendCopyMessage);
        }
    }

    @EventHandler
    public void protectCartography(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof CartographyInventory &&
                event.getSlotType() == InventoryType.SlotType.RESULT) {
            if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() &&
                    event.getCurrentItem().getPersistentDataContainer().has(SignedMaps.SIGNED_BY, PersistentDataType.STRING)) {
                event.setCancelled(true);
                event.getViewers().forEach(this::sendCopyMessage);
            }
        }
    }

    public void unregister() {
        PrepareItemCraftEvent.getHandlerList().unregister(this);
        PrepareAnvilEvent.getHandlerList().unregister(this);
        PrepareSmithingEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
        cooldowns.invalidateAll();
    }
}
