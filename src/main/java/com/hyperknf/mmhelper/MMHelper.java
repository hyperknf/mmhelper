package com.hyperknf.mmhelper;

import com.hyperknf.mmhelper.config.Config;
import com.hyperknf.mmhelper.config.ConfigManager;
import com.hyperknf.mmhelper.utils.MinecraftUtils;
import com.hyperknf.mmhelper.utils.ModProperties;
import com.hyperknf.mmhelper.gui.ScreenBuilder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.option.KeyBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class MMHelper implements ModInitializer {
    public static final Logger logger = LoggerFactory.getLogger("mmhelper");

    private static KeyBinding keyBindingOpenSettings;
    private static KeyBinding keyToggleEnabled;
    private static KeyBinding keyShowMarkedPlayers;

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
    public static HashSet<UUID> markedMurders = new HashSet<>();
    public static HashSet<UUID> markedDetectives = new HashSet<>();

    @Override
    public void onInitialize() {
        ConfigManager.init();

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
        keyShowMarkedPlayers = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + ModProperties.MOD_ID + ".show_marked_players",
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
        if (keyToggleEnabled.wasPressed())
            ConfigManager.getConfig().enabled = !ConfigManager.getConfig().enabled;
        if (keyShowMarkedPlayers.wasPressed())
            showMarkedPlayers(client);
    }

    public void showMarkedPlayers(MinecraftClient client) {
        if (!isActive()) return;

        StringBuilder murderersList = new StringBuilder();
        StringBuilder detectivesList = new StringBuilder();
        if (client.world != null)
            for (PlayerEntity player : client.world.getPlayers()) {
                UUID uuid = player.getGameProfile().getId();
                if (markedMurders.contains(uuid))
                    murderersList.append(player.getGameProfile().getName()).append(" ");
                if (markedDetectives.contains(uuid))
                    detectivesList.append(player.getGameProfile().getName()).append(" ");
            }
        if (murderersList.isEmpty()) murderersList = new StringBuilder("None");
        if (detectivesList.isEmpty()) detectivesList = new StringBuilder("None");

        MinecraftUtils.sendChatMessage("");
        MinecraftUtils.sendChatMessage("[MMHelper]");
        MinecraftUtils.sendChatMessage(Text.translatable("message.show_murderers").getString() + Formatting.RED + murderersList);
        MinecraftUtils.sendChatMessage(Text.translatable("message.show_detectives").getString() + Formatting.AQUA + detectivesList);
        MinecraftUtils.sendChatMessage("");
    }

    public static void setModEnabled(boolean state) {
        Config config = ConfigManager.getConfig();
        if (state != config.enabled)
            config.enabled = state;
        else
            logger.warn("MMHelper is already enabled");
    }

    public static void setHighlightMurders(boolean state) {
        Config config = ConfigManager.getConfig();
        if (config.mm.highlightMurders != state) {
            config.mm.highlightMurders = state;
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
