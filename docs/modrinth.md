<!-- Готовое описание для страницы мода на Modrinth. -->

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

![Меню выбора языка](https://raw.githubusercontent.com/RBXLU/BurmaldynskyyLanguage/main/docs/screenshot-language-menu.png)

![Главное меню](https://raw.githubusercontent.com/RBXLU/BurmaldynskyyLanguage/main/docs/screenshot-title.png)

![Настройки](https://raw.githubusercontent.com/RBXLU/BurmaldynskyyLanguage/main/docs/screenshot-options.png)

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

## Установка

1. Minecraft **26.2**, Fabric Loader **0.19.5+**, Java **25**.
2. [Fabric API](https://modrinth.com/mod/fabric-api) для 26.2.
3. Положить jar в папку `mods`.
4. **Настройкости → Языкость → Бурмалдинский (Мурино)**.

Мод клиентский: на сервере он не нужен, играть с ним можно на любом сервере.

## Для мододелов: API

Перевод дополняется двумя способами — можно вообще без кода.

**1. Файлом ресурсов.** Положите в свой мод или ресурспак
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

Работает и обычный lang-файл `assets/<namespace>/lang/brm_brm.json` — он
перекрывает автоперевод.

**2. Из Java.** Точка входа `"burmaldyn"` в вашем `fabric.mod.json`:

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

Перевести строку вручную: `BurmaldynApi.translate("Каменная кирка")` →
`Каменная киркость`.

Подробности — в [README на GitHub](https://github.com/RBXLU/BurmaldynskyyLanguage).

## Честно о качестве перевода

Морфология эвристическая: полноценного словаря русского языка внутри нет.
Движок специально осторожен — лучше оставить слово, чем сломать строку,
поэтому часть слов проходит мимо («деревней», «стеной»), а часть ловится
неверно («жить» → «житость»). Любое такое слово правится словарём в одну
строку — ради этого API и сделан.

## Исходники и лицензия

[GitHub](https://github.com/RBXLU/BurmaldynskyyLanguage) · GPL-3.0-or-later

---

**English:** adds the Burmaldyn language to Minecraft — Russian where every noun
gets the suffix *-ost* while keeping its grammatical case. The whole game
(including other mods) is translated on the fly from the vanilla Russian locale,
and other mods can extend the translation through a small API. Requires the
Russian-speaking sense of humour.
