package de.saschat.createcomputing;

import de.saschat.createcomputing.blocks.ComputerizedDisplaySourceBlock;
import de.saschat.createcomputing.blocks.ComputerizedDisplayTargetBlock;
import de.saschat.createcomputing.tiles.ComputerizedDisplaySourceTile;
import de.saschat.createcomputing.tiles.ComputerizedDisplayTargetTile;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.function.Supplier;

public class Registries {
    public static DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, CreateComputingMod.MOD_ID);
    public static DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, CreateComputingMod.MOD_ID);
    public static DeferredRegister<BlockEntityType<?>> TILE_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, CreateComputingMod.MOD_ID);

    public static RegistryObject<Block> registerBlock(String name, Supplier<Block> blockSupplier) {
        CreateComputingMod.LOGGER.info("Queuing block: " + name);
        return BLOCK_REGISTRY.register(name, blockSupplier);
    }

    public static RegistryObject<Item> registerBlockItem(String name, RegistryObject<Block> block, Item.Properties properties) {
        CreateComputingMod.LOGGER.info("Queuing block item: " + name);
        return ITEM_REGISTRY.register(name, () -> {
            return new BlockItem(block.get(), properties);
        });
    }

    public static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerTile(String name, BlockEntityType.BlockEntitySupplier<? extends T> supplier, Supplier<Block>... blocks) {
        CreateComputingMod.LOGGER.info("Queuing tile: " + name);
        return TILE_REGISTRY.register(name, () -> {
            Block[] rBlocks = Arrays.stream(blocks).map(Supplier::get).toList().toArray(new Block[0]);
            return BlockEntityType.Builder.<T>of(
                supplier,
                rBlocks).build(null);
        });
    }

    public static CreativeModeTab TAB = new CreativeModeTab("createcomputing.tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(COMPUTERIZED_DISPLAY_TARGET_ITEM.get());
        }
    };

    // Computerized Display Source
    public static RegistryObject<Block> COMPUTERIZED_DISPLAY_SOURCE = registerBlock(
        "computerized_display_source",
        ComputerizedDisplaySourceBlock::new
    );
    public static RegistryObject<Item> COMPUTERIZED_DISPLAY_SOURCE_ITEM = registerBlockItem(
        "computerized_display_source",
        COMPUTERIZED_DISPLAY_SOURCE,
        new Item.Properties().tab(TAB)
    );
    public static RegistryObject<BlockEntityType<ComputerizedDisplaySourceTile>> COMPUTERIZED_DISPLAY_SOURCE_TILE = registerTile(
        "computerized_display_source",
        ComputerizedDisplaySourceTile::new,
        COMPUTERIZED_DISPLAY_SOURCE
    );


    // Computerized Display Target
    public static RegistryObject<Block> COMPUTERIZED_DISPLAY_TARGET = registerBlock(
        "computerized_display_target",
        ComputerizedDisplayTargetBlock::new
    );
    public static RegistryObject<Item> COMPUTERIZED_DISPLAY_TARGET_ITEM = registerBlockItem(
        "computerized_display_target",
        COMPUTERIZED_DISPLAY_TARGET,
        new Item.Properties().tab(TAB)
    );
    public static RegistryObject<BlockEntityType<ComputerizedDisplayTargetTile>> COMPUTERIZED_DISPLAY_TARGET_TILE = registerTile(
        "computerized_display_target",
        ComputerizedDisplayTargetTile::new,
        COMPUTERIZED_DISPLAY_TARGET
    );

    // Events
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void fmlCommon(final FMLCommonSetupEvent blockRegistryEvent) {
            // Register a new block here
            CreateComputingMod.LOGGER.info("Registering all Create behaviours.");
            Behaviours.register();
            CreateComputingMod.LOGGER.info("Registered all Create behaviour.");
        }
        @SubscribeEvent
        public static void fmlClient(final FMLClientSetupEvent blockRegistryEvent) {
            ItemBlockRenderTypes.setRenderLayer(COMPUTERIZED_DISPLAY_TARGET.get(), RenderType.cutout());
        }
        public static void modData(final GatherDataEvent event) {}
    }


    // Real loading

    public static void init(IEventBus modEventBus) {
        CreateComputingMod.LOGGER.info("Registering all registries.");
        BLOCK_REGISTRY.register(modEventBus);
        ITEM_REGISTRY.register(modEventBus);
        TILE_REGISTRY.register(modEventBus);
        CreateComputingMod.LOGGER.info("Registered all registries.");
    } // For loading.


}
