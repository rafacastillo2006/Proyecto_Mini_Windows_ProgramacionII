package MiniWindows.Insta.Hilos;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import java.io.File;

public class CargaIMGS {

    public static void cargarImagenAsync(String rutaImagen, double anchoDeseado, double altoDeseado, CallbackCarga callback) {
        Task<Image> tarea = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                File archivo = new File(rutaImagen);
                if (!archivo.exists()) {
                    return null;
                }
                return new Image(archivo.toURI().toString(), anchoDeseado, altoDeseado, true, true);
            }
        };

        tarea.setOnSucceeded(e -> {
            if (callback != null) {
                callback.onImagenCargada(tarea.getValue());
            }
        });

        tarea.setOnFailed(e -> {
            if (callback != null) {
                callback.onError(tarea.getException());
            }
        });

        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    @FunctionalInterface
    public interface CallbackCarga {
        void onImagenCargada(Image imagen);
        default void onError(Throwable e) {
            System.err.println("Error al cargar imagen: " + e.getMessage());
        }
    }
}