package dev.burmaldyn.engine;

import dev.burmaldyn.api.BurmaldynRegistry;
import dev.burmaldyn.api.Declension;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * Словарь бурмалдинского языка: всё, чем можно поправить работу автоперевода.
 *
 * <p>Словари складываются слоями: сначала встроенный словарь мода, затем
 * словари других модов (через API), затем словари из ресурспаков. Каждый
 * следующий слой перекрывает предыдущий.</p>
 */
public final class BurmaldynDictionary implements BurmaldynRegistry {
	private final Map<String, String> words = new HashMap<>();
	private final Map<String, String> stems = new HashMap<>();
	private final List<ExceptionRule> rules = new ArrayList<>();
	private final Set<String> forcedNouns = new HashSet<>();
	private final Set<String> forcedNounStems = new HashSet<>();
	private final Set<String> skippedWords = new HashSet<>();
	private final Map<String, String> overrides = new LinkedHashMap<>();
	private final List<UnaryOperator<String>> postProcessors = new ArrayList<>();

	/** Замена конкретной словоформы целиком: {@code кошка → котость}. */
	@Override
	public void word(final String word, final String replacement) {
		this.words.put(normalize(word), replacement);
	}

	/** Замена основы: {@code кошк → кот}, дальше слово склоняется по общим правилам. */
	@Override
	public void stem(final String sourceStem, final String targetStem) {
		this.stems.put(normalize(sourceStem), targetStem);
	}

	/** Слово-исключение со склонением: {@code жен → жинк}. */
	@Override
	public void exception(final String sourceStem, final Declension sourceDeclension,
			final String targetStem, final Declension targetDeclension) {
		this.rules.add(new ExceptionRule(normalize(sourceStem), sourceDeclension,
				targetStem, targetDeclension));
		// Длинные основы проверяем первыми, иначе «дедушк» перебьётся правилом «дед».
		this.rules.sort(Comparator.comparingInt((ExceptionRule rule) -> rule.sourceStem().length()).reversed());
	}

	/** Считать слово существительным, даже если анализатор так не думает. */
	@Override
	public void forcedNoun(final String word) {
		this.forcedNouns.add(normalize(word));
	}

	/**
	 * Считать существительными все формы слова с такой основой:
	 * {@code пчел} закрывает «пчела», «пчелы», «пчелой» и так далее.
	 */
	@Override
	public void forcedNounStem(final String stem) {
		this.forcedNounStems.add(normalize(stem));
	}

	/** Проверяет, объявлено ли слово существительным принудительно. */
	public boolean isForcedNoun(final String word) {
		if (this.forcedNouns.contains(word)) {
			return true;
		}

		for (String stem : this.forcedNounStems) {
			if (word.startsWith(stem) && Lexicon.NOUN_ENDINGS.contains(word.substring(stem.length()))) {
				return true;
			}
		}

		return false;
	}

	/** Никогда не трогать это слово. */
	@Override
	public void skip(final String word) {
		this.skippedWords.add(normalize(word));
	}

	/** Готовый перевод для конкретного ключа локализации — автоперевод для него не применяется. */
	@Override
	public void override(final String translationKey, final String value) {
		this.overrides.put(translationKey, value);
	}

	/** Финальная обработка каждой переведённой строки. */
	@Override
	public void postProcess(final UnaryOperator<String> postProcessor) {
		this.postProcessors.add(postProcessor);
	}

	/** Добавляет поверх текущего словаря содержимое другого. */
	public void merge(final BurmaldynDictionary other) {
		this.words.putAll(other.words);
		this.stems.putAll(other.stems);
		this.forcedNouns.addAll(other.forcedNouns);
		this.forcedNounStems.addAll(other.forcedNounStems);
		this.skippedWords.addAll(other.skippedWords);
		this.overrides.putAll(other.overrides);
		this.postProcessors.addAll(other.postProcessors);
		this.rules.addAll(other.rules);
		this.rules.sort(Comparator.comparingInt((ExceptionRule rule) -> rule.sourceStem().length()).reversed());
	}

	/** Копия словаря — чтобы слой ресурспаков не портил постоянные словари. */
	public BurmaldynDictionary copy() {
		BurmaldynDictionary copy = new BurmaldynDictionary();
		copy.merge(this);
		return copy;
	}

	public Map<String, String> words() {
		return this.words;
	}

	public Map<String, String> stems() {
		return this.stems;
	}

	public List<ExceptionRule> rules() {
		return this.rules;
	}

	public Set<String> forcedNouns() {
		return this.forcedNouns;
	}

	public Set<String> forcedNounStems() {
		return this.forcedNounStems;
	}

	public Set<String> skippedWords() {
		return this.skippedWords;
	}

	public Map<String, String> overrides() {
		return this.overrides;
	}

	public List<UnaryOperator<String>> postProcessors() {
		return this.postProcessors;
	}

	private static String normalize(final String word) {
		return word.toLowerCase(Locale.ROOT);
	}
}
