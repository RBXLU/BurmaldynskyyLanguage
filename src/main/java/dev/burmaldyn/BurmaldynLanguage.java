package dev.burmaldyn;

import dev.burmaldyn.api.BurmaldynApi;
import net.minecraft.client.resources.language.LanguageInfo;

/** Описание бурмалдинского языка для ванильного меню выбора языка. */
public final class BurmaldynLanguage {
	/** Код языка. */
	public static final String CODE = BurmaldynApi.LANGUAGE_CODE;

	/** Строка в списке языков: «Бурмалдинский (Мурино)». */
	public static final LanguageInfo INFO = new LanguageInfo("Мурино", "Бурмалдинский", false);

	private BurmaldynLanguage() {
	}

	/** Выбран ли бурмалдинский прямо сейчас. */
	public static boolean isSelected(final String languageCode) {
		return CODE.equals(languageCode);
	}
}
