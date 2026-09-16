package MiniWindows.SistemaOp.AdminCuentas;

import MiniWindows.Modelo.Rol;
import MiniWindows.SistemaOp.Cuentas.SolicitudUsuario;
import MiniWindows.SistemaOp.Escritorio.Dialogos;
import MiniWindows.SistemaOp.Escritorio.Estilos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import MiniWindows.SistemaOp.Escritorio.CampoContrasena;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class DialogoUsuario extends Dialog<SolicitudUsuario> {

    private final TextField nombreCompleto = Estilos.campo("Nombre y apellido");
    private final TextField username = Estilos.campo("Sin espacios");
    private final CampoContrasena contrasena = new CampoContrasena("Letras y números, mínimo 6");
    private final Spinner<Integer> edad = new Spinner<>(1, 120, 18);
    private final ComboBox<Character> genero = new ComboBox<>();
    private final ComboBox<Rol> rol = new ComboBox<>();

    public DialogoUsuario(Node origen) {
        Dialogos.preparar(this, origen, "Nuevo usuario", null);
        getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().setContent(formulario());
        setResultConverter(boton -> boton == ButtonType.OK ? construir() : null);
    }

    private GridPane formulario() {
        genero.getItems().setAll('M', 'F');
        genero.setValue('M');
        rol.getItems().setAll(Rol.values());
        rol.setValue(Rol.ESTANDAR);
        edad.setEditable(true);

        GridPane formulario = new GridPane();
        formulario.setHgap(12);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(8, 4, 4, 4));
        formulario.addRow(0, Estilos.etiqueta("Nombre completo"), nombreCompleto);
        formulario.addRow(1, Estilos.etiqueta("Username"), username);
        formulario.addRow(2, Estilos.etiqueta("Contraseña"), contrasena);
        formulario.addRow(3, Estilos.etiqueta("Edad"), edad);
        formulario.addRow(4, Estilos.etiqueta("Género"), genero);
        formulario.addRow(5, Estilos.etiqueta("Rol"), rol);
        return formulario;
    }

    private SolicitudUsuario construir() {
        return new SolicitudUsuario(nombreCompleto.getText().trim(), genero.getValue(),
                username.getText().trim(), contrasena.getTexto(), edadIngresada(), rol.getValue());
    }

    private int edadIngresada() {
        try {
            return Integer.parseInt(edad.getEditor().getText().trim());
        } catch (NumberFormatException error) {
            return edad.getValue();
        }
    }
}
