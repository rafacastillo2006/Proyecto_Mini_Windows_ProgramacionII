package MiniWindows.Insta;

import MiniWindows.Insta.Hilos.CanalAvisos;
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
import MiniWindows.Insta.Vistas.TarjetaPublicacion;
import MiniWindows.Modelo.Publicacion;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Red.EventoInsta;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Escritorio.Dialogos;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import MiniWindows.SistemaOp.Escritorio.VentanaInterna;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class VentanaInsta extends BorderPane {

    private static final double ANCHO_CELULAR = 640;

    private final ContextoInsta contexto;
    private final CargaIMGS cargador = new CargaIMGS();
    private final StackPane contenedor = new StackPane();
    private final VBox menu = new VBox(4);
    private final HBox barraInferior = new HBox();
    private final HBox barraSuperior = new HBox(10);
    private final Map<String, Button> opciones = new LinkedHashMap<>();
    private final Map<String, Button> atajos = new LinkedHashMap<>();
    private final List<Consumer<EventoInsta>> oyentes = new ArrayList<>();

    private String activa = "";
    private CanalAvisos canal;
    private boolean conSesion;
    private boolean modoCelular;

    public VentanaInsta(ContextoApp contextoApp) {
        ServicioInsta remoto = ServicioRemoto.siHayServidor();
        ServicioInsta servicio = remoto != null ? remoto : new ServicioLocal();

        String usuarioWindows = contextoApp.getSesion().getUsuario().getUsername();
        this.contexto = new ContextoInsta(servicio, new SesionInsta(usuarioWindows), contextoApp);

        construirMenu();
        construirBarras();
        contenedor.setStyle(EstilosInsta.PAGINA);
        setCenter(contenedor);
        setStyle(EstilosInsta.PAGINA);

        widthProperty().addListener((observable, anterior, actual) -> revisarAncho());

        if (!reabrirSesionRecordada()) {
            mostrarLogin();
        }

        sceneProperty().addListener((observable, anterior, actual) -> {
            if (actual == null) {
                cargador.detener();
                cerrarCanal();
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

    public void alRecibirEvento(Consumer<EventoInsta> oyente) {
        oyentes.add(oyente);
    }

    public void publicarEvento(EventoInsta evento) {
        for (Consumer<EventoInsta> oyente : new ArrayList<>(oyentes)) {
            oyente.accept(evento);
        }
    }

    public boolean esModoCelular() {
        return modoCelular;
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

    private void construirBarras() {
        barraInferior.setAlignment(Pos.CENTER);
        barraInferior.setSpacing(6);
        barraInferior.setPadding(new Insets(6, 10, 6, 10));
        barraInferior.setStyle("-fx-background-color: " + EstilosInsta.SUPERFICIE + "; "
                + "-fx-border-color: " + EstilosInsta.BORDE + "; -fx-border-width: 1 0 0 0;");
        barraInferior.getChildren().addAll(
                atajo(Iconos.CASA, "Comentarios", this::mostrarInicio),
                atajo(Iconos.BUSCAR, "Buscar profile", this::mostrarBusqueda),
                atajo(Iconos.MAS_CUADRADO, "Cargar imágenes", this::mostrarCargarImagenes),
                atajo(Iconos.COMENTARIO, "Inbox", () -> abrirConversacionCon(null)),
                atajo(Iconos.USUARIO, "Perfil", () -> mostrarPerfilDe(contexto.getUsuarioActual())));

        barraSuperior.setAlignment(Pos.CENTER_LEFT);
        barraSuperior.setPadding(new Insets(8, 12, 8, 14));
        barraSuperior.setStyle("-fx-background-color: " + EstilosInsta.SUPERFICIE + "; "
                + "-fx-border-color: " + EstilosInsta.BORDE + "; -fx-border-width: 0 0 1 0;");
        barraSuperior.getChildren().addAll(EstilosInsta.marca(18), EstilosInsta.espaciador(),
                atajo(Iconos.TABLA, "Buscar hashtag", () -> buscarHashtag("")),
                atajo(Iconos.CORAZON, "Interacciones", this::mostrarInteracciones),
                atajo(Iconos.SALIR, "Cerrar sesión", this::cerrarSesion));
    }

    private Button opcion(String icono, String texto, Runnable accion) {
        Button boton = new Button(texto);
        boton.setMnemonicParsing(false);
        boton.setGraphic(Iconos.crear(icono, 19, Color.web(EstilosInsta.TEXTO)));
        boton.setGraphicTextGap(14);
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setAlignment(Pos.CENTER_LEFT);
        boton.setFocusTraversable(false);
        boton.setOnMouseEntered(evento -> pintarOpcion(texto, boton, true));
        boton.setOnMouseExited(evento -> pintarOpcion(texto, boton, false));
        boton.setOnAction(evento -> accion.run());
        pintarOpcion(texto, boton, false);
        opciones.put(texto, boton);
        return boton;
    }

    private Button atajo(String icono, String texto, Runnable accion) {
        Button boton = new Button();
        boton.setMnemonicParsing(false);
        boton.setGraphic(Iconos.crear(icono, 21, Color.web(EstilosInsta.TEXTO)));
        boton.setTooltip(new Tooltip(texto));
        boton.setFocusTraversable(false);
        boton.setOnMouseEntered(evento -> pintarAtajo(texto, boton, true));
        boton.setOnMouseExited(evento -> pintarAtajo(texto, boton, false));
        boton.setOnAction(evento -> accion.run());
        pintarAtajo(texto, boton, false);
        atajos.put(texto, boton);
        HBox.setHgrow(boton, javafx.scene.layout.Priority.ALWAYS);
        boton.setMaxWidth(Double.MAX_VALUE);
        return boton;
    }

    private void pintarOpcion(String nombre, Button boton, boolean encima) {
        boolean esActiva = nombre.equals(activa);
        boton.setStyle("-fx-font-family: '" + EstilosInsta.FUENTE + "'; -fx-font-size: 13.5px; "
                + "-fx-font-weight: " + (esActiva ? "bold" : "normal") + "; "
                + "-fx-text-fill: " + EstilosInsta.TEXTO + "; -fx-background-radius: 8; "
                + "-fx-padding: 9 12 9 10; -fx-cursor: hand; -fx-background-color: "
                + (esActiva || encima ? EstilosInsta.SUAVE : "transparent") + ";");
    }

    private void pintarAtajo(String nombre, Button boton, boolean encima) {
        boolean esActiva = nombre.equals(activa);
        boton.setStyle("-fx-background-radius: 8; -fx-padding: 8 10 8 10; -fx-cursor: hand; "
                + "-fx-background-color: " + (esActiva || encima ? EstilosInsta.SUAVE : "transparent") + ";");
    }

    private void marcar(String texto) {
        activa = texto == null ? "" : texto;
        for (Map.Entry<String, Button> par : opciones.entrySet()) {
            pintarOpcion(par.getKey(), par.getValue(), false);
        }
        for (Map.Entry<String, Button> par : atajos.entrySet()) {
            pintarAtajo(par.getKey(), par.getValue(), false);
        }
    }

    private void revisarAncho() {
        boolean celular = getWidth() > 0 && getWidth() < ANCHO_CELULAR;
        if (celular != modoCelular) {
            modoCelular = celular;
            aplicarDiseno();
        }
    }

    private void aplicarDiseno() {
        setLeft(conSesion && !modoCelular ? menu : null);
        setBottom(conSesion && modoCelular ? barraInferior : null);
        setTop(conSesion && modoCelular ? barraSuperior : null);
    }

    private void conNavegacion(boolean visible) {
        conSesion = visible;
        aplicarDiseno();
    }

    public void mostrarLogin() {
        conNavegacion(false);
        cambiarVista(new LoginInsta(this));
    }

    public void mostrarRegistro() {
        conNavegacion(false);
        cambiarVista(new RegistrarseInsta(this));
    }

    public void mostrarSugerenciasIniciales() {
        conNavegacion(false);
        abrirCanal();
        cambiarVista(new SugerenciasInsta(this));
    }

    public void mostrarInicio() {
        conNavegacion(true);
        abrirCanal();
        marcar("Comentarios");
        cambiarVista(new LineaTiempoInsta(this));
    }

    public void mostrarBusqueda() {
        conNavegacion(true);
        marcar("Buscar profile");
        cambiarVista(new BuscarInsta(this));
    }

    public void mostrarInteracciones() {
        conNavegacion(true);
        marcar("Interacciones");
        cambiarVista(new InteraccionesInsta(this));
    }

    public void mostrarCargarImagenes() {
        conNavegacion(true);
        marcar("Cargar imágenes");
        cambiarVista(new HacerPost(this));
    }

    public void mostrarPerfilDe(String username) {
        conNavegacion(true);
        marcar("Perfil");
        cambiarVista(new MiPerfilInsta(this, username));
    }

    public void mostrarEditarPerfil() {
        conNavegacion(true);
        marcar("Editar perfil");
        cambiarVista(new EditarPerfilInsta(this));
    }

    public void buscarHashtag(String hashtag) {
        conNavegacion(true);
        marcar("Buscar hashtag");
        cambiarVista(new BuscarHashtagInsta(this, hashtag));
    }

    public void abrirConversacionCon(String username) {
        conNavegacion(true);
        marcar("Inbox");
        cambiarVista(new BandejaEntrada(this, username));
    }

    public void mostrarPublicacion(Publicacion publicacion) {
        if (publicacion == null) {
            return;
        }
        TarjetaPublicacion tarjeta = new TarjetaPublicacion(this, publicacion);
        ScrollPane marco = new ScrollPane(tarjeta);
        marco.setFitToWidth(true);
        marco.setPrefViewportWidth(TarjetaPublicacion.ANCHO_MAXIMO);
        marco.setMaxWidth(TarjetaPublicacion.ANCHO_MAXIMO + 18);
        marco.setMaxHeight(560);
        marco.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        marco.setStyle("-fx-background: transparent; -fx-background-color: transparent; "
                + "-fx-border-color: transparent;");

        Button cerrar = EstilosInsta.botonIcono(Iconos.CERRAR, "Cerrar", 18);
        StackPane.setAlignment(cerrar, Pos.TOP_RIGHT);

        StackPane capa = new StackPane(marco, cerrar);
        capa.setPadding(new Insets(20));
        capa.setStyle("-fx-background-color: rgba(0, 0, 0, 0.55);");
        capa.setOnMouseClicked(evento -> {
            if (evento.getTarget() == capa) {
                contenedor.getChildren().remove(capa);
            }
        });
        cerrar.setOnAction(evento -> contenedor.getChildren().remove(capa));
        contenedor.getChildren().add(capa);
    }

    public boolean confirmar(String titulo, String pregunta) {
        return Dialogos.confirmar(this, titulo, pregunta);
    }

    private void cerrarSesion() {
        if (!confirmar("Cerrar sesión", "¿Seguro que quieres cerrar la sesión de INSTA+?")) {
            return;
        }
        cerrarCanal();
        contexto.getSesion().cerrar();
        mostrarLogin();
    }

    public void cambiarVista(Node vista) {
        oyentes.clear();
        contenedor.getChildren().setAll(vista);
    }

    private void abrirCanal() {
        if (canal != null || contexto.getUsuarioActual().isEmpty()) {
            return;
        }
        canal = contexto.crearAvisos(this::recibir);
        canal.iniciar();
    }

    private void cerrarCanal() {
        if (canal != null) {
            canal.detener();
            canal = null;
        }
    }

    private void recibir(EventoInsta evento) {
        publicarEvento(evento);
        anunciarEnEscritorio(evento);
    }

    private void anunciarEnEscritorio(EventoInsta evento) {
        String yo = contexto.getUsuarioActual();
        if (yo.isEmpty() || yo.equalsIgnoreCase(evento.actor())) {
            return;
        }
        if (evento.es(EventoInsta.MENSAJE) && yo.equalsIgnoreCase(evento.objetivo())) {
            String detalle = evento.mensaje() == null ? "" : evento.mensaje().esSticker()
                    ? "Te envió un sticker" : evento.mensaje().getContenido();
            contexto.getContextoApp().avisar(Iconos.COMENTARIO,
                    "@" + evento.actor() + " te escribió por INSTA+", detalle,
                    () -> {
                        traerAlFrente();
                        abrirConversacionCon(evento.actor());
                    });
        } else if (evento.es(EventoInsta.ME_GUSTA) && yo.equalsIgnoreCase(evento.objetivo())) {
            contexto.getContextoApp().avisar(Iconos.CORAZON_LLENO, "INSTA+",
                    "A @" + evento.actor() + " le gustó tu publicación",
                    () -> {
                        traerAlFrente();
                        mostrarPerfilDe(yo);
                    });
        } else if (evento.es(EventoInsta.SEGUIR) && yo.equalsIgnoreCase(evento.objetivo())) {
            contexto.getContextoApp().avisar(Iconos.USUARIO, "INSTA+",
                    "@" + evento.actor() + " empezó a seguirte",
                    () -> {
                        traerAlFrente();
                        mostrarPerfilDe(evento.actor());
                    });
        }
    }

    private void traerAlFrente() {
        Node actual = this;
        while (actual != null && !(actual instanceof VentanaInterna)) {
            actual = actual.getParent();
        }
        if (actual instanceof VentanaInterna ventana) {
            contexto.getContextoApp().getVentanas().activar(ventana);
        }
    }
}
