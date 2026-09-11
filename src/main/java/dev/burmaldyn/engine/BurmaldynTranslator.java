package dev.burmaldyn.engine;

import dev.burmaldyn.api.WordForm;
import java.util.Locale;
import java.util.function.UnaryOperator;

/**
 * Переводчик с русского на бурмалдинский.
 *
 * <p>Строка разбирается на куски: всё, что не является русским словом
 * (плейсхолдеры {@code %s}, цветовые коды {@code §a}, числа, латиница,
 * знаки препинания), проходит насквозь без изменений. Каждое русское слово
 * прогоняется через словарь и морфологию.</p>
 */
public final class BurmaldynTranslator {
	private final BurmaldynDictionary dictionary;

	public BurmaldynTranslator(final BurmaldynDictionary dictionary) {
		this.dictionary = dictionary;
	}

	public BurmaldynDictionary dictionary() {
		return this.dictionary;
	}

	/** Переводит целую строку, сохраняя форматирование и плейсхолдеры. */
	public String translate(final String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}

		StringBuilder result = new StringBuilder(text.length() + 16);
		int index = 0;

		while (index < text.length()) {
			char current = text.charAt(index);

			if (!Lexicon.isCyrillic(current)) {
				result.append(current);
				index++;
				continue;
			}

			int wordStart = index;

			while (index < text.length() && Lexicon.isCyrillic(text.charAt(index))) {
				index++;
			}

			result.append(this.translateWord(text.substring(wordStart, index)));
		}

		String translated = result.toString();

		for (UnaryOperator<String> postProcessor : this.dictionary.postProcessors()) {
			translated = postProcessor.apply(translated);
		}

		return translated;
	}

	/** Переводит одно слово, сохраняя регистр исходника. */
	public String translateWord(final String word) {
		String lowercase = word.toLowerCase(Locale.ROOT);
		String replacement = this.resolve(lowercase);

		return replacement == null ? word : restoreCase(word, replacement);
	}

	/**
	 * Ищет бурмалдинскую форму слова.
	 *
	 * @return замена или {@code null}, если слово надо оставить как есть
	 */
	private String resolve(final String lowercase) {
		String direct = this.dictionary.words().get(lowercase);

		if (direct != null) {
			return direct;
		}

		if (this.dictionary.skippedWords().contains(lowercase)) {
			return null;
		}

		for (ExceptionRule rule : this.dictionary.rules()) {
			String replaced = rule.apply(lowercase);

			if (replaced != null) {
				return replaced;
			}
		}

		WordForm form = WordAnalyzer.analyze(lowercase, this.dictionary.isForcedNoun(lowercase));

		if (form == null) {
			return null;
		}

		// Основу можно подменить словарём: «кошк» → «кот», чтобы вышло «котость».
		String stem = this.dictionary.stems().get(form.stem());

		if (stem != null) {
			form = new WordForm(stem, form.grammaticalCase(), form.plural());
		}

		return Inflector.burmaldynize(form);
	}

	/** «Кошка» → «Котость», «КОШКА» → «КОТОСТЬ», «кошка» → «котость». */
	private static String restoreCase(final String original, final String replacement) {
		if (replacement.isEmpty()) {
			return replacement;
		}

		boolean firstUpper = Character.isUpperCase(original.charAt(0));

		if (!firstUpper) {
			return replacement;
		}

		boolean allUpper = original.length() > 1 && original.chars().allMatch(Character::isUpperCase);

		if (allUpper) {
			return replacement.toUpperCase(Locale.ROOT);
		}

		return Character.toUpperCase(replacement.charAt(0)) + replacement.substring(1);
	}
}
