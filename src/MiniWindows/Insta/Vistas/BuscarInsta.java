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
import javafx.scene.layout.VBox;

public class BuscarInsta extends VBox {

    private static final double ANCHO = 460;

    private final VentanaInsta ventana;
    private final TextField campo = new TextField();
    private final VBox resultados = new VBox(2);
    private final Label aviso = EstilosInsta.error("");

    public BuscarInsta(VentanaInsta ventana) {
        this.ventana = ventana;

        setPadding(new Insets(20, 20, 24, 20));
        setSpacing(12);
        setAlignment(Pos.TOP_CENTER);
        setStyle(EstilosInsta.PAGINA);

        Label titulo = EstilosInsta.titulo("Buscar perfil", 18);
        titulo.setMaxWidth(ANCHO);
        titulo.setMinWidth(ANCHO);

        campo.setPromptText("Buscar por username o nombre");
        campo.setStyle(EstilosInsta.CAMPO);
        campo.setMaxWidth(ANCHO);
        campo.setMinWidth(ANCHO);
        campo.textProperty().addListener((observable, anterior, actual) -> buscar(actual));

        resultados.setMaxWidth(ANCHO);
        resultados.setMinWidth(ANCHO);
        resultados.setPadding(new Insets(6, 4, 6, 4));
        resultados.setStyle(EstilosInsta.TARJETA);

        ScrollPane marco = new ScrollPane(resultados);
        marco.setFitToWidth(true);
        marco.setPrefHeight(430);
        marco.setMaxWidth(ANCHO + 4);
        marco.setStyle("-fx-background: " + EstilosInsta.FONDO + "; -fx-background-color: "
                + EstilosInsta.FONDO + "; -fx-border-color: transparent;");

        getChildren().addAll(titulo, campo, aviso, marco);
        buscar("");
    }

    private void buscar(String criterio) {
        aviso.setText("");
        resultados.getChildren().clear();
        String yo = ventana.getContexto().getUsuarioActual();
        for (UsuarioInsta encontrado : ventana.getContexto().getServicio().buscarPersonas(criterio)) {
            if (!encontrado.getUsername().equalsIgnoreCase(yo)) {
                resultados.getChildren().add(FilaUsuario.conEstado(ventana, encontrado, this::alternar));
            }
        }
        if (resultados.getChildren().isEmpty()) {
            Label vacio = EstilosInsta.leyenda("Sin resultados.");
            vacio.setPadding(new Insets(10, 0, 10, 10));
            resultados.getChildren().add(vacio);
        }
    }

    private void alternar(UsuarioInsta persona, Button boton) {
        String yo = ventana.getContexto().getUsuarioActual();
        try {
            if (ventana.getContexto().getServicio().sigue(yo, persona.getUsername())) {
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
            aviso.setText(error.getMessage());
        }
    }
}
