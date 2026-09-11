package dev.burmaldyn.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.burmaldyn.BurmaldynMod;
import dev.burmaldyn.api.Declension;
import dev.burmaldyn.engine.BurmaldynDictionary;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Загрузка словарей из ресурсов: {@code assets/<namespace>/burmaldyn/*.json}.
 *
 * <p>Это второй способ расширять перевод — без единой строчки кода, прямо
 * из ресурспака или из ассетов другого мода. Формат файла:</p>
 *
 * <pre>{@code
 * {
 *   "words":             { "меню": "менюсть" },
 *   "stems":             { "кошк": "кот" },
 *   "exceptions":        { "жен": { "target": "жинк",
 *                                   "source_declension": "feminine",
 *                                   "target_declension": "feminine" } },
 *   "forced_nouns":      [ "кровать" ],
 *   "forced_noun_stems": [ "пчел" ],
 *   "skip":              [ "стив" ],
 *   "overrides":         { "block.minecraft.stone": "Каменость" }
 * }
 * }</pre>
 */
public final class DictionaryLoader {
	private static final Gson GSON = new Gson();

	/** Папка внутри {@code assets/<namespace>/}, где мод ищет словари. */
	public static final String DIRECTORY = "burmaldyn";

	private DictionaryLoader() {
	}

	/** Читает все словари из активных ресурспаков и складывает их в {@code target}. */
	public static void loadInto(final ResourceManager resourceManager, final BurmaldynDictionary target) {
		Map<Identifier, List<Resource>> found = resourceManager.listResourceStacks(
				DIRECTORY, identifier -> identifier.getPath().endsWith(".json"));

		for (Map.Entry<Identifier, List<Resource>> entry : found.entrySet()) {
			for (Resource resource : entry.getValue()) {
				try (InputStream stream = resource.open()) {
					read(stream, target);
				} catch (Exception error) {
					BurmaldynMod.LOGGER.warn("Не удалось прочитать бурмалдинский словарь {}",
							entry.getKey(), error);
				}
			}
		}
	}

	private static void read(final InputStream stream, final BurmaldynDictionary target) {
		JsonObject root = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);

		if (root == null) {
			return;
		}

		forEachString(root, "words", target::word);
		forEachString(root, "stems", target::stem);
		forEachString(root, "overrides", target::override);
		forEachElement(root, "forced_nouns", target::forcedNoun);
		forEachElement(root, "forced_noun_stems", target::forcedNounStem);
		forEachElement(root, "skip", target::skip);
		readExceptions(root, target);
	}

	private static void readExceptions(final JsonObject root, final BurmaldynDictionary target) {
		if (!root.has("exceptions") || !root.get("exceptions").isJsonObject()) {
			return;
		}

		for (Map.Entry<String, JsonElement> entry : root.getAsJsonObject("exceptions").entrySet()) {
			if (!entry.getValue().isJsonObject()) {
				continue;
			}

			JsonObject rule = entry.getValue().getAsJsonObject();

			if (!rule.has("target")) {
				continue;
			}

			target.exception(entry.getKey(),
					declension(rule, "source_declension"),
					rule.get("target").getAsString(),
					declension(rule, "target_declension"));
		}
	}

	/** {@code "masculine"}, {@code "feminine"} или {@code "indeclinable"}. */
	private static Declension declension(final JsonObject rule, final String field) {
		if (!rule.has(field)) {
			return Declension.MASCULINE;
		}

		return switch (rule.get(field).getAsString().toLowerCase(Locale.ROOT)) {
			case "feminine", "feminine_a", "женский" -> Declension.FEMININE_A;
			case "indeclinable", "несклоняемое" -> Declension.INDECLINABLE;
			default -> Declension.MASCULINE;
		};
	}

	private interface PairConsumer {
		void accept(String key, String value);
	}

	private static void forEachString(final JsonObject root, final String field, final PairConsumer consumer) {
		if (!root.has(field) || !root.get(field).isJsonObject()) {
			return;
		}

		for (Map.Entry<String, JsonElement> entry : root.getAsJsonObject(field).entrySet()) {
			if (entry.getValue().isJsonPrimitive()) {
				consumer.accept(entry.getKey(), entry.getValue().getAsString());
			}
		}
	}

	private static void forEachElement(final JsonObject root, final String field,
			final java.util.function.Consumer<String> consumer) {
		if (!root.has(field) || !root.get(field).isJsonArray()) {
			return;
		}

		JsonArray array = root.getAsJsonArray(field);

		for (JsonElement element : array) {
			if (element.isJsonPrimitive()) {
				consumer.accept(element.getAsString());
			}
		}
	}
}
