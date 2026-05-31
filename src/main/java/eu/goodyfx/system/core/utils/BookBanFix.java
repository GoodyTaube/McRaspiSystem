package eu.goodyfx.system.core.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import eu.goodyfx.system.McRaspiSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
                            if (item != null) {
                                ItemStack sanitized = sanitizeItem(item);
                                event.getPacket().getItemModifier().write(0, sanitized);
                            }
                        } else {
                            List<ItemStack> items = event.getPacket().getItemListModifier().read(0);
                            if (items != null) {
                                List<ItemStack> sanatizedList = new ArrayList<>();
                                boolean isModded = false;

                                for (ItemStack item : items) {
                                    if (item != null) {
                                        ItemStack sanitized = sanitizeItem(item.clone());
                                        if (item != sanitized) {
                                            isModded = true;
                                        }
                                        sanatizedList.add(sanitized);
                                    } else {
                                        sanatizedList.add(null);
                                    }
                                }
                                if (isModded) {
                                    event.getPacket().getItemListModifier().write(0, sanatizedList);
                                }
                            }
                        }
                    }
                }
        );
    }


    private ItemStack sanitizeItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return item;

        if (item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.WRITABLE_BOOK) {
            if (item.getItemMeta() == null) return item;

            try {
                BookMeta meta = (BookMeta) item.getItemMeta();
                if (meta == null) {
                    return item;
                }

                boolean modified = false;
                List<Component> pages = meta.pages();
                List<Component> safePages = new ArrayList<>();

                int maxPages = Math.min(pages.size(), 72);
                for (int i = 0; i < maxPages; i++) {
                    Component page = pages.get(i);
                    if (page == null) continue;
                    String pageText = PlainTextComponentSerializer.plainText().serialize(page);

                    //Seitenlänge
                    if (pageText.length() > 256) {
                        pageText = pageText.substring(0, 256);
                        modified = true;
                    }

                    String sanitized = pageText.replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}\\n§<>]", "");
                    //SONDERZEICHEN
                    if (!sanitized.equals(pageText)) {
                        pageText = sanitized;
                        modified = true;
                    }

                    //JSON
                    long bracketCount = pageText.chars().filter(ch -> ch == '{').count();
                    if (bracketCount > 2 && pageText.contains("\"")) {
                        pageText = "<red>Inhalt Blockiert";
                        modified = true;
                    }
                    Component safePage = MiniMessage.miniMessage().deserialize(pageText);
                    safePages.add(safePage);
                }

                if (modified || pages.size() > maxPages) {
                    meta.pages(safePages);
                    item.setItemMeta(meta);
                    return item;
                }
            } catch (Exception e) {
                //Wenn ganz Korrupt
                ItemStack freshBook = item.withType(Material.BOOK);
                freshBook.setItemMeta(null);
                return freshBook;
            }
        }
        return item;
    }
}
