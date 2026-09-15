package MiniWindows.SistemaOp.ReproductorMusica;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Modelo.TipoArchivo;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Apps.RecibeArchivo;
import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import MiniWindows.SistemaOp.Archivos.ObservadorArchivos;
import MiniWindows.SistemaOp.Archivos.RutaVirtual;
import MiniWindows.SistemaOp.Archivos.SistemaArchivos;
import MiniWindows.SistemaOp.Escritorio.AccionesArchivos;
import MiniWindows.SistemaOp.Escritorio.Estilos;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class PanelReproductor extends BorderPane implements RecibeArchivo {

    public static final String NOMBRE_HILO = "MiniWindows-Audio";

    private static final double LADO_CARATULA = 220;
    private static final String SIN_PISTA = "Sin pista";
    private static final String FONDO = "-fx-background-color: #1d1f22;";
    private static final String LISTA = "-fx-background: #26282c; -fx-background-color: #26282c;";
    private static final String CLARO = "-fx-text-fill: #e8e8e8;";

    private final SistemaArchivos archivos;
    private final AccionesArchivos acciones;
    private final ExecutorService cargador;
    private final ObservadorArchivos observador = this::alCambiarCarpeta;

    private final ImageView caratula = new ImageView();
    private final StackPane marcoCaratula = new StackPane();
    private final Label titulo = new Label(SIN_PISTA);
    private final Label descripcion = new Label("");
    private final Label tiempo = new Label("0:00 / 0:00");
    private final Slider progreso = new Slider(0, 1, 0);
    private final Slider volumen = new Slider(0, 100, 70);
    private final ListView<NodoArchivo> lista = new ListView<>();
    private final Button botonPlay;

    private ListaEnlazada<NodoArchivo> pistas = new ListaEnlazada<>();
    private RutaVirtual carpeta;
    private MediaPlayer reproductor;
    private Path archivoTemporal;
    private int indice = -1;
    private long generacion;
    private boolean moviendoProgreso;

    public PanelReproductor(ContextoApp contexto, NodoArchivo pista) {
        this.archivos = contexto.getArchivos();
        this.acciones = contexto.getAcciones();
        this.cargador = Executors.newSingleThreadExecutor(tarea -> {
            Thread hilo = new Thread(tarea, NOMBRE_HILO);
            hilo.setDaemon(true);
            return hilo;
        });
        this.botonPlay = boton(Iconos.PLAY, "Reproducir o pausar");

        setCenter(zonaPrincipal());
        setBottom(zonaLista());
        setStyle(FONDO);

        carpeta = pista != null ? pista.getRuta().padre() : contexto.getSesion().getEscritorio();
        cargarCarpeta(pista);

        sceneProperty().addListener((observable, anterior, actual) -> {
            if (actual == null) {
                archivos.quitarObservador(observador);
                generacion++;
                cargador.shutdownNow();
                soltarReproductor();
            } else {
                archivos.agregarObservador(observador);
            }
        });
    }

    @Override
    public void mostrarArchivo(NodoArchivo pista) {
        if (pista == null) {
            return;
        }
        carpeta = pista.getRuta().padre();
        cargarCarpeta(pista);
    }

    private VBox zonaPrincipal() {
        caratula.setFitWidth(LADO_CARATULA);
        caratula.setFitHeight(LADO_CARATULA);
        caratula.setPreserveRatio(true);
        marcoCaratula.setPrefSize(LADO_CARATULA, LADO_CARATULA);
        marcoCaratula.setMaxSize(LADO_CARATULA, LADO_CARATULA);
        marcoCaratula.setStyle("-fx-background-color: #2c2f34; -fx-background-radius: 12;");
        mostrarCaratula(null);

        titulo.setStyle("-fx-font-family: '" + Estilos.FUENTE + "'; -fx-font-size: 18px; "
                + "-fx-font-weight: bold;" + CLARO);
        descripcion.setStyle("-fx-font-family: '" + Estilos.FUENTE + "'; -fx-font-size: 13px; "
                + "-fx-text-fill: #b6bcc4;");
        tiempo.setStyle("-fx-font-family: '" + Estilos.FUENTE + "'; -fx-font-size: 12px; "
                + "-fx-text-fill: #b6bcc4;");

        progreso.setMaxWidth(420);
        progreso.setOnMousePressed(evento -> moviendoProgreso = true);
        progreso.setOnMouseReleased(evento -> {
            if (reproductor != null) {
                reproductor.seek(Duration.seconds(progreso.getValue()));
            }
            moviendoProgreso = false;
        });

        volumen.setPrefWidth(120);

        VBox contenido = new VBox(14, marcoCaratula, titulo, descripcion, progreso, tiempo, controles());
        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(24, 20, 18, 20));
        contenido.setStyle(FONDO);
        return contenido;
    }

    private HBox controles() {
        Button anterior = boton(Iconos.ATRAS, "Anterior");
        anterior.setOnAction(evento -> mover(-1));

        botonPlay.setOnAction(evento -> alternarReproduccion());

        Button detener = boton(Iconos.DETENER, "Detener");
        detener.setOnAction(evento -> detener());

        Button siguiente = boton(Iconos.ADELANTE, "Siguiente");
        siguiente.setOnAction(evento -> mover(1));

        Button importar = boton(Iconos.IMPORTAR, "Importar música a esta carpeta");
        importar.setOnAction(evento -> acciones.importar(this, carpeta));

        HBox fila = new HBox(10, anterior, botonPlay, detener, siguiente,
                Iconos.crear(Iconos.VOLUMEN, 16, Color.web("#b6bcc4")), volumen, importar);
        fila.setAlignment(Pos.CENTER);
        return fila;
    }

    private VBox zonaLista() {
        lista.setStyle(LISTA);
        lista.setPrefHeight(170);
        lista.setPlaceholder(etiquetaClara("No hay música en esta carpeta"));
        lista.setCellFactory(vista -> new ListCell<>() {
            @Override
            protected void updateItem(NodoArchivo nodo, boolean vacio) {
                super.updateItem(nodo, vacio);
                if (vacio || nodo == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                    return;
                }
                setText(nodo.getNombre());
                setGraphic(Iconos.crear(Iconos.MUSICA, 16, Color.web("#b6bcc4")));
                boolean sonando = getIndex() == indice;
                setStyle("-fx-background-color: " + (sonando ? "#33363b" : "transparent") + "; "
                        + "-fx-text-fill: " + (sonando ? "#4cc2ff" : "#e8e8e8") + "; "
                        + "-fx-font-family: '" + Estilos.FUENTE + "'; -fx-font-size: 13px;");
            }
        });
        lista.setOnMouseClicked(evento -> {
            int seleccion = lista.getSelectionModel().getSelectedIndex();
            if (evento.getButton() == MouseButton.PRIMARY && seleccion >= 0 && seleccion != indice) {
                reproducir(seleccion);
            }
        });

        Label encabezado = etiquetaClara("Lista de canciones");
        encabezado.setPadding(new Insets(8, 12, 4, 12));

        VBox zona = new VBox(encabezado, lista);
        zona.setStyle(LISTA);
        return zona;
    }

    private void cargarCarpeta(NodoArchivo destacada) {
        pistas = pistasDeLaCarpeta();
        lista.getItems().setAll(pistas.aLista());
        if (pistas.estaVacia()) {
            return;
        }
        int inicial = destacada == null ? 0 : Math.max(0, pistas.indiceDe(destacada));
        reproducir(inicial);
    }

    private ListaEnlazada<NodoArchivo> pistasDeLaCarpeta() {
        try {
            return archivos.listar(carpeta).filtrar(nodo -> nodo.getTipo() == TipoArchivo.MUSICA);
        } catch (MiniWindowsException error) {
            return new ListaEnlazada<>();
        }
    }

    private void reproducir(int nuevoIndice) {
        if (pistas.estaVacia()) {
            return;
        }
        indice = Math.clamp(nuevoIndice, 0, pistas.tamano() - 1);
        NodoArchivo pista = pistas.obtener(indice);
        titulo.setText(pista.getNombre());
        descripcion.setText("Preparando la pista...");
        lista.getSelectionModel().select(indice);
        lista.refresh();

        soltarReproductor();
        long token = ++generacion;
        try {
            cargador.execute(() -> {
                Path temporal = extraer(pista);
                Platform.runLater(() -> {
                    if (token != generacion) {
                        borrar(temporal);
                        return;
                    }
                    if (temporal == null) {
                        descripcion.setText("No se pudo leer la pista");
                        return;
                    }
                    archivoTemporal = temporal;
                    abrirMedia(pista, temporal);
                });
            });
        } catch (RejectedExecutionException cerrado) {
            descripcion.setText("El reproductor se está cerrando");
        }
    }

    private Path extraer(NodoArchivo pista) {
        try {
            byte[] datos = archivos.abrir(pista).datos();
            Path temporal = Files.createTempFile("miniwindows-", extension(pista.getNombre()));
            Files.write(temporal, datos);
            temporal.toFile().deleteOnExit();
            return temporal;
        } catch (MiniWindowsException | IOException error) {
            return null;
        }
    }

    private String extension(String nombre) {
        int punto = nombre.lastIndexOf('.');
        return punto < 0 ? ".mp3" : nombre.substring(punto).toLowerCase();
    }

    private void abrirMedia(NodoArchivo pista, Path temporal) {
        try {
            Media media = new Media(temporal.toUri().toString());
            media.getMetadata().addListener((MapChangeListener<String, Object>) cambio -> describir(pista, media));
            reproductor = new MediaPlayer(media);
            reproductor.volumeProperty().bind(volumen.valueProperty().divide(100));
            reproductor.statusProperty().addListener((observable, anterior, estado) ->
                    mostrarBoton(estado == MediaPlayer.Status.PLAYING));
            reproductor.setOnReady(() -> {
                progreso.setMax(Math.max(1, media.getDuration().toSeconds()));
                describir(pista, media);
                actualizarTiempo();
            });
            reproductor.currentTimeProperty().addListener((observable, anterior, actual) -> {
                if (!moviendoProgreso) {
                    progreso.setValue(actual.toSeconds());
                }
                actualizarTiempo();
            });
            reproductor.setOnEndOfMedia(() -> mover(1));
            reproductor.play();
        } catch (RuntimeException error) {
            descripcion.setText("No se pudo reproducir esta pista");
        }
    }

    private void describir(NodoArchivo pista, Media media) {
        Object nombre = media.getMetadata().get("title");
        Object artista = media.getMetadata().get("artist");
        Object album = media.getMetadata().get("album");
        Object imagen = media.getMetadata().get("image");

        titulo.setText(nombre == null ? pista.getNombre() : nombre.toString());
        StringBuilder detalle = new StringBuilder();
        detalle.append(artista == null ? "Artista desconocido" : artista.toString());
        if (album != null) {
            detalle.append("  ·  ").append(album);
        }
        detalle.append("  ·  ").append(pista.tamanoLegible());
        descripcion.setText(detalle.toString());
        mostrarCaratula(imagen instanceof Image portada ? portada : null);
    }

    private void mostrarCaratula(Image imagen) {
        caratula.setImage(imagen);
        marcoCaratula.getChildren().setAll(imagen == null
                ? Iconos.crear(Iconos.MUSICA, 72, Color.web("#6f7680"))
                : caratula);
    }

    private void alternarReproduccion() {
        if (reproductor == null) {
            reproducir(indice < 0 ? 0 : indice);
            return;
        }
        if (reproductor.getStatus() == MediaPlayer.Status.PLAYING) {
            reproductor.pause();
        } else {
            reproductor.play();
        }
    }

    private void mostrarBoton(boolean sonando) {
        botonPlay.setGraphic(Iconos.crear(sonando ? Iconos.PAUSA : Iconos.PLAY, 16, Color.WHITE));
    }

    private void detener() {
        if (reproductor != null) {
            reproductor.stop();
            progreso.setValue(0);
            actualizarTiempo();
        }
        mostrarBoton(false);
    }

    private void olvidarPista() {
        generacion++;
        soltarReproductor();
        titulo.setText(SIN_PISTA);
        descripcion.setText("");
        mostrarCaratula(null);
        actualizarTiempo();
    }

    private void mover(int desplazamiento) {
        if (pistas.estaVacia()) {
            return;
        }
        int total = pistas.tamano();
        reproducir(((indice + desplazamiento) % total + total) % total);
    }

    private void actualizarTiempo() {
        if (reproductor == null) {
            tiempo.setText("0:00 / 0:00");
            return;
        }
        tiempo.setText(formato(reproductor.getCurrentTime()) + " / " + formato(reproductor.getTotalDuration()));
    }

    private String formato(Duration duracion) {
        if (duracion == null || duracion.isUnknown() || duracion.isIndefinite()) {
            return "0:00";
        }
        int segundos = (int) duracion.toSeconds();
        return segundos / 60 + ":" + String.format("%02d", segundos % 60);
    }

    private void soltarReproductor() {
        if (reproductor != null) {
            reproductor.volumeProperty().unbind();
            reproductor.stop();
            reproductor.dispose();
            reproductor = null;
        }
        mostrarBoton(false);
        borrar(archivoTemporal);
        archivoTemporal = null;
        progreso.setValue(0);
    }

    private void borrar(Path temporal) {
        if (temporal == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporal);
        } catch (IOException error) {
            temporal.toFile().deleteOnExit();
        }
    }

    private void alCambiarCarpeta(RutaVirtual cambiada) {
        if (!cambiada.equals(carpeta)) {
            return;
        }
        Runnable recarga = () -> {
            NodoArchivo actual = indice >= 0 && indice < pistas.tamano() ? pistas.obtener(indice) : null;
            pistas = pistasDeLaCarpeta();
            lista.getItems().setAll(pistas.aLista());
            indice = actual == null ? -1 : pistas.indiceDe(actual);
            if (indice < 0) {
                olvidarPista();
            }
            lista.getSelectionModel().select(indice);
            lista.refresh();
        };
        if (Platform.isFxApplicationThread()) {
            recarga.run();
        } else {
            Platform.runLater(recarga);
        }
    }

    private Button boton(String icono, String ayuda) {
        Button boton = new Button();
        boton.setGraphic(Iconos.crear(icono, 16, Color.WHITE));
        boton.setTooltip(new Tooltip(ayuda));
        boton.setMinSize(42, 36);
        boton.setFocusTraversable(false);
        String base = "-fx-background-color: #33363b; -fx-background-radius: 8; -fx-cursor: hand;";
        Estilos.hover(boton, base, "-fx-background-color: #40444a; -fx-background-radius: 8; -fx-cursor: hand;");
        return boton;
    }

    private Label etiquetaClara(String texto) {
        Label etiqueta = new Label(texto);
        etiqueta.setStyle("-fx-font-family: '" + Estilos.FUENTE + "'; -fx-font-size: 12px; "
                + "-fx-text-fill: #b6bcc4;");
        return etiqueta;
    }

    public int getIndice() {
        return indice;
    }

    public int getTotal() {
        return pistas.tamano();
    }
}
