package MiniWindows.Insta.Hilos;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Insta.Servicio.ServicioInsta;
import MiniWindows.Modelo.Mensaje;
import javafx.application.Platform;

import java.util.function.Consumer;

public class Notificaciones extends Thread {

    public static final String NOMBRE_HILO = "MiniWindows-InstaAvisos";

    private static final long INTERVALO_MS = 3000;

    private final ServicioInsta servicio;
    private final String username;
    private final Consumer<Mensaje> alLlegar;

    private volatile boolean activo = true;
    private int mensajesVistos;

    public Notificaciones(ServicioInsta servicio, String username, Consumer<Mensaje> alLlegar) {
        super(NOMBRE_HILO);
        this.servicio = servicio;
        this.username = username;
        this.alLlegar = alLlegar;
        this.mensajesVistos = contarMensajes();
        setDaemon(true);
    }

    @Override
    public void run() {
        while (activo) {
            try {
                Thread.sleep(INTERVALO_MS);
            } catch (InterruptedException interrumpido) {
                return;
            }
            ListaEnlazada<Mensaje> bandeja = servicio.bandejaDe(username);
            if (bandeja.tamano() > mensajesVistos) {
                Mensaje ultimo = bandeja.ultimo();
                mensajesVistos = bandeja.tamano();
                if (!ultimo.getEmisor().equalsIgnoreCase(username)) {
                    Platform.runLater(() -> alLlegar.accept(ultimo));
                }
            }
        }
    }

    public void detener() {
        activo = false;
        interrupt();
    }

    private int contarMensajes() {
        return servicio.bandejaDe(username).tamano();
    }
}
