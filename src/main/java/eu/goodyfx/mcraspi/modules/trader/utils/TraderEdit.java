package eu.goodyfx.mcraspi.modules.trader.utils;

import eu.goodyfx.mcraspi.core.database.RaspiPlayer;
import lombok.Getter;
import lombok.Setter;

@Getter
public class TraderEdit {

    private final String traderName;
    private final RaspiPlayer raspiPlayer;
    @Setter
    private boolean recipeEditActive = false;
    @Setter
    private int recipeID = 0;

    public TraderEdit(String traderName, RaspiPlayer editor) {
        this.traderName = traderName;
        this.raspiPlayer = editor;
    }

}
