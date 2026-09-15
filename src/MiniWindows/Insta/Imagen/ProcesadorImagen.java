package MiniWindows.Insta.Imagen;

import java.io.File;

public class ProcesadorImagen {

    public static boolean esImagenValida(String ruta) {
        if (ruta == null) return false;
        File archivo = new File(ruta);
        if (!archivo.exists() || !archivo.isFile()) return false;
        String r = ruta.toLowerCase();
        return r.endsWith(".png") || r.endsWith(".jpg") || r.endsWith(".jpeg");
    }

    public static double[] obtenerDimensionesMobile(String modoMobile) {
        if (modoMobile == null) return new double[]{1080, 1080};

        switch (modoMobile.toUpperCase()) {
            case "RETRATO":
                return new double[]{1080, 1350};
            case "PAISAJE":
                return new double[]{1080, 566};
            case "CUADRADO":
            default:
                return new double[]{1080, 1080};
        }
    }
}