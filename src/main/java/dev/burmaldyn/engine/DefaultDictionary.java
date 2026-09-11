package dev.burmaldyn.engine;

import dev.burmaldyn.api.Declension;

/**
 * Встроенный словарь: слова-исключения бурмалдинского языка и пара подсказок
 * анализатору. Всё остальное мод выводит по правилам.
 */
public final class DefaultDictionary {
	private DefaultDictionary() {
	}

	/** Создаёт словарь со стандартными исключениями. */
	public static BurmaldynDictionary create() {
		BurmaldynDictionary dictionary = new BurmaldynDictionary();

		// Местоимение «я» → «ч». Склоняется как короткое слово мужского рода.
		dictionary.word("я", "ч");
		dictionary.word("меня", "ча");
		dictionary.word("мне", "чу");
		dictionary.word("мной", "чом");
		dictionary.word("мною", "чом");

		// дед → дод, сын → сыр, папа → батч, жена → жинка.
		dictionary.exception("дедушк", Declension.FEMININE_A, "дод", Declension.MASCULINE);
		dictionary.exception("дед", Declension.MASCULINE, "дод", Declension.MASCULINE);
		dictionary.exception("сын", Declension.MASCULINE, "сыр", Declension.MASCULINE);
		dictionary.exception("папаш", Declension.FEMININE_A, "батч", Declension.MASCULINE);
		dictionary.exception("пап", Declension.FEMININE_A, "батч", Declension.MASCULINE);
		dictionary.exception("жен", Declension.FEMININE_A, "жинк", Declension.FEMININE_A);
		dictionary.exception("жён", Declension.FEMININE_A, "жинк", Declension.FEMININE_A);

		// Неправильные формы «сын» во множественном числе.
		dictionary.word("сыновья", "сыры");
		dictionary.word("сыновей", "сыров");
		dictionary.word("сыновьям", "сырам");
		dictionary.word("сыновьями", "сырами");
		dictionary.word("сыновьях", "сырах");

		// дочь → дотч: в русском слово склоняется нерегулярно, перечисляем формы.
		dictionary.word("дочь", "дотч");
		dictionary.word("дочери", "дотча");
		dictionary.word("дочерью", "дотчем");
		dictionary.word("дочерей", "дотчей");
		dictionary.word("дочерям", "дотчам");
		dictionary.word("дочерями", "дотчами");
		dictionary.word("дочерях", "дотчах");
		dictionary.word("дочка", "дотч");
		dictionary.word("дочки", "дотча");
		dictionary.word("дочку", "дотча");
		dictionary.word("дочкой", "дотчем");

		// друзья → друны: множественное число «друга» тоже нерегулярное.
		dictionary.word("друзья", "друны");
		dictionary.word("друзей", "друнов");
		dictionary.word("друзьям", "друнам");
		dictionary.word("друзьями", "друнами");
		dictionary.word("друзьях", "друнах");

		// Канонические основы: кошка и кот в бурмалдинском — одна и та же котость.
		dictionary.stem("кошк", "кот");
		dictionary.stem("собачк", "собак");

		// Существительные, которые эвристика принимает за глаголы:
		// «кровать» — не глагол, «пчела» — не «пчела что-то сделала».
		for (String stem : new String[]{
				"кроват", "лошад", "площад", "тетрад", "пчел", "стрел", "скал", "смол",
				"игл", "метл", "пил", "сил", "зол", "щел", "постел", "модел", "медал",
				"портал", "сигнал", "канал", "финал", "журнал", "квартал", "материал",
				"кристал", "узел", "узл", "стул", "котёл", "котл", "орёл", "орл", "осёл",
				"осл", "пепел", "пепл", "уголь", "угл", "минут", "маршрут", "парашют",
				"хомут", "салют", "ребят", "гост", "кост", "сет", "путеш"}) {
			dictionary.forcedNounStem(stem);
		}

		// Несклоняемые заимствования проще прописать вручную.
		dictionary.word("меню", "менюсть");
		dictionary.word("радио", "радиость");
		dictionary.word("видео", "видеость");

		// Имена собственные оставляем как есть.
		dictionary.skip("майнкрафт");
		dictionary.skip("стив");
		dictionary.skip("алекс");
		dictionary.skip("мохамед");
		dictionary.skip("бурмалдинский");
		dictionary.skip("мурино");

		return dictionary;
	}
}
