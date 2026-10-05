package com.perfpatch.mixin;

import com.perfpatch.PerfPatchConfig;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Enchantment glint off (covers armor and other paths that ask the stack directly). */
@Mixin(ItemStack.class)
public abstract class ItemStackFoilMixin {
	@Inject(method = "hasFoil", at = @At("HEAD"), cancellable = true, require = 0)
	private void perfpatch$noFoil(CallbackInfoReturnable<Boolean> cir) {
		if (PerfPatchConfig.get().glintMode == PerfPatchConfig.GlintMode.OFF) {
			cir.setReturnValue(false);
		}
	}
}
