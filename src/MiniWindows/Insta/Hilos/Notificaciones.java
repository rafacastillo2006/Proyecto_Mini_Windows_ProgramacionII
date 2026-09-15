package MiniWindows.Insta.Hilos;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Modelo.Mensaje;
import MiniWindows.Persistencia.GestorBinario;
import MiniWindows.Util.Rutas;
import javafx.application.Platform;

public class Notificaciones extends Thread {
    private String username;
    private boolean ejecutando;
    private int cantidadMensajesPrevia;
    private CallbackNotificacion callback;
    private static final int INTERVALO_CHEQUEO_MS = 3000;

    public Notificaciones(String username, CallbackNotificacion callback) {
        this.username = username;
        this.callback = callback;
        this.ejecutando = true;
        this.cantidadMensajesPrevia = obtenerTotalMensajesLocales();
        setDaemon(true);
    }

    @Override
    public void run() {
        while (ejecutando) {
            try {
                Thread.sleep(INTERVALO_CHEQUEO_MS);

                int totalActual = obtenerTotalMensajesLocales();

                if (totalActual > cantidadMensajesPrevia) {
                    Mensaje ultimoMensaje = obtenerUltimoMensaje();
                    cantidadMensajesPrevia = totalActual;

                    if (ultimoMensaje != null && !ultimoMensaje.getEmisor().equalsIgnoreCase(username)) {
                        Platform.runLater(() -> {
                            if (callback != null) {
                                callback.onNuevoMensaje(ultimoMensaje);
                            }
                        });
                    }
                }
            } catch (InterruptedException e) {
                ejecutando = false;
            }
        }
    }

    public void detener() {
        this.ejecutando = false;
        this.interrupt();
    }

    private int obtenerTotalMensajesLocales() {
        GestorBinario<Mensaje> gb = new GestorBinario<>(Rutas.getRutaInbox(username));
        ListaEnlazada<Mensaje> lista = gb.leerLista();
        return lista.getTamano();
    }

    private Mensaje obtenerUltimoMensaje() {
        GestorBinario<Mensaje> gb = new GestorBinario<>(Rutas.getRutaInbox(username));
        ListaEnlazada<Mensaje> lista = gb.leerLista();
        if (!lista.esVacia()) {
            return lista.obtener(lista.getTamano() - 1);
        }
        return null;
    }

    @FunctionalInterface
    public interface CallbackNotificacion {
        void onNuevoMensaje(Mensaje mensaje);
    }
}