package MiniWindows.Insta;

import MiniWindows.Insta.Hilos.CargaIMGS;
import MiniWindows.Insta.Servicio.ServicioInsta;
import MiniWindows.Insta.Servicio.ServicioLocal;
import MiniWindows.Insta.Vistas.BandejaEntrada;
import MiniWindows.Insta.Vistas.BuscarHashtagInsta;
import MiniWindows.Insta.Vistas.BuscarInsta;
import MiniWindows.Insta.Vistas.EditarPerfilInsta;
import MiniWindows.Insta.Vistas.HacerPost;
import MiniWindows.Insta.Vistas.InteraccionesInsta;
import MiniWindows.Insta.Vistas.LineaTiempoInsta;
import MiniWindows.Insta.Vistas.LoginInsta;
import MiniWindows.Insta.Vistas.MiPerfilInsta;
import MiniWindows.Insta.Vistas.RegistrarseInsta;
import MiniWindows.Insta.Vistas.SugerenciasInsta;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Escritorio.Dialogos;
import MiniWindows.Util.InicializadorInsta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class VentanaInsta extends BorderPane {

    private final ContextoInsta contexto;
    private final CargaIMGS cargador = new CargaIMGS();
    private final StackPane contenedor = new StackPane();
    private final VBox menu = new VBox(4);

    public VentanaInsta(ContextoApp contextoApp) {
        ServicioInsta servicio = new ServicioLocal();
        InicializadorInsta.sembrarSiHaceFalta(servicio);

        String usuarioWindows = contextoApp.getSesion().getUsuario().getUsername();
        this.contexto = new ContextoInsta(servicio, new SesionInsta(usuarioWindows), contextoApp);

        construirMenu();
        contenedor.setStyle("-fx-background-color: " + EstilosInsta.FONDO + ";");
        setCenter(contenedor);
        setStyle("-fx-background-color: " + EstilosInsta.FONDO + ";");

        if (!reabrirSesionRecordada()) {
            mostrarLogin();
        }

        sceneProperty().addListener((observable, anterior, actual) -> {
            if (actual == null) {
                cargador.detener();
            }
        });
    }

    public ContextoInsta getContexto() {
        return contexto;
    }

    public CargaIMGS getCargador() {
        return cargador;
    }

    private boolean reabrirSesionRecordada() {
        String recordada = contexto.getSesion().getCuentaRecordada();
        if (recordada == null) {
            return false;
        }
        UsuarioInsta usuario = contexto.getServicio().perfilDe(recordada);
        if (usuario == null || !usuario.estaActiva()) {
            return false;
        }
        contexto.getSesion().abrir(usuario);
        mostrarInicio();
        return true;
    }

    private void construirMenu() {
        menu.setPrefWidth(196);
        menu.setMinWidth(196);
        menu.setPadding(new Insets(20, 12, 16, 16));
        menu.setStyle("-fx-background-color: white; -fx-border-color: " + EstilosInsta.BORDE + "; "
                + "-fx-border-width: 0 1 0 0;");

        Node logo = EstilosInsta.titulo("INSTA+", 22);
        VBox.setMargin(logo, new Insets(0, 0, 12, 4));

        menu.getChildren().addAll(logo,
                opcion("Perfil", () -> mostrarPerfilDe(contexto.getUsuarioActual())),
                opcion("Cargar imágenes", () -> cambiarVista(new HacerPost(this))),
                opcion("Comentarios", this::mostrarInicio),
                opcion("Interacciones", () -> cambiarVista(new InteraccionesInsta(this))),
                opcion("Buscar profile", () -> cambiarVista(new BuscarInsta(this))),
                opcion("Buscar hashtag", () -> buscarHashtag("")),
                opcion("Inbox", () -> abrirConversacionCon(null)),
                opcion("Editar perfil", this::mostrarEditarPerfil),
                EstilosInsta.espaciador(),
                opcion("Cerrar sesión", this::cerrarSesion));
    }

    private Button opcion(String texto, Runnable accion) {
        Button boton = new Button(texto);
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setAlignment(Pos.CENTER_LEFT);
        boton.setFocusTraversable(false);
        String base = "-fx-font-family: '" + EstilosInsta.FUENTE + "'; -fx-font-size: 13.5px; "
                + "-fx-text-fill: " + EstilosInsta.TEXTO + "; -fx-background-radius: 8; -fx-padding: 8 12; "
                + "-fx-cursor: hand; -fx-background-color: ";
        boton.setStyle(base + "transparent;");
        boton.setOnMouseEntered(evento -> boton.setStyle(base + "#f2f2f2;"));
        boton.setOnMouseExited(evento -> boton.setStyle(base + "transparent;"));
        boton.setOnAction(evento -> accion.run());
        return boton;
    }

    public void mostrarLogin() {
        setLeft(null);
        cambiarVista(new LoginInsta(this));
    }

    public void mostrarRegistro() {
        setLeft(null);
        cambiarVista(new RegistrarseInsta(this));
    }

    public void mostrarSugerenciasIniciales() {
        setLeft(null);
        cambiarVista(new SugerenciasInsta(this));
    }

    public void mostrarInicio() {
        setLeft(menu);
        cambiarVista(new LineaTiempoInsta(this));
    }

    public void mostrarPerfilDe(String username) {
        setLeft(menu);
        cambiarVista(new MiPerfilInsta(this, username));
    }

    public void mostrarEditarPerfil() {
        setLeft(menu);
        cambiarVista(new EditarPerfilInsta(this));
    }

    public void buscarHashtag(String hashtag) {
        setLeft(menu);
        cambiarVista(new BuscarHashtagInsta(this, hashtag));
    }

    public void abrirConversacionCon(String username) {
        setLeft(menu);
        cambiarVista(new BandejaEntrada(this, username));
    }

    public boolean confirmar(String titulo, String pregunta) {
        return Dialogos.confirmar(this, titulo, pregunta);
    }

    private void cerrarSesion() {
        if (!confirmar("Cerrar sesión", "¿Seguro que quieres cerrar la sesión de INSTA+?")) {
            return;
        }
        contexto.getSesion().cerrar();
        mostrarLogin();
    }

    public void cambiarVista(Node vista) {
        contenedor.getChildren().setAll(vista);
    }
}
