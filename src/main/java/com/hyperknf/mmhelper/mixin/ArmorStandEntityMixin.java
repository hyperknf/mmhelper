package com.hyperknf.mmhelper.mixin;

import com.hyperknf.mmhelper.access.ArmorStandEntityMixinAccess;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EulerAngle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandEntity.class)
public class ArmorStandEntityMixin implements ArmorStandEntityMixinAccess {
    private boolean _isHoldingDetectiveBow = false;
    private float lastRightArmYaw = Float.NEGATIVE_INFINITY;

    @Unique @Override
    public boolean isHoldingDetectiveBow() {
        return _isHoldingDetectiveBow;
    }

    @Inject(at = @At("HEAD"), method = "setRightArmRotation")
    private void onSetRightArmRotation(EulerAngle angle, CallbackInfo info) {
        if (lastRightArmYaw == Float.NEGATIVE_INFINITY)
            lastRightArmYaw = angle.yaw();

        if (!_isHoldingDetectiveBow && Math.abs(lastRightArmYaw - angle.yaw()) >= 0.1) {
            ArmorStandEntity self = (ArmorStandEntity)(Object)this;
            if (self.getOffHandStack().getItem() == Items.BOW || self.getMainHandStack().getItem() == Items.BOW) {
                _isHoldingDetectiveBow = true;
            }
            lastRightArmYaw = angle.yaw();
        }
    }
}
