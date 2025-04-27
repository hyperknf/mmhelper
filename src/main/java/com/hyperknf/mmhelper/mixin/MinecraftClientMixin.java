package com.hyperknf.mmhelper.mixin;

import com.hyperknf.mmhelper.MMHelper;
import com.hyperknf.mmhelper.access.ArmorStandEntityMixinAccess;
import com.hyperknf.mmhelper.access.PlayerEntityMixinAccess;
import com.hyperknf.mmhelper.access.EntityMixinAccess;
import com.hyperknf.mmhelper.config.Config;
import com.hyperknf.mmhelper.config.ConfigManager;
import com.hyperknf.mmhelper.utils.MinecraftUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(at = @At("HEAD"), method = "joinWorld")
    private void onJoinWorld(ClientWorld world, CallbackInfo info) {
        if (!MMHelper.onHypixel) {
            ServerInfo entry = ((MinecraftClient)(Object)this).getCurrentServerEntry();
            if (entry != null) {
                MMHelper.onHypixel = entry.address.contains("hypixel");
                if (MMHelper.onHypixel)
                    MinecraftUtils.sendChatMessage("[MMHelper] " + Formatting.YELLOW + Text.translatable("message.mm.is_mm_lobby").getString());
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "setWorld")
    private void onSetWorld(ClientWorld world, CallbackInfo info) {
        MMHelper.setCurrentLobby(MMHelper.HypixelLobbies.None);
    }

    @Inject(at = @At("HEAD"), method = "hasOutline", cancellable = true)
    private void onHasOutline(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (MMHelper.isActive()) {
            Config config = ConfigManager.getConfig();

            if (entity instanceof PlayerEntity) {
                if (((PlayerEntityMixinAccess) entity).isRealPlayer())
                    if (!ConfigManager.getConfig().mm.shouldHighlightSpectators() && ((PlayerEntityMixinAccess) entity).isDeadSpectator())
                        ((EntityMixinAccess) entity).setGlowColor(-1);
                    else if (config.mm.shouldHighlightMurders() && ((PlayerEntityMixinAccess) entity).isMurder()) {
                        ((EntityMixinAccess) entity).setGlowColor(Config.MurderMystery.murderTeamColorValue);
                        info.setReturnValue(true);
                    } else if (config.mm.shouldHighlightDetectives(MMHelper.clientIsMurder) && ((PlayerEntityMixinAccess) entity).hasBow()) {
                        ((EntityMixinAccess) entity).setGlowColor(Config.MurderMystery.detectiveTeamColorValue);
                        info.setReturnValue(true);
                    } else if (config.mm.shouldHighlightInnocents(MMHelper.clientIsMurder))
                        info.setReturnValue(true);
            } else if (entity instanceof ItemEntity) {
                if (config.mm.shouldHighlightGold())
                    if (((ItemEntity)entity).getStack().getItem() == Items.GOLD_INGOT) {
                        ((EntityMixinAccess)entity).setGlowColor(Config.MurderMystery.goldTeamColorValue);
                        info.setReturnValue(true);
                    }
            } else if (entity instanceof ArmorStandEntity) {
                if (config.mm.shouldHighlightBows())
                    if (((ArmorStandEntityMixinAccess)entity).isHoldingDetectiveBow()) {
                        ((EntityMixinAccess)entity).setGlowColor(Config.MurderMystery.bowTeamColorValue);
                        info.setReturnValue(true);
                    }
            }
        }
    }
}
