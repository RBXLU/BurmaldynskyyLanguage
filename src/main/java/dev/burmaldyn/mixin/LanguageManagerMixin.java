package dev.burmaldyn.mixin;

import dev.burmaldyn.BurmaldynClientLanguage;
import dev.burmaldyn.BurmaldynLanguage;
import dev.burmaldyn.BurmaldynMod;
import dev.burmaldyn.BurmaldynTranslations;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.locale.Language;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Встраивает бурмалдинский в ванильный менеджер языков.
 *
 * <p>Три вмешательства:</p>
 * <ol>
 *     <li>язык появляется в списке меню выбора языка — и всегда первым;</li>
 *     <li>менеджер знает описание языка по его коду;</li>
 *     <li>при выбранном бурмалдинском подменяются сами строки перевода.</li>
 * </ol>
 */
@Mixin(LanguageManager.class)
public abstract class LanguageManagerMixin {
	@Shadow
	private String currentCode;

	@Shadow
	private Map<String, LanguageInfo> languages;

	/**
	 * Бурмалдинский — первый в списке: список языков в меню строится ровно
	 * в том порядке, в котором его отдаёт {@code getLanguages()}.
	 */
	@Inject(method = "getLanguages", at = @At("HEAD"), cancellable = true)
	private void burmaldyn$listLanguage(final CallbackInfoReturnable<SortedMap<String, LanguageInfo>> info) {
		SortedMap<String, LanguageInfo> sorted = new TreeMap<>(burmaldyn$firstComparator());
		sorted.putAll(this.languages);
		sorted.put(BurmaldynLanguage.CODE, BurmaldynLanguage.INFO);
		info.setReturnValue(sorted);
	}

	/** Чтобы игра могла узнать язык по коду, даже если его нет ни в одном ресурспаке. */
	@Inject(method = "getLanguage", at = @At("HEAD"), cancellable = true)
	private void burmaldyn$getLanguage(final String code, final CallbackInfoReturnable<LanguageInfo> info) {
		if (BurmaldynLanguage.isSelected(code)) {
			info.setReturnValue(BurmaldynLanguage.INFO);
		}
	}

	/**
	 * Ванильная перезагрузка уже положила в игру английский (бурмалдинского
	 * нет ни в одном lang-файле, поэтому вышел запасной вариант). Теперь
	 * собираем настоящий перевод и подменяем язык целиком.
	 */
	@Inject(method = "onResourceManagerReload", at = @At("TAIL"))
	private void burmaldyn$applyTranslations(final ResourceManager resourceManager, final CallbackInfo info) {
		if (!BurmaldynLanguage.isSelected(this.currentCode)) {
			return;
		}

		try {
			BurmaldynClientLanguage language = BurmaldynTranslations.build(resourceManager);
			Language.inject(language);
		} catch (Throwable error) {
			// Сломанный словарь не должен мешать игре запускаться.
			BurmaldynMod.LOGGER.error("Не удалось собрать бурмалдинский перевод", error);
		}
	}

	/** Сравнение кодов языков: бурмалдинский впереди, остальные по алфавиту. */
	private static Comparator<String> burmaldyn$firstComparator() {
		return Comparator.comparing((String code) -> BurmaldynLanguage.CODE.equals(code) ? 0 : 1)
				.thenComparing(Comparator.naturalOrder());
	}
}
