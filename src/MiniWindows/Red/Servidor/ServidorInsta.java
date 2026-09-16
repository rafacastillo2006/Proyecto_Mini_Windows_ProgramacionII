package MiniWindows.Red.Servidor;

import MiniWindows.Insta.Servicio.ServicioInsta;
import MiniWindows.Insta.Servicio.ServicioLocal;
import MiniWindows.Red.Cliente.ClienteInsta;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ServidorInsta {

    public static final String NOMBRE_HILO = "INSTA-Servidor";

    private final int puerto;
    private final ServicioInsta servicio;
    private final AtomicInteger clientesAtendidos = new AtomicInteger();
    private final AvisosInsta avisos = new AvisosInsta();

    private ServerSocket puerta;
    private Thread aceptador;
    private volatile boolean encendido;

    public ServidorInsta() {
        this(ClienteInsta.PUERTO_POR_DEFECTO, new ServicioLocal());
    }

    public ServidorInsta(int puerto, ServicioInsta servicio) {
        this.puerto = puerto;
        this.servicio = servicio;
    }

    public void encender() throws IOException {
        puerta = new ServerSocket(puerto);
        encendido = true;
        aceptador = new Thread(this::aceptar, NOMBRE_HILO);
        aceptador.setDaemon(true);
        aceptador.start();
        System.out.println("INSTA+ escuchando en el puerto " + getPuerto());
    }

    public void apagar() {
        encendido = false;
        try {
            if (puerta != null) {
                puerta.close();
            }
        } catch (IOException error) {
            System.err.println("No se pudo cerrar el servidor: " + error.getMessage());
        }
        if (aceptador != null) {
            aceptador.interrupt();
            try {
                aceptador.join(1000);
            } catch (InterruptedException interrumpido) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public int getPuerto() {
        return puerta == null ? puerto : puerta.getLocalPort();
    }

    public int getClientesAtendidos() {
        return clientesAtendidos.get();
    }

    public int getOyentes() {
        return avisos.cuantosEscuchan();
    }

    public boolean estaEncendido() {
        return encendido;
    }

    private void aceptar() {
        while (encendido) {
            try {
                Socket cliente = puerta.accept();
                clientesAtendidos.incrementAndGet();
                Thread atencion = new Thread(new AtencionCliente(cliente, servicio, avisos),
                        NOMBRE_HILO + "-" + clientesAtendidos.get());
                atencion.setDaemon(true);
                atencion.start();
            } catch (IOException error) {
                if (encendido) {
                    System.err.println("Fallo aceptando un cliente: " + error.getMessage());
                }
            }
        }
    }
}
