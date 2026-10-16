package com.replaymod.recording.mixin;

import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;

//#if MC>=11903
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.gen.Accessor;
//#endif

@Mixin(ClientHandshakePacketListenerImpl.class)
public interface ClientLoginNetworkHandlerAccessor {
    //#if MC>=11903
    @Accessor("serverData")
    ServerData getServerInfo();
    //#endif
}
