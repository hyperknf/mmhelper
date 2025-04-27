package com.hyperknf.mmhelper;

import com.hyperknf.mmhelper.config.Config;
import com.hyperknf.mmhelper.config.ConfigManager;
import com.hyperknf.mmhelper.utils.GithubFetcher;
import com.hyperknf.mmhelper.utils.ModProperties;
import com.hyperknf.mmhelper.gui.ScreenBuilder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.option.KeyBinding;

import java.util.ArrayList;
import java.util.UUID;

public class MMHelper implements ModInitializer {
    public static final Logger logger = LoggerFactory.getLogger("mmhelper");

    private static KeyBinding keyBindingOpenSettings;
    private static KeyBinding keyToggleEnabled;

    public static boolean onHypixel = false;
    public enum HypixelLobbies {
        None,
        MurderMystery,
        MurderMysteryLobby
    }
    public static HypixelLobbies lobby;
    public static boolean roundEnded = false;

    public static boolean isEnabled() {
        return ConfigManager.getConfig().enabled;
    }

    public static boolean isActive() {
        return isEnabled() && lobby == HypixelLobbies.MurderMystery;
    }

    public static boolean clientIsMurder = false;
    public static boolean clientIsDead = false;
    public static ArrayList<UUID> markedMurders = new ArrayList<>();
    public static ArrayList<UUID> markedDetectives = new ArrayList<>();

    @Override
    public void onInitialize() {
        ConfigManager.init();
        /* outdated list
        GithubFetcher.getMurderItems(items -> {
            ConfigManager.getConfig().mm.murderItems = items;
            ConfigManager.writeConfig(true);
        });*/

        keyBindingOpenSettings = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + ModProperties.MOD_ID + ".settings",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "key.category." + ModProperties.MOD_ID
        ));
        keyToggleEnabled = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + ModProperties.MOD_ID + ".enable",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "key.category." + ModProperties.MOD_ID
        ));
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);

        logger.info("MMHelper successfully initialized");
    }

    public void tick(MinecraftClient client) {
        if (keyBindingOpenSettings.wasPressed())
            ScreenBuilder.openConfigScreen(client);
    }

    public static void setModEnabled(boolean state) {
        Config config = ConfigManager.getConfig();
        if (state != config.enabled)
            config.enabled = state;
    }

    public static void setHighlightMurders(boolean state) {
        Config config = ConfigManager.getConfig();
        if (config.mm.highlightMurders != state) {
            config.mm.highlightMurders = state;
            // remove marked murderers
            if (!state)
                markedMurders.clear();
        }
    }

    public static void setDetectiveHighlightOptions(Config.MurderMystery.DetectiveHighlightOptions state) {
        Config config = ConfigManager.getConfig();
        if (config.mm.detectiveHighlightOptions != state) {
            config.mm.detectiveHighlightOptions = state;
            if (!config.mm.shouldHighlightDetectives(clientIsMurder))
                markedDetectives.clear();
        }
    }

    public static void setCurrentLobby(HypixelLobbies slobby) {
        resetLobby(lobby);
        lobby = slobby;
    }

    public static void resetLobby(HypixelLobbies lobby) {
        if (lobby == HypixelLobbies.MurderMystery) {
            roundEnded = false;
            clientIsMurder = false;
            clientIsDead = false;
            markedMurders.clear();
            markedDetectives.clear();
        }
    }
}
