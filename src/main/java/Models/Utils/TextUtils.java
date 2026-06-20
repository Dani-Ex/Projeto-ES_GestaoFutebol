package Models.Utils;

import java.util.Locale;

public final class TextUtils {

    private TextUtils() {
    }

    public static String normalizar(String valor) {
        return limparCaracteresInvisiveis(valor)
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    public static String limparCaracteresInvisiveis(String valor) {
        return valor == null
                ? ""
                : valor
                .replace("\uFEFF", "")
                .replace("\u200B", "")
                .replace("\u00A0", " ");
    }
}
