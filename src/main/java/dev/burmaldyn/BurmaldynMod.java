package dev.burmaldyn;

import com.mojang.logging.LogUtils;
import dev.burmaldyn.api.BurmaldynApi;
import dev.burmaldyn.api.BurmaldynExtension;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import org.slf4j.Logger;

/**
 * Точка входа мода.
 *
 * <p>Сам перевод собирается при загрузке ресурсов (см. {@code LanguageManagerMixin}),
 * здесь только собираются правила от других модов.</p>
 */
public class BurmaldynMod implements ClientModInitializer {
	public static final String MOD_ID = "burmaldyn";
	public static final Logger LOGGER = LogUtils.getLogger();

	/** Имя точки входа для модов-аддонов в их {@code fabric.mod.json}. */
	public static final String ENTRYPOINT = "burmaldyn";

	@Override
	public void onInitializeClient() {
		int loaded = 0;

		for (EntrypointContainer<BurmaldynExtension> container
				: FabricLoader.getInstance().getEntrypointContainers(ENTRYPOINT, BurmaldynExtension.class)) {
			String modId = container.getProvider().getMetadata().getId();

			try {
				container.getEntrypoint().registerBurmaldyn(BurmaldynApi.registry());
				loaded++;
			} catch (Throwable error) {
				LOGGER.error("Мод {} не смог зарегистрировать бурмалдинские правила", modId, error);
			}
		}

		LOGGER.info("Бурмалдинскость загружена, дополнений подключено: {}", loaded);
	}
}
