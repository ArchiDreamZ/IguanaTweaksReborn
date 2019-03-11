package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleMisc;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class LivingUpdate {
	
	@SubscribeEvent
	public static void eventLivingUpdate(LivingUpdateEvent event) {
		//ModuleMovementRestriction.ApplyPlayer(event.getEntityLiving());
		//ModuleMovementRestriction.ApplyEntity(event.getEntityLiving());
		ModuleMisc.applyPoison(event.getEntityLiving());
	}
}