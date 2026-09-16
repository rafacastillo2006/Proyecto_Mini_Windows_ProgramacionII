package MiniWindows.Insta.Hilos;

import MiniWindows.Modelo.Mensaje;
import MiniWindows.Red.Cliente.EscuchaMensajes;
import javafx.application.Platform;

import java.util.function.Consumer;

public class AvisosRemotos implements AvisoDeMensajes {

    private final EscuchaMensajes escucha;

    public AvisosRemotos(String host, int puerto, String username, Consumer<Mensaje> alLlegar) {
        this.escucha = new EscuchaMensajes(host, puerto, username,
                mensaje -> Platform.runLater(() -> alLlegar.accept(mensaje)));
    }

    @Override
    public void iniciar() {
        escucha.start();
    }

    @Override
    public void detener() {
        escucha.detener();
    }
}
