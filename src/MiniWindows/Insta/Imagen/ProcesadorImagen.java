package MiniWindows.Insta.Imagen;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class ProcesadorImagen {

    public static final String CUADRADO = "Cuadrado";
    public static final String RETRATO = "Retrato";
    public static final String PAISAJE = "Paisaje";

    private ProcesadorImagen() {
    }

    public static boolean esImagen(byte[] datos) {
        return leer(datos) != null;
    }

    public static byte[] ajustar(byte[] datos, int ladoMaximo) {
        BufferedImage original = leer(datos);
        if (original == null) {
            return null;
        }
        double escala = Math.min(1.0, (double) ladoMaximo / Math.max(original.getWidth(), original.getHeight()));
        int ancho = Math.max(1, (int) Math.round(original.getWidth() * escala));
        int alto = Math.max(1, (int) Math.round(original.getHeight() * escala));

        BufferedImage destino = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D lienzo = destino.createGraphics();
        lienzo.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        lienzo.drawImage(original, 0, 0, ancho, alto, null);
        lienzo.dispose();

        try {
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            ImageIO.write(destino, "png", salida);
            return salida.toByteArray();
        } catch (IOException error) {
            return null;
        }
    }

    public static double proporcionDe(String formato) {
        if (RETRATO.equalsIgnoreCase(formato)) {
            return 4.0 / 5.0;
        }
        if (PAISAJE.equalsIgnoreCase(formato)) {
            return 16.0 / 9.0;
        }
        return 1.0;
    }

    private static BufferedImage leer(byte[] datos) {
        if (datos == null || datos.length == 0) {
            return null;
        }
        try {
            return ImageIO.read(new ByteArrayInputStream(datos));
        } catch (IOException error) {
            return null;
        }
    }
}
