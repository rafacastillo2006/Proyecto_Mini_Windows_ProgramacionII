package MiniWindows.Util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Fechas {

    private static final Locale ESPANOL = Locale.forLanguageTag("es");

    public static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a", ESPANOL);
    public static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy", ESPANOL);
    public static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("hh:mm a", ESPANOL);
    public static final DateTimeFormatter FECHA_LARGA = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", ESPANOL);

    private Fechas() {
    }

    public static LocalDateTime ahora() {
        return LocalDateTime.now();
    }

    public static String formatear(LocalDateTime fecha) {
        return fecha == null ? "" : FECHA_HORA.format(fecha);
    }

    public static String formatearFecha(LocalDateTime fecha) {
        return fecha == null ? "" : FECHA.format(fecha);
    }

    public static String formatearHora(LocalDateTime fecha) {
        return fecha == null ? "" : HORA.format(fecha);
    }

    public static String formatearFechaLarga(LocalDateTime fecha) {
        return fecha == null ? "" : FECHA_LARGA.format(fecha);
    }

    public static LocalDateTime desdeMilisegundos(long milisegundos) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milisegundos), ZoneId.systemDefault());
    }

    public static long aMilisegundos(LocalDateTime fecha) {
        return fecha.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
