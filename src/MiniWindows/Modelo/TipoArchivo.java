package MiniWindows.Modelo;

import java.util.Locale;

public enum TipoArchivo {

    CARPETA("Carpeta", ""),
    TEXTO("Documento de texto", "Mis Documentos"),
    IMAGEN("Imagen", "Mis Imágenes"),
    MUSICA("Música", "Música"),
    GENERICO("Archivo", "");

    private static final String[] EXTENSIONES_TEXTO = {".txt", ".md", ".log"};
    private static final String[] EXTENSIONES_IMAGEN = {".png", ".jpg", ".jpeg", ".bmp", ".gif"};
    private static final String[] EXTENSIONES_MUSICA = {".mp3", ".wav", ".m4a"};

    private final String etiqueta;
    private final String carpetaSugerida;

    TipoArchivo(String etiqueta, String carpetaSugerida) {
        this.etiqueta = etiqueta;
        this.carpetaSugerida = carpetaSugerida;
    }

    public static TipoArchivo desdeNombre(String nombreVisible) {
        String nombre = nombreVisible == null ? "" : nombreVisible.toLowerCase(Locale.ROOT);
        if (terminaEn(nombre, EXTENSIONES_TEXTO)) {
            return TEXTO;
        }
        if (terminaEn(nombre, EXTENSIONES_IMAGEN)) {
            return IMAGEN;
        }
        if (terminaEn(nombre, EXTENSIONES_MUSICA)) {
            return MUSICA;
        }
        return GENERICO;
    }

    public static TipoArchivo desdeOrdinal(int ordinal) {
        TipoArchivo[] valores = values();
        if (ordinal < 0 || ordinal >= valores.length) {
            return GENERICO;
        }
        return valores[ordinal];
    }

    private static boolean terminaEn(String nombre, String[] extensiones) {
        for (String extension : extensiones) {
            if (nombre.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getCarpetaSugerida() {
        return carpetaSugerida;
    }

    public boolean tieneCarpetaSugerida() {
        return !carpetaSugerida.isEmpty();
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
