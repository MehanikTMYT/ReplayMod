package com.replaymod.render.hooks;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.pipeline.RenderTarget;

public interface FramebufferDelegateHolder {
    void setFramebufferDelegate(RenderTarget framebuffer);

    static FramebufferDelegateHolder get(Minecraft mc) {
        //#if MC >= 26.2
        //$$ return (FramebufferDelegateHolder) mc.gameRenderer;
        //#else
        return (FramebufferDelegateHolder) mc;
        //#endif
    }
}
