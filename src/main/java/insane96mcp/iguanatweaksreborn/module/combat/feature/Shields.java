package insane96mcp.iguanatweaksreborn.module.combat.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Shields", description = "Various changes to Shields")
public class Shields extends Feature {
	private final ForgeConfigSpec.BooleanValue removeShieldWindupConfig;
	private final ForgeConfigSpec.DoubleValue shieldBlockDamageConfig;
	private final ForgeConfigSpec.BooleanValue combatTestShieldDisablingConfig;

	public boolean removeShieldWindup = true;
	public double shieldBlockDamage = 5d;
	public boolean combatTestShieldDisabling = true;

	public Shields(Module module) {
		super(Config.builder, module);
		this.pushConfig(Config.builder);
		removeShieldWindupConfig = Config.builder
				.comment("In vanilla when you start blocking with a shield, there's a 0.25 seconds window where you are still not blocking. If true this windup time is removed.")
				.define("Remove Shield Windup", this.removeShieldWindup);
		shieldBlockDamageConfig = Config.builder
				.comment("Shields will only block this amount of damage. Setting to 0 will make shield block like vanilla")
				.defineInRange("Shield Damage Blocked", this.shieldBlockDamage, 0d, 128d);
		combatTestShieldDisablingConfig = Config.builder
				.comment("Makes shields always disable for 1.6 seconds like Combat Test snapshots.")
				.define("Combat Test shield disabling", this.combatTestShieldDisabling);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.removeShieldWindup = this.removeShieldWindupConfig.get();
		this.shieldBlockDamage = this.shieldBlockDamageConfig.get();
		this.combatTestShieldDisabling = this.combatTestShieldDisablingConfig.get();
	}

	@SubscribeEvent
	public void onShieldBlock(ShieldBlockEvent event) {
		if (!this.isEnabled() || this.shieldBlockDamage == 0)
			return;

		event.setBlockedDamage((float) this.shieldBlockDamage);
	}

	public boolean shouldRemoveShieldWindup() {
		return this.isEnabled() && this.removeShieldWindup;
	}

	public boolean combatTestShieldDisabling() {
		return this.isEnabled() && this.combatTestShieldDisabling;
	}
}