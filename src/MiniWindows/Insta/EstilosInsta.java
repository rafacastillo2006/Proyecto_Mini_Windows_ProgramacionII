package MiniWindows.Insta;

import MiniWindows.Insta.Imagen.ArteGenerado;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import MiniWindows.Util.Imagenes;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.HashMap;
import java.util.Map;

public final class EstilosInsta {

    public static final String FUENTE = "Segoe UI";

    public static final String AZUL = "#0095f6";
    public static final String AZUL_ENCIMA = "#1877f2";
    public static final String TEXTO = "#262626";
    public static final String TEXTO_SUAVE = "#8e8e8e";
    public static final String BORDE = "#dbdbdb";
    public static final String FONDO = "#fafafa";
    public static final String SUPERFICIE = "#ffffff";
    public static final String SUAVE = "#efefef";
    public static final String PELIGRO = "#ed4956";

    public static final String PAGINA = "-fx-background-color: " + FONDO + ";";
    public static final String TARJETA = "-fx-background-color: " + SUPERFICIE + "; "
            + "-fx-border-color: " + BORDE + "; -fx-border-radius: 8; -fx-background-radius: 8;";
    public static final String CAMPO = "-fx-background-color: " + FONDO + "; "
            + "-fx-border-color: " + BORDE + "; -fx-border-radius: 6; -fx-background-radius: 6; "
            + "-fx-padding: 9 10 9 10; -fx-font-family: '" + FUENTE + "'; -fx-font-size: 13px; "
            + "-fx-text-fill: " + TEXTO + "; -fx-prompt-text-fill: " + TEXTO_SUAVE + ";";

    private static final Map<String, Image> AVATARES = new HashMap<>();

    private EstilosInsta() {
    }

    public static Label titulo(String texto, double tamano) {
        return etiqueta(texto, tamano, true, TEXTO);
    }

    public static Label texto(String contenido) {
        return etiqueta(contenido, 13.5, false, TEXTO);
    }

    public static Label fuerte(String contenido) {
        return etiqueta(contenido, 13.5, true, TEXTO);
    }

    public static Label leyenda(String contenido) {
        return etiqueta(contenido, 12, false, TEXTO_SUAVE);
    }

    public static Label error(String contenido) {
        Label etiqueta = etiqueta(contenido, 12, false, PELIGRO);
        etiqueta.setWrapText(true);
        etiqueta.managedProperty().bind(etiqueta.visibleProperty());
        etiqueta.visibleProperty().bind(etiqueta.textProperty().isEmpty().not());
        return etiqueta;
    }

    private static Label etiqueta(String contenido, double tamano, boolean fuerte, String color) {
        Label etiqueta = new Label(contenido);
        etiqueta.setMnemonicParsing(false);
        etiqueta.setStyle("-fx-font-family: '" + FUENTE + "'; -fx-font-size: " + tamano + "px; "
                + "-fx-text-fill: " + color + ";" + (fuerte ? " -fx-font-weight: bold;" : ""));
        return etiqueta;
    }

    public static TextField campo(String textoGuia) {
        TextField campo = new TextField();
        campo.setPromptText(textoGuia);
        campo.setStyle(CAMPO);
        return campo;
    }

    public static Button botonPrincipal(String texto) {
        Button boton = new Button(texto);
        boton.setMnemonicParsing(false);
        boton.setMaxWidth(Double.MAX_VALUE);
        pintar(boton, 13, AZUL, "white", AZUL_ENCIMA);
        return boton;
    }

    public static Button botonSuave(String texto) {
        Button boton = new Button(texto);
        boton.setMnemonicParsing(false);
        pintar(boton, 12.5, SUAVE, TEXTO, "#dbdbdb");
        return boton;
    }

    public static Button enlace(String texto) {
        Button boton = new Button(texto);
        boton.setMnemonicParsing(false);
        boton.setFocusTraversable(false);
        boton.setStyle("-fx-font-family: '" + FUENTE + "'; -fx-font-size: 12.5px; -fx-font-weight: bold; "
                + "-fx-background-color: transparent; -fx-text-fill: " + AZUL + "; "
                + "-fx-padding: 2 2 2 2; -fx-cursor: hand;");
        return boton;
    }

