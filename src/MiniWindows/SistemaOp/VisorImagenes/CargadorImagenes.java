package MiniWindows.SistemaOp.VisorImagenes;

import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import MiniWindows.SistemaOp.Archivos.SistemaArchivos;
import MiniWindows.Util.Imagenes;
import javafx.application.Platform;
import javafx.scene.image.Image;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class CargadorImagenes {

    public static final String NOMBRE_HILO = "MiniWindows-Imagenes";

    private final SistemaArchivos archivos;
    private final ExecutorService ejecutor;

    public CargadorImagenes(SistemaArchivos archivos) {
        this.archivos = archivos;
        this.ejecutor = Executors.newSingleThreadExecutor(tarea -> {
            Thread hilo = new Thread(tarea, NOMBRE_HILO);
            hilo.setDaemon(true);
            return hilo;
        });
    }

    public void cargar(NodoArchivo nodo, double lado, Consumer<Image> destino) {
        try {
            ejecutor.execute(() -> {
                Image imagen = leer(nodo, lado);
                Platform.runLater(() -> destino.accept(imagen));
            });
        } catch (RejectedExecutionException cerrado) {
            destino.accept(null);
        }
    }

    public void detener() {
        ejecutor.shutdownNow();
    }

    private Image leer(NodoArchivo nodo, double lado) {
        try {
            byte[] datos = archivos.abrir(nodo).datos();
            return lado <= 0 ? Imagenes.desdeBytes(datos) : Imagenes.miniatura(datos, lado);
        } catch (MiniWindowsException | RuntimeException error) {
            return null;
        }
    }
}
