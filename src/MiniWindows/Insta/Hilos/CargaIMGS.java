package MiniWindows.Insta.Hilos;

import MiniWindows.Util.Imagenes;
import javafx.application.Platform;
import javafx.scene.image.Image;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;

public class CargaIMGS {

    public static final String NOMBRE_HILO = "MiniWindows-InstaImagenes";

    private final ExecutorService cargador = Executors.newSingleThreadExecutor(tarea -> {
        Thread hilo = new Thread(tarea, NOMBRE_HILO);
        hilo.setDaemon(true);
        return hilo;
    });

    public void cargar(byte[] datos, double lado, Consumer<Image> destino) {
        if (datos == null || datos.length == 0) {
            return;
        }
        try {
            cargador.execute(() -> {
                Image imagen = Imagenes.miniatura(datos, lado);
                if (imagen != null) {
                    Platform.runLater(() -> destino.accept(imagen));
                }
            });
        } catch (RejectedExecutionException cerrado) {
            cargador.shutdownNow();
        }
    }

    public void detener() {
        cargador.shutdownNow();
    }
}