    public static Button usuario(String username, boolean verificada) {
        Button boton = new Button(username);
        boton.setMnemonicParsing(false);
        boton.setFocusTraversable(false);
        String base = "-fx-font-family: '" + FUENTE + "'; -fx-font-size: 13.5px; -fx-font-weight: bold; "
                + "-fx-background-color: transparent; -fx-padding: 1 2 1 2; -fx-cursor: hand; "
                + "-fx-text-fill: ";
        boton.setStyle(base + TEXTO + ";");
        boton.setOnMouseEntered(evento -> boton.setStyle(base + TEXTO_SUAVE + ";"));
        boton.setOnMouseExited(evento -> boton.setStyle(base + TEXTO + ";"));
        if (verificada) {
            boton.setGraphic(Iconos.crear(Iconos.VERIFICADO, 13, Color.web(AZUL)));
            boton.setContentDisplay(ContentDisplay.RIGHT);
            boton.setGraphicTextGap(5);
        }
        return boton;
    }

    public static Button botonIcono(String icono, String ayuda, double tamano) {
        Button boton = new Button();
        boton.setMnemonicParsing(false);
        boton.setGraphic(Iconos.crear(icono, tamano, Color.web(TEXTO)));
        boton.setTooltip(new Tooltip(ayuda));
        boton.setFocusTraversable(false);
        String base = "-fx-background-color: transparent; -fx-background-radius: 20; "
                + "-fx-padding: 6 6 6 6; -fx-cursor: hand;";
        boton.setStyle(base);
        boton.setOnMouseEntered(evento -> boton.setStyle(base.replace("transparent", SUAVE)));
        boton.setOnMouseExited(evento -> boton.setStyle(base));
        return boton;
    }

    private static void pintar(Button boton, double tamano, String fondo, String texto, String encima) {
        boton.setFocusTraversable(false);
        String base = "-fx-font-family: '" + FUENTE + "'; -fx-font-size: " + tamano + "px; "
                + "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16 8 16; "
                + "-fx-cursor: hand; -fx-text-fill: " + texto + "; -fx-background-color: ";
        boton.setStyle(base + fondo + ";");
        boton.setOnMouseEntered(evento -> boton.setStyle(base + encima + ";"));
        boton.setOnMouseExited(evento -> boton.setStyle(base + fondo + ";"));
    }

    public static ImageView avatar(UsuarioInsta usuario, double lado) {
        byte[] datos = usuario != null && usuario.getFoto() != null && usuario.getFoto().length > 0
                ? usuario.getFoto()
                : null;
        String clave = (usuario == null ? "?" : usuario.getUsername()) + "@" + lado;
        Image imagen = datos != null
                ? Imagenes.miniatura(datos, lado * 2)
                : generado(clave, usuario == null ? "?" : usuario.getUsername(), lado);
        return recortar(imagen, lado);
    }

    public static ImageView avatar(byte[] datos, double lado) {
        return recortar(Imagenes.miniatura(datos, lado * 2), lado);
    }

    private static Image generado(String clave, String nombre, double lado) {
        return AVATARES.computeIfAbsent(clave,
                ignorado -> Imagenes.miniatura(ArteGenerado.avatar(nombre, nombre.length() + 1), lado * 2));
    }

    private static ImageView recortar(Image imagen, double lado) {
        ImageView vista = new ImageView(imagen);
        vista.setFitWidth(lado);
        vista.setFitHeight(lado);
        vista.setPreserveRatio(false);
        vista.setSmooth(true);
        vista.setClip(new Circle(lado / 2, lado / 2, lado / 2));
        return vista;
    }

    public static Region espaciador() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }

    public static Region separador() {
        Region linea = new Region();
        linea.setMinHeight(1);
        linea.setPrefHeight(1);
        linea.setMaxHeight(1);
        linea.setStyle("-fx-background-color: " + BORDE + ";");
        return linea;
    }

    public static Node marca(double tamano) {
        Label logo = new Label("INSTA+");
        logo.setStyle("-fx-font-family: '" + FUENTE + "'; -fx-font-size: " + tamano + "px; "
                + "-fx-font-weight: bold; -fx-text-fill: " + TEXTO + ";");
        return logo;
    }
}
