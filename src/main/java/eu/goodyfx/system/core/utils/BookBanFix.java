package eu.goodyfx.system.core.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import eu.goodyfx.system.McRaspiSystem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;

public class BookBanFix {


    private final McRaspiSystem plugin;

    public BookBanFix(McRaspiSystem plugin) {
        this.plugin = plugin;
        checkBooks();
    }


    public void checkBooks() {
        plugin.getHookManager().getProtocolManager().addPacketListener(
                new PacketAdapter(plugin, ListenerPriority.HIGH,
                        PacketType.Play.Server.SET_SLOT,
                        PacketType.Play.Server.WINDOW_ITEMS) {

                    @Override
                    public void onPacketSending(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
                            ItemStack item = event.getPacket().getItemModifier().read(0);
                            if (sanitizeItem(item)) {
                                event.setCancelled(true);
                            }
                        } else {
                            List<ItemStack> items = event.getPacket().getItemListModifier().read(0);
                            for (ItemStack item : items) {
                                if (sanitizeItem(item)) {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }
        );
    }


    private boolean sanitizeItem(ItemStack item) {
        if (item == null) return false;

        if (item.getType() == Material.WRITTEN_BOOK ||
                item.getType() == Material.WRITABLE_BOOK) {

            BookMeta meta = (BookMeta) item.getItemMeta();
            if (meta == null) return false;

            List<String> safePages = new ArrayList<>();

            for (String page : meta.getPages()) {
                if (page == null) continue;

                // 1. Länge begrenzen
                if (page.length() > 256) {
                    page = page.substring(0, 256);
                }

                // 2. Problematische Zeichen entfernen
                page = page.replaceAll("[^\\x20-\\x7E\\n]", "");

                // 3. Optional: JSON komplett killen
                if (page.contains("{") || page.contains("}")) {
                    page = "[Blocked malformed content]";
                    return true;
                }
                safePages.add(page);
            }

            meta.setPages(safePages);
            item.setItemMeta(meta);
        }
        return false;
    }

}
