package MiniWindows.Insta;

import MiniWindows.Insta.Servicio.ServicioLocal;
import MiniWindows.Insta.Util.InicializadorInsta;
import MiniWindows.Util.Rutas;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainInsta extends Application {

    @Override
    public void start(Stage primaryStage) {
        Rutas.inicializarEstructuraSO();

        VentanaInsta ventanaPrincipal = new VentanaInsta();

        if (ventanaPrincipal.getServicio() instanceof ServicioLocal) {
            InicializadorInsta.cargarDatosIniciales((ServicioLocal) ventanaPrincipal.getServicio());
        }

        Scene scene = new Scene(ventanaPrincipal, 900, 650);
        primaryStage.setTitle("INSTA+ - Modo Standalone");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}