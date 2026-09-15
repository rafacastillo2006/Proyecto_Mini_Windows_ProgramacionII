package MiniWindows.SistemaOp.Escritorio;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Modelo.Usuario;
import MiniWindows.SistemaOp.Cuentas.ServicioCuentas;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

public class PantallaLogin extends StackPane {

    private static final double DIAMETRO_AVATAR = 128;
    private static final String VELO = "-fx-background-color: rgba(6,18,38,0.42);";
    private static final String CAMPO = "-fx-background-color: rgba(255,255,255,0.92); "
            + "-fx-background-radius: 6 0 0 6; -fx-border-color: transparent; -fx-padding: 9 12 9 14; "
            + "-fx-font-family: '" + Estilos.FUENTE + "'; -fx-font-size: 13px; -fx-prompt-text-fill: #6b7280;";
    private static final String BOTON_ENTRAR = "-fx-background-color: rgba(255,255,255,0.92); "
            + "-fx-background-radius: 0 6 6 0; -fx-cursor: hand; -fx-padding: 6 14 6 12;";
    private static final String BOTON_ENTRAR_ENCIMA = "-fx-background-color: white; "
            + "-fx-background-radius: 0 6 6 0; -fx-cursor: hand; -fx-padding: 6 14 6 12;";
    private static final String FICHA = "-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand;";
    private static final String FICHA_ENCIMA = "-fx-background-color: rgba(255,255,255,0.16); "
            + "-fx-background-radius: 8; -fx-cursor: hand;";
    private static final String FICHA_ACTIVA = "-fx-background-color: rgba(255,255,255,0.28); "
            + "-fx-background-radius: 8; -fx-cursor: hand;";

    private final ServicioCuentas cuentas;
    private final Consumer<Usuario> alIniciarSesion;

    private final StackPane marcoAvatar = new StackPane();
    private final Label nombreMostrado = texto("", 24, true);
    private final Label detalleMostrado = texto("", 13, false);
    private final Label mensajeError = texto("", 12.5, false);
    private final PasswordField campoContrasena = new PasswordField();
    private final VBox listaUsuarios = new VBox(4);

    private Usuario seleccionado;

    public PantallaLogin(ServicioCuentas cuentas, Consumer<Usuario> alIniciarSesion, Runnable alSalir) {
        this.cuentas = cuentas;
        this.alIniciarSesion = alIniciarSesion;

        Region velo = new Region();
        velo.setStyle(VELO);

        getChildren().addAll(Fondo.escritorio(), velo, tarjetaCentral(), panelUsuarios(), botonApagar(alSalir));
        if (cuentas.esPrimerArranque()) {
            getChildren().add(pista());
        }

        seleccionar(usuarioInicial());
        Platform.runLater(campoContrasena::requestFocus);
    }

    private VBox tarjetaCentral() {
        mensajeError.setStyle(mensajeError.getStyle() + "-fx-text-fill: #ffb4a8;");
        mensajeError.setMinHeight(18);
        mensajeError.setWrapText(true);
        mensajeError.setMaxWidth(320);
        mensajeError.setAlignment(Pos.CENTER);

        campoContrasena.setPromptText("Contraseña");
        campoContrasena.setStyle(CAMPO);
        campoContrasena.setPrefWidth(238);
        campoContrasena.setOnAction(evento -> intentarIngreso());

        Button entrar = new Button();
        entrar.setGraphic(Iconos.crear(Iconos.ADELANTE, 16, Color.web(Estilos.TEXTO)));
        entrar.setFocusTraversable(false);
        entrar.setOnAction(evento -> intentarIngreso());
        Estilos.hover(entrar, BOTON_ENTRAR, BOTON_ENTRAR_ENCIMA);

        HBox entrada = new HBox(campoContrasena, entrar);
        entrada.setAlignment(Pos.CENTER);
        entrada.setMaxWidth(Region.USE_PREF_SIZE);

        VBox tarjeta = new VBox(10, marcoAvatar, nombreMostrado, detalleMostrado, entrada, mensajeError);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        VBox.setMargin(nombreMostrado, new Insets(10, 0, 0, 0));
        VBox.setMargin(entrada, new Insets(12, 0, 0, 0));
        return tarjeta;
    }

