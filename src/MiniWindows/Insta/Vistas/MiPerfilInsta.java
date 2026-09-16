package MiniWindows.Insta.Vistas;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Insta.EstilosInsta;
import MiniWindows.Insta.VentanaInsta;
import MiniWindows.Modelo.Publicacion;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Red.EventoInsta;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import MiniWindows.Util.Fechas;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MiPerfilInsta extends ScrollPane {

    private static final double ANCHO = 640;
    private static final double LADO_MINIATURA = 190;
    private static final int COLUMNAS = 3;

    private final VentanaInsta ventana;
    private final UsuarioInsta perfil;
    private final Label aviso = EstilosInsta.error("");

    public MiPerfilInsta(VentanaInsta ventana) {
        this(ventana, ventana.getContexto().getUsuarioActual());
    }

    public MiPerfilInsta(VentanaInsta ventana, String username) {
        this.ventana = ventana;
        UsuarioInsta encontrado = ventana.getContexto().getServicio().perfilDe(username);
        boolean propio = encontrado != null
                && encontrado.getUsername().equalsIgnoreCase(ventana.getContexto().getUsuarioActual());
        this.perfil = encontrado != null && (propio || encontrado.estaActiva()) ? encontrado : null;

        VBox columna = new VBox(0);
        columna.setPadding(new Insets(24, 20, 28, 20));
        columna.setAlignment(Pos.TOP_CENTER);
        columna.setStyle(EstilosInsta.PAGINA);

        if (perfil == null) {
            columna.getChildren().add(EstilosInsta.error("Ese perfil no existe o está desactivado."));
        } else {
            columna.getChildren().addAll(cabecera(), aviso, galeria());
        }

        setContent(columna);
        setFitToWidth(true);
        ventana.alRecibirEvento(this::atender);
        setStyle("-fx-background: " + EstilosInsta.FONDO + "; -fx-background-color: "
                + EstilosInsta.FONDO + "; -fx-border-color: transparent;");
    }

    private void atender(EventoInsta evento) {
        if (perfil == null || esMio(evento) || !evento.tocaA(perfil.getUsername())) {
            return;
        }
        if (evento.es(EventoInsta.PUBLICACION) || evento.es(EventoInsta.SEGUIR)
                || evento.es(EventoInsta.DEJAR_DE_SEGUIR)) {
            ventana.mostrarPerfilDe(perfil.getUsername());
        }
    }

    private boolean esMio(EventoInsta evento) {
        return ventana.getContexto().getUsuarioActual().equalsIgnoreCase(evento.actor());
    }

    private boolean esPropio() {
        return perfil.getUsername().equalsIgnoreCase(ventana.getContexto().getUsuarioActual());
    }

    private VBox cabecera() {
        Label arroba = EstilosInsta.titulo("@" + perfil.getUsername(), 21);
        if (perfil.esVerificada()) {
            arroba.setGraphic(Iconos.crear(Iconos.VERIFICADO, 17, Color.web(EstilosInsta.AZUL)));
            arroba.setContentDisplay(ContentDisplay.RIGHT);
            arroba.setGraphicTextGap(7);
        }

        HBox encabezado = new HBox(14, arroba, acciones());
        encabezado.setAlignment(Pos.CENTER_LEFT);

        HBox numeros = new HBox(26,
                dato(ventana.getContexto().getServicio().publicacionesDe(perfil.getUsername()).tamano(),
                        "publicaciones"),
                dato(ventana.getContexto().getServicio().seguidoresDe(perfil.getUsername()).tamano(),
                        "seguidores"),
                dato(ventana.getContexto().getServicio().seguidosDe(perfil.getUsername()).tamano(),
                        "seguidos"));
        numeros.setAlignment(Pos.CENTER_LEFT);
        numeros.setPadding(new Insets(4, 0, 2, 0));

        Label nombre = EstilosInsta.fuerte(perfil.getNombreCompleto());

        Label biografia = EstilosInsta.texto(perfil.getBiografia());
        biografia.setWrapText(true);
        biografia.setMaxWidth(380);

        Label ficha = EstilosInsta.leyenda(perfil.getEdad() + " años  ·  "
                + (perfil.getGenero() == 'F' ? "Femenino" : "Masculino")
                + "  ·  Registro " + Fechas.FECHA.format(perfil.getFechaRegistro())
                + "  ·  " + (perfil.estaActiva() ? "Cuenta activa" : "Cuenta desactivada"));
        ficha.setPadding(new Insets(6, 0, 0, 0));

        VBox bloque = new VBox(6, encabezado, numeros, nombre, biografia, ficha);
        bloque.setAlignment(Pos.CENTER_LEFT);

        HBox fila = new HBox(36, EstilosInsta.avatar(perfil, 150), bloque);
        fila.setAlignment(Pos.CENTER_LEFT);

        VBox tarjeta = new VBox(fila);
        tarjeta.setPadding(new Insets(26, 26, 26, 26));
        tarjeta.setMaxWidth(ANCHO);
        tarjeta.setMinWidth(ANCHO);
        tarjeta.setStyle(EstilosInsta.TARJETA);
        return tarjeta;
    }

    private HBox acciones() {
        if (esPropio()) {
            Button editar = EstilosInsta.botonSuave("Editar perfil");
            editar.setOnAction(evento -> ventana.mostrarEditarPerfil());
            return new HBox(8, editar);
        }
        String yo = ventana.getContexto().getUsuarioActual();
        boolean siguiendo = ventana.getContexto().getServicio().sigue(yo, perfil.getUsername());

        Button seguir = siguiendo ? EstilosInsta.botonSuave("Siguiendo")
                : EstilosInsta.botonPrincipal("Seguir");
        seguir.setMinWidth(104);
        seguir.setMaxWidth(104);
        seguir.setOnAction(evento -> alternarSeguir(siguiendo));

        Button mensaje = EstilosInsta.botonSuave("Enviar mensaje");
        mensaje.setOnAction(evento -> ventana.abrirConversacionCon(perfil.getUsername()));

        return new HBox(8, seguir, mensaje);
    }

    private void alternarSeguir(boolean siguiendo) {
        String yo = ventana.getContexto().getUsuarioActual();
        try {
            if (siguiendo) {
                if (!ventana.confirmar("Dejar de seguir",
                        "¿Seguro que quieres dejar de seguir a @" + perfil.getUsername() + "?")) {
                    return;
                }
                ventana.getContexto().getServicio().dejarDeSeguir(yo, perfil.getUsername());
            } else {
                ventana.getContexto().getServicio().seguir(yo, perfil.getUsername());
            }
            ventana.mostrarPerfilDe(perfil.getUsername());
        } catch (MiniWindowsException error) {
            aviso.setText(error.getMessage());
        }
    }

    private HBox dato(int cantidad, String etiqueta) {
        HBox caja = new HBox(5, EstilosInsta.fuerte(String.valueOf(cantidad)),
                EstilosInsta.texto(etiqueta));
        caja.setAlignment(Pos.CENTER_LEFT);
        return caja;
    }

    private VBox galeria() {
        ListaEnlazada<Publicacion> propias =
                ventana.getContexto().getServicio().publicacionesDe(perfil.getUsername());

        Label pestana = EstilosInsta.fuerte("PUBLICACIONES");
        pestana.setStyle(pestana.getStyle() + " -fx-font-size: 11.5px;");
        HBox barra = new HBox(pestana);
        barra.setAlignment(Pos.CENTER);
        barra.setPadding(new Insets(14, 0, 14, 0));

        GridPane rejilla = new GridPane();
        rejilla.setHgap(4);
        rejilla.setVgap(4);
        rejilla.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < propias.tamano(); i++) {
            rejilla.add(miniatura(propias.obtener(i)), i % COLUMNAS, i / COLUMNAS);
        }

        VBox contenido = new VBox(0, EstilosInsta.separador(), barra,
                propias.estaVacia() ? vacio() : rejilla);
        contenido.setMaxWidth(ANCHO);
        contenido.setMinWidth(ANCHO);
        contenido.setPadding(new Insets(0, 0, 0, 0));

        VBox tarjeta = new VBox(contenido);
        tarjeta.setPadding(new Insets(18, 0, 0, 0));
        tarjeta.setMaxWidth(ANCHO);
        tarjeta.setMinWidth(ANCHO);
        return tarjeta;
    }

    private HBox vacio() {
        Label texto = EstilosInsta.leyenda("Todavía no hay publicaciones.");
        HBox caja = new HBox(texto);
        caja.setAlignment(Pos.CENTER);
        caja.setPadding(new Insets(10, 0, 30, 0));
        return caja;
    }

    private StackPane miniatura(Publicacion publicacion) {
        return conApertura(marcoMiniatura(publicacion), publicacion);
    }

    private StackPane conApertura(StackPane marco, Publicacion publicacion) {
        marco.setCursor(javafx.scene.Cursor.HAND);
        marco.setOnMouseClicked(evento -> ventana.mostrarPublicacion(publicacion));
        return marco;
    }

    private StackPane marcoMiniatura(Publicacion publicacion) {
        ImageView vista = new ImageView();
        vista.setFitWidth(LADO_MINIATURA);
        vista.setFitHeight(LADO_MINIATURA);
        vista.setPreserveRatio(false);
        vista.setSmooth(true);

        StackPane marco = new StackPane(vista);
        marco.setPrefSize(LADO_MINIATURA, LADO_MINIATURA);
        marco.setMinSize(LADO_MINIATURA, LADO_MINIATURA);
        marco.setStyle("-fx-background-color: " + EstilosInsta.SUAVE + ";");
        if (publicacion.tieneImagen()) {
            ventana.getCargador().cargar(publicacion.getImagen(), LADO_MINIATURA, vista::setImage);
        } else {
            Label texto = EstilosInsta.leyenda(publicacion.getDescripcion());
            texto.setWrapText(true);
            texto.setMaxWidth(LADO_MINIATURA - 24);
            marco.getChildren().add(texto);
        }
        return marco;
    }
}
