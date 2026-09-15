package MiniWindows.SistemaOp.Escritorio;

import MiniWindows.Modelo.Usuario;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Locale;

public class Avatar extends StackPane {

    private static final String[] COLORES = {
            "#0067c0", "#2f7d32", "#a4373a", "#7719aa", "#c26a00", "#0e6e6e", "#4a5568", "#b3488b"};

    public Avatar(Usuario usuario, double diametro) {
        String color = colorPara(usuario.getUsername());
        Circle fondo = new Circle(diametro / 2);
        fondo.setFill(Color.web(color));

        Label iniciales = new Label(inicialesDe(usuario));
        iniciales.setStyle("-fx-font-family: '" + Estilos.FUENTE + "'; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-font-size: " + Math.max(11, diametro * 0.38) + "px;");

        getChildren().addAll(fondo, iniciales);
        setMinSize(diametro, diametro);
        setPrefSize(diametro, diametro);
        setMaxSize(diametro, diametro);
    }

    private static String colorPara(String username) {
        int indice = Math.abs(username.toLowerCase(Locale.ROOT).hashCode()) % COLORES.length;
        return COLORES[indice];
    }

    private static String inicialesDe(Usuario usuario) {
        String nombre = usuario.getNombreCompleto() == null ? "" : usuario.getNombreCompleto().trim();
        String[] partes = nombre.isEmpty() ? new String[0] : nombre.split("\\s+");
        if (partes.length >= 2) {
            return ("" + partes[0].charAt(0) + partes[1].charAt(0)).toUpperCase(Locale.ROOT);
        }
        if (partes.length == 1 && partes[0].length() >= 2) {
            return partes[0].substring(0, 2).toUpperCase(Locale.ROOT);
        }
        String username = usuario.getUsername();
        return username.substring(0, Math.min(2, username.length())).toUpperCase(Locale.ROOT);
    }
}
