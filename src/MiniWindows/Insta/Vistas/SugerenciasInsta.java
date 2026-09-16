package MiniWindows.Insta.Vistas;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Insta.EstilosInsta;
import MiniWindows.Insta.VentanaInsta;
import MiniWindows.Modelo.UsuarioInsta;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SugerenciasInsta extends StackPane {

    private static final int CUANTAS = 8;
    private static final double ANCHO = 430;

    private final VentanaInsta ventana;
    private final ListaEnlazada<UsuarioInsta> candidatos;
    private final VBox lista = new VBox(2);
    private final Label aviso = EstilosInsta.error("");

    public SugerenciasInsta(VentanaInsta ventana) {
        this.ventana = ventana;
        this.candidatos = ventana.getContexto().getServicio()
                .sugerencias(ventana.getContexto().getUsuarioActual(), CUANTAS);
        setStyle(EstilosInsta.PAGINA);

        ScrollPane interior = new ScrollPane(lista);
        interior.setFitToWidth(true);
        interior.setPrefHeight(310);
        interior.setStyle("-fx-background: " + EstilosInsta.SUPERFICIE + "; -fx-background-color: "
                + EstilosInsta.SUPERFICIE + "; -fx-border-color: transparent;");

        Button continuar = EstilosInsta.botonPrincipal("Continuar");
        continuar.setOnAction(evento -> ventana.mostrarInicio());

        Label titulo = EstilosInsta.titulo("Cuentas sugeridas", 18);
        Label lema = EstilosInsta.leyenda("Sigue algunas cuentas para llenar tu línea de tiempo");
        lema.setPadding(new Insets(0, 0, 6, 0));

        VBox tarjeta = new VBox(10, titulo, lema, EstilosInsta.separador(), interior,
                EstilosInsta.separador(), aviso, continuar);
        tarjeta.setPadding(new Insets(22, 22, 20, 22));
        tarjeta.setMaxWidth(ANCHO);
        tarjeta.setMinWidth(ANCHO);
        tarjeta.setStyle(EstilosInsta.TARJETA);

        llenar();
        getChildren().add(Portada.marco(tarjeta));
    }

    private void llenar() {
        lista.getChildren().clear();
        for (UsuarioInsta sugerido : candidatos) {
            lista.getChildren().add(FilaUsuario.crear(ventana, sugerido, this::alternar));
        }
        if (lista.getChildren().isEmpty()) {
            Label ninguna = EstilosInsta.leyenda("Ya sigues a todas las cuentas disponibles.");
            ninguna.setPadding(new Insets(12, 0, 12, 10));
            lista.getChildren().add(ninguna);
        }
    }

    private void alternar(UsuarioInsta sugerido, Button boton) {
        String yo = ventana.getContexto().getUsuarioActual();
        try {
            if (ventana.getContexto().getServicio().sigue(yo, sugerido.getUsername())) {
                ventana.getContexto().getServicio().dejarDeSeguir(yo, sugerido.getUsername());
            } else {
                ventana.getContexto().getServicio().seguir(yo, sugerido.getUsername());
            }
            aviso.setText("");
            llenar();
        } catch (MiniWindowsException error) {
            aviso.setText(error.getMessage());
        }
    }
}
