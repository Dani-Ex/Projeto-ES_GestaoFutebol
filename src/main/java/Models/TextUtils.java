package Models;

import java.util.Locale;

public final class TextUtils {

    private TextUtils() {
    }

    public static String normalizar(String valor) {
        return valor == null
                ? ""
                : valor
                .replace("\uFEFF", "")
                .replace("\u200B", "")
                .replace("\u00A0", " ")
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
