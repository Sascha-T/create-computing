package de.saschat.createcomputing.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class CreateComputingConfigServer {
    public ConfigValue<List<String>> BANNED_LINK_ITEMS;
    public LongValue MAXIMUM_CONCURRENT_LINKS;

    public CreateComputingConfigServer(ForgeConfigSpec.Builder builder) {
        BANNED_LINK_ITEMS = builder.comment("These are the items the computerized redstone link cannot use.").define(
            "computerized_redstone_link.banned_link_items",
            List.of(
                new ResourceLocation("minecraft", "dragon_egg").toString(),
                new ResourceLocation("minecraft", "nether_star").toString()
            )
        );
        MAXIMUM_CONCURRENT_LINKS = builder.comment("This is the maximum amount of concurrent handles one computerized redstone link is allowed ot have.").defineInRange(
            "computerized_redstone_link.maximum_concurrent_links",
            8,
            1,
            Long.MAX_VALUE
        );

    }

    public static Pair<CreateComputingConfigServer, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
        .configure(CreateComputingConfigServer::new);

    public static CreateComputingConfigServer get() {
        return pair.getLeft();
    }
}
