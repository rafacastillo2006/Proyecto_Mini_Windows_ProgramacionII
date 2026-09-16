package MiniWindows.Insta.Hilos;

import MiniWindows.Red.Cliente.EscuchaAvisos;
import MiniWindows.Red.EventoInsta;
import javafx.application.Platform;

import java.util.function.Consumer;

public class AvisosRemotos implements CanalAvisos {

    private final EscuchaAvisos escucha;

    public AvisosRemotos(String host, int puerto, String username, Consumer<EventoInsta> alLlegar) {
        this.escucha = new EscuchaAvisos(host, puerto, username,
                evento -> Platform.runLater(() -> alLlegar.accept(evento)));
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
