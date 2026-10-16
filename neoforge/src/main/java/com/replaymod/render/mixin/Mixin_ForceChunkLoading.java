package com.replaymod.render.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.replaymod.render.hooks.ForceChunkLoadingHook;
import com.replaymod.render.hooks.IForceChunkLoading;
import com.replaymod.render.utils.FlawlessFrames;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//#if MC >= 26.1
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//#endif

@Mixin(LevelRenderer.class)
public abstract class Mixin_ForceChunkLoading implements IForceChunkLoading {
    private ForceChunkLoadingHook replayModRender_hook;

    //#if MC>=12109
    private static final String SETUP_TERRAIN = "Lnet/minecraft/client/renderer/LevelRenderer;cullTerrain(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;Z)V";
    //#else
    //$$ private static final String SETUP_TERRAIN = "Lnet/minecraft/client/render/WorldRenderer;setupTerrain(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/Frustum;ZZ)V";
    //#endif

    @Override
    public void replayModRender_setHook(ForceChunkLoadingHook hook) {
        this.replayModRender_hook = hook;
    }

    @Shadow private SectionRenderDispatcher sectionRenderDispatcher;

    @Shadow @Final private SectionOcclusionGraph sectionOcclusionGraph;

    //#if MC >= 26.1
    //#elseif MC>=12109
    //$$ @Shadow protected abstract void updateCamera(Camera par1, Frustum par2, boolean par3);
    //#else
    //$$ @Shadow protected abstract void setupTerrain(Camera par1, Frustum par2, boolean par3, boolean par4);
    //$$
    //$$ @Shadow private Frustum frustum;
    //$$
    //$$ @Shadow private Frustum capturedFrustum;
    //#endif

    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract void applyFrustum(Frustum par1);

    //#if MC >= 26.1
    @WrapMethod(method = "update")
    private void forceAllChunks(Camera camera, Operation<Void> original) {
    //#else
    //$$ @Inject(method = "render", at = @At(value = "INVOKE", target = SETUP_TERRAIN))
    //$$ private void forceAllChunks(
    //$$         CallbackInfo ci,
            //#if MC>=12109
            //$$ @Local Frustum frustum,
            //#endif
    //$$         @Local(argsOnly = true) Camera camera
    //$$ ) {
    //#endif
        if (replayModRender_hook == null || FlawlessFrames.hasSodium()) {
            //#if MC >= 26.1
            original.call(camera);
            //#endif
            return;
        }

        assert this.minecraft.player != null;

        SectionOcclusionGraph renderingData = this.sectionOcclusionGraph;
        ChunkRenderingDataPreparerAccessor renderingDataAcc = (ChunkRenderingDataPreparerAccessor) renderingData;
        RenderRegionCache chunkRendererRegionBuilder = new RenderRegionCache();

        do {
            boolean areWeDoneYet = true;

            // Determine which chunks shall be visible
            //#if MC >= 26.1
            original.call(camera);
            //#elseif MC>=12109
            //$$ updateCamera(camera, frustum, this.client.player.isSpectator());
            //#else
            //$$ setupTerrain(camera, this.frustum, this.capturedFrustum != null, this.client.player.isSpectator());
            //#endif

            // Wait for async processing to be complete
            Future<?> fullUpdateFuture = renderingDataAcc.fullUpdateFuture();
            if (fullUpdateFuture != null) {
                try {
                    fullUpdateFuture.get(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }

            //#if MC < 26.1
            //$$ // If that async processing did change the chunk graph, we need to re-apply the frustum (otherwise this is
            //$$ // only done in the next setupTerrain call, which not happen this frame)
            //$$ if (renderingData.updateFrustum()) {
            //$$     this.applyFrustum((new Frustum(frustum)).coverBoxAroundSetPosition(8)); // call based on the one in setupTerrain
            //$$ }
            //#endif

            // Schedule all chunks which need rebuilding (we schedule even important rebuilds because we wait for
            // all of them anyway and this way we can take advantage of threading)
            for (SectionRenderDispatcher.RenderSection builtChunk : renderingDataAcc.builtChunkStorage().sections) {
                if (!builtChunk.isDirty()) {
                    continue;
                }
                // MC sometimes schedules invalid chunks when you're outside of loaded chunks (e.g. y > 256)
                if (builtChunk.hasAllNeighbors()) {
                    //#if MC>=12106
                    builtChunk.rebuildSectionAsync(chunkRendererRegionBuilder);
                    //#else
                    //$$ builtChunk.scheduleRebuild(this.chunkBuilder, chunkRendererRegionBuilder);
                    //#endif
                    areWeDoneYet = false;
                }
                builtChunk.setNotDirty();
            }

            // Upload all chunks
            if (((ForceChunkLoadingHook.IBlockOnChunkRebuilds) this.sectionRenderDispatcher).uploadEverythingBlocking()) {
                areWeDoneYet = false;
            }

            // Repeat until no more updates are needed
            if (!areWeDoneYet) {
                renderingData.invalidate(); // sets shouldUpdate to true
            }
        } while (renderingDataAcc.shouldUpdate());
    }
}
