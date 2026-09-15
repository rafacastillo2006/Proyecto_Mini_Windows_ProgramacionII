package MiniWindows.SistemaOp.Escritorio;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public final class Dialogos {

    private Dialogos() {
    }

    public static void informacion(Node origen, String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        preparar(alerta, origen, titulo, mensaje);
        alerta.showAndWait();
    }

    public static void error(Node origen, String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        preparar(alerta, origen, titulo, mensaje);
        alerta.showAndWait();
    }

    public static boolean confirmar(Node origen, String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        preparar(alerta, origen, titulo, mensaje);
        alerta.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        return alerta.showAndWait().filter(boton -> boton == ButtonType.YES).isPresent();
    }

    public static Optional<String> pedirTexto(Node origen, String titulo, String mensaje, String valorInicial) {
        TextInputDialog dialogo = new TextInputDialog(valorInicial);
        preparar(dialogo, origen, titulo, mensaje);
        dialogo.getEditor().setStyle(Estilos.CAMPO);
        return dialogo.showAndWait().map(String::trim).filter(texto -> !texto.isEmpty());
    }

    public static void preparar(Dialog<?> dialogo, Node origen, String titulo, String mensaje) {
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(null);
        if (mensaje != null) {
            dialogo.setContentText(mensaje);
        }
        dialogo.getDialogPane().setStyle("-fx-font-family: '" + Estilos.FUENTE + "'; -fx-font-size: 13px; "
                + "-fx-background-color: " + Estilos.SUPERFICIE + ";");
        if (origen != null && origen.getScene() != null && origen.getScene().getWindow() != null) {
            dialogo.initOwner(origen.getScene().getWindow());
        }
    }
}
