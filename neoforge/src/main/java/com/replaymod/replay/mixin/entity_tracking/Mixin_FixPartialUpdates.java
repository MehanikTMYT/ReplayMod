package com.replaymod.replay.mixin.entity_tracking;

import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPacketListener.class)
public class Mixin_FixPartialUpdates {
    // Looks like this has finally been fixed in 1.20.2
}
