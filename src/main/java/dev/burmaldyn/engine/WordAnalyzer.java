package dev.burmaldyn.engine;

import dev.burmaldyn.api.GrammaticalCase;
import dev.burmaldyn.api.WordForm;

/**
 * Морфологический анализатор «на глазок».
 *
 * <p>Задача простая: понять, что перед нами существительное, и отрезать
 * падежное окончание. Настоящего словаря русского языка в моде нет, поэтому
 * анализатор работает по окончаниям и стоп-спискам из {@link Lexicon}.
 * Все спорные случаи трактуются в пользу «это не существительное» — так мод
 * портит меньше строк, а недостающие слова добавляются словарём.</p>
 */
public final class WordAnalyzer {
	/** Минимальная длина основы: короче — почти наверняка мусор. */
	private static final int MIN_STEM_LENGTH = 2;

	private WordAnalyzer() {
	}

	/**
	 * Разбирает слово в нижнем регистре.
	 *
	 * @param word   слово из текста
	 * @param forced {@code true}, если слово принудительно объявлено существительным
	 *               (через словарь) — тогда проверки части речи пропускаются
	 * @return разбор или {@code null}, если слово трогать не надо
	 */
	public static WordForm analyze(final String word, final boolean forced) {
		if (word.length() < 3) {
			return null;
		}

		if (!forced) {
			if (Lexicon.STOP_WORDS.contains(word)) {
				return null;
			}

			// Слово уже бурмалдинское (или просто оканчивается на -ость) — второй раз не склоняем.
			if (Lexicon.endsWithAny(word, Lexicon.ALREADY_BURMALDYN)) {
				return null;
			}

			if (isVerbLike(word) || isAdjectiveLike(word)) {
				return null;
			}
		}

		return splitEnding(word);
	}

	/** Глаголы, причастия и деепричастия: их бурмалдинский не трогает. */
	public static boolean isVerbLike(final String word) {
		for (String ending : Lexicon.VERB_PAST_ENDINGS) {
			if (word.endsWith(ending) && word.length() >= 4) {
				return true;
			}
		}

		for (String ending : Lexicon.VERB_ENDINGS) {
			if (!word.endsWith(ending)) {
				continue;
			}

			// Короткие слова вроде «путь» или «сеть» — это существительные,
			// поэтому глагольное окончание засчитываем только длинным словам.
			if (word.length() >= 5 && word.length() >= ending.length() + 2) {
				return true;
			}
		}

		return false;
	}

	/** Прилагательные, порядковые числительные и причастия в полной форме. */
	public static boolean isAdjectiveLike(final String word) {
		// «-ие» бывает и у прилагательных («синие»), и у существительных («здание»).
		if (word.endsWith("ие") || word.endsWith("ье")) {
			return !Lexicon.endsWithAny(word, Lexicon.NEUTER_IE_ENDINGS) && !word.endsWith("ье");
		}

		if (Lexicon.endsWithAny(word, Lexicon.ADJECTIVE_ENDINGS)) {
			return true;
		}

		// «-ой», «-ей», «-ым», «-им» — окончания и прилагательных («большой»),
		// и существительных в творительном падеже («рукой»). Различаем по основе.
		for (String ending : Lexicon.AMBIGUOUS_ADJECTIVE_ENDINGS) {
			if (word.endsWith(ending) && word.length() >= 5) {
				String stem = word.substring(0, word.length() - ending.length());

				if (Lexicon.endsWithAny(stem, Lexicon.ADJECTIVE_MARKERS)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Отрезает падежное окончание и заодно определяет падеж и число.
	 *
	 * <p>Русские окончания омонимичны, поэтому для каждого выбран самый частый
	 * разбор. Родительный и предложный падежи всё равно дают одно и то же
	 * бурмалдинское окончание («-ости»), так что часть ошибок не видна.</p>
	 */
	private static WordForm splitEnding(final String word) {
		// Средний род на «-ие/-ье»: «здание», «зелье» — именительный падеж.
		if ((word.endsWith("ие") || word.endsWith("ье")) && word.length() > 3) {
			return form(word, 1, GrammaticalCase.NOMINATIVE, false);
		}

		// Множественное число — окончания длиннее и однозначнее, проверяем их первыми.
		if (word.endsWith("ами") || word.endsWith("ями")) {
			return form(word, 3, GrammaticalCase.INSTRUMENTAL, true);
		}

		if (word.endsWith("ах") || word.endsWith("ях")) {
			return form(word, 2, GrammaticalCase.PREPOSITIONAL, true);
		}

		if (word.endsWith("ам") || word.endsWith("ям")) {
			return form(word, 2, GrammaticalCase.DATIVE, true);
		}

		if (word.endsWith("ов") || word.endsWith("ев") || word.endsWith("ёв")) {
			return form(word, 2, GrammaticalCase.GENITIVE, true);
		}

		// Единственное число.
		if (word.endsWith("ом") || word.endsWith("ем") || word.endsWith("ём")
				|| word.endsWith("ой") || word.endsWith("ей") || word.endsWith("ёй")
				|| word.endsWith("ью")) {
			return form(word, 2, GrammaticalCase.INSTRUMENTAL, false);
		}

		char last = word.charAt(word.length() - 1);

		return switch (last) {
			case 'а', 'я' -> form(word, 1, GrammaticalCase.NOMINATIVE, false);
			case 'у', 'ю' -> form(word, 1, GrammaticalCase.ACCUSATIVE, false);
			case 'ы', 'и' -> form(word, 1, GrammaticalCase.GENITIVE, false);
			case 'е' -> form(word, 1, GrammaticalCase.PREPOSITIONAL, false);
			case 'о', 'ё' -> form(word, 1, GrammaticalCase.NOMINATIVE, false);
			case 'ь', 'й' -> form(word, 1, GrammaticalCase.NOMINATIVE, false);
			case 'э' -> null;
			default -> form(word, 0, GrammaticalCase.NOMINATIVE, false);
		};
	}

	private static WordForm form(final String word, final int endingLength,
			final GrammaticalCase grammaticalCase, final boolean plural) {
		String stem = word.substring(0, word.length() - endingLength);

		// «зелье» → «зель» → «зел»: мягкий знак и «й» в основу не входят.
		if (stem.length() > MIN_STEM_LENGTH
				&& (stem.endsWith("ь") || stem.endsWith("й"))) {
			stem = stem.substring(0, stem.length() - 1);
		}

		if (stem.length() < MIN_STEM_LENGTH) {
			// «дом» — это не «д» в творительном падеже: короткие слова
			// считаем целиком основой в именительном падеже.
			return wholeWordAsStem(word);
		}

		return new WordForm(stem, grammaticalCase, plural);
	}

	/** Запасной разбор: всё слово — основа, падеж именительный. */
	private static WordForm wholeWordAsStem(final String word) {
		char last = word.charAt(word.length() - 1);

		if (word.length() < 3 || Lexicon.VOWELS.indexOf(last) >= 0 || last == 'ь' || last == 'й') {
			return null;
		}

		return new WordForm(word, GrammaticalCase.NOMINATIVE, false);
	}
}
