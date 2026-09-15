package MiniWindows.SistemaOp.ConsolaComandos;

import MiniWindows.SistemaOp.Apps.ContextoApp;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.List;

public class PanelConsola extends BorderPane {

    private static final String FONDO = "-fx-background-color: #0c0c0c;";
    private static final String LETRA = "-fx-font-family: 'Consolas', 'Courier New', monospace; "
            + "-fx-font-size: 13px; -fx-text-fill: #e8e8e8;";
    private static final String SALIDA = FONDO + LETRA + " -fx-control-inner-background: #0c0c0c; "
            + "-fx-highlight-fill: #3a6ea5; -fx-border-color: transparent;";
    private static final String ENTRADA = FONDO + LETRA + " -fx-highlight-fill: #3a6ea5; "
            + "-fx-border-color: transparent; -fx-background-radius: 0; -fx-padding: 0;";

    private final InterpreteComandos interprete;
    private final TextArea salida = new TextArea();
    private final TextField entrada = new TextField();
    private final Label indicador = new Label();
    private final List<String> historial = new ArrayList<>();

    private int posicionHistorial;

    public PanelConsola(ContextoApp contexto) {
        this.interprete = new InterpreteComandos(contexto.getArchivos(), contexto.getSesion());

        salida.setEditable(false);
        salida.setWrapText(true);
        salida.setFocusTraversable(false);
        salida.setStyle(SALIDA);
        salida.setOnMouseClicked(evento -> {
            if (salida.getSelectedText().isEmpty()) {
                entrada.requestFocus();
            }
        });

        indicador.setStyle(LETRA);
        entrada.setStyle(ENTRADA);
        entrada.setOnAction(evento -> ejecutarLinea());
        entrada.setOnKeyPressed(evento -> {
            if (evento.getCode() == KeyCode.UP) {
                recorrerHistorial(-1);
                evento.consume();
            } else if (evento.getCode() == KeyCode.DOWN) {
                recorrerHistorial(1);
                evento.consume();
            }
        });
        HBox.setHgrow(entrada, Priority.ALWAYS);

        HBox linea = new HBox(6, indicador, entrada);
        linea.setAlignment(Pos.CENTER_LEFT);
        linea.setPadding(new Insets(0, 10, 10, 10));
        linea.setStyle(FONDO);

        setCenter(salida);
        setBottom(linea);
        setStyle(FONDO);

        escribir("MiniWindows [Version 1.0]");
        escribir("Escribe help para ver los comandos disponibles.");
        escribir("");
        actualizarIndicador();

        setOnMouseClicked(evento -> entrada.requestFocus());
        sceneProperty().addListener((observable, anterior, actual) -> {
            if (actual != null) {
                Platform.runLater(entrada::requestFocus);
            }
        });
    }

    private void ejecutarLinea() {
        String linea = entrada.getText();
        entrada.clear();
        escribir(indicador.getText() + linea);
        if (!linea.isBlank()) {
            historial.add(linea);
            posicionHistorial = historial.size();
        }
        String respuesta = interprete.ejecutar(linea);
        if (interprete.pidioLimpiarPantalla()) {
            salida.clear();
        } else if (!respuesta.isEmpty()) {
            escribir(respuesta);
        }
        actualizarIndicador();
        salida.setScrollTop(Double.MAX_VALUE);
    }

    private void recorrerHistorial(int desplazamiento) {
        if (historial.isEmpty()) {
            return;
        }
        posicionHistorial = Math.clamp(posicionHistorial + desplazamiento, 0, historial.size());
        entrada.setText(posicionHistorial == historial.size() ? "" : historial.get(posicionHistorial));
        entrada.positionCaret(entrada.getText().length());
    }

    private void escribir(String texto) {
        salida.appendText(texto + System.lineSeparator());
    }

    private void actualizarIndicador() {
        indicador.setText(interprete.getIndicador());
    }
}
