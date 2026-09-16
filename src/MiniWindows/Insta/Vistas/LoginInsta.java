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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class LoginInsta extends StackPane {

    private final VentanaInsta ventana;
    private final TextField usuario = EstilosInsta.campo("Nombre de usuario");
    private final CampoContrasena clave = new CampoContrasena("Contraseña", EstilosInsta.CAMPO);
    private final Label aviso = EstilosInsta.error("");
    private final HBox reintento = new HBox(8);

    public LoginInsta(VentanaInsta ventana) {
        this.ventana = ventana;
        setStyle(EstilosInsta.PAGINA);

        usuario.setOnAction(evento -> clave.pedirFoco());
        clave.alConfirmar(evento -> entrar());
        aviso.setMinHeight(16);
        aviso.setAlignment(Pos.CENTER);
        aviso.setMaxWidth(Double.MAX_VALUE);

        reintento.setAlignment(Pos.CENTER);
        mostrarOpciones(false);
        Button reintentar = EstilosInsta.botonSuave("Reintentar");
        reintentar.setOnAction(evento -> prepararReintento());
        Button crear = EstilosInsta.botonSuave("Crear una cuenta");
        crear.setOnAction(evento -> ventana.mostrarRegistro());
        reintento.getChildren().addAll(reintentar, crear);

        Button entrar = EstilosInsta.botonPrincipal("Iniciar sesión");
        entrar.setOnAction(evento -> entrar());

        Label marca = (Label) EstilosInsta.marca(30);
        marca.setPadding(new Insets(4, 0, 12, 0));

        ScrollPane marco = Portada.marco(
                Portada.tarjeta(10, marca, usuario, clave, entrar, aviso, reintento),
                Portada.banda(pieDeRegistro()));
        getChildren().add(marco);
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
