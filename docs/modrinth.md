<!-- Готовое описание для страницы мода на Modrinth. / Ready-to-paste Modrinth description. -->

# Burmaldyn Language

*English below · Русское описание ниже*

**Burmaldyn** is Russian with one twist: every noun takes the suffix **«-ость»**
(*-ost'*, roughly the Russian equivalent of English *-ness*) — or **«-сть»** after
a vowel — while keeping its grammatical case and number. The result is a language
where a cat is a *catness* and a tail is a *tailness*.

> **Russian:** Я вернулся домой и увидел свою кошку, у кошки был чёрный хвост.
>
> **Burmaldyn:** Ч вернулся домой и увидел свою котость, у котости был чёрный хвостость.

The language appears in the vanilla **Options → Language** menu, at the very top of
the list. **Every** string in the game is translated, including strings from other
mods: the mod takes the vanilla Russian locale (`ru_ru`) and runs it through a
morphological engine during resource loading. All of vanilla (8718 strings) is
processed in about 200 ms.

![Language menu](https://raw.githubusercontent.com/RBXLU/BurmaldynskyyLanguage/main/docs/screenshot-language-menu.png)

![Title screen](https://raw.githubusercontent.com/RBXLU/BurmaldynskyyLanguage/main/docs/screenshot-title.png)

![Options screen](https://raw.githubusercontent.com/RBXLU/BurmaldynskyyLanguage/main/docs/screenshot-options.png)

## Language rules

| Rule | Example |
| --- | --- |
| Noun + «-ость» | `хвост` (tail) → `хвостость` |
| Vowel-final stem takes «-сть» | `расстояние` (distance) → `расстоянисть` |
| Grammatical case is preserved | `у кошки` → `у котости`, `с мечом` → `с мечостью` |
| Plural is preserved | `с алмазами` (with diamonds) → `с алмазостями` |
| Capitalisation is preserved | `Кирка` → `Киркость`, `КИРКА` → `КИРКОСТЬ` |
| Verbs, adjectives and function words are left alone | `Каменная кирка сломалась` → `Каменная киркость сломалась` |
| Placeholders and colour codes stay intact | `§eПривет, %s!` → `§eПриветость, %s!` |

### Exception words

| Russian | Burmaldyn |
| --- | --- |
| я (I) | ч |
| дед (grandfather) | дод |
| папа (dad) | батч |
| сын (son) | сыр |
| жена (wife) | жинка |
| дочь (daughter) | дотч |
| друзья (friends) | друны |

Exceptions are declined too: `с сыном` → `с сыром`, `к жене` → `к жинке`,
`с дочерью` → `с дотчем`, `у друзей` → `у друнов`.

## Chekushka

All three regeneration potions — normal, splash and lingering — are rendered as
a bottle of cheap Russian vodka instead of the vanilla flask, and a breaking
splash or lingering one screams instead of tinkling like glass. Every other
potion looks and sounds exactly as in vanilla.

![Chekushka in game](https://raw.githubusercontent.com/RBXLU/BurmaldynskyyLanguage/main/docs/screenshot-chekushka.png)

## Installation

1. Minecraft **26.2**, Fabric Loader **0.19.5+**, Java **25**.
2. [Fabric API](https://modrinth.com/mod/fabric-api) for 26.2.
3. Drop the jar into your `mods` folder.
4. **Options → Language → Бурмалдинский (Мурино)** — it is the first entry.

Client-side only: the mod is not needed on the server, so you can use it on any server.

## For mod developers: the API

The translation can be extended in two ways — one of them needs no code at all.

**1. With a resource file.** Put this into your mod or resource pack as
`assets/<namespace>/burmaldyn/dictionary.json`:

```json
{
  "words":             { "меню": "менюсть" },
  "stems":             { "кошк": "кот" },
  "exceptions":        { "жен": { "target": "жинк",
                                  "source_declension": "feminine",
                                  "target_declension": "feminine" } },
  "forced_nouns":      [ "кровать" ],
  "forced_noun_stems": [ "пчел" ],
  "skip":              [ "стив" ],
  "overrides":         { "item.mymod.wand": "Волшебная палкость" }
}
```

| Field | What it does |
| --- | --- |
| `words` | replace an exact word form |
| `stems` | swap the stem before the suffix is added |
| `exceptions` | replace a word entirely, keeping its grammatical case |
| `forced_nouns` | treat the word as a noun even if the engine disagrees |
| `forced_noun_stems` | the same, for every case form of a stem |
| `skip` | never translate this word |
| `overrides` | a ready-made string for a translation key (no auto-translation) |

A plain language file `assets/<namespace>/lang/brm_brm.json` works too and
overrides the auto-translation.

**2. From Java.** Add the `"burmaldyn"` entrypoint to your `fabric.mod.json`:

```java
public class MyBurmaldynAddon implements BurmaldynExtension {
    @Override
    public void registerBurmaldyn(BurmaldynRegistry registry) {
        registry.override("item.mymod.magic_wand", "Волшебная палкость");
        registry.forcedNounStem("посох");
        registry.exception("брат", Declension.MASCULINE, "брот", Declension.MASCULINE);
    }
}
```

Translate a string by hand with `BurmaldynApi.translate("Каменная кирка")` →
`Каменная киркость`.

## An honest note on quality

The morphology is heuristic — there is no full Russian dictionary inside. The
engine is deliberately cautious: leaving a word alone beats breaking a string, so
some words are skipped and some are caught by mistake. Anything wrong can be fixed
with a one-line dictionary entry — that is exactly what the API is for.

## Source and licence

[GitHub](https://github.com/RBXLU/BurmaldynskyyLanguage) · GPL-3.0-or-later

---

# Бурмалдинский язык

**Бурмалдинский** — это русский, в котором все существительные получают окончание
**«-ость»** (или **«-сть»**, если основа кончается на гласную), но при этом
сохраняют падеж и число.

> Я вернулся домой и увидел свою кошку, у кошки был чёрный хвост.
>
> **Ч вернулся домой и увидел свою котость, у котости был чёрный хвостость.**

Язык выбирается в обычном меню **Настройкости → Языкость** и стоит там **первым
в списке**. Переводятся **все** строки игры, включая строки других модов, —
мод берёт русский перевод (`ru_ru`) и прогоняет его через морфологический движок
прямо во время загрузки ресурсов. Вся ваниль (8718 строк) обрабатывается
примерно за 200 мс.

## Правила языка

| Правило | Пример |
| --- | --- |
| Существительное + «-ость» | `хвост` → `хвостость` |
| Основа на гласную → «-сть» | `расстояние` → `расстоянисть` |
| Падеж сохраняется | `у кошки` → `у котости`, `с мечом` → `с мечостью` |
| Множественное число сохраняется | `с алмазами` → `с алмазостями` |
| Регистр сохраняется | `Кирка` → `Киркость`, `КИРКА` → `КИРКОСТЬ` |
| Глаголы, прилагательные и предлоги не трогаем | `Каменная кирка сломалась` → `Каменная киркость сломалась` |
| Плейсхолдеры и цветовые коды не ломаются | `§eПривет, %s!` → `§eПриветость, %s!` |

### Слова-исключения

| Русский | Бурмалдинский |
| --- | --- |
| я | ч |
| дед | дод |
| папа | батч |
| сын | сыр |
| жена | жинка |
| дочь | дотч |
| друзья | друны |

Исключения тоже склоняются: `с сыном` → `с сыром`, `к жене` → `к жинке`,
`с дочерью` → `с дотчем`, `у друзей` → `у друнов`.

## Чекушка

Зелье регенерации во всех трёх видах — обычное, взрывное и оседающее —
выглядит как чекушка. Когда взрывная или оседающая чекушка разбивается,
вместо звона стекла раздаётся крик. Остальные зелья не трогаются.

## Установка

1. Minecraft **26.2**, Fabric Loader **0.19.5+**, Java **25**.
2. [Fabric API](https://modrinth.com/mod/fabric-api) для 26.2.
3. Положить jar в папку `mods`.
4. **Настройкости → Языкость → Бурмалдинский (Мурино)**.

Мод клиентский: на сервере он не нужен, играть с ним можно на любом сервере.

## Для мододелов: API

Перевод дополняется двумя способами — можно вообще без кода.

**1. Файлом ресурсов.** Положите в свой мод или ресурспак
`assets/<namespace>/burmaldyn/dictionary.json` (формат — в английской части выше).

| Поле | Что делает |
| --- | --- |
| `words` | жёсткая замена конкретной словоформы |
| `stems` | подмена основы перед добавлением «-ость» |
| `exceptions` | замена слова целиком с сохранением падежа |
| `forced_nouns` | считать слово существительным, даже если движок решил иначе |
| `forced_noun_stems` | то же, но сразу для всех падежных форм основы |
| `skip` | никогда не переводить это слово |
| `overrides` | готовая строка для ключа локализации |

Работает и обычный lang-файл `assets/<namespace>/lang/brm_brm.json` — он
перекрывает автоперевод.

**2. Из Java.** Точка входа `"burmaldyn"` в вашем `fabric.mod.json`, интерфейс
`BurmaldynExtension` (пример — в английской части выше). Перевести строку вручную:
`BurmaldynApi.translate("Каменная кирка")` → `Каменная киркость`.

Подробности — в [README на GitHub](https://github.com/RBXLU/BurmaldynskyyLanguage).

## Честно о качестве перевода

Морфология эвристическая: полноценного словаря русского языка внутри нет.
Движок специально осторожен — лучше оставить слово, чем сломать строку,
поэтому часть слов проходит мимо («деревней», «стеной»), а часть ловится
неверно («жить» → «житость»). Любое такое слово правится словарём в одну
строку — ради этого API и сделан.

## Исходники и лицензия

[GitHub](https://github.com/RBXLU/BurmaldynskyyLanguage) · GPL-3.0-or-later
