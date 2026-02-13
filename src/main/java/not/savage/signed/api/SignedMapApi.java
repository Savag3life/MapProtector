package not.savage.signed.api;

import lombok.NonNull;
import not.savage.signed.SignedMaps;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

class SignedMapApi {

    /**
     * @param name The name of the player who signed a given map
     * @param id Player UUID of the player who signed a given map
     * @param timestamp Unix timestamp of when the map was signed
     */
    public record Signature(String name, String id, long timestamp) {
        public static Signature empty() {
            return new Signature(null, null, 0);
        }
        public boolean isEmpty() {
            return name == null || id == null || timestamp == 0;
        }
    }

    /**
     * Quick check if an item is signed.
     * @param item Item to verify against
     * @return True if the item is signed, false otherwise
     */
    public static boolean isSigned(org.bukkit.inventory.ItemStack item) {
        return item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(SignedMaps.SIGNED_BY, PersistentDataType.STRING);
    }

    /**
     * Return the signature details for a given signed object.
     * @param item Item to get the signature from
     * @return Signature details, or Signature.empty() if not signed
     */
    public static @NonNull Signature getSignature(ItemStack item) {
        if (!isSigned(item)) return Signature.empty();
        final ItemMeta meta = item.getItemMeta();
        final String name = meta.getPersistentDataContainer().get(SignedMaps.SIGNED_BY, PersistentDataType.STRING);
        if (name == null) return Signature.empty();
        if (!meta.getPersistentDataContainer().has(SignedMaps.SIGNED_BY_ID, PersistentDataType.STRING)) return Signature.empty();
        final String id = meta.getPersistentDataContainer().get(SignedMaps.SIGNED_BY_ID, PersistentDataType.STRING);
        if (id == null) return Signature.empty();
        if (!meta.getPersistentDataContainer().has(SignedMaps.SIGNED_AT, PersistentDataType.LONG)) return Signature.empty();
        final long timestamp = meta.getPersistentDataContainer().get(SignedMaps.SIGNED_AT, PersistentDataType.LONG);
        return new Signature(name, id, timestamp);
    }

}