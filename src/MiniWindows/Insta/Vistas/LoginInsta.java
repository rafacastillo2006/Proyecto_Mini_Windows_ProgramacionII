package MiniWindows.Insta.Vistas;

import MiniWindows.Excepciones.CuentaDesactivadaException;
import MiniWindows.Insta.EstilosInsta;
import MiniWindows.Insta.VentanaInsta;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.SistemaOp.Escritorio.CampoContrasena;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LoginInsta extends VBox {

    private final VentanaInsta ventana;
    private final TextField usuario = new TextField();
    private final CampoContrasena clave = new CampoContrasena("Contraseña", EstilosInsta.CAMPO);
    private final Label aviso = EstilosInsta.error("");
    private final HBox reintento = new HBox(8);

    public LoginInsta(VentanaInsta ventana) {
        this.ventana = ventana;

        setAlignment(Pos.CENTER);
        setPadding(new Insets(24));
        setStyle(EstilosInsta.DEGRADADO);

        usuario.setPromptText("Nombre de usuario");
        usuario.setStyle(EstilosInsta.CAMPO);
        usuario.setOnAction(evento -> clave.pedirFoco());
        clave.alConfirmar(evento -> entrar());
        aviso.setMinHeight(30);

        reintento.setAlignment(Pos.CENTER);
        reintento.setVisible(false);
        reintento.setManaged(false);
        Button reintentar = EstilosInsta.botonSuave("Reintentar");
        reintentar.setOnAction(evento -> prepararReintento());
        Button crear = EstilosInsta.botonPrincipal("Crear una cuenta");
        crear.setMaxWidth(160);
        crear.setOnAction(evento -> ventana.mostrarRegistro());
        reintento.getChildren().addAll(reintentar, crear);

        VBox tarjeta = new VBox(12, EstilosInsta.titulo("INSTA+", 30),
                EstilosInsta.leyenda("Entra con tu cuenta de INSTA+"),
                usuario, clave, botonEntrar(), aviso, reintento, pieDeRegistro());
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(28, 26, 22, 26));
        tarjeta.setMaxWidth(340);
        tarjeta.setStyle(EstilosInsta.TARJETA);

        getChildren().add(tarjeta);
    }

    private Button botonEntrar() {
        Button boton = EstilosInsta.botonPrincipal("Iniciar sesión");
        boton.setOnAction(evento -> entrar());
        return boton;
    }

    private HBox pieDeRegistro() {
        Button registrarse = EstilosInsta.enlace("Regístrate");
        registrarse.setOnAction(evento -> ventana.mostrarRegistro());
        HBox pie = new HBox(4, EstilosInsta.leyenda("¿No tienes una cuenta?"), registrarse);
        pie.setAlignment(Pos.CENTER);
        return pie;
    }

    private void prepararReintento() {
        clave.limpiar();
        aviso.setText("");
        mostrarOpciones(false);
        usuario.requestFocus();
    }

    private void mostrarOpciones(boolean visible) {
        reintento.setVisible(visible);
        reintento.setManaged(visible);
    }

    private void entrar() {
        String nombre = usuario.getText().trim();
        String contrasena = clave.getTexto();
        if (nombre.isEmpty() || contrasena.isEmpty()) {
            aviso.setText("Escribe tu usuario y tu contraseña.");
            return;
        }
        try {
            UsuarioInsta encontrado = ventana.getContexto().getServicio().autenticar(nombre, contrasena);
            if (encontrado == null) {
                aviso.setText("Usuario o contraseña incorrectos. ¿Quieres reintentar o crear una cuenta?");
                mostrarOpciones(true);
                return;
            }
            ventana.getContexto().getSesion().abrir(encontrado);
            ventana.mostrarInicio();
        } catch (CuentaDesactivadaException desactivada) {
            aviso.setText(desactivada.getMessage());
            mostrarOpciones(true);
        }
    }
}
