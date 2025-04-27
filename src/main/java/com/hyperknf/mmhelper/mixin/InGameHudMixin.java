package com.hyperknf.mmhelper.mixin;

import com.hyperknf.mmhelper.MMHelper;
import com.hyperknf.mmhelper.utils.MinecraftUtils;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(at = @At("RETURN"), method = "setTitle")
    private void onSetTitle(Text title, CallbackInfo info) {
        if (MMHelper.isEnabled()) {
            String s = title.getString().split("\n")[0].toLowerCase();
            if (s.startsWith("you win") || s.startsWith("you lose")) {
                MMHelper.roundEnded = true;
                MinecraftUtils.sendChatMessage("[MMHelper] Round ended");
            }
        }
    }
}
