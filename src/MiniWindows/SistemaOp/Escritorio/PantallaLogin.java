package MiniWindows.SistemaOp.Escritorio;

import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Modelo.Usuario;
import MiniWindows.SistemaOp.Cuentas.ServicioCuentas;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.function.Consumer;

public class PantallaLogin extends StackPane {

    private static final double DIAMETRO_AVATAR = 116;
    private static final double ANCHO_CAMPO = 268;
    private static final String VELO = "-fx-background-color: rgba(6,18,38,0.46);";
    private static final String CAMPO = "-fx-background-color: rgba(255,255,255,0.92); "
            + "-fx-background-radius: 6; -fx-border-color: transparent; -fx-padding: 9 12 9 14; "
            + "-fx-font-family: '" + Estilos.FUENTE + "'; -fx-font-size: 13px; -fx-prompt-text-fill: #6b7280;";

    private final ServicioCuentas cuentas;
    private final Consumer<Usuario> alIniciarSesion;

    private final TextField campoUsuario = new TextField();
    private final CampoContrasena campoContrasena = new CampoContrasena("Contraseña", CAMPO);
    private final Label mensajeError = texto("", 12.5, false);

    public PantallaLogin(ServicioCuentas cuentas, Consumer<Usuario> alIniciarSesion, Runnable alSalir) {
        this.cuentas = cuentas;
        this.alIniciarSesion = alIniciarSesion;

        Region velo = new Region();
        velo.setStyle(VELO);

        getChildren().addAll(Fondo.escritorio(), velo, tarjetaCentral(), botonApagar(alSalir));
        if (cuentas.esPrimerArranque()) {
            getChildren().add(pista());
        }
        Platform.runLater(campoUsuario::requestFocus);
    }

    private VBox tarjetaCentral() {
        mensajeError.setStyle(mensajeError.getStyle() + "-fx-text-fill: #ffb4a8;");
        mensajeError.setMinHeight(34);
        mensajeError.setWrapText(true);
        mensajeError.setMaxWidth(ANCHO_CAMPO);
        mensajeError.setAlignment(Pos.CENTER);

        campoUsuario.setPromptText("Nombre de usuario");
        campoUsuario.setStyle(CAMPO);
        campoUsuario.setPrefWidth(ANCHO_CAMPO);
        campoUsuario.setMaxWidth(ANCHO_CAMPO);
        campoUsuario.setOnAction(evento -> campoContrasena.pedirFoco());

        campoContrasena.setPrefWidth(ANCHO_CAMPO);
        campoContrasena.setMaxWidth(ANCHO_CAMPO);
        campoContrasena.alConfirmar(evento -> intentarIngreso());

        Button entrar = new Button("Entrar");
        entrar.setMnemonicParsing(false);
        entrar.setPrefWidth(ANCHO_CAMPO);
        entrar.setOnAction(evento -> intentarIngreso());
        String base = "-fx-background-color: rgba(255,255,255,0.92); -fx-background-radius: 6; "
                + "-fx-cursor: hand; -fx-padding: 9 14 9 14; -fx-font-family: '" + Estilos.FUENTE + "'; "
                + "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + Estilos.TEXTO + ";";
        Estilos.hover(entrar, base, base.replace("rgba(255,255,255,0.92)", "white"));

        VBox tarjeta = new VBox(10, avatarGenerico(), texto("Iniciar sesión", 22, true),
                texto("Escribe tu usuario y tu contraseña", 12.5, false),
                campoUsuario, campoContrasena, entrar, mensajeError);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        VBox.setMargin(campoUsuario, new Insets(14, 0, 0, 0));
        return tarjeta;
    }

    private StackPane avatarGenerico() {
        Circle disco = new Circle(DIAMETRO_AVATAR / 2);
        disco.setFill(Color.web("#ffffff", 0.22));
        disco.setStroke(Color.web("#ffffff", 0.45));
        disco.setStrokeWidth(2);

        StackPane marco = new StackPane(disco, Iconos.crear(Iconos.USUARIO, 62, Color.web("#ffffff", 0.92)));
        marco.setMaxSize(DIAMETRO_AVATAR, DIAMETRO_AVATAR);
        return marco;
    }

    private Button botonApagar(Runnable alSalir) {
        Button apagar = new Button("Apagar");
        apagar.setMnemonicParsing(false);
        apagar.setGraphic(Iconos.crear(Iconos.APAGAR, 16, Color.WHITE));
        apagar.setGraphicTextGap(8);
        apagar.setOnAction(evento -> alSalir.run());
        String base = "-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand; "
                + "-fx-font-family: '" + Estilos.FUENTE + "'; -fx-font-size: 13px; "
                + "-fx-background-radius: 6; -fx-padding: 8 14 8 12;";
        Estilos.hover(apagar, base, base.replace("transparent", "rgba(255,255,255,0.18)"));
        StackPane.setAlignment(apagar, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(apagar, new Insets(0, 24, 22, 0));
        return apagar;
    }

    private Label pista() {
        Label pista = texto("Cuenta inicial: " + ServicioCuentas.ADMINISTRADOR_POR_DEFECTO
                + " / " + ServicioCuentas.CONTRASENA_POR_DEFECTO, 12, false);
        pista.setStyle(pista.getStyle() + "-fx-background-color: rgba(0,0,0,0.32); -fx-background-radius: 6; "
                + "-fx-padding: 6 12 6 12;");
        StackPane.setAlignment(pista, Pos.BOTTOM_CENTER);
        StackPane.setMargin(pista, new Insets(0, 0, 24, 0));
        return pista;
    }

    private void intentarIngreso() {
        String usuario = campoUsuario.getText().trim();
        String contrasena = campoContrasena.getTexto();
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mensajeError.setText("Escribe tu usuario y tu contraseña.");
            return;
        }
        try {
            Usuario entrando = cuentas.iniciarSesion(usuario, contrasena);
            mensajeError.setText("");
            alIniciarSesion.accept(entrando);
        } catch (MiniWindowsException error) {
            mensajeError.setText(error.getMessage());
            campoContrasena.limpiar();
            campoContrasena.pedirFoco();
        }
    }

    private Label texto(String contenido, double tamano, boolean fuerte) {
        Label etiqueta = new Label(contenido);
        etiqueta.setStyle("-fx-font-family: '" + Estilos.FUENTE + "'; -fx-text-fill: white; "
                + "-fx-font-size: " + tamano + "px;"
                + (fuerte ? " -fx-font-weight: bold;" : "")
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 6, 0.3, 0, 1);");
        return etiqueta;
    }
}
