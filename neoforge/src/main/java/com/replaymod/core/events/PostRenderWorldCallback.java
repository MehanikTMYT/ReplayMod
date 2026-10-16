package com.replaymod.core.events;

import de.johni0702.minecraft.gui.utils.Event;
import com.mojang.blaze3d.vertex.PoseStack;

//#if MC >= 26.2
//$$ import net.minecraft.client.renderer.SubmitNodeStorage;
//#endif

public interface PostRenderWorldCallback {
    Event<PostRenderWorldCallback> EVENT = Event.create((listeners) ->
            //#if MC >= 26.2
            //$$ (PoseStack matrixStack, SubmitNodeStorage submitNodeStorage) -> {
            //#else
            (PoseStack matrixStack) -> {
            //#endif
                for (PostRenderWorldCallback listener : listeners) {
                    //#if MC >= 26.2
                    //$$ listener.postRenderWorld(matrixStack, submitNodeStorage);
                    //#else
                    listener.postRenderWorld(matrixStack);
                    //#endif
                }
            }
    );

    //#if MC >= 26.2
    //$$ void postRenderWorld(PoseStack matrixStack, SubmitNodeStorage submitNodeStorage);
    //#else
    void postRenderWorld(PoseStack matrixStack);
    //#endif
}
