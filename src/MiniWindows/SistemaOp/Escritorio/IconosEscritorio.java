package MiniWindows.SistemaOp.Escritorio;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.SistemaOp.Apps.Aplicacion;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import MiniWindows.SistemaOp.Archivos.ObservadorArchivos;
import MiniWindows.SistemaOp.Archivos.RutaVirtual;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class IconosEscritorio extends FlowPane {

    private static final double ANCHO_CASILLA = 96;
    private static final double TAMANO_ICONO = 46;
    private static final String CASILLA = "-fx-background-color: transparent; -fx-background-radius: 6;";
    private static final String CASILLA_ENCIMA = "-fx-background-color: rgba(255,255,255,0.20); -fx-background-radius: 6;";
    private static final String CASILLA_ACTIVA = "-fx-background-color: rgba(255,255,255,0.34); -fx-background-radius: 6;";

    private final ContextoApp contexto;
    private final ListaEnlazada<Aplicacion> aplicaciones;
    private final RutaVirtual carpeta;
    private final ObservadorArchivos observador = this::alCambiarCarpeta;

    private VBox seleccionada;

    public IconosEscritorio(ContextoApp contexto, ListaEnlazada<Aplicacion> aplicaciones) {
        this.contexto = contexto;
        this.aplicaciones = aplicaciones;
        this.carpeta = contexto.getSesion().getEscritorio();

        setOrientation(Orientation.VERTICAL);
        setHgap(6);
        setVgap(4);
        setPadding(new Insets(14, 14, 14, 14));
        setAlignment(Pos.TOP_LEFT);
        setColumnHalignment(javafx.geometry.HPos.LEFT);
        setPickOnBounds(false);
        prefWrapLengthProperty().bind(heightProperty().subtract(28));

        reconstruir();
        sceneProperty().addListener((observable, anterior, actual) -> {
            if (actual == null) {
                contexto.getArchivos().quitarObservador(observador);
            } else {
                contexto.getArchivos().agregarObservador(observador);
                reconstruir();
            }
        });
    }

    public ContextMenu menuDelEscritorio() {
        MenuItem nuevaCarpeta = new MenuItem("Nueva carpeta");
        nuevaCarpeta.setOnAction(evento -> contexto.getAcciones().crearCarpeta(this, carpeta));

        MenuItem nuevoDocumento = new MenuItem("Nuevo documento de texto");
        nuevoDocumento.setOnAction(evento -> contexto.getAcciones().crearDocumento(this, carpeta));

        MenuItem importar = new MenuItem("Importar archivo aquí");
        importar.setOnAction(evento -> contexto.getAcciones().importar(this, carpeta));

        MenuItem pegar = new MenuItem("Pegar");
        pegar.setOnAction(evento -> contexto.getAcciones().pegar(this, carpeta));
        pegar.disableProperty().bind(contexto.getAcciones().getPortapapeles().contenidoProperty().isNull());

        MenuItem actualizar = new MenuItem("Actualizar");
        actualizar.setOnAction(evento -> reconstruir());

        return new ContextMenu(nuevaCarpeta, nuevoDocumento, importar, new SeparatorMenuItem(), pegar,
                new SeparatorMenuItem(), actualizar);
    }

    public void reconstruir() {
        getChildren().clear();
        seleccionada = null;
        for (Aplicacion aplicacion : aplicaciones) {
            if (aplicacion.enElEscritorio()) {
                getChildren().add(casillaDeAplicacion(aplicacion));
            }
        }
        try {
            contexto.getArchivos().asegurarCarpeta(carpeta);
            for (NodoArchivo nodo : contexto.getArchivos().listar(carpeta)) {
                getChildren().add(casillaDeArchivo(nodo));
            }
        } catch (MiniWindowsException error) {
            getChildren().add(etiqueta("No se pudo leer el escritorio"));
        }
    }

    private VBox casillaDeAplicacion(Aplicacion aplicacion) {
        VBox casilla = casilla(Iconos.imagen(aplicacion.icono(), TAMANO_ICONO), aplicacion.titulo());
        casilla.setOnMouseClicked(evento -> {
            seleccionar(casilla);
            if (evento.getButton() == MouseButton.PRIMARY && evento.getClickCount() == 2) {
                contexto.abrir(aplicacion);
            }
        });
        MenuItem abrir = new MenuItem("Abrir");
        abrir.setOnAction(evento -> contexto.abrir(aplicacion));
        casilla.setOnContextMenuRequested(evento -> {
            seleccionar(casilla);
            new ContextMenu(abrir).show(casilla, evento.getScreenX(), evento.getScreenY());
        });
        return casilla;
    }

    private VBox casillaDeArchivo(NodoArchivo nodo) {
        VBox casilla = casilla(Iconos.deTipo(nodo.getTipo(), TAMANO_ICONO), nodo.getNombre());
        casilla.setOnMouseClicked(evento -> {
            seleccionar(casilla);
            if (evento.getButton() == MouseButton.PRIMARY && evento.getClickCount() == 2) {
                contexto.abrirArchivo(this, nodo);
            }
        });
        casilla.setOnContextMenuRequested(evento -> {
            seleccionar(casilla);
            menuDeArchivo(nodo).show(casilla, evento.getScreenX(), evento.getScreenY());
        });
        return casilla;
    }

    private ContextMenu menuDeArchivo(NodoArchivo nodo) {
        MenuItem abrir = new MenuItem("Abrir");
        abrir.setOnAction(evento -> contexto.abrirArchivo(this, nodo));

        MenuItem renombrar = new MenuItem("Renombrar");
        renombrar.setOnAction(evento -> contexto.getAcciones().renombrar(this, nodo));

        MenuItem copiar = new MenuItem("Copiar");
        copiar.setOnAction(evento -> contexto.getAcciones().copiar(nodo));

        MenuItem cortar = new MenuItem("Cortar");
        cortar.setOnAction(evento -> contexto.getAcciones().cortar(nodo));

        MenuItem pegar = new MenuItem("Pegar");
        pegar.setOnAction(evento -> contexto.getAcciones().pegar(this, carpeta));
        pegar.disableProperty().bind(contexto.getAcciones().getPortapapeles().contenidoProperty().isNull());

        MenuItem eliminar = new MenuItem("Eliminar");
        eliminar.setOnAction(evento -> contexto.getAcciones().eliminar(this, nodo));

        return new ContextMenu(abrir, new SeparatorMenuItem(), copiar, cortar, pegar,
                new SeparatorMenuItem(), renombrar, eliminar);
    }

    private VBox casilla(javafx.scene.Node icono, String texto) {
        VBox casilla = new VBox(6, icono, etiqueta(texto));
        casilla.setAlignment(Pos.TOP_CENTER);
        casilla.setPrefWidth(ANCHO_CASILLA);
        casilla.setPadding(new Insets(10, 4, 10, 4));
        Estilos.hover(casilla, CASILLA, CASILLA_ENCIMA);
        return casilla;
    }

    private Label etiqueta(String texto) {
        Label etiqueta = new Label(texto);
        etiqueta.setWrapText(true);
        etiqueta.setAlignment(Pos.CENTER);
        etiqueta.setMaxWidth(ANCHO_CASILLA - 8);
        etiqueta.setStyle("-fx-font-family: '" + Estilos.FUENTE + "'; -fx-font-size: 12px; -fx-text-fill: white; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75), 4, 0.4, 0, 1);");
        return etiqueta;
    }

    private void seleccionar(VBox casilla) {
        if (seleccionada != null) {
            Estilos.hover(seleccionada, CASILLA, CASILLA_ENCIMA);
        }
        seleccionada = casilla;
        Estilos.hover(casilla, CASILLA_ACTIVA, CASILLA_ACTIVA);
    }

    private void alCambiarCarpeta(RutaVirtual cambiada) {
        if (!cambiada.equals(carpeta)) {
            return;
        }
        if (Platform.isFxApplicationThread()) {
            reconstruir();
        } else {
            Platform.runLater(this::reconstruir);
        }
    }
}
