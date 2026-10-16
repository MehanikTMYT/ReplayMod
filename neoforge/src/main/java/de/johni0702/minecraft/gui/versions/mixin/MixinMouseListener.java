package de.johni0702.minecraft.gui.versions.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.johni0702.minecraft.gui.versions.callbacks.MouseCallback;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public abstract class MixinMouseListener {
    @WrapOperation(method = "onButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z"))
    private boolean mouseDown(Screen screen, MouseButtonEvent click, boolean doubled, Operation<Boolean> original) {
        if (MouseCallback.EVENT.invoker().mouseDown(new de.johni0702.minecraft.gui.function.Click(click))) {
            return true;
        }
        return original.call(screen, click, doubled);
    }

    @WrapOperation(method = "onButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseReleased(Lnet/minecraft/client/input/MouseButtonEvent;)Z"))
    private boolean mouseUp(Screen screen, MouseButtonEvent click, Operation<Boolean> original) {
        if (MouseCallback.EVENT.invoker().mouseUp(new de.johni0702.minecraft.gui.function.Click(click))) {
            return true;
        }
        return original.call(screen, click);
    }

    @WrapOperation(method = "handleAccumulatedMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseDragged(Lnet/minecraft/client/input/MouseButtonEvent;DD)Z"))
    private boolean mouseDrag(Screen screen, MouseButtonEvent click, double dx, double dy, Operation<Boolean> original) {
        if (MouseCallback.EVENT.invoker().mouseDrag(new de.johni0702.minecraft.gui.function.Click(click), dx, dy)) {
            return true;
        }
        return original.call(screen, click, dx, dy);
    }

    @WrapOperation(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDDD)Z"))
    private boolean mouseScroll(Screen screen, double x, double y, double horizontal, double vertical, Operation<Boolean> original) {
        if (MouseCallback.EVENT.invoker().mouseScroll(x, y, horizontal, vertical)) {
            return true;
        }
        return original.call(screen, x, y, horizontal, vertical);
    }
}
