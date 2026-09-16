package MiniWindows.Util;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class Validador {

    public static final int LONGITUD_MINIMA_CONTRASENA = 6;
    public static final String REGLA_CONTRASENA = "La contraseña debe ser alfanumérica: mínimo "
            + LONGITUD_MINIMA_CONTRASENA + " caracteres con al menos una letra y un número";
    public static final int LONGITUD_MAXIMA_NOMBRE_ARCHIVO = 60;
    public static final String CARACTERES_PROHIBIDOS = "\\/:*?\"<>|";

    private static final Pattern USERNAME = Pattern.compile("^[a-zA-Z0-9._-]{3,20}$");
    private static final Set<String> RESERVADOS_WINDOWS = Set.of(
            "con", "prn", "aux", "nul",
            "com1", "com2", "com3", "com4", "com5", "com6", "com7", "com8", "com9",
            "lpt1", "lpt2", "lpt3", "lpt4", "lpt5", "lpt6", "lpt7", "lpt8", "lpt9");

    private Validador() {
    }

    public static boolean textoConContenido(String texto) {
        return texto != null && !texto.isBlank();
    }

    public static boolean usernameValido(String username) {
        return textoConContenido(username)
                && USERNAME.matcher(username).matches()
                && !username.endsWith(".")
                && !nombreReservado(username);
    }

    public static boolean nombreReservado(String nombre) {
        return RESERVADOS_WINDOWS.contains(Nombres.sinExtension(nombre).trim().toLowerCase(Locale.ROOT));
    }

    public static boolean contrasenaValida(String contrasena) {
        if (contrasena == null || contrasena.length() < LONGITUD_MINIMA_CONTRASENA) {
            return false;
        }
        boolean letra = false;
        boolean numero = false;
        for (char caracter : contrasena.toCharArray()) {
            if (Character.isLetter(caracter)) {
                letra = true;
            } else if (Character.isDigit(caracter)) {
                numero = true;
            }
        }
        return letra && numero;
    }

    public static boolean edadValida(int edad) {
        return edad >= 1 && edad <= 120;
    }

    public static boolean generoValido(char genero) {
        char normalizado = Character.toUpperCase(genero);
        return normalizado == 'M' || normalizado == 'F';
    }

    public static boolean nombreArchivoValido(String nombre) {
        if (!textoConContenido(nombre) || nombre.length() > LONGITUD_MAXIMA_NOMBRE_ARCHIVO) {
            return false;
        }
        if (nombre.startsWith(".") || nombre.endsWith(".") || nombre.endsWith(" ")) {
            return false;
        }
        for (char caracter : nombre.toCharArray()) {
            if (CARACTERES_PROHIBIDOS.indexOf(caracter) >= 0 || caracter < 32) {
                return false;
            }
        }
        return !nombreReservado(nombre);
    }
}
