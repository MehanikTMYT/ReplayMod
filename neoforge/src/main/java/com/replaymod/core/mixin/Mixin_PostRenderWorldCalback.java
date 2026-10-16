//#if MC>=11400
package com.replaymod.core.mixin;

import com.replaymod.core.events.PostRenderWorldCallback;
import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 26.2
//$$ import net.minecraft.client.renderer.SubmitNodeStorage;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Shadow;
//#endif

//#if MC>=11500
import com.llamalad7.mixinextras.sugar.Local;
//#endif

@Mixin(LevelRenderer.class)
public class Mixin_PostRenderWorldCalback {
    //#if MC>=12005
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4fStack;popMatrix()Lorg/joml/Matrix4fStack;"))
    private void postRenderWorld(CallbackInfo ci) {
        PoseStack matrixStack = new PoseStack();
    //#else
    //#if MC>=11500
    //$$ @Inject(method = "render", at = @At("RETURN"))
    //$$ private void postRenderWorld(CallbackInfo ci, @Local(argsOnly = true) MatrixStack matrixStack) {
    //#else
    //$$ @Inject(method = "render", at = @At("RETURN"))
    //$$ private void postRenderWorld(CallbackInfo ci) {
    //$$     MatrixStack matrixStack = new MatrixStack();
    //#endif
    //#endif
        //#if MC >= 26.2
        //$$ PostRenderWorldCallback.EVENT.invoker().postRenderWorld(matrixStack, this.submitNodeStorage);
        //#else
        PostRenderWorldCallback.EVENT.invoker().postRenderWorld(matrixStack);
        //#endif
    }

    //#if MC >= 26.2
    //$$ @Shadow @Final private SubmitNodeStorage submitNodeStorage;
    //#endif
}
//#endif
