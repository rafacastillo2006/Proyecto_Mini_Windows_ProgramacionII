package MiniWindows;

import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Modelo.Usuario;
import MiniWindows.SistemaOp.Escritorio.Escritorio;
import MiniWindows.SistemaOp.Escritorio.PantallaLogin;
import MiniWindows.SistemaOp.Nucleo.Sesion;
import MiniWindows.SistemaOp.Nucleo.SistemaOperativo;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Launcher extends Application {

    private static final double ANCHO_INICIAL = 1280;
    private static final double ALTO_INICIAL = 760;

    private final SistemaOperativo sistema = new SistemaOperativo();
    private final StackPane raiz = new StackPane();

    @Override
    public void start(Stage escenario) {
        escenario.setScene(new Scene(raiz, ANCHO_INICIAL, ALTO_INICIAL));
        escenario.setTitle("MiniWindows");
        escenario.initStyle(StageStyle.UNDECORATED);
        escenario.setMaximized(true);

        try {
            sistema.iniciar();
        } catch (MiniWindowsException error) {
            reportarFalloDeArranque(error);
            return;
        }

        mostrarLogin();
        escenario.show();
    }

    private void mostrarLogin() {
        raiz.getChildren().setAll(new PantallaLogin(sistema.getServicioCuentas(), this::abrirEscritorio, this::apagar));
    }

    private void abrirEscritorio(Usuario usuario) {
        raiz.getChildren().setAll(new Escritorio(sistema, new Sesion(usuario), this::mostrarLogin, this::apagar));
    }

    private void apagar() {
        Platform.exit();
    }

    private void reportarFalloDeArranque(MiniWindowsException error) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("MiniWindows");
        alerta.setHeaderText("No se pudo iniciar el sistema");
        alerta.setContentText(error.getMessage());
        alerta.showAndWait();
        Platform.exit();
    }
}
