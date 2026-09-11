package dev.burmaldyn.api;

import dev.burmaldyn.engine.BurmaldynDictionary;
import dev.burmaldyn.engine.BurmaldynTranslator;
import dev.burmaldyn.engine.DefaultDictionary;

/**
 * Публичная точка входа бурмалдинского языка.
 *
 * <p>Всё, что нужно другому моду:</p>
 *
 * <pre>{@code
 * BurmaldynApi.registry().override("item.mymod.sword", "Мечость судьбости");
 * String text = BurmaldynApi.translate("Каменная кирка"); // Каменная киркость
 * }</pre>
 *
 * <p>Класс потокобезопасен настолько, насколько это нужно моду: правила
 * регистрируются при запуске игры, а читаются при перезагрузке ресурсов.</p>
 */
public final class BurmaldynApi {
	/** Код языка в меню выбора языка и в {@code options.txt}. */
	public static final String LANGUAGE_CODE = "brm_brm";

	/** Язык, с которого делается автоперевод. */
	public static final String SOURCE_LANGUAGE_CODE = "ru_ru";

	/** Постоянный словарь: встроенные правила плюс всё, что добавили моды. */
	private static final BurmaldynDictionary REGISTRY = DefaultDictionary.create();

	/** Переводчик текущей загрузки ресурсов (со словарями из ресурспаков). */
	private static volatile BurmaldynTranslator activeTranslator = new BurmaldynTranslator(REGISTRY);

	private BurmaldynApi() {
	}

	/**
	 * Реестр правил перевода. Добавлять правила можно в любой момент, но
	 * применятся они при следующей перезагрузке ресурсов (F3+T или смене языка).
	 */
	public static BurmaldynRegistry registry() {
		return REGISTRY;
	}

	/**
	 * Переводит произвольный русский текст на бурмалдинский.
	 *
	 * <p>Плейсхолдеры ({@code %s}, {@code %1$s}), коды цвета ({@code §a}),
	 * латиница и числа остаются нетронутыми.</p>
	 */
	public static String translate(final String russianText) {
		return activeTranslator.translate(russianText);
	}

	/** Переводит одно слово, сохраняя регистр. */
	public static String translateWord(final String russianWord) {
		return activeTranslator.translateWord(russianWord);
	}

	/** Словарь для внутреннего использования модом. */
	public static BurmaldynDictionary dictionary() {
		return REGISTRY;
	}

	/** Переводчик, собранный на последней перезагрузке ресурсов. */
	public static BurmaldynTranslator translator() {
		return activeTranslator;
	}

	/** Вызывается модом при пересборке перевода. */
	public static void setTranslator(final BurmaldynTranslator translator) {
		activeTranslator = translator;
	}
}
