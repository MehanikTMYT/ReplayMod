package com.replaymod.render.mixin;

import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(SectionOcclusionGraph.class)
public interface ChunkRenderingDataPreparerAccessor {
    @Accessor("viewArea")
    ViewArea builtChunkStorage();

    @Accessor("needsFullUpdate")
    boolean shouldUpdate();

    @Accessor("fullUpdateTask")
    Future<?> fullUpdateFuture();
}
