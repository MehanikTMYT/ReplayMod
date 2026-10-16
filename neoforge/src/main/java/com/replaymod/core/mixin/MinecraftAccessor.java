package com.replaymod.core.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.CrashReport;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

//#if MC>=11800
import java.util.function.Supplier;
//#endif

//#if MC>=11400
import java.util.concurrent.CompletableFuture;
//#endif

//#if MC<11400
//$$ import java.util.concurrent.FutureTask;
//#endif

//#if MC<11400
//$$ import net.minecraft.client.resources.IResourcePack;
//$$ import java.util.List;
//$$ import java.util.Queue;
//#endif

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    //#if MC>=12100
    @Accessor("deltaTracker")
    DeltaTracker.Timer getTimer();
    @Accessor("deltaTracker")
    @Mutable
    void setTimer(DeltaTracker.Timer value);
    //#else
    //$$ @Accessor("renderTickCounter")
    //$$ RenderTickCounter getTimer();
    //$$ @Accessor("renderTickCounter")
    //#if MC>=11200
    //$$ @Mutable
    //#endif
    //$$ void setTimer(RenderTickCounter value);
    //#endif

    //#if MC>=11400
    @Accessor("pendingReload")
    CompletableFuture<Void> getResourceReloadFuture();
    @Accessor("pendingReload")
    void setResourceReloadFuture(CompletableFuture<Void> value);
    //#endif

    //#if MC>=11400
    //#else
    //$$ @Accessor
    //$$ Queue<FutureTask<?>> getScheduledTasks();
    //#endif

    //#if MC >= 26.1
    // Now in BlockableEventLoopAccessor
    //#else
    //$$ @Accessor("crashReportSupplier")
    //#if MC>=11800
    //$$ Supplier<CrashReport> getCrashReporter();
    //#else
    //$$ CrashReport getCrashReporter();
    //#endif
    //#endif

    //#if MC<11400
    //$$ @Accessor
    //$$ List<IResourcePack> getDefaultResourcePacks();
    //#endif

    //#if MC>=11400
    @Accessor("pendingConnection")
    void setConnection(Connection connection);
    //#endif
}
