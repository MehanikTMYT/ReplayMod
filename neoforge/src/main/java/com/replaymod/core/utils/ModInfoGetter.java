package com.replaymod.core.utils;

import com.replaymod.replaystudio.data.ModInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

// Fleet port (NeoForge 26.1.2): mod metadata from ModList instead of FabricLoader.
class ModInfoGetter {
    static Collection<ModInfo> getInstalledNetworkMods() {
        Map<String, ModInfo> modInfoMap = ModList.get().getMods().stream()
                .map((IModInfo m) -> new ModInfo(m.getModId(), m.getDisplayName(), m.getVersion().toString()))
                .collect(Collectors.toMap(ModInfo::getId, Function.identity(), (a, b) -> a));
        return net.minecraft.core.registries.BuiltInRegistries.REGISTRY.stream()
                .map(Registry::keySet).flatMap(Set::stream)
                .map(Identifier::getNamespace).filter(s -> !s.equals("minecraft")).distinct()
                .map(modInfoMap::get).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
