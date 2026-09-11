package dev.burmaldyn.engine;

import dev.burmaldyn.api.Declension;
import dev.burmaldyn.api.GrammaticalCase;
import dev.burmaldyn.api.WordForm;

/**
 * Склонение бурмалдинских слов.
 *
 * <p>Главное правило языка: существительное превращается в слово на «-ость»
 * («хвост» → «хвостость») и дальше склоняется как обычное русское слово
 * третьего склонения — «хвостость, хвостости, хвостостью». Если основа
 * оканчивается на гласную, «о» выпадает: «герой» → «герость».</p>
 */
public final class Inflector {
	private Inflector() {
	}

	/**
	 * Делает из разобранного русского слова бурмалдинское, сохраняя падеж и число.
	 *
	 * @param form разбор исходного слова
	 * @return бурмалдинская словоформа в нижнем регистре
	 */
	public static String burmaldynize(final WordForm form) {
		String stem = form.stem();
		// «-ость» после согласной, «-сть» после гласной: котость, но герость.
		String root = Lexicon.endsWithVowel(stem) ? "ст" : "ост";

		return stem + root + suffix(form.grammaticalCase(), form.plural());
	}

	/** Окончание слова на «-ость» для нужного падежа и числа. */
	private static String suffix(final GrammaticalCase grammaticalCase, final boolean plural) {
		if (plural) {
			return switch (grammaticalCase) {
				case NOMINATIVE, ACCUSATIVE -> "и";
				case GENITIVE -> "ей";
				case DATIVE -> "ям";
				case INSTRUMENTAL -> "ями";
				case PREPOSITIONAL -> "ях";
			};
		}

		return switch (grammaticalCase) {
			case NOMINATIVE, ACCUSATIVE -> "ь";
			case GENITIVE, DATIVE, PREPOSITIONAL -> "и";
			case INSTRUMENTAL -> "ью";
		};
	}

	/**
	 * Склоняет слово-исключение («дод», «жинка») в заданный падеж.
	 *
	 * @param stem        основа слова-замены без окончания
	 * @param declension  тип склонения
	 * @param grammaticalCase падеж
	 * @param plural      множественное число
	 */
	public static String inflect(final String stem, final Declension declension,
			final GrammaticalCase grammaticalCase, final boolean plural) {
		if (stem.isEmpty() || declension == Declension.INDECLINABLE) {
			return stem;
		}

		boolean hushing = Lexicon.HUSHING.indexOf(stem.charAt(stem.length() - 1)) >= 0;
		boolean velar = Lexicon.VELAR_AND_HUSHING.indexOf(stem.charAt(stem.length() - 1)) >= 0;

		if (declension == Declension.MASCULINE) {
			if (plural) {
				return stem + switch (grammaticalCase) {
					case NOMINATIVE, ACCUSATIVE -> velar ? "и" : "ы";
					case GENITIVE -> hushing ? "ей" : "ов";
					case DATIVE -> "ам";
					case INSTRUMENTAL -> "ами";
					case PREPOSITIONAL -> "ах";
				};
			}

			return stem + switch (grammaticalCase) {
				case NOMINATIVE -> "";
				case GENITIVE, ACCUSATIVE -> "а";
				case DATIVE -> "у";
				case INSTRUMENTAL -> hushing ? "ем" : "ом";
				case PREPOSITIONAL -> "е";
			};
		}

		// Женский род на -а: жинка, жинки, жинке, жинку, жинкой.
		if (plural) {
			return stem + switch (grammaticalCase) {
				case NOMINATIVE, ACCUSATIVE -> velar ? "и" : "ы";
				case GENITIVE -> "";
				case DATIVE -> "ам";
				case INSTRUMENTAL -> "ами";
				case PREPOSITIONAL -> "ах";
			};
		}

		return stem + switch (grammaticalCase) {
			case NOMINATIVE -> "а";
			case GENITIVE -> velar ? "и" : "ы";
			case DATIVE, PREPOSITIONAL -> "е";
			case ACCUSATIVE -> "у";
			case INSTRUMENTAL -> hushing ? "ей" : "ой";
		};
	}
}
