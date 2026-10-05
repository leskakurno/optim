package com.perfpatch.mixin;

import com.perfpatch.PerfPatchConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Item model layers carry their own foil type in newer versions. Cancelling the setter
 * leaves the default (no foil). Targeted by string so a renamed class cannot break compilation.
 */
@Mixin(targets = "net.minecraft.client.renderer.item.ItemStackRenderState$LayerRenderState")
public abstract class LayerRenderStateFoilMixin {
	@Inject(method = "setFoilType", at = @At("HEAD"), cancellable = true, require = 0)
	private void perfpatch$noFoilType(CallbackInfo ci) {
		if (PerfPatchConfig.get().glintMode == PerfPatchConfig.GlintMode.OFF) {
			ci.cancel();
		}
	}
}
