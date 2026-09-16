package MiniWindows.Insta.Vistas;

import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Insta.EstilosInsta;
import MiniWindows.Insta.Imagen.ArteGenerado;
import MiniWindows.Insta.Imagen.ProcesadorImagen;
import MiniWindows.Insta.VentanaInsta;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Util.Validador;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import MiniWindows.SistemaOp.Escritorio.CampoContrasena;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;

public class RegistrarseInsta extends VBox {

    private static final int LADO_AVATAR = 320;

    private final VentanaInsta ventana;
    private final TextField nombre = new TextField();
    private final TextField usuario = new TextField();
    private final CampoContrasena clave = new CampoContrasena("Letras y números, mínimo 6", EstilosInsta.CAMPO);
    private final TextField edad = new TextField();
    private final ComboBox<String> genero = new ComboBox<>();
    private final Label aviso = EstilosInsta.error("");
    private final Label archivoElegido = EstilosInsta.leyenda("Se usara un avatar generado");

    private byte[] foto;

    public RegistrarseInsta(VentanaInsta ventana) {
        this.ventana = ventana;

        setAlignment(Pos.CENTER);
        setPadding(new Insets(20));
        setStyle(EstilosInsta.DEGRADADO);

        nombre.setPromptText("Nombre completo");
        usuario.setPromptText("Nombre de usuario");
        edad.setPromptText("Edad");
        genero.getItems().addAll("Femenino", "Masculino");
        genero.setPromptText("Genero");
        genero.setMaxWidth(Double.MAX_VALUE);
        for (javafx.scene.control.Control campo : new javafx.scene.control.Control[]{nombre, usuario, edad}) {
            campo.setStyle(EstilosInsta.CAMPO);
        }
        aviso.setMinHeight(46);

        VBox tarjeta = new VBox(10, EstilosInsta.titulo("Crear cuenta", 24),
                EstilosInsta.leyenda("Registrate para ver fotos y videos de tus amigos"),
                nombre, usuario, clave, edad, genero, botonFoto(), archivoElegido,
                botonRegistrar(), aviso, pieDeLogin());
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(24, 26, 20, 26));
        tarjeta.setMaxWidth(340);
        tarjeta.setStyle(EstilosInsta.TARJETA);

        ScrollPane marco = new ScrollPane(tarjeta);
        marco.setFitToWidth(true);
        marco.setMaxWidth(380);
        marco.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        getChildren().add(marco);
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
                new FileChooser.ExtensionFilter("Imagenes", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif"));
        File elegido = selector.showOpenDialog(getScene() == null ? null : getScene().getWindow());
        if (elegido == null) {
            return;
        }
        try {
            byte[] ajustada = ProcesadorImagen.ajustar(Files.readAllBytes(elegido.toPath()), LADO_AVATAR);
            if (ajustada == null) {
                archivoElegido.setText("Ese archivo no es una imagen valida");
                return;
            }
            foto = ajustada;
            archivoElegido.setText(elegido.getName());
        } catch (Exception error) {
            archivoElegido.setText("No se pudo leer la imagen");
        }
    }

    private Button botonRegistrar() {
        Button boton = EstilosInsta.botonPrincipal("Registrarme");
        boton.setOnAction(evento -> registrar());
        return boton;
    }

    private HBox pieDeLogin() {
        Button entrar = EstilosInsta.enlace("Entrar");
        entrar.setOnAction(evento -> ventana.mostrarLogin());
        HBox pie = new HBox(4, EstilosInsta.leyenda("Ya tienes una cuenta?"), entrar);
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
            aviso.setText("El usuario solo admite letras, numeros, punto y guion bajo (3 a 20).");
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
            aviso.setText("La edad debe ser un numero.");
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
