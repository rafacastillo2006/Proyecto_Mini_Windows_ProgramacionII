package MiniWindows.Insta.Vistas;

import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Insta.EstilosInsta;
import MiniWindows.Insta.VentanaInsta;
import MiniWindows.Modelo.UsuarioInsta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SugerenciasInsta extends VBox {

    private static final int CUANTAS = 8;

    private final VentanaInsta ventana;
    private final Label aviso = EstilosInsta.leyenda("");

    public SugerenciasInsta(VentanaInsta ventana) {
        this.ventana = ventana;

        setAlignment(Pos.CENTER);
        setPadding(new Insets(24));
        setStyle(EstilosInsta.DEGRADADO);

        VBox lista = new VBox(8);
        lista.setPadding(new Insets(4));
        String yo = ventana.getContexto().getUsuarioActual();
        for (UsuarioInsta sugerido : ventana.getContexto().getServicio().sugerencias(yo, CUANTAS)) {
            lista.getChildren().add(fila(sugerido));
        }

        ScrollPane marco = new ScrollPane(lista);
        marco.setFitToWidth(true);
        marco.setPrefHeight(300);
        marco.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: transparent;");

        Button continuar = EstilosInsta.botonPrincipal("Continuar");
        continuar.setOnAction(evento -> ventana.mostrarInicio());

        VBox tarjeta = new VBox(12, EstilosInsta.titulo("Cuentas sugeridas", 22),
                EstilosInsta.leyenda("Sigue algunas cuentas para llenar tu linea de tiempo"),
                marco, aviso, continuar);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(24));
        tarjeta.setMaxWidth(420);
        tarjeta.setStyle(EstilosInsta.TARJETA);

        getChildren().add(tarjeta);
    }

    private HBox fila(UsuarioInsta sugerido) {
        Label nombre = EstilosInsta.texto(sugerido.getNombreCompleto()
                + (sugerido.esVerificada() ? "  (verificada)" : ""));
        Label arroba = EstilosInsta.leyenda("@" + sugerido.getUsername() + "  -  " + sugerido.getBiografia());

        VBox datos = new VBox(1, nombre, arroba);
        datos.setAlignment(Pos.CENTER_LEFT);

        Button seguir = EstilosInsta.botonPrincipal("Seguir");
        seguir.setMaxWidth(90);
        seguir.setOnAction(evento -> alternar(sugerido, seguir));

        HBox fila = new HBox(12, EstilosInsta.avatar(sugerido, 44), datos,
                EstilosInsta.espaciador(), seguir);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(6, 10, 6, 6));
        return fila;
    }

    private void alternar(UsuarioInsta sugerido, Button boton) {
        String yo = ventana.getContexto().getUsuarioActual();
        try {
            if (ventana.getContexto().getServicio().sigue(yo, sugerido.getUsername())) {
                ventana.getContexto().getServicio().dejarDeSeguir(yo, sugerido.getUsername());
                boton.setText("Seguir");
            } else {
                ventana.getContexto().getServicio().seguir(yo, sugerido.getUsername());
                boton.setText("Siguiendo");
            }
            aviso.setText("");
        } catch (MiniWindowsException error) {
            aviso.setText(error.getMessage());
        }
    }
}