    private Region panelUsuarios() {
        ListaEnlazada<Usuario> usuarios = cuentas.listar();
        listaUsuarios.setAlignment(Pos.BOTTOM_LEFT);
        listaUsuarios.setPadding(new Insets(0, 0, 6, 0));
        for (Usuario usuario : usuarios) {
            listaUsuarios.getChildren().add(ficha(usuario));
        }

        Label titulo = texto("Cuentas del equipo", 12, false);
        VBox panel = new VBox(8, titulo, listaUsuarios);
        panel.setPadding(new Insets(0, 0, 28, 28));
        panel.setAlignment(Pos.BOTTOM_LEFT);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        panel.setVisible(usuarios.tamano() > 1);
        panel.setManaged(usuarios.tamano() > 1);

        ScrollPane contenedor = new ScrollPane(panel);
        contenedor.setFitToWidth(true);
        contenedor.setMaxSize(300, 260);
        contenedor.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        contenedor.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        contenedor.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        StackPane.setAlignment(contenedor, Pos.BOTTOM_LEFT);
        return contenedor;
    }

    private HBox ficha(Usuario usuario) {
        Label nombre = texto(usuario.getNombreCompleto(), 13, false);
        Label detalle = texto(usuario.estaActiva() ? usuario.getUsername() : "Cuenta desactivada", 11.5, false);
        VBox datos = new VBox(nombre, detalle);
        datos.setAlignment(Pos.CENTER_LEFT);

        HBox ficha = new HBox(10, new Avatar(usuario, 34), datos);
        ficha.setAlignment(Pos.CENTER_LEFT);
        ficha.setPadding(new Insets(6, 12, 6, 8));
        ficha.setUserData(usuario);
        Estilos.hover(ficha, FICHA, FICHA_ENCIMA);
        ficha.setOnMouseClicked(evento -> seleccionar(usuario));
        return ficha;
    }

    private Button botonApagar(Runnable alSalir) {
        Button apagar = new Button("Apagar");
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

    private Usuario usuarioInicial() {
        ListaEnlazada<Usuario> usuarios = cuentas.listar();
        Usuario administrador = usuarios.buscar(usuario -> usuario.esAdministrador() && usuario.estaActiva());
        if (administrador != null) {
            return administrador;
        }
        Usuario activo = usuarios.buscar(Usuario::estaActiva);
        return activo != null ? activo : (usuarios.estaVacia() ? null : usuarios.primero());
    }

    private void seleccionar(Usuario usuario) {
        seleccionado = usuario;
        marcoAvatar.getChildren().clear();
        if (usuario != null) {
            marcoAvatar.getChildren().add(new Avatar(usuario, DIAMETRO_AVATAR));
        }
        nombreMostrado.setText(usuario == null ? "Sin cuentas" : usuario.getNombreCompleto());
        detalleMostrado.setText(usuario == null ? "" : usuario.getUsername());
        mensajeError.setText(usuario != null && !usuario.estaActiva()
                ? "Esta cuenta está desactivada" : "");
        campoContrasena.clear();
        campoContrasena.setDisable(usuario == null || !usuario.estaActiva());
        campoContrasena.requestFocus();
        for (var nodo : listaUsuarios.getChildren()) {
            boolean activa = nodo.getUserData() == usuario;
            Estilos.hover((Region) nodo, activa ? FICHA_ACTIVA : FICHA, FICHA_ENCIMA);
        }
    }

    private void intentarIngreso() {
        if (seleccionado == null) {
            return;
        }
        Usuario usuario;
        try {
            usuario = cuentas.iniciarSesion(seleccionado.getUsername(), campoContrasena.getText());
        } catch (MiniWindowsException error) {
            mensajeError.setText(error.getMessage());
            campoContrasena.clear();
            campoContrasena.requestFocus();
            return;
        }
        mensajeError.setText("");
        alIniciarSesion.accept(usuario);
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
