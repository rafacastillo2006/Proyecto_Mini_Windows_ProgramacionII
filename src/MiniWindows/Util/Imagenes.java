package MiniWindows.Util;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;

public final class Imagenes {

    private Imagenes() {
    }

    public static Image desdeBytes(byte[] datos) {
        Image imagen = new Image(new ByteArrayInputStream(datos));
        return imagen.isError() ? null : imagen;
    }

    public static Image miniatura(byte[] datos, double lado) {
        Image imagen = new Image(new ByteArrayInputStream(datos), lado, lado, true, true);
        return imagen.isError() ? null : imagen;
    }

    public static String descripcion(Image imagen) {
        if (imagen == null) {
            return "Formato no reconocido";
        }
        return (int) imagen.getWidth() + " x " + (int) imagen.getHeight() + " px";
    }
}
