package MiniWindows.SistemaOp.Escritorio;

import MiniWindows.SistemaOp.Nucleo.Sesion;
import MiniWindows.Util.Fechas;
import MiniWindows.Util.Nombres;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class BarraTareas extends StackPane {

    private static final double ALTURA = 48;
    private static final double SEPARACION_INICIO = 6;
    private static final double MARGEN_DERECHO = 16;
    private static final double ANCHO_MAXIMO_BOTON = 170;
    private static final String BOTON = "-fx-background-color: transparent; -fx-background-radius: 6; -fx-cursor: hand;";
    private static final String BOTON_ENCIMA = "-fx-background-color: rgba(0,0,0,0.07); -fx-background-radius: 6; -fx-cursor: hand;";
    private static final String BOTON_ACTIVO = "-fx-background-color: rgba(0,103,192,0.16); -fx-background-radius: 6; -fx-cursor: hand;";

    private final GestorVentanas gestor;
    private final HBox ventanasAbiertas = new HBox(4);
    private final Button inicio;
    private final Label hora = Estilos.etiqueta("");
    private final Label fecha = Estilos.leyenda("");
    private final Timeline reloj;

    public BarraTareas(Sesion sesion, GestorVentanas gestor, Runnable alPulsarInicio, Runnable alCerrarSesion) {
        this.gestor = gestor;

        setMinHeight(ALTURA);
        setPrefHeight(ALTURA);
        setStyle("-fx-background-color: rgba(243,243,243,0.94); "
                + "-fx-border-color: rgba(0,0,0,0.08) transparent transparent transparent; -fx-border-width: 1 0 0 0;");

        inicio = botonDeInicio();
        inicio.setOnAction(evento -> alPulsarInicio.run());

        ventanasAbiertas.setAlignment(Pos.CENTER_LEFT);
        ventanasAbiertas.setMaxWidth(Region.USE_PREF_SIZE);
        ventanasAbiertas.setPickOnBounds(false);

        HBox derecha = new HBox(10, chipUsuario(sesion, alCerrarSesion), reloj());
        derecha.setAlignment(Pos.CENTER_RIGHT);
        derecha.setPadding(new Insets(0, 12, 0, 8));
        derecha.setMaxWidth(Region.USE_PREF_SIZE);
        derecha.setPickOnBounds(false);

        getChildren().addAll(inicio, ventanasAbiertas, derecha);
        setAlignment(inicio, Pos.CENTER);
        setAlignment(ventanasAbiertas, Pos.CENTER);
        setAlignment(derecha, Pos.CENTER_RIGHT);

        ventanasAbiertas.translateXProperty().bind(inicio.widthProperty().divide(2)
                .add(SEPARACION_INICIO)
                .add(ventanasAbiertas.widthProperty().divide(2)));
        ventanasAbiertas.maxWidthProperty().bind(Bindings.createDoubleBinding(
                () -> Math.max(0, getWidth() / 2 - derecha.getWidth()
                        - inicio.getWidth() / 2 - SEPARACION_INICIO - MARGEN_DERECHO),
                widthProperty(), derecha.widthProperty(), inicio.widthProperty()));

        reloj = crearReloj();
        sceneProperty().addListener((observable, anterior, actual) -> {
            if (actual == null) {
                reloj.stop();
            } else {
                reloj.play();
            }
        });

        gestor.getVentanas().addListener((ListChangeListener<VentanaInterna>) cambio -> refrescar());
        gestor.ventanaActivaProperty().addListener((observable, anterior, actual) -> refrescar());
        refrescar();
    }

    public Button getBotonInicio() {
        return inicio;
    }

    public void detener() {
        reloj.stop();
    }

    private void refrescar() {
        ventanasAbiertas.getChildren().clear();
        for (VentanaInterna ventana : gestor.getVentanas()) {
            ventanasAbiertas.getChildren().add(botonDeVentana(ventana));
        }
    }

    private Button botonDeVentana(VentanaInterna ventana) {
        Button boton = new Button(Nombres.recortar(ventana.getAplicacion().titulo(), 18));
        boton.setGraphic(Iconos.imagen(ventana.getAplicacion().icono(), 16));
        boton.setGraphicTextGap(8);
        boton.setPrefHeight(38);
        boton.setMaxWidth(ANCHO_MAXIMO_BOTON);
        boton.setFocusTraversable(false);
        boolean activa = gestor.ventanaActivaProperty().get() == ventana && !ventana.estaMinimizada();
        Estilos.hover(boton, (activa ? BOTON_ACTIVO : BOTON) + textoBoton(), BOTON_ENCIMA + textoBoton());
        boton.setOnAction(evento -> gestor.alternar(ventana));
        return boton;
    }

    private String textoBoton() {
        return "-fx-font-family: '" + Estilos.FUENTE + "'; -fx-font-size: 12.5px; -fx-text-fill: " + Estilos.TEXTO + ";";
    }

    private HBox chipUsuario(Sesion sesion, Runnable alCerrarSesion) {
        Label nombre = Estilos.etiqueta(sesion.getUsuario().getUsername());
        Label rol = Estilos.leyenda(sesion.getUsuario().getRol().getEtiqueta());
        VBox datos = new VBox(nombre, rol);
        datos.setAlignment(Pos.CENTER_LEFT);

        Button salir = botonCuadrado(Iconos.SALIR, "Cerrar sesión", Color.web(Estilos.TEXTO));
        salir.setOnAction(evento -> alCerrarSesion.run());

        HBox chip = new HBox(8, Iconos.crear(Iconos.USUARIO, 20, Color.web(Estilos.ACENTO)), datos, salir);
        chip.setAlignment(Pos.CENTER_LEFT);
        return chip;
    }

    private VBox reloj() {
        VBox caja = new VBox(hora, fecha);
        caja.setAlignment(Pos.CENTER_RIGHT);
        caja.setPadding(new Insets(0, 4, 0, 8));
        return caja;
    }

    private Timeline crearReloj() {
        Timeline animacion = new Timeline(new KeyFrame(Duration.ZERO, evento -> {
            hora.setText(Fechas.formatearHora(Fechas.ahora()));
            fecha.setText(Fechas.formatearFecha(Fechas.ahora()));
        }), new KeyFrame(Duration.seconds(1)));
        animacion.setCycleCount(Animation.INDEFINITE);
        return animacion;
    }

    private Button botonDeInicio() {
        Button boton = new Button();
        boton.setGraphic(Iconos.imagen("inicio", 28));
        boton.setTooltip(new Tooltip("Inicio"));
        boton.setMinSize(44, 40);
        boton.setPrefSize(44, 40);
        boton.setFocusTraversable(false);
        Estilos.hover(boton, BOTON, BOTON_ENCIMA);
        return boton;
    }

    private Button botonCuadrado(String icono, String ayuda, Color color) {
        Button boton = new Button();
        boton.setGraphic(Iconos.crear(icono, 18, color));
        boton.setTooltip(new Tooltip(ayuda));
        boton.setMinSize(40, 38);
        boton.setPrefSize(40, 38);
        boton.setFocusTraversable(false);
        Estilos.hover(boton, BOTON, BOTON_ENCIMA);
        return boton;
    }
}
