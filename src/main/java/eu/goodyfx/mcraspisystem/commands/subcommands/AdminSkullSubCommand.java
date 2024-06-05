package eu.goodyfx.mcraspisystem.commands.subcommands;

import com.destroystokyo.paper.profile.PlayerProfile;
import eu.goodyfx.mcraspisystem.commands.SubCommand;
import eu.goodyfx.mcraspisystem.utils.RaspiPlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

public class AdminSkullSubCommand extends SubCommand {
    @Override
    public String getLabel() {
        return "skull";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public boolean commandPerform(RaspiPlayer player, String[] args) {
        if (args.length >= 2) {

            PlayerProfile profile = Bukkit.createProfile("Voting");
            ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            PlayerTextures textures = profile.getTextures();
            try {
                URL url = new URL("https://textures.minecraft.net/texture/" + args[1]);
                textures.setSkin(url);
                profile.setTextures(textures);
                meta.setPlayerProfile(profile);

                if (args[5] != null) {
                    meta.displayName(MiniMessage.miniMessage().deserialize(args[5]));
                }
                stack.setItemMeta(meta);
                if (isNumber(player, args[2]) && isNumber(player, args[3]) && isNumber(player, args[4])) {
                    Location location = new Location(Bukkit.getWorld("world"), convertDouble(args[2]), convertDouble(args[3]), convertDouble(args[4]));
                    location.getWorld().dropItem(location, stack);
                }
            } catch (MalformedURLException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Error while Getting HEAD!", e);
            }
        }
        return true;
    }

    private Double convertDouble(String arg) {
        return Double.parseDouble(arg);
    }

    private boolean isNumber(RaspiPlayer player, String arg) {
        try {
            Integer.valueOf(arg);
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage("Du Kek, Du musst eine Nummer eingeben!");
            return false;
        }
    }

}
