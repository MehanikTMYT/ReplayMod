package com.replaymod.recording.mixin;

import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nonnull;

@Mixin(PacketDecoder.class)
public interface DecoderHandlerAccessor<T extends PacketListener> {
    @Accessor("protocolInfo")
    @Nonnull
    ProtocolInfo<T> getState();
}
