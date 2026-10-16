package com.replaymod.core;

import com.replaymod.core.gui.GuiReplaySettings;
import net.minecraft.SharedConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import com.replaymod.core.utils.Restrictions;

import static com.replaymod.core.ReplayMod.MOD_ID;

/**
 * Fleet port (NeoForge 26.1.2): replaces the Fabric client entrypoint. NeoForge constructs mods in parallel before the
 * Minecraft instance exists, but ReplayMod (like Fabric's client entrypoint) expects it, so the mod is created and its
 * modules initialised in {@link RegisterKeyMappingsEvent} — posted from the Minecraft constructor — and the key mappings
 * collected meanwhile are registered right after. The Mod Menu config screen becomes an {@link IConfigScreenFactory}.
 */
@Mod(value = MOD_ID, dist = Dist.CLIENT)
public class ReplayModBackend {
    private final ModContainer container;
    private ReplayMod mod;

    public ReplayModBackend(IEventBus modBus, ModContainer container) {
        this.container = container;
        modBus.addListener(RegisterKeyMappingsEvent.class, event -> {
            if (mod == null) {
                mod = new ReplayMod(this);
                mod.initModules();
            }
            KeyBindingRegistry.registerPending(event);
        });
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) ->
                new GuiReplaySettings(parent, ReplayMod.instance.getSettingsRegistry()).toMinecraft());
        // Announces the restrictions channel (servers use it to tell ReplayMod what not to record); payloads are ignored,
        // as on Fabric. Optional, so vanilla servers are unaffected.
        modBus.addListener(RegisterPayloadHandlersEvent.class, event -> {
            var registrar = event.registrar("1").optional();
            registrar.configurationToClient(Restrictions.ID, Restrictions.CODEC, (payload, context) -> {});
            registrar.playToClient(Restrictions.ID, Restrictions.CODEC, (payload, context) -> {});
        });
    }

    public String getVersion() {
        return container.getModInfo().getVersion().toString();
    }

    public String getMinecraftVersion() {
        return SharedConstants.getCurrentVersion().name();
    }

    public boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id.toLowerCase());
    }
}
