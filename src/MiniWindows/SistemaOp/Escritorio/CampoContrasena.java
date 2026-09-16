package MiniWindows.SistemaOp.Escritorio;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class CampoContrasena extends StackPane {

    private static final double ANCHO_OJO = 34;

    private final PasswordField oculto = new PasswordField();
    private final TextField visible = new TextField();
    private final Button ojo = new Button();

    public CampoContrasena(String textoGuia, String estiloCampo) {
        preparar(oculto, textoGuia, estiloCampo);
        preparar(visible, textoGuia, estiloCampo);
        visible.textProperty().bindBidirectional(oculto.textProperty());
        visible.setVisible(false);
        visible.setManaged(false);

        ojo.setGraphic(Iconos.crear(Iconos.OJO, 15, Color.web(Estilos.TEXTO_SUAVE)));
        ojo.setTooltip(new Tooltip("Mostrar la contraseña"));
        ojo.setFocusTraversable(false);
        ojo.setMinWidth(ANCHO_OJO);
        ojo.setPrefWidth(ANCHO_OJO);
        String base = "-fx-background-color: transparent; -fx-background-radius: 6; -fx-cursor: hand; "
                + "-fx-padding: 4 8 4 8;";
        ojo.setStyle(base);
        ojo.setOnMouseEntered(evento -> ojo.setStyle(base.replace("transparent", "rgba(0,0,0,0.06)")));
        ojo.setOnMouseExited(evento -> ojo.setStyle(base));
        ojo.setOnAction(evento -> alternar());

        StackPane.setAlignment(ojo, Pos.CENTER_RIGHT);
        StackPane.setMargin(ojo, new Insets(0, 4, 0, 0));
        getChildren().addAll(oculto, visible, ojo);
    }

    public CampoContrasena(String textoGuia) {
        this(textoGuia, Estilos.CAMPO);
    }

    private void preparar(TextInputControl campo, String textoGuia, String estiloCampo) {
        campo.setPromptText(textoGuia);
        campo.setStyle(estiloCampo + " -fx-padding: 9 " + (ANCHO_OJO + 6) + " 9 10;");
        campo.setMaxWidth(Double.MAX_VALUE);
    }

    private void alternar() {
        boolean mostrando = visible.isVisible();
        visible.setVisible(!mostrando);
        visible.setManaged(!mostrando);
        oculto.setVisible(mostrando);
        oculto.setManaged(mostrando);
        ojo.setGraphic(Iconos.crear(mostrando ? Iconos.OJO : Iconos.OJO_TACHADO, 15,
                Color.web(Estilos.TEXTO_SUAVE)));
        ojo.getTooltip().setText(mostrando ? "Mostrar la contraseña" : "Ocultar la contraseña");
        TextInputControl activo = mostrando ? oculto : visible;
        activo.requestFocus();
        activo.positionCaret(getTexto().length());
    }

    public String getTexto() {
        return oculto.getText() == null ? "" : oculto.getText();
    }

    public void setTexto(String texto) {
        oculto.setText(texto);
    }

    public void limpiar() {
        oculto.clear();
    }

    public void alConfirmar(EventHandler<ActionEvent> accion) {
        oculto.setOnAction(accion);
        visible.setOnAction(accion);
    }

    public void pedirFoco() {
        (visible.isVisible() ? visible : oculto).requestFocus();
    }

    public boolean estaVisible() {
        return visible.isVisible();
    }
}
