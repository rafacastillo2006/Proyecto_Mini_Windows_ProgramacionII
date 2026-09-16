package MiniWindows.Red.Cliente;

import MiniWindows.Red.EventoInsta;
import MiniWindows.Red.RespuestaInsta;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

public class EscuchaAvisos extends Thread {

    public static final String NOMBRE_HILO = "INSTA-Escucha";

    private static final long ESPERA_RECONEXION = 2000;

    private final String host;
    private final int puerto;
    private final String username;
    private final Consumer<EventoInsta> alLlegar;

    private volatile boolean activo = true;
    private volatile Socket conexion;

    public EscuchaAvisos(String host, int puerto, String username, Consumer<EventoInsta> alLlegar) {
        super(NOMBRE_HILO);
        this.host = host;
        this.puerto = puerto;
        this.username = username;
        this.alLlegar = alLlegar;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (activo) {
            try (Socket socket = new Socket(host, puerto);
                 ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {
                conexion = socket;
                salida.writeUTF("ESCUCHAR");
                salida.writeObject(new Object[]{username});
                salida.flush();
                entrada.readObject();
                while (activo) {
                    Object aviso = entrada.readObject();
                    if (aviso instanceof RespuestaInsta respuesta
                            && respuesta.valor() instanceof EventoInsta evento) {
                        alLlegar.accept(evento);
                    }
                }
            } catch (Exception cortado) {
                if (!activo) {
                    return;
                }
                esperar();
            }
        }
    }

    public void detener() {
        activo = false;
        Socket abierta = conexion;
        if (abierta != null) {
            try {
                abierta.close();
            } catch (Exception ignorado) {
                interrupt();
            }
        }
        interrupt();
    }

    private void esperar() {
        try {
            Thread.sleep(ESPERA_RECONEXION);
        } catch (InterruptedException interrumpido) {
            activo = false;
            Thread.currentThread().interrupt();
        }
    }
}
