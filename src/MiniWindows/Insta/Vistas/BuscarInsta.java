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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BuscarInsta extends VBox {

    private final VentanaInsta ventana;
    private final TextField campo = new TextField();
    private final VBox resultados = new VBox(6);

    public BuscarInsta(VentanaInsta ventana) {
        this.ventana = ventana;

        setPadding(new Insets(18));
        setSpacing(12);
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: " + EstilosInsta.FONDO + ";");

        campo.setPromptText("Buscar por username o nombre");
        campo.setStyle(EstilosInsta.CAMPO);
        campo.setMaxWidth(460);
        campo.textProperty().addListener((observable, anterior, actual) -> buscar(actual));

        ScrollPane marco = new ScrollPane(resultados);
        marco.setFitToWidth(true);
        marco.setPrefHeight(470);
        marco.setMaxWidth(500);
        marco.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: transparent;");

        getChildren().addAll(EstilosInsta.titulo("Buscar perfil", 22), campo, marco);
        buscar("");
    }

    private void buscar(String criterio) {
        resultados.getChildren().clear();
        String yo = ventana.getContexto().getUsuarioActual();
        for (UsuarioInsta encontrado : ventana.getContexto().getServicio().buscarPersonas(criterio)) {
            if (!encontrado.getUsername().equalsIgnoreCase(yo)) {
                resultados.getChildren().add(fila(encontrado));
            }
        }
        if (resultados.getChildren().isEmpty()) {
            resultados.getChildren().add(EstilosInsta.leyenda("Sin resultados."));
        }
    }

    private HBox fila(UsuarioInsta persona) {
        String yo = ventana.getContexto().getUsuarioActual();
        boolean siguiendo = ventana.getContexto().getServicio().sigue(yo, persona.getUsername());

        Button nombre = EstilosInsta.enlace(persona.getUsername().toUpperCase()
                + "  —  " + (siguiendo ? "Lo sigo" : "No lo sigues"));
        nombre.setOnAction(evento -> ventana.mostrarPerfilDe(persona.getUsername()));

        Label detalle = EstilosInsta.leyenda(persona.getNombreCompleto()
                + (persona.esVerificada() ? "  (verificada)" : ""));

        VBox datos = new VBox(0, nombre, detalle);

        Button seguir = siguiendo ? EstilosInsta.botonSuave("Dejar de seguir")
                : EstilosInsta.botonPrincipal("Seguir");
        seguir.setMaxWidth(130);
        seguir.setOnAction(evento -> alternar(persona, siguiendo));

        HBox fila = new HBox(12, EstilosInsta.avatar(persona, 42), datos,
                EstilosInsta.espaciador(), seguir);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(4));
        return fila;
    }

    private void alternar(UsuarioInsta persona, boolean siguiendo) {
        String yo = ventana.getContexto().getUsuarioActual();
        try {
            if (siguiendo) {
                if (!ventana.confirmar("Dejar de seguir",
                        "¿Seguro que quieres dejar de seguir a @" + persona.getUsername() + "?")) {
                    return;
                }
                ventana.getContexto().getServicio().dejarDeSeguir(yo, persona.getUsername());
            } else {
                ventana.getContexto().getServicio().seguir(yo, persona.getUsername());
            }
            buscar(campo.getText());
        } catch (MiniWindowsException error) {
            resultados.getChildren().add(EstilosInsta.error(error.getMessage()));
        }
    }
}
