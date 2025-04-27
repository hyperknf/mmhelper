package com.hyperknf.mmhelper.gui;

import com.hyperknf.mmhelper.MMHelper;
import com.hyperknf.mmhelper.config.Config;
import com.hyperknf.mmhelper.config.ConfigManager;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ScreenBuilder {
    public static Screen buildConfigScreen(Screen parent) {
        Config config = ConfigManager.getConfig();
        Config defaults = ConfigManager.getDefaults();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("config.title"))
                .transparentBackground()
                .setSavingRunnable(() -> {
                    ConfigManager.writeConfig(true);
                });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory catGeneric = builder.getOrCreateCategory(Text.translatable("config.generic.title"));

        // enable
        AbstractConfigListEntry<Boolean> toggleEnabled = entryBuilder.startBooleanToggle(
                Text.translatable("config.generic.enabled.title"),
                config.enabled
        )
                .setDefaultValue(defaults.enabled)
                .setTooltip(Text.translatable("config.generic.enabled.tooltip"))
                .setSaveConsumer(MMHelper::setModEnabled)
                .build();

        // highlight innocents
        EnumListEntry<Config.MurderMystery.InnocentHighlightOptions> enumMurderMysteryInnocentHighlightOptions = entryBuilder.startEnumSelector(
                Text.translatable("config.generic.hypixel.mm.highlight.innocent.title"),
                Config.MurderMystery.InnocentHighlightOptions.class,
                config.mm.innocentHighlightOptions
        )
                .setDefaultValue(defaults.mm.innocentHighlightOptions)
                .setEnumNameProvider(e -> ((Config.MurderMystery.InnocentHighlightOptions)e).getText())
                .setSaveConsumer(config.mm::setInnocentHighlightOptions)
                .build();

        // highlight detectives
        EnumListEntry<Config.MurderMystery.DetectiveHighlightOptions> enumMurderMysteryDetectiveHighlightOptions = entryBuilder.startEnumSelector(
                Text.translatable("config.generic.hypixel.mm.highlight.detective.title"),
                Config.MurderMystery.DetectiveHighlightOptions.class,
                config.mm.detectiveHighlightOptions
        )
                .setDefaultValue(defaults.mm.detectiveHighlightOptions)
                .setEnumNameProvider((e) -> ((Config.MurderMystery.DetectiveHighlightOptions)e).getText())
                .setSaveConsumer(MMHelper::setDetectiveHighlightOptions)
                .build();

        // highlight murders
        AbstractConfigListEntry<Boolean> toggleMurderMysteryHighlightMurders = entryBuilder.startBooleanToggle(
                Text.translatable("config.generic.hypixel.mm.highlight.murder.title"),
                config.mm.highlightMurders
        )
                .setDefaultValue(defaults.mm.highlightMurders)
                .setTooltip(Text.translatable("config.generic.hypixel.mm.highlight.murder.tooltip"))
                .setSaveConsumer(MMHelper::setHighlightMurders)
                .build();

        // highlight gold
        AbstractConfigListEntry<Boolean> toggleMurderMysteryHighlightGold = entryBuilder.startBooleanToggle(
                Text.translatable("config.generic.hypixel.mm.highlight.gold.title"),
                config.mm.highlightGold
        )
                .setDefaultValue(defaults.mm.highlightGold)
                .setTooltip(Text.translatable("config.generic.hypixel.mm.highlight.gold.tooltip"))
                .setSaveConsumer(config.mm::setHighlightGold)
                .build();

        // highlight bows
        AbstractConfigListEntry<Boolean> toggleMurderMysteryHighlightBows = entryBuilder.startBooleanToggle(
                Text.translatable("config.generic.hypixel.mm.highlight.bow.title"),
                config.mm.highlightBows
        )
                .setDefaultValue(defaults.mm.highlightBows)
                .setTooltip(Text.translatable("config.generic.hypixel.mm.highlight.bow.tooltip"))
                .setSaveConsumer(config.mm::setHighlightBows)
                .build();

        // show name tags
        AbstractConfigListEntry<Boolean> toggleMurderMysteryShowNameTags = entryBuilder.startBooleanToggle(
                Text.translatable("config.generic.hypixel.mm.misc.show_name_tags.title"),
                config.mm.showNameTags
        )
                .setDefaultValue(defaults.mm.showNameTags)
                .setTooltip(Text.translatable("config.generic.hypixel.mm.misc.show_name_tags.tooltip"))
                .setSaveConsumer(config.mm::setShowNameTags)
                .build();

        // highlight spectators
        AbstractConfigListEntry<Boolean> toggleMurderMysteryHighlightSpectators = entryBuilder.startBooleanToggle(
                Text.translatable("config.generic.hypixel.mm.misc.highlight_spectators.title"),
                config.mm.highlightSpectators
        )
                .setDefaultValue(defaults.mm.highlightSpectators)
                .setTooltip(Text.translatable("config.generic.hypixel.mm.misc.highlight_spectators.tooltip"))
                .setSaveConsumer(config.mm::setHighlightSpectators)
                .build();

        SubCategoryBuilder subCatHighlight = entryBuilder.startSubCategory(Text.translatable("config.generic.hypixel.mm.highlight.title"));
        subCatHighlight.add(enumMurderMysteryInnocentHighlightOptions);
        subCatHighlight.add(enumMurderMysteryDetectiveHighlightOptions);
        subCatHighlight.add(toggleMurderMysteryHighlightMurders);
        subCatHighlight.add(toggleMurderMysteryHighlightSpectators);
        subCatHighlight.add(toggleMurderMysteryHighlightGold);
        subCatHighlight.add(toggleMurderMysteryHighlightBows);
        subCatHighlight.add(toggleMurderMysteryShowNameTags);
        subCatHighlight.setExpanded(true);

        catGeneric.addEntry(toggleEnabled);
        catGeneric.addEntry(subCatHighlight.build());

        return builder.build();
    }

    public static void openConfigScreen() {
        openConfigScreen(MinecraftClient.getInstance());
    }
    public static void openConfigScreen(MinecraftClient client) {
        client.setScreenAndRender(buildConfigScreen(client.currentScreen));
    }
}
