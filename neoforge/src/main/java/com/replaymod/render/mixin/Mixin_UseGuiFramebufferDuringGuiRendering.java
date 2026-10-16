package com.replaymod.render.mixin;

import com.replaymod.render.hooks.FramebufferDelegateHolder;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.pipeline.RenderTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 26.2
//$$ import net.minecraft.client.renderer.GameRenderer;
//#endif

//#if MC >= 26.2
//$$ @Mixin(GameRenderer.class)
//#else
@Mixin(Minecraft.class)
//#endif
public abstract class Mixin_UseGuiFramebufferDuringGuiRendering implements FramebufferDelegateHolder {

    @Unique
    private RenderTarget framebufferDelegate;

    @Override
    public void setFramebufferDelegate(RenderTarget framebuffer) {
        this.framebufferDelegate = framebuffer;
    }

    //#if MC >= 26.2
    //$$ @Inject(method = "mainRenderTarget", at = @At("HEAD"), cancellable = true)
    //#else
    @Inject(method = "getMainRenderTarget", at = @At("HEAD"), cancellable = true)
    //#endif
    private void useGuiFramebuffer(CallbackInfoReturnable<RenderTarget> ci) {
        RenderTarget delegate = this.framebufferDelegate;
        if (delegate != null) {
            ci.setReturnValue(delegate);
        }
    }
}
