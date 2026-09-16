package MiniWindows.Insta.Vistas;

import MiniWindows.Insta.EstilosInsta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public final class Portada {

    public static final double ANCHO_TARJETA = 330;

    private Portada() {
    }

    public static ScrollPane marco(Node... bloques) {
        VBox columna = new VBox(10, bloques);
        columna.setAlignment(Pos.CENTER);
        columna.setPadding(new Insets(28, 20, 28, 20));
        columna.setStyle(EstilosInsta.PAGINA);

        ScrollPane marco = new ScrollPane(columna);
        marco.setFitToWidth(true);
        marco.setStyle("-fx-background: " + EstilosInsta.FONDO + "; -fx-background-color: "
                + EstilosInsta.FONDO + "; -fx-border-color: transparent;");
        return marco;
    }

    public static VBox tarjeta(double espacio, Node... hijos) {
        VBox tarjeta = new VBox(espacio, hijos);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(28, 30, 22, 30));
        tarjeta.setMaxWidth(ANCHO_TARJETA);
        tarjeta.setMinWidth(ANCHO_TARJETA);
        tarjeta.setStyle(EstilosInsta.TARJETA);
        return tarjeta;
    }

    public static VBox banda(Node... hijos) {
        VBox banda = new VBox(6, hijos);
        banda.setAlignment(Pos.CENTER);
        banda.setPadding(new Insets(16, 30, 16, 30));
        banda.setMaxWidth(ANCHO_TARJETA);
        banda.setMinWidth(ANCHO_TARJETA);
        banda.setStyle(EstilosInsta.TARJETA);
        return banda;
    }
}
