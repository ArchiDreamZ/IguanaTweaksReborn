package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.HHModule;
import insane96mcp.iguanatweaksreborn.modules.misc.ai.ITCenaSwellGoal;
import insane96mcp.iguanatweaksreborn.modules.misc.ai.ITCreeperSwellGoal;
import insane96mcp.iguanatweaksreborn.modules.misc.other.ITExplosion;
import insane96mcp.iguanatweaksreborn.setup.ModSounds;
import insane96mcp.iguanatweaksreborn.utils.RandomHelper;
import net.minecraft.entity.ai.goal.CreeperSwellGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.Explosion;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class Break {

    @SubscribeEvent
    public static void eventBreak(BlockEvent.BreakEvent event) {
        HHModule.breakExaustion(event);
    }

    @SubscribeEvent
    public static void explosionStartEvent(ExplosionEvent.Detonate event) {

        Explosion e = event.getExplosion();
        if (e.exploder instanceof CreeperEntity) {
            CreeperEntity creeper = (CreeperEntity) e.exploder;

            if (creeper.hasCustomName() && creeper.getCustomName().getString().equals("John Cena")){
                creeper.playSound(ModSounds.CREEPER_CENA_EXPLODE.get(), 3.0f, 1.0f);
            }
        }

        if (e.world instanceof ServerWorld && !e.getAffectedBlockPositions().isEmpty()) {
            ServerWorld world = (ServerWorld) e.world;
            int particleCount = (int)(e.size * 200);
            world.spawnParticle(ParticleTypes.POOF, e.getPosition().x, e.getPosition().y, e.getPosition().z, particleCount, e.size / 4f, e.size / 4f, e.size / 4f, 0.33D);
        }
    }

    @SubscribeEvent
    public static void livingDamageEvent(LivingDamageEvent event) {

        if (event.getSource().isExplosion() && event.getEntityLiving() instanceof CreeperEntity){
            CreeperEntity creeper = (CreeperEntity) event.getEntityLiving();
            CompoundNBT compoundNBT = new CompoundNBT();
            int fuse = RandomHelper.getInt(creeper.world.getRandom(), 10, 20);
            compoundNBT.putShort("Fuse", (short)fuse);
            creeper.readAdditional(compoundNBT);
            creeper.ignite();
        }
    }

    @SubscribeEvent
    public static void onExplosionStart(ExplosionEvent.Start event) {
        if (event.getWorld().isRemote)
            return;

        event.setCanceled(true);

        ServerWorld world = (ServerWorld) event.getWorld();

        Explosion e = event.getExplosion();
        float size = e.size;
        boolean causesFire = e.causesFire;
        if (e.exploder instanceof CreeperEntity) {
            CreeperEntity creeper = (CreeperEntity) e.exploder;

            if (creeper.hasCustomName() && creeper.getCustomName().getString().equals("John Cena")){
                size *= 2;
                causesFire = true;
            }
        }
        ITExplosion explosion = new ITExplosion(e.world, e.exploder, e.getDamageSource(), e.context, e.getPosition().x, e.getPosition().y, e.getPosition().z, size, causesFire, e.mode);

        explosion.gatherAffectedBlocks();
        explosion.fallingBlocks();
        explosion.processEntities();
        explosion.destroyBlocks();
        explosion.doExplosionB(false);
        if (explosion.mode == Explosion.Mode.NONE) {
            explosion.clearAffectedBlockPositions();
        }

        for(ServerPlayerEntity serverplayerentity : world.getPlayers()) {
            if (serverplayerentity.getDistanceSq(explosion.getPosition().x, explosion.getPosition().y, explosion.getPosition().z) < 4096.0D) {
                serverplayerentity.connection.sendPacket(new SExplosionPacket(explosion.getPosition().x, explosion.getPosition().y, event.getExplosion().getPosition().z, explosion.size, explosion.getAffectedBlockPositions(), explosion.getPlayerKnockbackMap().get(serverplayerentity)));
            }
        }
    }
    @SubscribeEvent
    public static void eventEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof CreeperEntity){
            CreeperEntity creeper = (CreeperEntity) event.getEntity();

            ArrayList<Goal> goalsToRemove = new ArrayList<>();
            creeper.goalSelector.goals.forEach(prioritizedGoal -> {
                if (prioritizedGoal.getGoal() instanceof CreeperSwellGoal)
                    goalsToRemove.add(prioritizedGoal.getGoal());
            });

            goalsToRemove.forEach(creeper.goalSelector::removeGoal);

            //creeper.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(creeper, LivingEntity.class, true));

            if (creeper.hasCustomName() && creeper.getCustomName().getString().equals("John Cena")) {
                CompoundNBT compoundNBT = new CompoundNBT();
                compoundNBT.putShort("Fuse", (short)35);
                creeper.readAdditional(compoundNBT);
                creeper.goalSelector.addGoal(2, new ITCenaSwellGoal(creeper));
            }
            else {
                creeper.goalSelector.addGoal(2, new ITCreeperSwellGoal(creeper));
            }
        }
    }
}
