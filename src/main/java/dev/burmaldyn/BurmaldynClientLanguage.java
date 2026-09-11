package dev.burmaldyn;

import java.util.Map;
import net.minecraft.client.resources.language.FormattedBidiReorder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

/**
 * Загруженный бурмалдинский язык: обычная карта «ключ → строка»,
 * которую игра спрашивает при каждом обращении к переводу.
 */
public final class BurmaldynClientLanguage extends Language {
	private final Map<String, String> storage;

	public BurmaldynClientLanguage(final Map<String, String> storage) {
		this.storage = storage;
	}

	@Override
	public String getOrDefault(final String key, final String defaultValue) {
		return this.storage.getOrDefault(key, defaultValue);
	}

	@Override
	public boolean has(final String key) {
		return this.storage.containsKey(key);
	}

	@Override
	public boolean isDefaultRightToLeft() {
		return false;
	}

	@Override
	public FormattedCharSequence getVisualOrder(final FormattedText logicalOrderText) {
		return FormattedBidiReorder.reorder(logicalOrderText, false);
	}

	/** Сколько строк удалось собрать — для лога. */
	public int size() {
		return this.storage.size();
	}
}
