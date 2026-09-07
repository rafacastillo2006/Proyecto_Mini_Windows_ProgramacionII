package MiniWindows.Util;

import java.io.File;

public class Rutas {
    public static final String RAIZ_SO = "Z_Drive" + File.separator;
    public static final String ARCHIVO_USUARIOS = RAIZ_SO + "usuarios.sop";


    public static void inicializarEstructuraSO() {
        File raiz = new File(RAIZ_SO);
        if (!raiz.exists()) {
            raiz.mkdirs();
        }
    }


    public static void crearCarpetaUsuario(String username) {
        String rutaUser = RAIZ_SO + username + File.separator;
        new File(rutaUser + "Mis Documentos").mkdirs();
        new File(rutaUser + "Musica").mkdirs();
        new File(rutaUser + "Mis Imagenes").mkdirs();
    }
}
