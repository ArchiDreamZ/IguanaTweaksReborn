package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.Modules;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
	@Shadow
	public int experienceLevel;

	protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
		super(type, level);
	}

	@Inject(at = @At("RETURN"), method = "getXpNeededForNextLevel", cancellable = true)
	private void xpBarCap(CallbackInfoReturnable<Integer> callback) {
		int exp = Modules.experience.playerExperience.getBetterScalingLevel(this.experienceLevel);
		if (exp != -1)
			callback.setReturnValue(exp);
	}

	@Inject(at = @At("HEAD"), method = "getExperienceReward", cancellable = true)
	private void getExperiencePoints(Player player, CallbackInfoReturnable<Integer> callback) {
		int exp = Modules.experience.playerExperience.getExperienceOnDeath((Player) (Object) this);
		if (exp != -1)
			callback.setReturnValue(exp);
	}

	@ModifyVariable(method = "causeFoodExhaustion", argsOnly = true, at = @At("HEAD"))
	private float applyHungerToFoodExhaustion(float amount) {
		return Modules.hungerHealth.exhaustionIncrease.increaseHungerEffectiviness((Player) (Object) this, amount);
	}

	@Inject(method = "disableShield", at = @At("HEAD"), cancellable = true)
	private void disableShield(boolean efficiencyAffected, CallbackInfo callbackInfo) {
		if (Modules.combat.shields.combatTestShieldDisabling()) {
			callbackInfo.cancel();
			this.getCooldowns().addCooldown(this.getUseItem().getItem(), 32);
			this.stopUsingItem();
		}
	}

	@Inject(at = @At("HEAD"), method = "causeFoodExhaustion")
	private void tiredness(float amount, CallbackInfo ci) {
		//Modules.sleepRespawn.tiredness.applyTiredness((Player) (Object) this, amount);
	}

	@Shadow
	public abstract ItemCooldowns getCooldowns();
}
