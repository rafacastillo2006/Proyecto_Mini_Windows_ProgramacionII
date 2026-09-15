package MiniWindows.SistemaOp.AdminCuentas;

import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Modelo.Rol;
import MiniWindows.Modelo.Usuario;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Cuentas.ServicioCuentas;
import MiniWindows.SistemaOp.Escritorio.Dialogos;
import MiniWindows.SistemaOp.Escritorio.Estilos;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import MiniWindows.Util.Fechas;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.util.List;

public class PanelCuentas extends BorderPane {

    private final ServicioCuentas cuentas;
    private final Usuario administrador;
    private final TableView<Usuario> tabla = new TableView<>();
    private final Label estado = Estilos.leyenda("");

    public PanelCuentas(ContextoApp contexto) {
        this.cuentas = contexto.getCuentas();
        this.administrador = contexto.getSesion().getUsuario();

        setTop(crearBarraHerramientas());
        setCenter(crearTabla());
        setBottom(crearBarraEstado());
        setStyle(Estilos.PANEL);

        refrescar();
    }

    private FlowPane crearBarraHerramientas() {
        Button nuevo = Estilos.botonHerramienta(Iconos.USUARIO, "Nuevo usuario", "Crear una cuenta");
        nuevo.setOnAction(evento -> crearUsuario());

        Button rol = Estilos.botonHerramienta(Iconos.CUENTAS, "Cambiar rol", "Cambiar el rol de la cuenta");
        rol.setOnAction(evento -> cambiarRol());

        Button estadoCuenta = Estilos.botonHerramienta(Iconos.APAGAR, "Activar / Desactivar",
                "Cambiar el estado de la cuenta");
        estadoCuenta.setOnAction(evento -> alternarEstado());

        Button contrasena = Estilos.botonHerramienta(Iconos.RENOMBRAR, "Restablecer contraseña",
                "Asignar una contraseña nueva");
        contrasena.setOnAction(evento -> restablecerContrasena());

        Button eliminar = Estilos.botonHerramienta(Iconos.ELIMINAR, "Eliminar", "Eliminar la cuenta y su carpeta");
        eliminar.setOnAction(evento -> eliminar());

        Button actualizar = Estilos.botonIcono(Iconos.ACTUALIZAR, "Actualizar");
        actualizar.setOnAction(evento -> refrescar());

        for (Button boton : List.of(rol, estadoCuenta, contrasena, eliminar)) {
            boton.disableProperty().bind(tabla.getSelectionModel().selectedItemProperty().isNull());
        }

        FlowPane barra = new FlowPane(4, 4, nuevo, Estilos.separadorVertical(), rol, estadoCuenta, contrasena,
                eliminar, Estilos.separadorVertical(), actualizar);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setPadding(new Insets(8, 10, 8, 10));
        barra.setStyle(Estilos.BARRA_HERRAMIENTAS);
        return barra;
    }

    private TableView<Usuario> crearTabla() {
        tabla.getColumns().setAll(List.of(
                columna("Usuario", 150, Usuario::getUsername),
                columna("Nombre completo", 220, Usuario::getNombreCompleto),
                columna("Rol", 130, usuario -> usuario.getRol().getEtiqueta()),
                columna("Edad", 70, usuario -> String.valueOf(usuario.getEdad())),
                columna("Género", 80, usuario -> String.valueOf(usuario.getGenero())),
                columna("Registro", 170, usuario -> Fechas.formatear(usuario.getFechaRegistro())),
                columna("Estado", 100, usuario -> usuario.estaActiva() ? "Activa" : "Inactiva")));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setPlaceholder(Estilos.leyenda("No hay cuentas registradas"));
        tabla.setStyle(Estilos.TABLA);
        return tabla;
    }

    private TableColumn<Usuario, String> columna(String titulo, double ancho,
                                                 java.util.function.Function<Usuario, String> valor) {
        TableColumn<Usuario, String> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(dato -> new ReadOnlyStringWrapper(valor.apply(dato.getValue())));
        columna.setPrefWidth(ancho);
        return columna;
    }

    private HBox crearBarraEstado() {
        HBox barra = Estilos.fila(12, estado);
        barra.setPadding(new Insets(6, 12, 6, 12));
        barra.setStyle(Estilos.BARRA_ESTADO);
        return barra;
    }

    private void crearUsuario() {
        new DialogoUsuario(this).showAndWait().ifPresent(solicitud -> ejecutar(() -> {
            Usuario creado = cuentas.crearUsuario(solicitud);
            refrescar();
            Dialogos.informacion(this, "Cuenta creada",
                    "Se creó la cuenta \"" + creado.getUsername() + "\" con su carpeta en "
                            + "Z:\\" + creado.getUsername());
        }));
    }

    private void cambiarRol() {
        Usuario usuario = seleccionado();
        ChoiceDialog<Rol> dialogo = new ChoiceDialog<>(usuario.getRol(), Rol.values());
        Dialogos.preparar(dialogo, this, "Cambiar rol", "Rol para \"" + usuario.getUsername() + "\"");
        dialogo.showAndWait().ifPresent(rol -> ejecutar(() -> {
            cuentas.cambiarRol(usuario, rol, administrador);
            refrescar();
        }));
    }

    private void alternarEstado() {
        Usuario usuario = seleccionado();
        boolean activar = !usuario.estaActiva();
        String mensaje = activar
                ? "¿Activar la cuenta \"" + usuario.getUsername() + "\"?"
                : "¿Desactivar la cuenta \"" + usuario.getUsername() + "\"? No podrá iniciar sesión.";
        if (!Dialogos.confirmar(this, "Estado de la cuenta", mensaje)) {
            return;
        }
        ejecutar(() -> {
            cuentas.cambiarEstado(usuario, activar, administrador);
            refrescar();
        });
    }

    private void restablecerContrasena() {
        Usuario usuario = seleccionado();
        Dialogos.pedirTexto(this, "Restablecer contraseña",
                        "Nueva contraseña para \"" + usuario.getUsername() + "\"", "")
                .ifPresent(nueva -> ejecutar(() -> {
                    cuentas.cambiarContrasena(usuario, nueva);
                    Dialogos.informacion(this, "Contraseña actualizada",
                            "La contraseña de \"" + usuario.getUsername() + "\" fue cambiada.");
                }));
    }

    private void eliminar() {
        Usuario usuario = seleccionado();
        if (!Dialogos.confirmar(this, "Eliminar cuenta",
                "Se eliminará la cuenta \"" + usuario.getUsername() + "\" y su carpeta Z:\\"
                        + usuario.getUsername() + " con todo su contenido. ¿Continuar?")) {
            return;
        }
        ejecutar(() -> {
            cuentas.eliminar(usuario, administrador);
            refrescar();
        });
    }

    private void refrescar() {
        tabla.getItems().setAll(cuentas.listar().aLista());
        estado.setText(tabla.getItems().size() + " cuenta(s) registradas");
    }

    private Usuario seleccionado() {
        return tabla.getSelectionModel().getSelectedItem();
    }

    private void ejecutar(OperacionCuentas operacion) {
        try {
            operacion.ejecutar();
        } catch (MiniWindowsException error) {
            Dialogos.error(this, "Operación no completada", error.getMessage());
        }
    }

    @FunctionalInterface
    private interface OperacionCuentas {
        void ejecutar() throws MiniWindowsException;
    }
}
