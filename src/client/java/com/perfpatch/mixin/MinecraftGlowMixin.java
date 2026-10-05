package com.perfpatch.mixin;

import com.perfpatch.PerfPatchConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Glowing outline: vanilla renders every glowing entity into a separate framebuffer
 * and runs a post-process blur over it. Skipping far/all entities avoids that work
 * (and avoids the whole outline pass when nothing is left to outline).
 *
 * Soft-fails (require = 0) if the target method is named differently in this version.
 */
@Mixin(Minecraft.class)
public abstract class MinecraftGlowMixin {
	@Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true, require = 0)
	private void perfpatch$limitGlow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		PerfPatchConfig cfg = PerfPatchConfig.get();
		switch (cfg.glowMode) {
			case OFF -> cir.setReturnValue(false);
			case NEAR -> {
				LocalPlayer player = Minecraft.getInstance().player;
				if (player != null && entity != player
					&& entity.distanceToSqr(player) > cfg.glowMaxDistanceSq) {
					cir.setReturnValue(false);
				}
			}
			default -> { }
		}
	}
}
