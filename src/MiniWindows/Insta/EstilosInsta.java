package MiniWindows.Insta;

import MiniWindows.Insta.Imagen.ArteGenerado;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Util.Imagenes;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;

public final class EstilosInsta {

    public static final String AZUL = "#0095f6";
    public static final String TEXTO = "#262626";
    public static final String TEXTO_SUAVE = "#8e8e8e";
    public static final String BORDE = "#dbdbdb";
    public static final String FONDO = "#fafafa";
    public static final String FUENTE = "Segoe UI";

    public static final String TARJETA = "-fx-background-color: white; -fx-border-color: " + BORDE + "; "
            + "-fx-border-radius: 8; -fx-background-radius: 8;";
    public static final String CAMPO = "-fx-background-color: #fafafa; -fx-border-color: " + BORDE + "; "
            + "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8;";
    public static final String DEGRADADO =
            "-fx-background-color: linear-gradient(to bottom right, #f9ce34, #ee2a7b, #6228d7);";

    private EstilosInsta() {
    }

    public static Label titulo(String texto, double tamano) {
        Label etiqueta = new Label(texto);
        etiqueta.setStyle("-fx-font-family: '" + FUENTE + "'; -fx-font-size: " + tamano + "px; "
                + "-fx-font-weight: bold; -fx-text-fill: " + TEXTO + ";");
        return etiqueta;
    }

    public static Label texto(String contenido) {
        Label etiqueta = new Label(contenido);
        etiqueta.setStyle("-fx-font-family: '" + FUENTE + "'; -fx-font-size: 13px; -fx-text-fill: " + TEXTO + ";");
        return etiqueta;
    }

    public static Label leyenda(String contenido) {
        Label etiqueta = new Label(contenido);
        etiqueta.setStyle("-fx-font-family: '" + FUENTE + "'; -fx-font-size: 12px; "
                + "-fx-text-fill: " + TEXTO_SUAVE + ";");
        return etiqueta;
    }

    public static Label error(String contenido) {
        Label etiqueta = new Label(contenido);
        etiqueta.setWrapText(true);
        etiqueta.setStyle("-fx-font-family: '" + FUENTE + "'; -fx-font-size: 12px; -fx-text-fill: #ed4956;");
        return etiqueta;
    }

    public static Button botonPrincipal(String texto) {
        Button boton = new Button(texto);
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setStyle("-fx-font-family: '" + FUENTE + "'; -fx-font-size: 13px; -fx-font-weight: bold; "
                + "-fx-background-color: " + AZUL + "; -fx-text-fill: white; -fx-background-radius: 8; "
                + "-fx-padding: 8 14; -fx-cursor: hand;");
        return boton;
    }

    public static Button botonSuave(String texto) {
        Button boton = new Button(texto);
        boton.setStyle("-fx-font-family: '" + FUENTE + "'; -fx-font-size: 12px; -fx-font-weight: bold; "
                + "-fx-background-color: #efefef; -fx-text-fill: " + TEXTO + "; -fx-background-radius: 8; "
                + "-fx-padding: 6 14; -fx-cursor: hand;");
        return boton;
    }

    public static Button enlace(String texto) {
        Button boton = new Button(texto);
        boton.setStyle("-fx-font-family: '" + FUENTE + "'; -fx-font-size: 12px; -fx-font-weight: bold; "
                + "-fx-background-color: transparent; -fx-text-fill: " + AZUL + "; -fx-padding: 2 4; "
                + "-fx-cursor: hand;");
        return boton;
    }

    public static ImageView avatar(UsuarioInsta usuario, double lado) {
        byte[] datos = usuario != null && usuario.getFoto() != null && usuario.getFoto().length > 0
                ? usuario.getFoto()
                : ArteGenerado.avatar(usuario == null ? "?" : usuario.getUsername(), 1);
        return avatar(datos, lado);
    }

    public static ImageView avatar(byte[] datos, double lado) {
        Image imagen = Imagenes.miniatura(datos, lado * 2);
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
        javafx.scene.layout.HBox.setHgrow(region, javafx.scene.layout.Priority.ALWAYS);
        return region;
    }
}
