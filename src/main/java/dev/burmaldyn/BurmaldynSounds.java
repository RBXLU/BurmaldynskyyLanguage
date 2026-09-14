package dev.burmaldyn;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

/**
 * Звуки мода.
 *
 * <p>Событие звука не регистрируется в реестре: клиенту достаточно
 * идентификатора, по которому он найдёт звук в {@code assets/burmaldyn/sounds.json}.
 * Так мод остаётся чисто клиентским и ничего не ломает на сервере.</p>
 */
public final class BurmaldynSounds {
	/** Крик, который раздаётся, когда разбивается чекушка. */
	public static final SoundEvent CHEKUSHKA_BREAK = SoundEvent.createVariableRangeEvent(
			Identifier.fromNamespaceAndPath(BurmaldynMod.MOD_ID, "chekushka_break"));

	private BurmaldynSounds() {
	}
}
