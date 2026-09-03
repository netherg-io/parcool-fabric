package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.registries.EventRegistry;
import com.alrex.parcool.fabric.RenderFrameEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Источник RenderFrameEvent: у NeoForge он шлётся вокруг этого же вызова в Minecraft.runTick. */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    public void onRenderPre(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        EventRegistry.getActionProcessor().onRenderTick(new RenderFrameEvent.Pre(deltaTracker));
    }
}
