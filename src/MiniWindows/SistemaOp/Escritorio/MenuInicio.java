package MiniWindows.SistemaOp.Escritorio;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.SistemaOp.Apps.Aplicacion;
import MiniWindows.SistemaOp.Nucleo.Sesion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MenuInicio extends VBox {

    private static final String CASILLA = "-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand;";
    private static final String CASILLA_ENCIMA = "-fx-background-color: rgba(0,0,0,0.06); -fx-background-radius: 8; -fx-cursor: hand;";

    private final GestorVentanas gestor;

    public MenuInicio(ListaEnlazada<Aplicacion> aplicaciones, Sesion sesion, GestorVentanas gestor,
                      Runnable alCerrarSesion, Runnable alApagar) {
        this.gestor = gestor;

        setSpacing(16);
        setPadding(new Insets(20));
        setPrefSize(520, 380);
        setMaxSize(520, 380);
        setStyle(Estilos.SUPERFICIE_FLOTANTE);
        setVisible(false);

        FlowPane casillas = new FlowPane(12, 12);
        casillas.setPrefWrapLength(460);
        for (Aplicacion aplicacion : aplicaciones) {
            casillas.getChildren().add(casilla(aplicacion));
        }

        VBox contenido = new VBox(10, Estilos.subtitulo("Aplicaciones"), casillas);
        VBox.setVgrow(contenido, javafx.scene.layout.Priority.ALWAYS);

        getChildren().addAll(contenido, separador(), pie(sesion, alCerrarSesion, alApagar));
    }

    public void alternar() {
        setVisible(!isVisible());
    }

    public void ocultar() {
        setVisible(false);
    }

    private VBox casilla(Aplicacion aplicacion) {
        Label nombre = Estilos.leyenda(aplicacion.titulo());
        nombre.setWrapText(true);
        nombre.setAlignment(Pos.CENTER);
        nombre.setMaxWidth(96);

        VBox casilla = new VBox(8, Iconos.imagen(aplicacion.icono(), 32), nombre);
        casilla.setAlignment(Pos.CENTER);
        casilla.setPrefSize(104, 96);
        Estilos.hover(casilla, CASILLA, CASILLA_ENCIMA);
        casilla.setOnMouseClicked(evento -> {
            if (evento.getButton() != MouseButton.PRIMARY) {
                return;
            }
            ocultar();
            gestor.abrir(aplicacion);
        });
        return casilla;
    }

    private HBox pie(Sesion sesion, Runnable alCerrarSesion, Runnable alApagar) {
        Label nombre = Estilos.etiqueta(sesion.getUsuario().getNombreCompleto());
        Label rol = Estilos.leyenda(sesion.getUsuario().getRol().getEtiqueta());
        VBox datos = new VBox(nombre, rol);

        Button cerrarSesion = Estilos.botonHerramienta(Iconos.SALIR, "Cerrar sesión", "Volver a la pantalla de inicio");
        cerrarSesion.setOnAction(evento -> {
            ocultar();
            alCerrarSesion.run();
        });

        Button apagar = Estilos.botonHerramienta(Iconos.APAGAR, "Apagar", "Salir de MiniWindows");
        apagar.setOnAction(evento -> {
            ocultar();
            alApagar.run();
        });

        HBox pie = new HBox(12, Iconos.crear(Iconos.USUARIO, 28, Color.web(Estilos.ACENTO)), datos,
                Estilos.espaciador(), cerrarSesion, apagar);
        pie.setAlignment(Pos.CENTER_LEFT);
        return pie;
    }

    private Region separador() {
        Region separador = new Region();
        separador.setMinHeight(1);
        separador.setPrefHeight(1);
        separador.setStyle("-fx-background-color: " + Estilos.BORDE + ";");
        return separador;
    }
}
