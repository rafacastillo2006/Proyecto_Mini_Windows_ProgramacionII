package MiniWindows.SistemaOp.Escritorio;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class CentroAvisos extends VBox {

    private static final double ANCHO = 330;
    private static final int MAXIMO = 4;
    private static final Duration VISIBLE = Duration.seconds(7);

    public CentroAvisos() {
        super(10);
        setPickOnBounds(false);
        setAlignment(Pos.TOP_RIGHT);
        setPadding(new Insets(16, 16, 16, 16));
        setMaxWidth(ANCHO + 32);
        setMaxHeight(Region.USE_PREF_SIZE);
        setMouseTransparent(false);
    }

    public void mostrar(String icono, String titulo, String detalle, Runnable alPulsar) {
        while (getChildren().size() >= MAXIMO) {
            getChildren().remove(0);
        }
        VBox tarjeta = crearTarjeta(icono, titulo, detalle, alPulsar);
        getChildren().add(tarjeta);
        animarEntrada(tarjeta);
        programarSalida(tarjeta);
    }

    public int cuantosVisibles() {
        return getChildren().size();
    }

    public void limpiar() {
        getChildren().clear();
    }

    private VBox crearTarjeta(String icono, String titulo, String detalle, Runnable alPulsar) {
        Label encabezado = new Label(titulo);
        encabezado.setMnemonicParsing(false);
        encabezado.setWrapText(true);
        encabezado.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; -fx-font-weight: bold; "
                + "-fx-text-fill: #1b1b1b;");

        Label cuerpo = new Label(detalle);
        cuerpo.setMnemonicParsing(false);
        cuerpo.setWrapText(true);
        cuerpo.setMaxWidth(ANCHO - 90);
        cuerpo.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 12px; -fx-text-fill: #5b5b5b;");

        VBox textos = new VBox(2, encabezado, cuerpo);
        textos.setAlignment(Pos.CENTER_LEFT);

        Button cerrar = new Button();
        cerrar.setGraphic(Iconos.crear(Iconos.CERRAR, 12, Color.web("#5b5b5b")));
        cerrar.setFocusTraversable(false);
        cerrar.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 2 2 2 2;");

        HBox fila = new HBox(10, Iconos.crear(icono, 22, Color.web("#0095f6")), textos);
        fila.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textos, javafx.scene.layout.Priority.ALWAYS);

        HBox superior = new HBox(6, fila, espaciador(), cerrar);
        superior.setAlignment(Pos.TOP_LEFT);

        VBox tarjeta = new VBox(superior);
        tarjeta.setMinWidth(ANCHO);
        tarjeta.setMaxWidth(ANCHO);
        tarjeta.setPadding(new Insets(12, 12, 12, 14));
        tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 10; "
                + "-fx-border-color: #d8d8d8; -fx-border-radius: 10; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 14, 0.2, 0, 4);");

        cerrar.setOnAction(evento -> getChildren().remove(tarjeta));
        if (alPulsar != null) {
            tarjeta.setCursor(javafx.scene.Cursor.HAND);
            tarjeta.setOnMouseClicked(evento -> {
                getChildren().remove(tarjeta);
                alPulsar.run();
            });
        }
        return tarjeta;
    }

    private Region espaciador() {
        Region region = new Region();
        HBox.setHgrow(region, javafx.scene.layout.Priority.ALWAYS);
        return region;
    }

    private void animarEntrada(VBox tarjeta) {
        tarjeta.setTranslateX(ANCHO);
        TranslateTransition entrada = new TranslateTransition(Duration.millis(220), tarjeta);
        entrada.setToX(0);
        entrada.play();
    }

    private void programarSalida(VBox tarjeta) {
        PauseTransition espera = new PauseTransition(VISIBLE);
        espera.setOnFinished(evento -> {
            FadeTransition salida = new FadeTransition(Duration.millis(260), tarjeta);
            salida.setToValue(0);
            salida.setOnFinished(fin -> getChildren().remove(tarjeta));
            salida.play();
        });
        espera.play();
    }
}
