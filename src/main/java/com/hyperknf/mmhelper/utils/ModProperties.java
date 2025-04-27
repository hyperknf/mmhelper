package com.hyperknf.mmhelper.utils;

import com.hyperknf.mmhelper.utils.Version;
import com.hyperknf.mmhelper.utils.MinecraftUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class ModProperties {
    public static final String MOD_ID = "mmhelper";
    public static final ModMetadata METADATA = MinecraftUtils.getModMetadata(MOD_ID);
    public static final String MOD_NAME = METADATA.getName();
    public static final Version MOD_VERSION = new Version(METADATA.getVersion().getFriendlyString());
    public static final Version MC_VERSION = new Version("1.20.4");
}
