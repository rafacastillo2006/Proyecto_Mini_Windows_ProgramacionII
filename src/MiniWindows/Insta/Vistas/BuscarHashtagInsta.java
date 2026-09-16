package MiniWindows.Insta.Vistas;

import MiniWindows.Insta.EstilosInsta;
import MiniWindows.Insta.VentanaInsta;
import MiniWindows.Modelo.Publicacion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class BuscarHashtagInsta extends VBox {

    private final VentanaInsta ventana;
    private final TextField campo = new TextField();
    private final VBox resultados = new VBox(16);

    public BuscarHashtagInsta(VentanaInsta ventana, String inicial) {
        this.ventana = ventana;

        setPadding(new Insets(18));
        setSpacing(12);
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: " + EstilosInsta.FONDO + ";");

        campo.setPromptText("Escribe un hashtag, por ejemplo #viajes");
        campo.setStyle(EstilosInsta.CAMPO);
        campo.setMaxWidth(460);
        campo.textProperty().addListener((observable, anterior, actual) -> buscar(actual));

        ScrollPane marco = new ScrollPane(resultados);
        marco.setFitToWidth(true);
        marco.setPrefHeight(470);
        marco.setStyle("-fx-background: " + EstilosInsta.FONDO + "; -fx-background-color: "
                + EstilosInsta.FONDO + "; -fx-border-color: transparent;");
        resultados.setAlignment(Pos.TOP_CENTER);

        getChildren().addAll(EstilosInsta.titulo("Buscar hashtag", 22), campo, marco);

        if (inicial != null && !inicial.isBlank()) {
            campo.setText(inicial);
        } else {
            buscar("");
        }
    }

    private void buscar(String criterio) {
        resultados.getChildren().clear();
        if (criterio == null || criterio.replace("#", "").trim().length() < 2) {
            resultados.getChildren().add(EstilosInsta.leyenda("Escribe al menos dos letras."));
            return;
        }
        for (Publicacion publicacion : ventana.getContexto().getServicio().buscarHashtag(criterio)) {
            resultados.getChildren().add(new TarjetaPublicacion(ventana, publicacion));
        }
        if (resultados.getChildren().isEmpty()) {
            resultados.getChildren().add(EstilosInsta.leyenda("Ninguna publicacion con ese hashtag."));
        }
    }
}
