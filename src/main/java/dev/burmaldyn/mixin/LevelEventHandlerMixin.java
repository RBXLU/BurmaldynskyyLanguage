package dev.burmaldyn.mixin;

import dev.burmaldyn.BurmaldynSounds;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Подменяет звук разбившегося зелья регенерации — то есть чекушки — на крик.
 *
 * <p>Разбитое зелье приходит на клиент событием мира 2002 (или 2007 для
 * мгновенных зелий), и единственное, что о зелье известно в этот момент, —
 * цвет его эффекта. По цвету регенерации и опознаём чекушку: у остальных
 * зелий звук остаётся ванильным.</p>
 */
@Mixin(LevelEventHandler.class)
public class LevelEventHandlerMixin {
	/** Цвет эффекта регенерации (0xCD5CAB) — им красится и зелье, и его брызги. */
	private static final int REGENERATION_COLOR = 13458603;

	/** Событие разбившегося зелья и его вариант для мгновенных эффектов. */
	private static final int POTION_SPLASH_EVENT = 2002;
	private static final int INSTANT_POTION_SPLASH_EVENT = 2007;

	@Redirect(
			method = "levelEvent",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound"
							+ "(Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;"
							+ "Lnet/minecraft/sounds/SoundSource;FFZ)V"
			)
	)
	private void burmaldyn$screamInsteadOfGlass(final ClientLevel level, final BlockPos soundPos,
			final SoundEvent sound, final SoundSource source, final float volume, final float pitch,
			final boolean distanceDelay, final int eventType, final BlockPos eventPos, final int color) {
		if (burmaldyn$isChekushka(sound, eventType, color)) {
			level.playLocalSound(soundPos, BurmaldynSounds.CHEKUSHKA_BREAK, SoundSource.NEUTRAL,
					1.0F, 1.0F, false);
			return;
		}

		level.playLocalSound(soundPos, sound, source, volume, pitch, distanceDelay);
	}

	private static boolean burmaldyn$isChekushka(final SoundEvent sound, final int eventType, final int color) {
		if (sound != SoundEvents.SPLASH_POTION_BREAK) {
			return false;
		}

		if (eventType != POTION_SPLASH_EVENT && eventType != INSTANT_POTION_SPLASH_EVENT) {
			return false;
		}

		// В старших битах цвета может лежать альфа-канал — сравниваем только RGB.
		return (color & 0xFFFFFF) == REGENERATION_COLOR;
	}
}
