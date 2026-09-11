package dev.burmaldyn.api;

import java.util.function.UnaryOperator;

/**
 * Реестр правил бурмалдинского языка — то, через что моды и ресурспаки
 * дописывают перевод.
 *
 * <p>Экземпляр выдаётся точкой входа {@link BurmaldynExtension} и доступен
 * через {@link BurmaldynApi#registry()}. Любое правило, добавленное сюда,
 * применяется при следующей перезагрузке ресурсов.</p>
 *
 * <p>Порядок применения при переводе слова:</p>
 * <ol>
 *     <li>{@link #word(String, String)} — точное совпадение словоформы;</li>
 *     <li>{@link #skip(String)} — слово не трогаем;</li>
 *     <li>{@link #exception(String, Declension, String, Declension)} — замена с сохранением падежа;</li>
 *     <li>морфологический разбор + {@link #stem(String, String)};</li>
 *     <li>общее правило «существительное + -ость/-сть».</li>
 * </ol>
 */
public interface BurmaldynRegistry {
	/**
	 * Жёсткая замена конкретной словоформы.
	 *
	 * <pre>{@code registry.word("меню", "менюсть");}</pre>
	 */
	void word(String form, String replacement);

	/**
	 * Подмена основы перед добавлением «-ость»: {@code кошк → кот} даёт
	 * «кошку» → «котость», «кошки» → «котости».
	 */
	void stem(String sourceStem, String targetStem);

	/**
	 * Слово-исключение: заменяется целиком, но падеж исходного слова сохраняется.
	 *
	 * <pre>{@code registry.exception("жен", Declension.FEMININE_A, "жинк", Declension.FEMININE_A);}</pre>
	 *
	 * @param sourceStem       основа исходного слова без окончания
	 * @param sourceDeclension как склоняется исходное слово
	 * @param targetStem       основа слова-замены
	 * @param targetDeclension как склоняется замена
	 */
	void exception(String sourceStem, Declension sourceDeclension, String targetStem, Declension targetDeclension);

	/** Считать слово существительным, даже если анализатор решил иначе. */
	void forcedNoun(String form);

	/** То же, но сразу для всех падежных форм с указанной основой. */
	void forcedNounStem(String stem);

	/** Никогда не переводить это слово (имена собственные, названия модов). */
	void skip(String form);

	/**
	 * Готовая строка для ключа локализации — автоперевод для этого ключа
	 * не применяется вообще.
	 *
	 * <pre>{@code registry.override("block.minecraft.stone", "Каменость");}</pre>
	 */
	void override(String translationKey, String value);

	/** Финальная обработка каждой уже переведённой строки. */
	void postProcess(UnaryOperator<String> processor);
}
