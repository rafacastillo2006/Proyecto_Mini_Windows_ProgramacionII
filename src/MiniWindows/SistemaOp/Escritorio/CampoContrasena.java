package MiniWindows.SistemaOp.Escritorio;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class CampoContrasena extends HBox {

    private final PasswordField oculto = new PasswordField();
    private final TextField visible = new TextField();
    private final Button ojo = new Button();

    public CampoContrasena(String textoGuia, String estiloCampo) {
        super(4);

        oculto.setPromptText(textoGuia);
        visible.setPromptText(textoGuia);
        oculto.setStyle(estiloCampo);
        visible.setStyle(estiloCampo);
        visible.textProperty().bindBidirectional(oculto.textProperty());

        visible.setVisible(false);
        visible.setManaged(false);

        HBox.setHgrow(oculto, Priority.ALWAYS);
        HBox.setHgrow(visible, Priority.ALWAYS);

        ojo.setGraphic(Iconos.crear(Iconos.OJO, 15, Color.web(Estilos.TEXTO_SUAVE)));
        ojo.setTooltip(new Tooltip("Mostrar la contraseña"));
        ojo.setFocusTraversable(false);
        String base = "-fx-background-color: transparent; -fx-background-radius: 6; -fx-cursor: hand; "
                + "-fx-padding: 6 8 6 8;";
        Estilos.hover(ojo, base, "-fx-background-color: rgba(0,0,0,0.07); -fx-background-radius: 6; "
                + "-fx-cursor: hand; -fx-padding: 6 8 6 8;");
        ojo.setOnAction(evento -> alternar());

        getChildren().addAll(oculto, visible, ojo);
    }

    public CampoContrasena(String textoGuia) {
        this(textoGuia, Estilos.CAMPO);
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
        (mostrando ? oculto : visible).requestFocus();
        (mostrando ? oculto : visible).positionCaret(getTexto().length());
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
