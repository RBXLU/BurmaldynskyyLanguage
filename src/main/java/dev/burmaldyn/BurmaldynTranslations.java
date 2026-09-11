package dev.burmaldyn;

import dev.burmaldyn.api.BurmaldynApi;
import dev.burmaldyn.data.DictionaryLoader;
import dev.burmaldyn.engine.BurmaldynDictionary;
import dev.burmaldyn.engine.BurmaldynTranslator;
import dev.burmaldyn.mixin.ClientLanguageAccessor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Сборка бурмалдинского перевода.
 *
 * <p>Порядок слоёв (каждый следующий перекрывает предыдущий):</p>
 * <ol>
 *     <li>автоперевод русского {@code ru_ru} (с английским как запасным вариантом);</li>
 *     <li>ручные строки из {@code assets/<namespace>/lang/brm_brm.json} любого мода или ресурспака;</li>
 *     <li>переопределения ключей из словарей ({@code overrides}).</li>
 * </ol>
 */
public final class BurmaldynTranslations {
	private BurmaldynTranslations() {
	}

	/** Собирает полный словарь строк для текущего набора ресурсов. */
	public static BurmaldynClientLanguage build(final ResourceManager resourceManager) {
		long started = System.currentTimeMillis();

		// Постоянные правила модов + словари из ресурспаков (они живут только до перезагрузки).
		BurmaldynDictionary dictionary = BurmaldynApi.dictionary().copy();
		DictionaryLoader.loadInto(resourceManager, dictionary);

		BurmaldynTranslator translator = new BurmaldynTranslator(dictionary);
		BurmaldynApi.setTranslator(translator);

		Map<String, String> source = storageOf(ClientLanguage.loadFrom(resourceManager,
				List.of("en_us", BurmaldynApi.SOURCE_LANGUAGE_CODE), false));
		Map<String, String> result = new HashMap<>(source.size());

		for (Map.Entry<String, String> entry : source.entrySet()) {
			result.put(entry.getKey(), translator.translate(entry.getValue()));
		}

		// Ручные переводы: обычные lang-файлы с кодом бурмалдинского.
		result.putAll(storageOf(ClientLanguage.loadFrom(resourceManager, List.of(BurmaldynLanguage.CODE), false)));

		// Переопределения из словарей — последнее слово за ними.
		result.putAll(dictionary.overrides());

		BurmaldynMod.LOGGER.info("Бурмалдинский собран: {} строк за {} мс",
				result.size(), System.currentTimeMillis() - started);

		return new BurmaldynClientLanguage(Map.copyOf(result));
	}

	private static Map<String, String> storageOf(final ClientLanguage language) {
		return ((ClientLanguageAccessor) language).burmaldyn$storage();
	}
}
