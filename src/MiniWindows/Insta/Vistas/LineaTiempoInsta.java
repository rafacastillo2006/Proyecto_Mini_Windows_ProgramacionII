package MiniWindows.Insta.Vistas;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Insta.EstilosInsta;
import MiniWindows.Insta.VentanaInsta;
import MiniWindows.Modelo.Publicacion;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Red.EventoInsta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class LineaTiempoInsta extends ScrollPane {

    private static final int SUGERENCIAS = 4;

    private final VentanaInsta ventana;
    private final VBox columna = new VBox(16);

    public LineaTiempoInsta(VentanaInsta ventana) {
        this.ventana = ventana;

        columna.setAlignment(Pos.TOP_CENTER);
        columna.setPadding(new Insets(20, 20, 26, 20));
        columna.setStyle(EstilosInsta.PAGINA);

        setContent(columna);
        setFitToWidth(true);
        setStyle("-fx-background: " + EstilosInsta.FONDO + "; -fx-background-color: "
                + EstilosInsta.FONDO + "; -fx-border-color: transparent;");

        reconstruir();
        ventana.alRecibirEvento(this::atender);
    }

    private void atender(EventoInsta evento) {
        if (ventana.getContexto().getUsuarioActual().equalsIgnoreCase(evento.actor())) {
            return;
        }
        if (evento.es(EventoInsta.PUBLICACION) || evento.es(EventoInsta.SEGUIR)
                || evento.es(EventoInsta.DEJAR_DE_SEGUIR)) {
            reconstruir();
        }
    }

    private void reconstruir() {
        columna.getChildren().clear();
        String yo = ventana.getContexto().getUsuarioActual();
        ListaEnlazada<Publicacion> feed = ventana.getContexto().getServicio().lineaDeTiempo(yo);

        columna.getChildren().add(panelSugerencias());
        if (feed.estaVacia() && ventana.getContexto().servidorCaido()) {
            Label caido = EstilosInsta.error(
                    "Se perdió la conexión con el servidor de INSTA+. Cierra y vuelve a abrir la app.");
            caido.setPadding(new Insets(16, 0, 0, 0));
            columna.getChildren().add(caido);
        } else if (feed.estaVacia()) {
            Label vacio = EstilosInsta.leyenda("Todavía no hay publicaciones. Sigue algunas cuentas para empezar.");
            vacio.setPadding(new Insets(20, 0, 0, 0));
            columna.getChildren().add(vacio);
        }
        for (Publicacion publicacion : feed) {
            columna.getChildren().add(new TarjetaPublicacion(ventana, publicacion));
        }
    }

    private VBox panelSugerencias() {
        String yo = ventana.getContexto().getUsuarioActual();
        ListaEnlazada<UsuarioInsta> sugeridos =
                ventana.getContexto().getServicio().sugerencias(yo, SUGERENCIAS);

        Label titulo = EstilosInsta.leyenda("Sugerencias para ti");
        titulo.setPadding(new Insets(0, 0, 2, 10));

        VBox panel = new VBox(2, titulo);
        panel.setMaxWidth(TarjetaPublicacion.ANCHO_MAXIMO);
        panel.setPadding(new Insets(12, 4, 8, 4));
        panel.setStyle(EstilosInsta.TARJETA);

        if (sugeridos.estaVacia()) {
            Label ninguna = EstilosInsta.leyenda("Ya sigues a todas las cuentas disponibles.");
            ninguna.setPadding(new Insets(4, 0, 4, 10));
            panel.getChildren().add(ninguna);
            return panel;
        }
        for (UsuarioInsta sugerido : sugeridos) {
            panel.getChildren().add(FilaUsuario.crear(ventana, sugerido, this::seguir, true));
        }
        return panel;
    }

    private void seguir(UsuarioInsta sugerido, Button boton) {
        try {
            ventana.getContexto().getServicio()
                    .seguir(ventana.getContexto().getUsuarioActual(), sugerido.getUsername());
            reconstruir();
        } catch (MiniWindowsException error) {
            columna.getChildren().add(EstilosInsta.error(error.getMessage()));
        }
    }
}
