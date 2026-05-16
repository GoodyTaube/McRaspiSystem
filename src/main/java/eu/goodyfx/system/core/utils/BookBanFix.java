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
        if (item == null || item.getType() == Material.AIR) return false;

        if (item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.WRITABLE_BOOK) {
            BookMeta meta = (BookMeta) item.getItemMeta();
            if (meta == null) return false;

            boolean modified = false;
            List<String> pages = meta.getPages();
            List<String> safePages = new ArrayList<>();

            // Maximale Anzahl an Seiten begrenzen (Standard Minecraft ist 100)
            int maxPages = Math.min(pages.size(), 100);

            for (int i = 0; i < maxPages; i++) {
                String page = pages.get(i);
                if (page == null) continue;

                // 1. Länge pro Seite hart begrenzen (256 ist sicher)
                if (page.length() > 256) {
                    page = page.substring(0, 256);
                    modified = true;
                }

                // 2. Erlaubt Buchstaben (inkl. Umlaute), Zahlen, gängige Satzzeichen
                // \p{L} deckt alle Unicode-Buchstaben ab (Ä, Ö, Ü, ß, é, etc.)
                String sanitized = page.replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}\\n]", "");

                if (!sanitized.equals(page)) {
                    page = sanitized;
                    modified = true;
                }

                // 3. JSON-Exploit Schutz
                // Book-Bans nutzen oft verschachtelte JSON-Tags.
                // Wenn die Seite kein echtes JSON sein muss, blocken wir { }
                if (page.contains("{") && page.contains("\"")) {
                    page = "§c[Inhalt blockiert]";
                    modified = true;
                }

                safePages.add(page);
            }

            if (modified) {
                meta.setPages(safePages);
                item.setItemMeta(meta);
                return true; // Signalisiert, dass das Item geändert wurde
            }
        }
        return false;
    }
}
