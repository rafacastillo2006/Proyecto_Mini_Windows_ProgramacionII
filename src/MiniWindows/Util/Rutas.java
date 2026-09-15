package MiniWindows.Util;

import java.io.File;

public class Rutas {

    public static final String INSTA_RAIZ = "Z_Drive/INSTA_RAIZ";
    public static final String USERS_FILE = INSTA_RAIZ + "/users.ins";
    public static final String STICKERS_GLOBALES = INSTA_RAIZ + "/stickers_globales";

    public static void inicializarEstructuraSO() {
        crearDirectorio(INSTA_RAIZ);
        crearDirectorio(STICKERS_GLOBALES);
    }

    public static void crearEstructuraUsuario(String username) {
        String userDir = INSTA_RAIZ + "/" + username;
        crearDirectorio(userDir);
        crearDirectorio(userDir + "/imagenes");
        crearDirectorio(userDir + "/folders_personales");
        crearDirectorio(userDir + "/stickers_personales");
    }

    public static String getRutaInbox(String username) {
        return INSTA_RAIZ + "/" + username + "/inbox.ins";
    }

    public static String getRutaStickers(String username) {
        return INSTA_RAIZ + "/" + username + "/stickers.ins";
    }

    public static String getRutaFollowing(String username) {
        return INSTA_RAIZ + "/" + username + "/following.ins";
    }

    public static String getRutaFollowers(String username) {
        return INSTA_RAIZ + "/" + username + "/followers.ins";
    }

    public static String getRutaPosts(String username) {
        return INSTA_RAIZ + "/" + username + "/insta.ins";
    }

    private static void crearDirectorio(String ruta) {
        File dir = new File(ruta);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}