package MiniWindows.Util;

import java.util.Locale;

public final class Nombres {

    private Nombres() {
    }

    public static String extension(String nombre) {
        int punto = nombre.lastIndexOf('.');
        if (punto <= 0 || punto == nombre.length() - 1) {
            return "";
        }
        return nombre.substring(punto).toLowerCase(Locale.ROOT);
    }

    public static String sinExtension(String nombre) {
        int punto = nombre.lastIndexOf('.');
        return punto <= 0 ? nombre : nombre.substring(0, punto);
    }

    public static String conSufijo(String nombre, String sufijo) {
        String extension = extension(nombre);
        if (extension.isEmpty()) {
            return nombre + sufijo;
        }
        return sinExtension(nombre) + sufijo + extension;
    }

    public static String recortar(String texto, int longitudMaxima) {
        if (texto == null || texto.length() <= longitudMaxima) {
            return texto;
        }
        return texto.substring(0, Math.max(0, longitudMaxima - 3)) + "...";
    }
}
