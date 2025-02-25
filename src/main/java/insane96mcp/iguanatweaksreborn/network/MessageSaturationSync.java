package insane96mcp.iguanatweaksreborn.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageSaturationSync {
	float saturationLevel;

	public MessageSaturationSync(float saturationLevel) {
		this.saturationLevel = saturationLevel;
	}

	public static void encode(MessageSaturationSync pkt, FriendlyByteBuf buf) {
		buf.writeFloat(pkt.saturationLevel);
	}

	public static MessageSaturationSync decode(FriendlyByteBuf buf) {
		return new MessageSaturationSync(buf.readFloat());
	}

	public static void handle(final MessageSaturationSync message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> NetworkHelper.getSidedPlayer(ctx.get()).getFoodData().saturationLevel = message.saturationLevel);
		ctx.get().setPacketHandled(true);
	}
}
