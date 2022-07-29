package de.saschat.createcomputing;

import com.mojang.logging.LogUtils;
import de.saschat.createcomputing.config.CreateComputingConfigServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CreateComputingMod.MOD_ID)
public class CreateComputingMod {
    public static final String MOD_ID = "createcomputing";

    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateComputingMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CreateComputingConfigServer.pair.getRight());
        Registries.init(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
    }
}
