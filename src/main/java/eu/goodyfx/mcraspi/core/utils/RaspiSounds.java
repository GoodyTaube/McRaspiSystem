package eu.goodyfx.mcraspi.core.utils;

import lombok.Getter;
import org.bukkit.Sound;

@Getter
public enum RaspiSounds {

    ERROR(Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f),
    SUCCESS(Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f),
    WARNING(Sound.ENTITY_ITEM_BREAK, 1f, 1f);

    private final Sound sound;
    private final float volume;
    private final float pitch;

    RaspiSounds(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

}
