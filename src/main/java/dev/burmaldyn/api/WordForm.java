package dev.burmaldyn.api;

/**
 * Результат морфологического разбора русского слова.
 *
 * @param stem           основа без падежного окончания («хвост» для «хвостом»)
 * @param grammaticalCase падеж исходной словоформы
 * @param plural         {@code true}, если слово стоит во множественном числе
 */
public record WordForm(String stem, GrammaticalCase grammaticalCase, boolean plural) {
}
