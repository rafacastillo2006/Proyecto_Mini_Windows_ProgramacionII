package MiniWindows.Insta.Vistas;

import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Insta.EstilosInsta;
import MiniWindows.Insta.Imagen.ArteGenerado;
import MiniWindows.Insta.Imagen.ProcesadorImagen;
import MiniWindows.Insta.VentanaInsta;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.SistemaOp.Escritorio.CampoContrasena;
import MiniWindows.Util.Validador;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;

public class RegistrarseInsta extends StackPane {

    private static final int LADO_AVATAR = 320;

    private final VentanaInsta ventana;
    private final TextField nombre = EstilosInsta.campo("Nombre completo");
    private final TextField usuario = EstilosInsta.campo("Nombre de usuario");
    private final CampoContrasena clave = new CampoContrasena("Contraseña", EstilosInsta.CAMPO);
    private final TextField edad = EstilosInsta.campo("Edad");
    private final ComboBox<String> genero = new ComboBox<>();
    private final Label aviso = EstilosInsta.error("");
    private final Label archivoElegido = EstilosInsta.leyenda("Se usará un avatar generado");

    private byte[] foto;

    public RegistrarseInsta(VentanaInsta ventana) {
        this.ventana = ventana;
        setStyle(EstilosInsta.PAGINA);

        genero.getItems().addAll("Femenino", "Masculino");
        genero.setPromptText("Género");
        genero.setMaxWidth(Double.MAX_VALUE);
        genero.setStyle("-fx-font-family: '" + EstilosInsta.FUENTE + "'; -fx-font-size: 13px;");

        aviso.setMinHeight(30);
        aviso.setAlignment(Pos.CENTER);
        aviso.setMaxWidth(Double.MAX_VALUE);
        archivoElegido.setAlignment(Pos.CENTER);
        archivoElegido.setMaxWidth(Double.MAX_VALUE);

        Label marca = (Label) EstilosInsta.marca(26);
        Label lema = EstilosInsta.leyenda("Regístrate para ver fotos y videos de tus amigos");
        lema.setWrapText(true);
        lema.setAlignment(Pos.CENTER);
        lema.setMaxWidth(Double.MAX_VALUE);
        lema.setPadding(new Insets(0, 0, 8, 0));

        Button registrar = EstilosInsta.botonPrincipal("Registrarme");
        registrar.setOnAction(evento -> registrar());

        getChildren().add(Portada.marco(
                Portada.tarjeta(9, marca, lema, nombre, usuario, clave, edad, genero,
                        botonFoto(), archivoElegido, registrar, aviso),
                Portada.banda(pieDeLogin())));
    }

    private Button botonFoto() {
        Button boton = EstilosInsta.botonSuave("Elegir foto de perfil");
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setOnAction(evento -> elegirFoto());
        return boton;
    }

    private void elegirFoto() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Foto de perfil");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif"));
        File elegido = selector.showOpenDialog(getScene() == null ? null : getScene().getWindow());
        if (elegido == null) {
            return;
        }
        try {
            byte[] ajustada = ProcesadorImagen.ajustar(Files.readAllBytes(elegido.toPath()), LADO_AVATAR);
            if (ajustada == null) {
                archivoElegido.setText("Ese archivo no es una imagen válida");
                return;
            }
            foto = ajustada;
            archivoElegido.setText(elegido.getName());
        } catch (Exception error) {
            archivoElegido.setText("No se pudo leer la imagen");
        }
    }

    private HBox pieDeLogin() {
        Button entrar = EstilosInsta.enlace("Entrar");
        entrar.setOnAction(evento -> ventana.mostrarLogin());
        HBox pie = new HBox(4, EstilosInsta.leyenda("¿Ya tienes una cuenta?"), entrar);
        pie.setAlignment(Pos.CENTER);
        return pie;
    }

    private void registrar() {
        String nombreCompleto = nombre.getText().trim();
        String username = usuario.getText().trim();
        String contrasena = clave.getTexto();
        String edadTexto = edad.getText().trim();

        if (nombreCompleto.isEmpty() || username.isEmpty() || contrasena.isEmpty()
                || edadTexto.isEmpty() || genero.getValue() == null) {
            aviso.setText("Llena todos los campos.");
            return;
        }
        if (!username.matches("[a-zA-Z0-9._]{3,20}")) {
            aviso.setText("El usuario solo admite letras, números, punto y guion bajo (3 a 20).");
            return;
        }
        if (!Validador.contrasenaValida(contrasena)) {
            aviso.setText(Validador.REGLA_CONTRASENA);
            return;
        }
        int anios;
        try {
            anios = Integer.parseInt(edadTexto);
        } catch (NumberFormatException invalida) {
            aviso.setText("La edad debe ser un número.");
            return;
        }
        if (anios < 13 || anios > 120) {
            aviso.setText("La edad debe estar entre 13 y 120.");
            return;
        }

        byte[] avatar = foto != null ? foto : ArteGenerado.avatar(nombreCompleto, username.length() + 4);
        UsuarioInsta nuevo = new UsuarioInsta(nombreCompleto, genero.getValue().charAt(0),
                username, contrasena, anios, avatar);
        try {
            ventana.getContexto().getServicio().registrar(nuevo);
            ventana.getContexto().getSesion().abrir(nuevo);
            ventana.mostrarSugerenciasIniciales();
        } catch (MiniWindowsException error) {
            aviso.setText(error.getMessage());
        }
    }
}
