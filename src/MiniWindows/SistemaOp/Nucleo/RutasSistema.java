package MiniWindows.SistemaOp.Nucleo;

import MiniWindows.Persistencia.ArchivoIns;

import java.nio.file.Path;

public final class RutasSistema {

    public static final String UNIDAD = "Z:";
    public static final String CARPETA_UNIDAD = "Z";
    public static final String CARPETA_SISTEMA = "sistema";
    public static final String ARCHIVO_USUARIOS = "usuarios" + ArchivoIns.EXTENSION;
    public static final String PROPIEDAD_RAIZ = "miniwindows.raiz";

    public static final String CARPETA_ESCRITORIO = "Escritorio";
    public static final String[] CARPETAS_POR_DEFECTO = {"Mis Documentos", "Música", "Mis Imágenes"};

    private RutasSistema() {
    }

    public static Path raizFisica() {
        String configurada = System.getProperty(PROPIEDAD_RAIZ);
        if (configurada != null && !configurada.isBlank()) {
            return Path.of(configurada).toAbsolutePath().normalize();
        }
        return Path.of(System.getProperty("user.dir"), CARPETA_UNIDAD).toAbsolutePath().normalize();
    }
}
