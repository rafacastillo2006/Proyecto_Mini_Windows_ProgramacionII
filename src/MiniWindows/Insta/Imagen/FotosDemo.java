package MiniWindows.Insta.Imagen;

import java.io.IOException;
import java.io.InputStream;

public final class FotosDemo {

    private static final String CARPETA = "/MiniWindows/recursos/insta/";

    private FotosDemo() {
    }

    public static byte[] avatar(String username) {
        return leer("av-" + username + ".jpg");
    }

    public static byte[] publicacion(String nombre) {
        return leer("po-" + nombre + ".jpg");
    }

    public static boolean hayFotos() {
        return avatar("lucia.travel") != null;
    }

    private static byte[] leer(String archivo) {
        try (InputStream entrada = FotosDemo.class.getResourceAsStream(CARPETA + archivo)) {
            return entrada == null ? null : entrada.readAllBytes();
        } catch (IOException error) {
            return null;
        }
    }
}
