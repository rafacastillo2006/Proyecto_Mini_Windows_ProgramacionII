package MiniWindows.Insta.Vistas;

import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Insta.EstilosInsta;
import MiniWindows.Insta.Imagen.ProcesadorImagen;
import MiniWindows.Insta.VentanaInsta;
import MiniWindows.Modelo.UsuarioInsta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;

public class EditarPerfilInsta extends VBox {

    private static final int LADO_AVATAR = 320;

    private final VentanaInsta ventana;
    private final UsuarioInsta perfil;
    private final TextField nombre = new TextField();
    private final TextField biografia = new TextField();
    private final Spinner<Integer> edad = new Spinner<>(13, 120, 18);
    private final Label aviso = EstilosInsta.leyenda("");

    private byte[] foto;

    public EditarPerfilInsta(VentanaInsta ventana) {
        this.ventana = ventana;
        this.perfil = ventana.getContexto().getSesion().getUsuarioActual();

        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(24));
        setStyle("-fx-background-color: " + EstilosInsta.FONDO + ";");

        nombre.setText(perfil.getNombreCompleto());
        nombre.setStyle(EstilosInsta.CAMPO);
        biografia.setText(perfil.getBiografia());
        biografia.setPromptText("Cuenta algo sobre ti");
        biografia.setStyle(EstilosInsta.CAMPO);
        edad.getValueFactory().setValue(perfil.getEdad());
        edad.setMaxWidth(Double.MAX_VALUE);

        VBox tarjeta = new VBox(10, EstilosInsta.titulo("Editar perfil", 22),
                EstilosInsta.leyenda("Nombre completo"), nombre,
                EstilosInsta.leyenda("Biografía"), biografia,
                EstilosInsta.leyenda("Edad"), edad,
                botonFoto(), botonGuardar(), separador(), estadoDeCuenta(), aviso);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(22));
        tarjeta.setMaxWidth(420);
        tarjeta.setStyle(EstilosInsta.TARJETA);

        getChildren().add(tarjeta);
    }

    private Label separador() {
        Label linea = EstilosInsta.leyenda("Estado de la cuenta");
        VBox.setMargin(linea, new Insets(10, 0, 0, 0));
        return linea;
    }

    private Button botonFoto() {
        Button boton = EstilosInsta.botonSuave("Cambiar foto de perfil");
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setOnAction(evento -> elegirFoto());
        return boton;
    }

    private void elegirFoto() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Foto de perfil");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagenes", "*.png", "*.jpg", "*.jpeg"));
        File elegido = selector.showOpenDialog(getScene() == null ? null : getScene().getWindow());
        if (elegido == null) {
            return;
        }
        try {
            byte[] ajustada = ProcesadorImagen.ajustar(Files.readAllBytes(elegido.toPath()), LADO_AVATAR);
            if (ajustada == null) {
                aviso.setText("Ese archivo no es una imagen valida.");
                return;
            }
            foto = ajustada;
            aviso.setText("Foto lista, pulsa Guardar cambios.");
        } catch (Exception error) {
            aviso.setText("No se pudo leer la imagen.");
        }
    }

    private Button botonGuardar() {
        Button boton = EstilosInsta.botonPrincipal("Guardar cambios");
        boton.setOnAction(evento -> guardar());
        return boton;
    }

    private void guardar() {
        try {
            perfil.setNombreCompleto(nombre.getText().trim());
            perfil.setBiografia(biografia.getText().trim());
            perfil.setEdad(edad.getValue());
            if (foto != null) {
                perfil.setFoto(foto);
            }
            ventana.getContexto().getServicio().actualizarPerfil(perfil);
            ventana.mostrarPerfilDe(perfil.getUsername());
        } catch (MiniWindowsException error) {
            aviso.setText(error.getMessage());
        }
    }

    private HBox estadoDeCuenta() {
        Button alternar = perfil.estaActiva()
                ? EstilosInsta.botonSuave("Desactivar cuenta")
                : EstilosInsta.botonPrincipal("Reactivar cuenta");
        alternar.setOnAction(evento -> cambiarEstado());

        Label estado = EstilosInsta.texto(perfil.estaActiva() ? "Activa" : "Desactivada");
        HBox fila = new HBox(10, estado, EstilosInsta.espaciador(), alternar);
        fila.setAlignment(Pos.CENTER_LEFT);
        return fila;
    }

    private void cambiarEstado() {
        boolean activa = perfil.estaActiva();
        if (activa && !ventana.confirmar("Desactivar cuenta",
                "Mientras este desactivada tu cuenta no aparecera en las busquedas "
                        + "ni se veran tus publicaciones. ¿Continuar?")) {
            return;
        }
        try {
            ventana.getContexto().getServicio().cambiarEstadoCuenta(perfil.getUsername(), !activa);
            perfil.setActiva(!activa);
            if (activa) {
                ventana.getContexto().getSesion().cerrar();
                ventana.mostrarLogin();
            } else {
                aviso.setText("Tu cuenta quedo activa otra vez.");
                ventana.mostrarEditarPerfil();
            }
        } catch (MiniWindowsException error) {
            aviso.setText(error.getMessage());
        }
    }
}
