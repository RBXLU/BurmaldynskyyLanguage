package dev.burmaldyn.api;

/**
 * Точка входа для модов, которые хотят дописать бурмалдинский перевод.
 *
 * <p>Добавьте в свой {@code fabric.mod.json}:</p>
 *
 * <pre>{@code
 * "entrypoints": {
 *     "burmaldyn": ["com.example.MyBurmaldynAddon"]
 * }
 * }</pre>
 *
 * <p>и реализуйте интерфейс:</p>
 *
 * <pre>{@code
 * public class MyBurmaldynAddon implements BurmaldynExtension {
 *     @Override
 *     public void registerBurmaldyn(BurmaldynRegistry registry) {
 *         registry.override("item.mymod.magic_wand", "Волшебная палкость");
 *         registry.forcedNounStem("посох");
 *     }
 * }
 * }</pre>
 *
 * <p>Мод-аддон не обязан зависеть от бурмалдинского жёстко: достаточно
 * указать {@code "burmaldyn"} в {@code suggests}, точка входа просто
 * не вызовется, если мода языка нет.</p>
 */
@FunctionalInterface
public interface BurmaldynExtension {
	/**
	 * Вызывается один раз при запуске игры, до первой загрузки ресурсов.
	 *
	 * @param registry реестр, куда добавляются правила
	 */
	void registerBurmaldyn(BurmaldynRegistry registry);
}
