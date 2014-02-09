package mods.flammpfeil.spawnStopper;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(name="SpawnStopper", modid="flammpfeil.spawnstopper", version="mc1.7.2-r1")
public class SpawnStopper {

	public static Configuration mainConfiguration;
    public static Block block;
    public static String blockName;
	public static int posX = 0;
	public static int posY = 1;
	public static int posZ = 0;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		mainConfiguration = new Configuration(evt.getSuggestedConfigurationFile());

		try{
			mainConfiguration.load();

			Property propBlockId;
			propBlockId = mainConfiguration.get(Configuration.CATEGORY_GENERAL, "spawnStopperBlockName", "minecraft:cobblestone", "Existing blockName");
            blockName = propBlockId.getString();

			Property propPos;
			propPos = mainConfiguration.get(Configuration.CATEGORY_GENERAL, "spawnStopperXPosInChunk", posX , "0-15:Installation position in ChunkX");
			posX = propPos.getInt() & 0xf;
			propPos.set(posX);


			propPos = mainConfiguration.get(Configuration.CATEGORY_GENERAL, "spawnStopperYPosInChunk", posY, "0<Y:Installation position in ChunkY");
			posY = propPos.getInt();
			if(posY <=0)
				posY=1;
			propPos.set(posY);


			propPos = mainConfiguration.get(Configuration.CATEGORY_GENERAL, "spawnStopperZPosInChunk", posZ, "0-15:Installation position in ChunkZ");
			posZ = propPos.getInt() & 0xf;
			propPos.set(posZ);

		}
		finally
		{
			mainConfiguration.save();
		}

		MinecraftForge.EVENT_BUS.register(this);
	}

    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
        block = (Block)Block.blockRegistry.getObject(blockName);
        if(block == null){
            System.out.println("not found blockName - \"" + blockName + "\"");
            block = Blocks.cobblestone;
        }
    }

	@SubscribeEvent
	public void livingSpawnEvent(LivingSpawnEvent.CheckSpawn event) {
		if(event.world.getChunkFromBlockCoords((int)event.x, (int)event.z).getBlock(posX, posY, posZ) == block)
			event.setResult(Event.Result.DENY);
		else if(event.entityLiving instanceof IMob){
			int tmp = event.world.skylightSubtracted;
			event.world.skylightSubtracted = 15;
			int light = event.world.getBlockLightValue((int)event.x,(int)event.y,(int)event.z);
			event.world.skylightSubtracted = tmp;

			if(light > 8)
				event.setResult(Event.Result.DENY);
		}
	}
}
