package com.hyperknf.mmhelper.mixin;

import com.hyperknf.mmhelper.MMHelper;
import com.hyperknf.mmhelper.access.PlayerEntityMixinAccess;
import com.hyperknf.mmhelper.config.Config;
import com.hyperknf.mmhelper.config.ConfigManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(at = @At("HEAD"), method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", cancellable = true)
    public void onShouldRenderName(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> info) {
        if (MMHelper.isActive() && ConfigManager.getConfig().mm.shouldShowNameTags())
            if (livingEntity instanceof PlayerEntity && ((PlayerEntityMixinAccess)livingEntity).isRealPlayer())
                info.setReturnValue(true);
    }
}
