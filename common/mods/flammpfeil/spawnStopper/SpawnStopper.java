package mods.flammpfeil.spawnStopper;

import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(name="SpawnStopper",modid="flammpfeil.SpawnStopper",useMetadata=false, version="@VERSION@")
public class SpawnStopper {

	public static Configuration mainConfiguration;
	public static int blockId =138;
	public static int posX = 0;
	public static int posY = 1;
	public static int posZ = 0;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		mainConfiguration = new Configuration(evt.getSuggestedConfigurationFile());

		try{
			mainConfiguration.load();

			Property propBlockId;
			propBlockId = mainConfiguration.get(Configuration.CATEGORY_GENERAL, "spawnStopperBlock", blockId,"Existing blockID");
			blockId = propBlockId.getInt();

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
	@ForgeSubscribe
	@SideOnly(Side.CLIENT)
	public void livingSpawnEvent(LivingSpawnEvent.CheckSpawn event) {
		if(event.world.getChunkFromBlockCoords((int)event.x, (int)event.z).getBlockID(posX, posY, posZ) == blockId)
			event.setResult(Result.DENY);
		else if(event.entityLiving instanceof EntityMob){
			int tmp = event.world.skylightSubtracted;
			event.world.skylightSubtracted = 15;
			int light = event.world.getBlockLightValue((int)event.x,(int)event.y,(int)event.z);
			event.world.skylightSubtracted = tmp;

			if(light > 8)
				event.setResult(Result.DENY);
		}
	}
}
