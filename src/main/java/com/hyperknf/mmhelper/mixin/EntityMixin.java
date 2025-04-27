package com.hyperknf.mmhelper.mixin;

import com.hyperknf.mmhelper.MMHelper;
import com.hyperknf.mmhelper.access.EntityMixinAccess;
import com.hyperknf.mmhelper.config.Config;
import com.hyperknf.mmhelper.config.ConfigManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin implements EntityMixinAccess {
    private int glowColor = -1;

    @Unique @Override
    public void setGlowColor(int color) {
        glowColor = color;
    }

    @Inject(at = @At("HEAD"), method = "getTeamColorValue", cancellable = true)
    private void onGetTeamColorValue(CallbackInfoReturnable<Integer> info) {
        if (MMHelper.isActive() && glowColor >= 0)
            info.setReturnValue(glowColor);
    }
}
