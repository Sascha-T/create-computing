package de.saschat.createcomputing;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CreateComputingMod.MOD_ID)
public class CreateComputingMod {
    public static final String MOD_ID = "createcomputing";

    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateComputingMod() {
        Registries.init(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
    }
}
