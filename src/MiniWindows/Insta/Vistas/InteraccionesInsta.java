package MiniWindows.Insta.Vistas;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Insta.EstilosInsta;
import MiniWindows.Insta.VentanaInsta;
import MiniWindows.Modelo.Publicacion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class InteraccionesInsta extends ScrollPane {

    public InteraccionesInsta(VentanaInsta ventana) {
        VBox columna = new VBox(18);
        columna.setAlignment(Pos.TOP_CENTER);
        columna.setPadding(new Insets(20));

        String yo = ventana.getContexto().getUsuarioActual();
        ListaEnlazada<Publicacion> menciones = ventana.getContexto().getServicio().menciones(yo);

        columna.getChildren().add(EstilosInsta.titulo("Interacciones", 22));
        columna.getChildren().add(EstilosInsta.leyenda(
                "Publicaciones de otras cuentas donde te mencionaron con @" + yo));

        if (menciones.estaVacia()) {
            columna.getChildren().add(EstilosInsta.leyenda("Todavia nadie te ha mencionado."));
        }
        for (Publicacion publicacion : menciones) {
            columna.getChildren().add(new TarjetaPublicacion(ventana, publicacion));
        }

        setContent(columna);
        setFitToWidth(true);
        setStyle("-fx-background: " + EstilosInsta.FONDO + "; -fx-background-color: "
                + EstilosInsta.FONDO + "; -fx-border-color: transparent;");
    }
}
