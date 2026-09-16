package MiniWindows.SistemaOp.Escritorio;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public final class Estilos {

    public static final String ACENTO = "#0067c0";
    public static final String ACENTO_OSCURO = "#00518f";
    public static final String TEXTO = "#1b1b1b";
    public static final String TEXTO_SUAVE = "#5f6368";
    public static final String SUPERFICIE = "#fbfbfb";
    public static final String SUPERFICIE_ALTERNA = "#f3f3f3";
    public static final String BORDE = "#e3e3e3";
    public static final String BORDE_CAMPO = "#d8dce2";
    public static final String PELIGRO = "#c42b1c";
    public static final String FUENTE = "Segoe UI";

    public static final String PANEL = "-fx-background-color: " + SUPERFICIE + ";";
    public static final String PANEL_ALTERNO = "-fx-background-color: " + SUPERFICIE_ALTERNA + ";";
    public static final String BARRA_HERRAMIENTAS = "-fx-background-color: " + SUPERFICIE_ALTERNA + "; "
            + "-fx-border-color: transparent transparent " + BORDE + " transparent; -fx-border-width: 0 0 1 0;";
    public static final String BARRA_ESTADO = "-fx-background-color: " + SUPERFICIE_ALTERNA + "; "
            + "-fx-border-color: " + BORDE + " transparent transparent transparent; -fx-border-width: 1 0 0 0;";
    public static final String TARJETA = "-fx-background-color: white; -fx-background-radius: 12; "
            + "-fx-border-color: #e4e7eb; -fx-border-radius: 12; "
            + "-fx-effect: dropshadow(gaussian, rgba(16,24,40,0.10), 24, 0.05, 0, 8);";
    public static final String PISTA = "-fx-background-color: #f4f6f8; -fx-background-radius: 8;";
    public static final String SUPERFICIE_FLOTANTE = "-fx-background-color: rgba(249,249,249,0.97); "
            + "-fx-background-radius: 12; -fx-border-color: rgba(0,0,0,0.08); -fx-border-radius: 12; "
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.28), 36, 0.1, 0, 12);";
    public static final String TABLA = "-fx-background-color: white; -fx-border-color: transparent; "
            + "-fx-table-cell-border-color: transparent; -fx-font-family: '" + FUENTE + "'; -fx-font-size: 13px;";
    public static final String ARBOL = "-fx-background-color: " + SUPERFICIE_ALTERNA + "; "
            + "-fx-border-color: transparent; -fx-font-family: '" + FUENTE + "'; -fx-font-size: 13px;";

    private static final String BOTON_BASE = "-fx-font-family: '" + FUENTE + "'; -fx-font-size: 13px; "
            + "-fx-background-radius: 6; -fx-border-radius: 6; -fx-padding: 7 16 7 16; -fx-cursor: hand;";

    public static final String BOTON_PRIMARIO = BOTON_BASE
            + "-fx-background-color: " + ACENTO + "; -fx-text-fill: white;";
    public static final String BOTON_PRIMARIO_ENCIMA = BOTON_BASE
            + "-fx-background-color: " + ACENTO_OSCURO + "; -fx-text-fill: white;";
    public static final String BOTON_SECUNDARIO = BOTON_BASE
            + "-fx-background-color: white; -fx-text-fill: " + TEXTO + "; -fx-border-color: " + BORDE + ";";
    public static final String BOTON_SECUNDARIO_ENCIMA = BOTON_BASE
            + "-fx-background-color: #f0f0f0; -fx-text-fill: " + TEXTO + "; -fx-border-color: #d5d5d5;";
    public static final String BOTON_PLANO = "-fx-font-family: '" + FUENTE + "'; -fx-font-size: 13px; "
            + "-fx-background-radius: 6; -fx-padding: 6 10 6 10; -fx-cursor: hand; "
            + "-fx-background-color: transparent; -fx-text-fill: " + TEXTO + ";";
    public static final String BOTON_PLANO_ENCIMA = "-fx-font-family: '" + FUENTE + "'; -fx-font-size: 13px; "
            + "-fx-background-radius: 6; -fx-padding: 6 10 6 10; -fx-cursor: hand; "
            + "-fx-background-color: rgba(0,0,0,0.06); -fx-text-fill: " + TEXTO + ";";

    public static final String CAMPO = "-fx-font-family: '" + FUENTE + "'; -fx-font-size: 13px; "
            + "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + BORDE_CAMPO + "; "
            + "-fx-background-color: white; -fx-padding: 9 12 9 12;";
    public static final String CAMPO_ENFOCADO = "-fx-font-family: '" + FUENTE + "'; -fx-font-size: 13px; "
            + "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + ACENTO + "; "
            + "-fx-background-color: white; -fx-padding: 9 12 9 12;";

    private Estilos() {
    }

    public static Button botonPrimario(String texto) {
        Button boton = new Button(texto);
        boton.setMnemonicParsing(false);
        hover(boton, BOTON_PRIMARIO, BOTON_PRIMARIO_ENCIMA);
        return boton;
    }

    public static Button botonSecundario(String texto) {
        Button boton = new Button(texto);
        boton.setMnemonicParsing(false);
        hover(boton, BOTON_SECUNDARIO, BOTON_SECUNDARIO_ENCIMA);
        return boton;
    }

    public static Button botonHerramienta(String icono, String texto, String ayuda) {
        Button boton = new Button(texto);
        boton.setMnemonicParsing(false);
        boton.setGraphic(Iconos.crear(icono, 16, Color.web(TEXTO)));
        boton.setGraphicTextGap(8);
        boton.setTooltip(new Tooltip(ayuda));
        hover(boton, BOTON_PLANO, BOTON_PLANO_ENCIMA);
        return boton;
    }

    public static Button botonIcono(String icono, String ayuda) {
        Button boton = new Button();
        boton.setMnemonicParsing(false);
        boton.setGraphic(Iconos.crear(icono, 16, Color.web(TEXTO)));
        boton.setTooltip(new Tooltip(ayuda));
        boton.setMinSize(32, 32);
        boton.setPrefSize(32, 32);
        hover(boton, BOTON_PLANO, BOTON_PLANO_ENCIMA);
        return boton;
    }

    public static TextField campo(String textoGuia) {
        TextField campo = new TextField();
        campo.setPromptText(textoGuia);
        prepararCampo(campo);
        return campo;
    }

    public static PasswordField campoSecreto(String textoGuia) {
        PasswordField campo = new PasswordField();
        campo.setPromptText(textoGuia);
        prepararCampo(campo);
        return campo;
    }

    public static Label etiquetaCampo(String texto) {
        return etiquetaCon(texto, "-fx-font-size: 12px; -fx-text-fill: " + TEXTO_SUAVE + ";");
    }

    public static Label titulo(String texto) {
        return etiquetaCon(texto, "-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXTO + ";");
    }

    public static Label subtitulo(String texto) {
        return etiquetaCon(texto, "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXTO + ";");
    }

    public static Label etiqueta(String texto) {
        return etiquetaCon(texto, "-fx-font-size: 13px; -fx-text-fill: " + TEXTO + ";");
    }

    public static Label leyenda(String texto) {
        return etiquetaCon(texto, "-fx-font-size: 12px; -fx-text-fill: " + TEXTO_SUAVE + ";");
    }

    public static Label error(String texto) {
        return etiquetaCon(texto, "-fx-font-size: 12px; -fx-text-fill: " + PELIGRO + ";");
    }

    public static void hover(Region nodo, String normal, String encima) {
        nodo.setStyle(normal);
        nodo.setOnMouseEntered(evento -> {
            if (!nodo.isDisabled()) {
                nodo.setStyle(encima);
            }
        });
        nodo.setOnMouseExited(evento -> nodo.setStyle(normal));
        nodo.disabledProperty().addListener((observable, anterior, actual) -> nodo.setStyle(normal));
    }

    public static Region espaciador() {
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        return espacio;
    }

    public static Region separadorVertical() {
        Region separador = new Region();
        separador.setMinWidth(1);
        separador.setPrefWidth(1);
        separador.setMaxHeight(22);
        separador.setStyle("-fx-background-color: " + BORDE + ";");
        return separador;
    }

    public static HBox fila(double espacio, Node... hijos) {
        HBox caja = new HBox(espacio, hijos);
        caja.setAlignment(Pos.CENTER_LEFT);
        return caja;
    }

    private static void prepararCampo(TextInputControl campo) {
        campo.setStyle(CAMPO);
        campo.focusedProperty().addListener((observable, anterior, enfocado) ->
                campo.setStyle(enfocado ? CAMPO_ENFOCADO : CAMPO));
    }

    private static Label etiquetaCon(String texto, String estilo) {
        Label etiqueta = new Label(texto);
        etiqueta.setStyle("-fx-font-family: '" + FUENTE + "';" + estilo);
        return etiqueta;
    }
}
