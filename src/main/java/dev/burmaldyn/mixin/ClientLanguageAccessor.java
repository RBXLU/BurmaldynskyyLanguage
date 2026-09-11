package dev.burmaldyn.mixin;

import java.util.Map;
import net.minecraft.client.resources.language.ClientLanguage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Ванильный {@link ClientLanguage} умеет только отвечать на вопрос
 * «как переводится ключ X», а моду нужно пройтись по всем строкам сразу.
 * Аксессор открывает исходную карту переводов.
 */
@Mixin(ClientLanguage.class)
public interface ClientLanguageAccessor {
	@Accessor("storage")
	Map<String, String> burmaldyn$storage();
}
