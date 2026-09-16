package MiniWindows.Insta;

import MiniWindows.Insta.Hilos.CargaIMGS;
import MiniWindows.Insta.Servicio.ServicioInsta;
import MiniWindows.Insta.Servicio.ServicioLocal;
import MiniWindows.Insta.Servicio.ServicioRemoto;
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
import MiniWindows.SistemaOp.Escritorio.Iconos;
import MiniWindows.Util.InicializadorInsta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;

public class VentanaInsta extends BorderPane {

    private final ContextoInsta contexto;
    private final CargaIMGS cargador = new CargaIMGS();
    private final StackPane contenedor = new StackPane();
    private final VBox menu = new VBox(4);
    private final Map<String, Button> opciones = new LinkedHashMap<>();

    private Button seleccionada;

    public VentanaInsta(ContextoApp contextoApp) {
        ServicioInsta remoto = ServicioRemoto.siHayServidor();
        ServicioInsta servicio = remoto != null ? remoto : new ServicioLocal();
        InicializadorInsta.sembrarSiHaceFalta(servicio);

        String usuarioWindows = contextoApp.getSesion().getUsuario().getUsername();
        this.contexto = new ContextoInsta(servicio, new SesionInsta(usuarioWindows), contextoApp);

        construirMenu();
        contenedor.setStyle(EstilosInsta.PAGINA);
        setCenter(contenedor);
        setStyle(EstilosInsta.PAGINA);

        if (!reabrirSesionRecordada()) {
            mostrarLogin();
        }

        sceneProperty().addListener((observable, anterior, actual) -> {
            if (actual == null) {
                cargador.detener();
                contexto.getSesion().liberar();
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
        if (recordada == null || SesionInsta.estaAbierta(recordada)) {
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
        menu.setPrefWidth(206);
        menu.setMinWidth(206);
        menu.setPadding(new Insets(18, 10, 14, 10));
        menu.setStyle("-fx-background-color: " + EstilosInsta.SUPERFICIE + "; "
                + "-fx-border-color: " + EstilosInsta.BORDE + "; -fx-border-width: 0 1 0 0;");

        Node logo = EstilosInsta.marca(22);
        VBox.setMargin(logo, new Insets(4, 0, 14, 10));

        menu.getChildren().addAll(logo, EstilosInsta.separador(),
                opcion(Iconos.CASA, "Comentarios", this::mostrarInicio),
                opcion(Iconos.BUSCAR, "Buscar profile", this::mostrarBusqueda),
                opcion(Iconos.TABLA, "Buscar hashtag", () -> buscarHashtag("")),
                opcion(Iconos.CORAZON, "Interacciones", this::mostrarInteracciones),
                opcion(Iconos.COMENTARIO, "Inbox", () -> abrirConversacionCon(null)),
                opcion(Iconos.MAS_CUADRADO, "Cargar imágenes", this::mostrarCargarImagenes),
                opcion(Iconos.USUARIO, "Perfil", () -> mostrarPerfilDe(contexto.getUsuarioActual())),
                opcion(Iconos.RENOMBRAR, "Editar perfil", this::mostrarEditarPerfil),
                EstilosInsta.espaciador(), EstilosInsta.separador(),
                opcion(Iconos.SALIR, "Cerrar sesión", this::cerrarSesion));
    }

    private Button opcion(String icono, String texto, Runnable accion) {
        Button boton = new Button(texto);
        boton.setMnemonicParsing(false);
        boton.setGraphic(Iconos.crear(icono, 19, Color.web(EstilosInsta.TEXTO)));
        boton.setGraphicTextGap(14);
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setAlignment(Pos.CENTER_LEFT);
        boton.setFocusTraversable(false);
        boton.setOnMouseEntered(evento -> pintarOpcion(boton, true));
        boton.setOnMouseExited(evento -> pintarOpcion(boton, false));
        boton.setOnAction(evento -> accion.run());
        pintarOpcion(boton, false);
        opciones.put(texto, boton);
        return boton;
    }

    private void pintarOpcion(Button boton, boolean encima) {
        boolean activa = boton == seleccionada;
        boton.setStyle("-fx-font-family: '" + EstilosInsta.FUENTE + "'; -fx-font-size: 13.5px; "
                + "-fx-font-weight: " + (activa ? "bold" : "normal") + "; "
                + "-fx-text-fill: " + EstilosInsta.TEXTO + "; -fx-background-radius: 8; "
                + "-fx-padding: 9 12 9 10; -fx-cursor: hand; -fx-background-color: "
                + (activa || encima ? EstilosInsta.SUAVE : "transparent") + ";");
    }

    private void marcar(String texto) {
        Button anterior = seleccionada;
        seleccionada = opciones.get(texto);
        if (anterior != null) {
            pintarOpcion(anterior, false);
        }
        if (seleccionada != null) {
            pintarOpcion(seleccionada, false);
        }
    }

    public void mostrarBusqueda() {
        setLeft(menu);
        marcar("Buscar profile");
        cambiarVista(new BuscarInsta(this));
    }

    public void mostrarInteracciones() {
        setLeft(menu);
        marcar("Interacciones");
        cambiarVista(new InteraccionesInsta(this));
    }

    public void mostrarCargarImagenes() {
        setLeft(menu);
        marcar("Cargar imágenes");
        cambiarVista(new HacerPost(this));
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
        marcar("Comentarios");
        cambiarVista(new LineaTiempoInsta(this));
    }

    public void mostrarPerfilDe(String username) {
        setLeft(menu);
        marcar("Perfil");
        cambiarVista(new MiPerfilInsta(this, username));
    }

    public void mostrarEditarPerfil() {
        setLeft(menu);
        marcar("Editar perfil");
        cambiarVista(new EditarPerfilInsta(this));
    }

    public void buscarHashtag(String hashtag) {
        setLeft(menu);
        marcar("Buscar hashtag");
        cambiarVista(new BuscarHashtagInsta(this, hashtag));
    }

    public void abrirConversacionCon(String username) {
        setLeft(menu);
        marcar("Inbox");
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
