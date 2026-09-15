package MiniWindows.SistemaOp.VisorImagenes;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Modelo.TipoArchivo;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import MiniWindows.SistemaOp.Archivos.ObservadorArchivos;
import MiniWindows.SistemaOp.Archivos.RutaVirtual;
import MiniWindows.SistemaOp.Archivos.SistemaArchivos;
import MiniWindows.SistemaOp.Escritorio.Estilos;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import MiniWindows.Util.Imagenes;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class PanelVisor extends BorderPane {

    private static final double LADO_MINIATURA = 74;
    private static final double MARGEN = 28;
    private static final double ZOOM_MINIMO = 0.1;
    private static final double ZOOM_MAXIMO = 8;
    private static final double PASO_ZOOM = 1.25;
    private static final String LIENZO = "-fx-background-color: #1d1f22;";
    private static final String CARRUSEL = "-fx-background: #26282c; -fx-background-color: #26282c;";
    private static final String MINIATURA = "-fx-background-color: #33363b; -fx-background-radius: 6; "
            + "-fx-border-color: transparent; -fx-border-radius: 6; -fx-cursor: hand;";
    private static final String MINIATURA_ACTIVA = "-fx-background-color: #33363b; -fx-background-radius: 6; "
            + "-fx-border-color: " + Estilos.ACENTO + "; -fx-border-width: 2; -fx-border-radius: 6; -fx-cursor: hand;";
    private static final String FLECHA = "-fx-background-color: rgba(0,0,0,0.45); -fx-background-radius: 24; "
            + "-fx-cursor: hand; -fx-padding: 12;";
    private static final String FLECHA_ENCIMA = "-fx-background-color: rgba(0,0,0,0.70); -fx-background-radius: 24; "
            + "-fx-cursor: hand; -fx-padding: 12;";

    private final SistemaArchivos archivos;
    private final CargadorImagenes cargador;
    private final ObservadorArchivos observador = this::alCambiarCarpeta;

    private final ImageView principal = new ImageView();
    private final StackPane lienzo = new StackPane(principal);
    private final ScrollPane area = new ScrollPane(lienzo);
    private final HBox tira = new HBox(8);
    private final ScrollPane carrusel = new ScrollPane(tira);
    private final Label titulo = Estilos.subtitulo("");
    private final Label detalle = Estilos.leyenda("");
    private final Label vacio = Estilos.leyenda("No hay imágenes en esta carpeta");
    private final List<StackPane> casillas = new ArrayList<>();

    private ListaEnlazada<NodoArchivo> imagenes = new ListaEnlazada<>();
    private RutaVirtual carpeta;
    private int indice = -1;
    private double zoom;
    private long generacion;

    public PanelVisor(ContextoApp contexto, NodoArchivo archivo) {
        this.archivos = contexto.getArchivos();
        this.cargador = new CargadorImagenes(archivos);

        setTop(barraHerramientas());
        setCenter(zonaPrincipal());
        setBottom(zonaCarrusel());
        setStyle(LIENZO);

        carpeta = archivo != null ? archivo.getRuta().padre() : contexto.getSesion().getEscritorio();
        cargarCarpeta(archivo);

        addEventFilter(KeyEvent.KEY_PRESSED, this::atajos);
        setFocusTraversable(true);
        sceneProperty().addListener((observable, anterior, actual) -> {
            if (actual == null) {
                archivos.quitarObservador(observador);
                cargador.detener();
            } else {
                archivos.agregarObservador(observador);
                Platform.runLater(this::requestFocus);
            }
        });
    }

    private HBox barraHerramientas() {
        Button anterior = Estilos.botonHerramienta(Iconos.ATRAS, "Anterior", "Imagen anterior");
        anterior.setOnAction(evento -> mover(-1));

        Button siguiente = Estilos.botonHerramienta(Iconos.ADELANTE, "Siguiente", "Imagen siguiente");
        siguiente.setOnAction(evento -> mover(1));

        Button alejar = Estilos.botonIcono(Iconos.ZOOM_MENOS, "Alejar");
        alejar.setOnAction(evento -> cambiarZoom(1 / PASO_ZOOM));

        Button ajustar = Estilos.botonIcono(Iconos.AJUSTAR, "Ajustar a la ventana");
        ajustar.setOnAction(evento -> {
            zoom = 0;
            actualizarEscala();
        });

        Button acercar = Estilos.botonIcono(Iconos.ZOOM_MAS, "Acercar");
        acercar.setOnAction(evento -> cambiarZoom(PASO_ZOOM));

        HBox barra = Estilos.fila(6, anterior, siguiente, Estilos.separadorVertical(),
                new VBox(titulo, detalle), Estilos.espaciador(), alejar, ajustar, acercar);
        barra.setPadding(new Insets(8, 12, 8, 10));
        barra.setStyle(Estilos.BARRA_HERRAMIENTAS);
        return barra;
    }

    private StackPane zonaPrincipal() {
        principal.setPreserveRatio(true);
        principal.setSmooth(true);
        lienzo.setStyle(LIENZO);
        lienzo.setMinSize(0, 0);

        area.setPannable(true);
        area.setFitToWidth(true);
        area.setFitToHeight(true);
        area.setStyle("-fx-background: #1d1f22; -fx-background-color: #1d1f22;");
        area.viewportBoundsProperty().addListener((observable, anterior, actual) -> actualizarEscala());

        Button anterior = flecha(Iconos.ATRAS, -1);
        Button siguiente = flecha(Iconos.ADELANTE, 1);
        StackPane.setAlignment(anterior, Pos.CENTER_LEFT);
        StackPane.setAlignment(siguiente, Pos.CENTER_RIGHT);
        StackPane.setMargin(anterior, new Insets(0, 0, 0, 16));
        StackPane.setMargin(siguiente, new Insets(0, 16, 0, 0));

        vacio.setStyle(vacio.getStyle() + "-fx-text-fill: #c8ccd2;");
        StackPane zona = new StackPane(area, vacio, anterior, siguiente);
        zona.setStyle(LIENZO);
        return zona;
    }

    private Button flecha(String icono, int desplazamiento) {
        Button boton = new Button();
        boton.setGraphic(Iconos.crear(icono, 20, Color.WHITE));
        boton.setFocusTraversable(false);
        boton.setOnAction(evento -> mover(desplazamiento));
        Estilos.hover(boton, FLECHA, FLECHA_ENCIMA);
        return boton;
    }

    private ScrollPane zonaCarrusel() {
        tira.setAlignment(Pos.CENTER_LEFT);
        tira.setPadding(new Insets(10, 12, 10, 12));
        carrusel.setStyle(CARRUSEL);
        carrusel.setFitToHeight(true);
        carrusel.setMinHeight(LADO_MINIATURA + 34);
        carrusel.setPrefHeight(LADO_MINIATURA + 34);
        carrusel.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        carrusel.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return carrusel;
    }

    private void cargarCarpeta(NodoArchivo destacada) {
        imagenes = imagenesDeLaCarpeta();
        construirCarrusel();
        int inicial = destacada == null ? 0 : Math.max(0, imagenes.indiceDe(destacada));
        mostrar(inicial);
    }

    private ListaEnlazada<NodoArchivo> imagenesDeLaCarpeta() {
        try {
            return archivos.listar(carpeta).filtrar(nodo -> nodo.getTipo() == TipoArchivo.IMAGEN);
        } catch (MiniWindowsException error) {
            return new ListaEnlazada<>();
        }
    }

    private void construirCarrusel() {
        tira.getChildren().clear();
        casillas.clear();
        int posicion = 0;
        for (NodoArchivo nodo : imagenes) {
            StackPane casilla = casilla(nodo, posicion);
            casillas.add(casilla);
            tira.getChildren().add(casilla);
            posicion++;
        }
        boolean hay = !imagenes.estaVacia();
        vacio.setVisible(!hay);
        carrusel.setVisible(hay);
        carrusel.setManaged(hay);
    }

    private StackPane casilla(NodoArchivo nodo, int posicion) {
        ImageView vista = new ImageView();
        vista.setFitWidth(LADO_MINIATURA - 10);
        vista.setFitHeight(LADO_MINIATURA - 10);
        vista.setPreserveRatio(true);
        vista.setSmooth(true);

        StackPane casilla = new StackPane(vista);
        casilla.setPrefSize(LADO_MINIATURA, LADO_MINIATURA);
        casilla.setMinSize(LADO_MINIATURA, LADO_MINIATURA);
        casilla.setStyle(MINIATURA);
        casilla.setOnMouseClicked(evento -> {
            if (evento.getButton() == MouseButton.PRIMARY) {
                mostrar(posicion);
            }
        });
        cargador.cargar(nodo, LADO_MINIATURA, imagen -> {
            if (imagen != null) {
                vista.setImage(imagen);
            }
        });
        return casilla;
    }

    private void mostrar(int nuevoIndice) {
        generacion++;
        if (imagenes.estaVacia()) {
            indice = -1;
            principal.setImage(null);
            titulo.setText("Sin imágenes");
            detalle.setText(carpeta.texto());
            return;
        }
        indice = Math.clamp(nuevoIndice, 0, imagenes.tamano() - 1);
        NodoArchivo nodo = imagenes.obtener(indice);
        titulo.setText(nodo.getNombre());
        detalle.setText(posicionYRuta(nodo.tamanoLegible()));
        resaltar();
        desplazarCarrusel();

        long token = generacion;
        principal.setImage(null);
        cargador.cargar(nodo, 0, imagen -> {
            if (token != generacion) {
                return;
            }
            principal.setImage(imagen);
            detalle.setText(posicionYRuta(Imagenes.descripcion(imagen) + "  ·  " + nodo.tamanoLegible()));
            zoom = 0;
            actualizarEscala();
        });
    }

    private String posicionYRuta(String extra) {
        return (indice + 1) + " de " + imagenes.tamano() + "  ·  " + extra;
    }

    private void resaltar() {
        for (int posicion = 0; posicion < casillas.size(); posicion++) {
            casillas.get(posicion).setStyle(posicion == indice ? MINIATURA_ACTIVA : MINIATURA);
        }
    }

    private void desplazarCarrusel() {
        if (imagenes.tamano() > 1) {
            carrusel.setHvalue((double) indice / (imagenes.tamano() - 1));
        }
    }

    private void mover(int desplazamiento) {
        if (imagenes.estaVacia()) {
            return;
        }
        int total = imagenes.tamano();
        mostrar(((indice + desplazamiento) % total + total) % total);
    }

    private void cambiarZoom(double factor) {
        Image imagen = principal.getImage();
        if (imagen == null) {
            return;
        }
        zoom = Math.clamp(escalaActual() * factor, ZOOM_MINIMO, ZOOM_MAXIMO);
        actualizarEscala();
    }

    private double escalaActual() {
        Image imagen = principal.getImage();
        if (zoom > 0 || imagen == null) {
            return zoom > 0 ? zoom : 1;
        }
        return escalaDeAjuste(imagen);
    }

    private double escalaDeAjuste(Image imagen) {
        double anchoDisponible = area.getViewportBounds().getWidth() - MARGEN;
        double altoDisponible = area.getViewportBounds().getHeight() - MARGEN;
        if (anchoDisponible <= 0 || altoDisponible <= 0) {
            return 1;
        }
        return Math.min(1, Math.min(anchoDisponible / imagen.getWidth(), altoDisponible / imagen.getHeight()));
    }

    private void actualizarEscala() {
        Image imagen = principal.getImage();
        if (imagen == null) {
            return;
        }
        double escala = zoom > 0 ? zoom : escalaDeAjuste(imagen);
        double ancho = imagen.getWidth() * escala;
        double alto = imagen.getHeight() * escala;
        principal.setFitWidth(ancho);
        principal.setFitHeight(alto);
        lienzo.setMinSize(ancho + MARGEN, alto + MARGEN);
    }

    private void atajos(KeyEvent evento) {
        if (evento.getCode() == KeyCode.LEFT) {
            mover(-1);
            evento.consume();
        } else if (evento.getCode() == KeyCode.RIGHT) {
            mover(1);
            evento.consume();
        }
    }

    private void alCambiarCarpeta(RutaVirtual cambiada) {
        if (!cambiada.equals(carpeta)) {
            return;
        }
        Runnable recarga = () -> {
            ListaEnlazada<NodoArchivo> actuales = imagenesDeLaCarpeta();
            if (mismasImagenes(actuales)) {
                return;
            }
            NodoArchivo visible = indice >= 0 && indice < imagenes.tamano() ? imagenes.obtener(indice) : null;
            imagenes = actuales;
            construirCarrusel();
            mostrar(visible == null ? 0 : Math.max(0, imagenes.indiceDe(visible)));
        };
        if (Platform.isFxApplicationThread()) {
            recarga.run();
        } else {
            Platform.runLater(recarga);
        }
    }

    private boolean mismasImagenes(ListaEnlazada<NodoArchivo> otras) {
        if (otras.tamano() != imagenes.tamano()) {
            return false;
        }
        for (int posicion = 0; posicion < otras.tamano(); posicion++) {
            if (!otras.obtener(posicion).equals(imagenes.obtener(posicion))) {
                return false;
            }
        }
        return true;
    }

    public int getIndice() {
        return indice;
    }

    public int getTotal() {
        return imagenes.tamano();
    }

    public Region getCarrusel() {
        return carrusel;
    }
}
