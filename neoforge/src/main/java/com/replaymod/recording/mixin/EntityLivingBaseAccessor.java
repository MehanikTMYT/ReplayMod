package com.replaymod.recording.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nonnull;

//#if MC>=10904
import net.minecraft.network.syncher.EntityDataAccessor;
//#endif

@Mixin(LivingEntity.class)
public interface EntityLivingBaseAccessor {
    //#if MC>=10904
    @Accessor("DATA_LIVING_ENTITY_FLAGS")
    @Nonnull
    @SuppressWarnings("ConstantConditions")
    static EntityDataAccessor<Byte> getLivingFlags() { return null; }
    //#endif
}
