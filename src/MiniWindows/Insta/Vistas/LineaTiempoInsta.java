package MiniWindows.Insta.Vistas;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Insta.EstilosInsta;
import MiniWindows.Insta.VentanaInsta;
import MiniWindows.Modelo.Publicacion;
import MiniWindows.Modelo.UsuarioInsta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LineaTiempoInsta extends ScrollPane {

    private static final int SUGERENCIAS = 5;

    private final VentanaInsta ventana;
    private final VBox columna = new VBox(18);

    public LineaTiempoInsta(VentanaInsta ventana) {
        this.ventana = ventana;

        columna.setAlignment(Pos.TOP_CENTER);
        columna.setPadding(new Insets(20, 20, 24, 20));

        setContent(columna);
        setFitToWidth(true);
        setStyle("-fx-background: " + EstilosInsta.FONDO + "; -fx-background-color: "
                + EstilosInsta.FONDO + "; -fx-border-color: transparent;");

        reconstruir();
    }

    private void reconstruir() {
        columna.getChildren().clear();
        String yo = ventana.getContexto().getUsuarioActual();
        ListaEnlazada<Publicacion> feed = ventana.getContexto().getServicio().lineaDeTiempo(yo);

        if (feed.estaVacia()) {
            Label vacio = EstilosInsta.leyenda("Todavia no hay publicaciones. Sigue algunas cuentas para empezar.");
            columna.getChildren().add(vacio);
        }
        columna.getChildren().add(panelSugerencias());
        for (Publicacion publicacion : feed) {
            columna.getChildren().add(new TarjetaPublicacion(ventana, publicacion));
        }
    }

    private VBox panelSugerencias() {
        String yo = ventana.getContexto().getUsuarioActual();
        ListaEnlazada<UsuarioInsta> sugeridos =
                ventana.getContexto().getServicio().sugerencias(yo, SUGERENCIAS);

        VBox panel = new VBox(8);
        panel.setMaxWidth(440);
        panel.setPadding(new Insets(12));
        panel.setStyle(EstilosInsta.TARJETA);
        panel.getChildren().add(EstilosInsta.leyenda("Sugerencias para ti"));

        if (sugeridos.estaVacia()) {
            panel.getChildren().add(EstilosInsta.leyenda("Ya sigues a todas las cuentas disponibles."));
            return panel;
        }
        for (UsuarioInsta sugerido : sugeridos) {
            panel.getChildren().add(fila(sugerido));
        }
        return panel;
    }

    private HBox fila(UsuarioInsta sugerido) {
        Label nombre = EstilosInsta.texto("@" + sugerido.getUsername());
        nombre.setStyle(nombre.getStyle() + " -fx-font-weight: bold;");

        VBox datos = new VBox(0, nombre, EstilosInsta.leyenda(sugerido.getNombreCompleto()));

        Button seguir = EstilosInsta.enlace("Seguir");
        seguir.setOnAction(evento -> seguir(sugerido));

        HBox fila = new HBox(10, EstilosInsta.avatar(sugerido, 36), datos,
                EstilosInsta.espaciador(), seguir);
        fila.setAlignment(Pos.CENTER_LEFT);
        return fila;
    }

    private void seguir(UsuarioInsta sugerido) {
        try {
            ventana.getContexto().getServicio()
                    .seguir(ventana.getContexto().getUsuarioActual(), sugerido.getUsername());
            reconstruir();
        } catch (MiniWindowsException error) {
            columna.getChildren().add(EstilosInsta.error(error.getMessage()));
        }
    }
}
