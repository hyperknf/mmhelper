package com.hyperknf.mmhelper.mixin;

import com.hyperknf.mmhelper.MMHelper;
import com.hyperknf.mmhelper.config.ConfigManager;
import com.hyperknf.mmhelper.utils.MinecraftUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Inject(at = @At("HEAD"), method = "setStack")
    private void onSetStack(int slot, ItemStack stack, CallbackInfo info) {
        if (MMHelper.isActive() && !MMHelper.clientIsMurder)
            if (ConfigManager.getConfig().mm.isMurderItem(stack.getItem())) {
                MMHelper.clientIsMurder = true;
                MinecraftUtils.sendChatMessage("[MMHelper] " + Text.translatable("message.mm.client_is_murder").getString());
            }
    }
}
