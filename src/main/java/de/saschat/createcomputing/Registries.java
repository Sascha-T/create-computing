package de.saschat.createcomputing;

import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBlockItem;
import com.simibubi.create.repack.registrate.util.nullness.NonNullBiFunction;
import de.saschat.createcomputing.blocks.ComputerizedDisplaySourceBlock;
import de.saschat.createcomputing.blocks.ComputerizedDisplayTargetBlock;
import de.saschat.createcomputing.blocks.ComputerizedRedstoneLinkBlock;
import de.saschat.createcomputing.blocks.TrainNetworkObserverBlock;
import de.saschat.createcomputing.tiles.ComputerizedDisplaySourceTile;
import de.saschat.createcomputing.tiles.ComputerizedDisplayTargetTile;
import de.saschat.createcomputing.tiles.ComputerizedRedstoneLinkTile;
import de.saschat.createcomputing.tiles.TrainNetworkObserverTile;
import de.saschat.createcomputing.tiles.renderer.TrainNetworkObserverRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
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

    public static RegistryObject<Item> registerBlockItem(String name, RegistryObject<Block> block, Item.Properties properties, NonNullBiFunction<? super Block, Item.Properties, ? extends BlockItem> function) {
        CreateComputingMod.LOGGER.info("Queuing block item: " + name);
        return ITEM_REGISTRY.register(name, () -> {
            return function.apply(block.get(), properties);
        });
    }

    public static RegistryObject<Item> registerBlockItem(String name, RegistryObject<Block> block, Item.Properties properties) {
        return registerBlockItem(name, block, properties, BlockItem::new);
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

    // Computerized Redstone Link
    public static RegistryObject<Block> COMPUTERIZED_REDSTONE_LINK = registerBlock(
        "computerized_redstone_link",
        ComputerizedRedstoneLinkBlock::new
    );
    public static RegistryObject<Item> COMPUTERIZED_REDSTONE_LINK_ITEM = registerBlockItem(
        "computerized_redstone_link",
        COMPUTERIZED_REDSTONE_LINK,
        new Item.Properties().tab(TAB)
    );
    public static RegistryObject<BlockEntityType<ComputerizedRedstoneLinkTile>> COMPUTERIZED_REDSTONE_LINK_TILE = registerTile(
        "computerized_redstone_link",
        ComputerizedRedstoneLinkTile::new,
        COMPUTERIZED_REDSTONE_LINK
    );

    // Train Network Observer
    public static RegistryObject<Block> TRAIN_NETWORK_OBSERVER = registerBlock(
        "train_network_observer",
        TrainNetworkObserverBlock::new
    );
    public static RegistryObject<Item> TRAIN_NETWORK_OBSERVER_ITEM = registerBlockItem(
        "train_network_observer",
        TRAIN_NETWORK_OBSERVER,
        new Item.Properties().tab(TAB),
        TrackTargetingBlockItem.ofType(TrainNetworkObserverTile.NETWORK_OBSERVER)
    );
    public static RegistryObject<BlockEntityType<TrainNetworkObserverTile>> TRAIN_NETWORK_OBSERVER_TILE = registerTile(
        "train_network_observer",
        TrainNetworkObserverTile::new,
        TRAIN_NETWORK_OBSERVER
    );

    // Events
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void fmlCommon(final FMLCommonSetupEvent blockRegistryEvent) {
            CreateComputingMod.LOGGER.info("Registering all Create behaviours.");
            Behaviours.register();
            CreateComputingMod.LOGGER.info("Registered all Create behaviour.");
        }

        @SubscribeEvent
        public static void fmlClient(final FMLClientSetupEvent blockRegistryEvent) {
            ItemBlockRenderTypes.setRenderLayer(COMPUTERIZED_DISPLAY_TARGET.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(COMPUTERIZED_REDSTONE_LINK.get(), RenderType.cutout());
        }

        @SubscribeEvent
        public static void modRenderer(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(TRAIN_NETWORK_OBSERVER_TILE.get(), TrainNetworkObserverRenderer::new);
        }
        public static void modData(final GatherDataEvent event) {
        }
    }
    /*@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void modCommands(final RegisterCommandsEvent event) {
            System.out.println("REGISTER COMMANDS");
            event.getDispatcher().register(
                Commands.literal("redstone_link").executes(context -> {
                    Map<Couple<RedstoneLinkNetworkHandler.Frequency>, Set<IRedstoneLinkable>> coupleSetMap = Create.REDSTONE_LINK_NETWORK_HANDLER.networksIn(context.getSource().getLevel());
                    coupleSetMap.forEach((frequencies, iRedstoneLinkables) -> {
                        System.out.println("Frequency: " + frequencies.get(true).getStack().getItem().getRegistryName().toString() + ", " + frequencies.get(false).getStack().getItem().getRegistryName().toString());
                        for (IRedstoneLinkable iRedstoneLinkable : iRedstoneLinkables) {
                            System.out.println("\tAt " + iRedstoneLinkable.getLocation().toString() + ", listening: " + iRedstoneLinkable.isListening() + ", alive: " + iRedstoneLinkable.isAlive() + ", strength: " + iRedstoneLinkable.getTransmittedStrength());
                        }
                    });
                    return 0;
                })
            );
        }
    }*/



    // Real loading

    public static void init(IEventBus modEventBus) {
        CreateComputingMod.LOGGER.info("Registering all registries.");
        BLOCK_REGISTRY.register(modEventBus);
        ITEM_REGISTRY.register(modEventBus);
        TILE_REGISTRY.register(modEventBus);
        CreateComputingMod.LOGGER.info("Registered all registries.");
    } // For loading.


}
