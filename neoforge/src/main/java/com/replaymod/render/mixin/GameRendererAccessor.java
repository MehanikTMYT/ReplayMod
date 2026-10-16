package com.replaymod.render.mixin;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//#if MC>=12106
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.fog.FogRenderer;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;
//#endif

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    //#if MC<12106
    //$$ @Accessor
    //$$ boolean getRenderHand();
    //$$ @Accessor
    //$$ void setRenderHand(boolean value);
    //#endif

    //#if MC>=12106
    @Accessor("resourcePool")
    CrossFrameResourcePool getPool();
    @Accessor
    GuiRenderer getGuiRenderer();
    //#if MC >= 26.1
    @Accessor
    net.minecraft.client.renderer.state.GameRenderState getGameRenderState();
    //#else
    //$$ @Accessor
    //$$ net.minecraft.client.gui.render.state.GuiRenderState getGuiState();
    //#endif
    @Accessor
    FogRenderer getFogRenderer();
    //#endif
}
