package com.hyperknf.mmhelper.mixin;

import com.hyperknf.mmhelper.MMHelper;
import com.hyperknf.mmhelper.config.Config;
import com.hyperknf.mmhelper.config.ConfigManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Scoreboard.class)
public class ScoreboardMixin {
    @Inject(at = @At("HEAD"), method = "addObjective")
    private void onAddObjective(String name, ScoreboardCriterion criterion, Text displayName, ScoreboardCriterion.RenderType renderType, boolean displayAutoUpdate, NumberFormat numberFormat, CallbackInfoReturnable<ScoreboardObjective> info) {
        // Used for detecting active mini-game
        if (MMHelper.onHypixel) {
            String displayNameString = displayName.getString();
            if (displayNameString.equalsIgnoreCase("murder mystery")) {
                if (name.equalsIgnoreCase("prescoreboard") || name.equalsIgnoreCase("mmlobby"))
                    MMHelper.setCurrentLobby(MMHelper.HypixelLobbies.MurderMysteryLobby);
                else if (MMHelper.lobby != MMHelper.HypixelLobbies.MurderMystery && name.equalsIgnoreCase("murdermystery"))
                    MMHelper.setCurrentLobby(MMHelper.HypixelLobbies.MurderMystery);
            }
        }
    }
}
