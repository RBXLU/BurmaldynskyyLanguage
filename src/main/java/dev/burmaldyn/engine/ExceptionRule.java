package dev.burmaldyn.engine;

import dev.burmaldyn.api.Declension;
import dev.burmaldyn.api.GrammaticalCase;
import dev.burmaldyn.api.WordForm;
import java.util.Map;

/**
 * Слово-исключение: целиком заменяется другим словом, но падеж сохраняется.
 *
 * <p>Например, правило {@code жен → жинк} превращает «жена» в «жинку»
 * там, где в исходном тексте был винительный падеж, и в «жинки» — там,
 * где был родительный.</p>
 *
 * @param sourceStem       основа исходного слова («жен»)
 * @param sourceDeclension по какому типу склоняется исходное слово
 * @param targetStem       основа слова-замены («жинк»)
 * @param targetDeclension по какому типу склоняется замена
 */
public record ExceptionRule(String sourceStem, Declension sourceDeclension,
		String targetStem, Declension targetDeclension) {

	/** Окончания слов мужского рода на согласный: дед, деда, деду, дедом… */
	private static final Map<String, WordForm> MASCULINE_ENDINGS = Map.ofEntries(
			Map.entry("", form(GrammaticalCase.NOMINATIVE, false)),
			Map.entry("а", form(GrammaticalCase.GENITIVE, false)),
			Map.entry("у", form(GrammaticalCase.DATIVE, false)),
			Map.entry("ом", form(GrammaticalCase.INSTRUMENTAL, false)),
			Map.entry("ем", form(GrammaticalCase.INSTRUMENTAL, false)),
			Map.entry("е", form(GrammaticalCase.PREPOSITIONAL, false)),
			Map.entry("ы", form(GrammaticalCase.NOMINATIVE, true)),
			Map.entry("и", form(GrammaticalCase.NOMINATIVE, true)),
			Map.entry("ов", form(GrammaticalCase.GENITIVE, true)),
			Map.entry("ев", form(GrammaticalCase.GENITIVE, true)),
			Map.entry("ам", form(GrammaticalCase.DATIVE, true)),
			Map.entry("ям", form(GrammaticalCase.DATIVE, true)),
			Map.entry("ами", form(GrammaticalCase.INSTRUMENTAL, true)),
			Map.entry("ями", form(GrammaticalCase.INSTRUMENTAL, true)),
			Map.entry("ах", form(GrammaticalCase.PREPOSITIONAL, true)),
			Map.entry("ях", form(GrammaticalCase.PREPOSITIONAL, true))
	);

	/** Окончания слов женского рода на -а: жена, жены, жене, жену, женой… */
	private static final Map<String, WordForm> FEMININE_ENDINGS = Map.ofEntries(
			Map.entry("а", form(GrammaticalCase.NOMINATIVE, false)),
			Map.entry("я", form(GrammaticalCase.NOMINATIVE, false)),
			Map.entry("ы", form(GrammaticalCase.GENITIVE, false)),
			Map.entry("и", form(GrammaticalCase.GENITIVE, false)),
			Map.entry("е", form(GrammaticalCase.DATIVE, false)),
			Map.entry("у", form(GrammaticalCase.ACCUSATIVE, false)),
			Map.entry("ю", form(GrammaticalCase.ACCUSATIVE, false)),
			Map.entry("ой", form(GrammaticalCase.INSTRUMENTAL, false)),
			Map.entry("ей", form(GrammaticalCase.INSTRUMENTAL, false)),
			Map.entry("ою", form(GrammaticalCase.INSTRUMENTAL, false)),
			Map.entry("", form(GrammaticalCase.GENITIVE, true)),
			Map.entry("ам", form(GrammaticalCase.DATIVE, true)),
			Map.entry("ям", form(GrammaticalCase.DATIVE, true)),
			Map.entry("ами", form(GrammaticalCase.INSTRUMENTAL, true)),
			Map.entry("ями", form(GrammaticalCase.INSTRUMENTAL, true)),
			Map.entry("ах", form(GrammaticalCase.PREPOSITIONAL, true)),
			Map.entry("ях", form(GrammaticalCase.PREPOSITIONAL, true))
	);

	private static WordForm form(final GrammaticalCase grammaticalCase, final boolean plural) {
		return new WordForm("", grammaticalCase, plural);
	}

	/**
	 * Пробует применить правило к слову в нижнем регистре.
	 *
	 * @return бурмалдинская форма или {@code null}, если правило не подходит
	 */
	public String apply(final String word) {
		if (!word.startsWith(this.sourceStem) || word.length() < this.sourceStem.length()) {
			return null;
		}

		String ending = word.substring(this.sourceStem.length());
		Map<String, WordForm> endings = this.sourceDeclension == Declension.FEMININE_A
				? FEMININE_ENDINGS
				: MASCULINE_ENDINGS;
		WordForm matched = endings.get(ending);

		if (matched == null) {
			return null;
		}

		return Inflector.inflect(this.targetStem, this.targetDeclension,
				matched.grammaticalCase(), matched.plural());
	}
}
