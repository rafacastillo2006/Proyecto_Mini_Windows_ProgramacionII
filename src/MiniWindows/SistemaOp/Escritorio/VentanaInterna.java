package MiniWindows.SistemaOp.Escritorio;

import MiniWindows.SistemaOp.Apps.Aplicacion;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class VentanaInterna extends StackPane {

    public static final double ANCHO_MINIMO = 360;
    public static final double ALTO_MINIMO = 240;

    private static final double ALTO_BARRA = 36;
    private static final double GROSOR_MANIJA = 6;
    private static final double LADO_ESQUINA = 14;

    private static final String MARCO_ACTIVO = "-fx-background-color: white; -fx-background-radius: 8; "
            + "-fx-border-color: rgba(0,0,0,0.16); -fx-border-radius: 8; "
            + "-fx-effect: dropshadow(gaussian, rgba(16,24,40,0.26), 22, 0.04, 0, 8);";
    private static final String MARCO_INACTIVO = "-fx-background-color: white; -fx-background-radius: 8; "
            + "-fx-border-color: rgba(0,0,0,0.10); -fx-border-radius: 8; "
            + "-fx-effect: dropshadow(gaussian, rgba(16,24,40,0.16), 16, 0.03, 0, 5);";
    private static final String BOTON_BARRA = "-fx-background-color: transparent; -fx-background-radius: 0;";
    private static final String BOTON_BARRA_ENCIMA = "-fx-background-color: rgba(0,0,0,0.07); -fx-background-radius: 0;";
    private static final String BOTON_CERRAR_ENCIMA = "-fx-background-color: #c42b1c; -fx-background-radius: 0 8 0 0;";

    private final GestorVentanas gestor;
    private final Aplicacion aplicacion;
    private final Node contenido;
    private final Button botonMaximizar;
    private final BooleanProperty minimizada = new SimpleBooleanProperty(false);

    private boolean maximizada;
    private boolean acoplada;
    private double xRestaurar;
    private double yRestaurar;
    private double anchoRestaurar;
    private double altoRestaurar;
    private double desplazamientoX;
    private double desplazamientoY;
    private double anchoInicial;
    private double altoInicial;
    private double origenArrastreX;
    private double origenArrastreY;
    private ZonaAcople zonaPrevista = ZonaAcople.NINGUNA;

    public VentanaInterna(GestorVentanas gestor, Aplicacion aplicacion, Node contenido) {
        this.contenido = contenido;
        this.gestor = gestor;
        this.aplicacion = aplicacion;
        this.botonMaximizar = botonDeBarra(Iconos.MAXIMIZAR, BOTON_BARRA_ENCIMA);

        HBox barra = crearBarraTitulo();
        StackPane marcoContenido = new StackPane(contenido);
        marcoContenido.setStyle("-fx-background-color: " + Estilos.SUPERFICIE + "; -fx-background-radius: 0 0 8 8;");
        VBox.setVgrow(marcoContenido, Priority.ALWAYS);

        VBox cuerpo = new VBox(barra, marcoContenido);
        cuerpo.setFillWidth(true);

        getChildren().addAll(cuerpo,
                crearManija(Pos.CENTER_RIGHT, Cursor.E_RESIZE, true, false),
                crearManija(Pos.BOTTOM_CENTER, Cursor.S_RESIZE, false, true),
                crearManija(Pos.BOTTOM_RIGHT, Cursor.SE_RESIZE, true, true));

        setStyle(MARCO_ACTIVO);
        setMinSize(ANCHO_MINIMO, ALTO_MINIMO);
        addEventFilter(MouseEvent.MOUSE_PRESSED, evento -> gestor.activar(this));
        minimizada.addListener((observable, anterior, actual) -> {
            setVisible(!actual);
            setManaged(!actual);
        });
    }

    public void alternarMaximizada() {
        if (maximizada) {
            restaurar();
        } else {
            maximizar();
        }
    }

    public void maximizar() {
        Pane capa = capa();
        if (capa == null || maximizada) {
            return;
        }
        if (!acoplada) {
            guardarRestauracion();
        }
        acoplada = false;
        setLayoutX(0);
        setLayoutY(0);
        prefWidthProperty().bind(capa.widthProperty());
        prefHeightProperty().bind(capa.heightProperty());
        maximizada = true;
        botonMaximizar.setGraphic(Iconos.crear(Iconos.RESTAURAR, 14, Color.web(Estilos.TEXTO)));
    }

    public void acoplar(ZonaAcople zona) {
        Pane capa = capa();
        if (capa == null || zona == ZonaAcople.NINGUNA) {
            return;
        }
        if (zona == ZonaAcople.SUPERIOR) {
            maximizar();
            return;
        }
        if (!maximizada && !acoplada) {
            guardarRestauracion();
        }
        soltarEnlaces();
        maximizada = false;
        acoplada = true;
        double mitad = capa.getWidth() / 2;
        setLayoutX(zona == ZonaAcople.IZQUIERDA ? 0 : mitad);
        setLayoutY(0);
        setPrefSize(mitad, capa.getHeight());
        botonMaximizar.setGraphic(Iconos.crear(Iconos.MAXIMIZAR, 14, Color.web(Estilos.TEXTO)));
    }

    public void restaurar() {
        if (!maximizada && !acoplada) {
            return;
        }
        Pane capa = capa();
        soltarEnlaces();
        maximizada = false;
        acoplada = false;
        if (capa != null) {
            anchoRestaurar = Math.min(anchoRestaurar, capa.getWidth());
            altoRestaurar = Math.min(altoRestaurar, capa.getHeight());
        }
        setPrefSize(anchoRestaurar, altoRestaurar);
        setLayoutX(xRestaurar);
        setLayoutY(yRestaurar);
        botonMaximizar.setGraphic(Iconos.crear(Iconos.MAXIMIZAR, 14, Color.web(Estilos.TEXTO)));
    }

    public void marcarActiva(boolean activa) {
        setStyle(activa ? MARCO_ACTIVO : MARCO_INACTIVO);
    }

    public Node getContenido() {
        return contenido;
    }

    public Aplicacion getAplicacion() {
        return aplicacion;
    }

    public BooleanProperty minimizadaProperty() {
        return minimizada;
    }

    public boolean estaMinimizada() {
        return minimizada.get();
    }

    public void setMinimizada(boolean valor) {
        minimizada.set(valor);
    }

    public boolean estaAcoplada() {
        return acoplada;
    }

    private HBox crearBarraTitulo() {
        Label titulo = new Label(aplicacion.titulo());
        titulo.setStyle("-fx-font-family: '" + Estilos.FUENTE + "'; -fx-font-size: 12.5px; "
                + "-fx-text-fill: " + Estilos.TEXTO + ";");

        Button minimizar = botonDeBarra(Iconos.MINIMIZAR, BOTON_BARRA_ENCIMA);
        minimizar.setOnAction(evento -> gestor.alternar(this));
        botonMaximizar.setOnAction(evento -> alternarMaximizada());

        Button cerrar = botonDeBarra(Iconos.CERRAR, BOTON_CERRAR_ENCIMA);
        cerrar.setOnAction(evento -> gestor.cerrar(this));

        HBox barra = new HBox(8, Iconos.imagen(aplicacion.icono(), 16), titulo,
                Estilos.espaciador(), minimizar, botonMaximizar, cerrar);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setPadding(new Insets(0, 0, 0, 12));
        barra.setMinHeight(ALTO_BARRA);
        barra.setPrefHeight(ALTO_BARRA);
        barra.setStyle("-fx-background-color: " + Estilos.SUPERFICIE_ALTERNA + "; -fx-background-radius: 8 8 0 0;");
        habilitarArrastre(barra);
        return barra;
    }

    private void habilitarArrastre(HBox barra) {
        barra.setOnMousePressed(evento -> {
            desplazamientoX = evento.getSceneX() - getLayoutX();
            desplazamientoY = evento.getSceneY() - getLayoutY();
            setCacheHint(CacheHint.SPEED);
            setCache(true);
        });
        barra.setOnMouseDragged(evento -> {
            Pane capa = capa();
            if (capa == null) {
                return;
            }
            if (maximizada || acoplada) {
                despegarArrastrando(evento);
            }
            double limiteX = Math.max(0, capa.getWidth() - getWidth());
            double limiteY = Math.max(0, capa.getHeight() - getHeight());
            setLayoutX(Math.clamp(evento.getSceneX() - desplazamientoX, 0, limiteX));
            setLayoutY(Math.clamp(evento.getSceneY() - desplazamientoY, 0, limiteY));

            Point2D enCapa = capa.sceneToLocal(evento.getSceneX(), evento.getSceneY());
            zonaPrevista = gestor.zonaPara(enCapa.getX(), enCapa.getY());
            gestor.mostrarPrevio(zonaPrevista);
        });
        barra.setOnMouseReleased(evento -> {
            setCache(false);
            gestor.ocultarPrevio();
            if (zonaPrevista != ZonaAcople.NINGUNA) {
                acoplar(zonaPrevista);
                zonaPrevista = ZonaAcople.NINGUNA;
            }
        });
        barra.setOnMouseClicked(evento -> {
            if (evento.getButton() == MouseButton.PRIMARY && evento.getClickCount() == 2) {
                alternarMaximizada();
            }
        });
    }

    private void despegarArrastrando(MouseEvent evento) {
        double proporcion = Math.clamp((evento.getSceneX() - getLayoutX()) / Math.max(1, getWidth()), 0, 1);
        restaurar();
        desplazamientoX = anchoRestaurar * proporcion;
        desplazamientoY = Math.min(desplazamientoY, ALTO_BARRA / 2);
    }

    private Region crearManija(Pos posicion, Cursor cursor, boolean cambiaAncho, boolean cambiaAlto) {
        Region manija = new Region();
        manija.setCursor(cursor);
        if (cambiaAncho && cambiaAlto) {
            manija.setPrefSize(LADO_ESQUINA, LADO_ESQUINA);
            manija.setMaxSize(LADO_ESQUINA, LADO_ESQUINA);
        } else if (cambiaAncho) {
            manija.setPrefWidth(GROSOR_MANIJA);
            manija.setMaxWidth(GROSOR_MANIJA);
            StackPane.setMargin(manija, new Insets(ALTO_BARRA, 0, LADO_ESQUINA, 0));
        } else {
            manija.setPrefHeight(GROSOR_MANIJA);
            manija.setMaxHeight(GROSOR_MANIJA);
            StackPane.setMargin(manija, new Insets(0, LADO_ESQUINA, 0, 8));
        }
        StackPane.setAlignment(manija, posicion);
        manija.setOnMousePressed(evento -> {
            anchoInicial = getWidth();
            altoInicial = getHeight();
            origenArrastreX = evento.getSceneX();
            origenArrastreY = evento.getSceneY();
            evento.consume();
        });
        manija.setOnMouseDragged(evento -> {
            if (maximizada) {
                return;
            }
            acoplada = false;
            double ancho = cambiaAncho ? anchoInicial + evento.getSceneX() - origenArrastreX : getWidth();
            double alto = cambiaAlto ? altoInicial + evento.getSceneY() - origenArrastreY : getHeight();
            redimensionar(ancho, alto);
            evento.consume();
        });
        return manija;
    }

    private void redimensionar(double ancho, double alto) {
        Pane capa = capa();
        double anchoMaximo = capa == null
                ? Double.MAX_VALUE
                : Math.max(ANCHO_MINIMO, capa.getWidth() - getLayoutX());
        double altoMaximo = capa == null
                ? Double.MAX_VALUE
                : Math.max(ALTO_MINIMO, capa.getHeight() - getLayoutY());
        setPrefSize(Math.clamp(ancho, ANCHO_MINIMO, anchoMaximo), Math.clamp(alto, ALTO_MINIMO, altoMaximo));
    }

    private void guardarRestauracion() {
        xRestaurar = getLayoutX();
        yRestaurar = getLayoutY();
        anchoRestaurar = getWidth();
        altoRestaurar = getHeight();
    }

    private void soltarEnlaces() {
        prefWidthProperty().unbind();
        prefHeightProperty().unbind();
    }

    private Button botonDeBarra(String icono, String estiloEncima) {
        Button boton = new Button();
        boton.setGraphic(Iconos.crear(icono, 14, Color.web(Estilos.TEXTO)));
        boton.setMinSize(46, ALTO_BARRA);
        boton.setPrefSize(46, ALTO_BARRA);
        boton.setFocusTraversable(false);
        boton.addEventHandler(MouseEvent.MOUSE_CLICKED, Event::consume);
        Estilos.hover(boton, BOTON_BARRA, estiloEncima);
        return boton;
    }

    private Pane capa() {
        return getParent() instanceof Pane pane ? pane : null;
    }
}
