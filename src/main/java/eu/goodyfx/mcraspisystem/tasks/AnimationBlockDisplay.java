package eu.goodyfx.mcraspisystem.tasks;

import com.destroystokyo.paper.ParticleBuilder;
import eu.goodyfx.mcraspisystem.McRaspiSystem;
import org.bukkit.Particle;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnimationBlockDisplay extends BukkitRunnable {

    public AnimationBlockDisplay(McRaspiSystem plugin) {
        this.runTaskTimerAsynchronously(plugin, 0, 1L); // Läuft alle 1 Ticks für eine flüssige Animation
    }

    private final static List<BlockDisplay> blockDisplayList = new ArrayList<>();

    private double tickCount = 0; // Zähler für die Sinuswellen-Berechnung
    private final double amplitude = 0.1; // Amplitude der Bewegung (maximale Höhe)
    private final double frequency = 0.1; // Frequenz der Bewegung (Geschwindigkeit)

    @Override
    public void run() {
        // Berechne die Verschiebung basierend auf der Sinuswelle
        double offset = amplitude * Math.sin(tickCount * frequency);
        checkDead();
        blockDisplayList.forEach(blockDisplay -> {

            Transformation transformation = blockDisplay.getTransformation();

            // Setze die Rotation des BlockDisplays (optional, hier um 5 Grad pro Tick)
            blockDisplay.setRotation(blockDisplay.getYaw() + 10, 0);

            // Wendet den neuen Offset für die glatte Auf- und Abwärtsbewegung an
            transformation.getTranslation().set(-0.25f, (float) offset + 1, -0.25f);
            blockDisplay.setTransformation(transformation);
            blockDisplay.getLocation().getWorld().spawnParticle(Particle.PORTAL, blockDisplay.getLocation().add(0,1, 0), 2, 0, 0, 0, .5, null, true);
        });

        // Aktualisiert den Zähler für die Sinusbewegung
        tickCount += 1;
    }

    private void killAll(){
        blockDisplayList.forEach(Entity::remove);
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        killAll();
        super.cancel();
    }


    private void checkDead() {
        blockDisplayList.removeIf(Objects::isNull);
    }

    public static List<BlockDisplay> getBlockDisplayList() {
        return blockDisplayList;
    }
}