package com.alrex.parcool.api;


import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.client.LocalStamina;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Stamina {
    public static Stamina get(Player player) {
		return new Stamina(player);
	}

	private final Player player;

	private Stamina(Player player) {
		this.player = player;
	}

	public int getMaxValue() {
		return player.getAttachedOrCreate(Attachments.STAMINA).max();
	}

	public int getValue() {
		return player.getAttachedOrCreate(Attachments.STAMINA).value();
	}

	public boolean isExhausted() {
		return player.getAttachedOrCreate(Attachments.STAMINA).isExhausted();
	}

	@Environment(EnvType.CLIENT)
	public void consume(int value) {
		if (!(player instanceof LocalPlayer localPlayer)) return;
		var stamina = LocalStamina.get(localPlayer);
		stamina.consume(localPlayer, value);
	}

	@Environment(EnvType.CLIENT)
	public void recover(int value) {
		if (!(player instanceof LocalPlayer localPlayer)) return;
		var stamina = LocalStamina.get(localPlayer);
		stamina.recover(localPlayer, value);
	}
}
