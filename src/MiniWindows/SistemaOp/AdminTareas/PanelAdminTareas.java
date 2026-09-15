package MiniWindows.SistemaOp.AdminTareas;

import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Escritorio.Dialogos;
import MiniWindows.SistemaOp.Escritorio.Estilos;
import MiniWindows.SistemaOp.Escritorio.GestorVentanas;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import MiniWindows.SistemaOp.Escritorio.VentanaInterna;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;

public class PanelAdminTareas extends BorderPane {

    private static final int MUESTRAS_GRAFICO = 60;
    private static final double MEGA = 1024 * 1024;

    private final GestorVentanas gestor;
    private final MonitorSistema monitor;

    private final TableView<TareaAbierta> tablaTareas = new TableView<>();
    private final TableView<InfoHilo> tablaHilos = new TableView<>();
    private final Button finalizar = Estilos.botonHerramienta(Iconos.FINALIZAR, "Finalizar tarea",
            "Cierra la aplicación seleccionada");

    private final ProgressBar barraMemoria = new ProgressBar(0);
    private final ProgressBar barraCpu = new ProgressBar(0);
    private final Label textoMemoria = Estilos.etiqueta("");
    private final Label textoCpu = Estilos.etiqueta("");
    private final Label textoHilos = Estilos.etiqueta("");
    private final Label textoEncendido = Estilos.leyenda("");
    private final Label estado = Estilos.leyenda("");

    private final Deque<Double> historialCpu = new ArrayDeque<>();
    private final Polyline lineaCpu = new Polyline();
    private final Pane lienzoCpu = new Pane(lineaCpu);

    public PanelAdminTareas(ContextoApp contexto) {
        this.gestor = contexto.getVentanas();
        this.monitor = new MonitorSistema(this::actualizar);

        TabPane pestanas = new TabPane(
                pestana("Procesos", panelProcesos()),
                pestana("Rendimiento", panelRendimiento()),
                pestana("Hilos", panelHilos()));
        pestanas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        setCenter(pestanas);
        setBottom(barraEstado());
        setStyle(Estilos.PANEL);

        gestor.getVentanas().addListener((ListChangeListener<VentanaInterna>) cambio -> refrescarTareas());
        actualizar(MonitorSistema.tomarMuestra());
        refrescarTareas();

        sceneProperty().addListener((observable, anterior, actual) -> {
            if (actual == null) {
                monitor.detener();
            } else {
                monitor.iniciar();
            }
        });
    }

    private Tab pestana(String titulo, javafx.scene.Node contenido) {
        Tab pestana = new Tab(titulo, contenido);
        pestana.setClosable(false);
        return pestana;
    }

    private BorderPane panelProcesos() {
        columna(tablaTareas, "Nombre", 300, TareaAbierta::nombre);
        columna(tablaTareas, "Tipo", 150, TareaAbierta::tipo);
        columna(tablaTareas, "Estado", 150, TareaAbierta::estado);
        tablaTareas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaTareas.setStyle(Estilos.TABLA);
        tablaTareas.setPlaceholder(Estilos.leyenda("No hay aplicaciones abiertas"));

        finalizar.setOnAction(evento -> finalizarSeleccion());
        finalizar.disableProperty().bind(tablaTareas.getSelectionModel().selectedItemProperty().isNull());
        tablaTareas.getSelectionModel().selectedItemProperty().addListener((observable, anterior, actual) ->
                estado.setText(actual != null && actual.protegida()
                        ? "\"" + actual.nombre() + "\" es parte del sistema y no se puede finalizar"
                        : ""));

        HBox herramientas = Estilos.fila(4, finalizar);
        herramientas.setPadding(new Insets(8, 10, 8, 10));
        herramientas.setStyle(Estilos.BARRA_HERRAMIENTAS);

        BorderPane panel = new BorderPane(tablaTareas);
        panel.setTop(herramientas);
        return panel;
    }

    private VBox panelRendimiento() {
        barraMemoria.setMaxWidth(Double.MAX_VALUE);
        barraMemoria.setPrefHeight(18);
        barraMemoria.setStyle("-fx-accent: " + Estilos.ACENTO + ";");
        barraCpu.setMaxWidth(Double.MAX_VALUE);
        barraCpu.setPrefHeight(18);
        barraCpu.setStyle("-fx-accent: " + Estilos.ACENTO + ";");

        lineaCpu.setStroke(Color.web(Estilos.ACENTO));
        lineaCpu.setStrokeWidth(1.6);
        lienzoCpu.setPrefHeight(120);
        lienzoCpu.setStyle("-fx-background-color: white; -fx-border-color: " + Estilos.BORDE + "; "
                + "-fx-border-radius: 8; -fx-background-radius: 8;");
        lienzoCpu.widthProperty().addListener((observable, anterior, actual) -> dibujarCpu());
        lienzoCpu.heightProperty().addListener((observable, anterior, actual) -> dibujarCpu());

        VBox panel = new VBox(16,
                bloque("Memoria en uso", textoMemoria, barraMemoria),
                bloque("Procesador", textoCpu, barraCpu),
                new VBox(6, Estilos.subtitulo("Uso del procesador (último minuto)"), lienzoCpu),
                new VBox(4, textoHilos, textoEncendido));
        panel.setPadding(new Insets(18));
        VBox.setVgrow(lienzoCpu, Priority.ALWAYS);
        return panel;
    }

    private VBox bloque(String titulo, Label detalle, ProgressBar barra) {
        VBox bloque = new VBox(6, Estilos.subtitulo(titulo), detalle, barra);
        bloque.setAlignment(Pos.TOP_LEFT);
        return bloque;
    }

    private BorderPane panelHilos() {
        columna(tablaHilos, "Hilo", 260, InfoHilo::nombre);
        columna(tablaHilos, "Estado", 170, InfoHilo::estado);
        columna(tablaHilos, "Origen", 120, InfoHilo::origen);
        columna(tablaHilos, "Demonio", 90, hilo -> hilo.demonio() ? "Sí" : "No");
        columna(tablaHilos, "Prioridad", 90, hilo -> String.valueOf(hilo.prioridad()));
        tablaHilos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaHilos.setStyle(Estilos.TABLA);
        tablaHilos.setPlaceholder(Estilos.leyenda("Sin hilos que mostrar"));

        HBox aviso = Estilos.fila(8, Estilos.leyenda(
                "Los hilos pertenecen a la máquina virtual: se muestran para diagnóstico y no se pueden finalizar."));
        aviso.setPadding(new Insets(8, 10, 8, 10));
        aviso.setStyle(Estilos.BARRA_HERRAMIENTAS);

        BorderPane panel = new BorderPane(tablaHilos);
        panel.setTop(aviso);
        return panel;
    }

    private <T> void columna(TableView<T> tabla, String titulo, double ancho, Function<T, String> valor) {
        TableColumn<T, String> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(dato -> new ReadOnlyStringWrapper(valor.apply(dato.getValue())));
        columna.setPrefWidth(ancho);
        tabla.getColumns().add(columna);
    }

    private HBox barraEstado() {
        HBox barra = Estilos.fila(12, estado);
        barra.setPadding(new Insets(6, 12, 6, 12));
        barra.setStyle(Estilos.BARRA_ESTADO);
        return barra;
    }

    private void actualizar(MuestraSistema muestra) {
        textoMemoria.setText(String.format("%.1f MB usados de %.1f MB reservados (máximo %.1f MB)",
                muestra.memoriaUsada() / MEGA, muestra.memoriaReservada() / MEGA, muestra.memoriaMaxima() / MEGA));
        barraMemoria.setProgress(muestra.proporcionMemoria());

        double cpu = muestra.cpuProceso();
        textoCpu.setText(cpu < 0
                ? "Uso del proceso: no disponible en este equipo"
                : String.format("MiniWindows %.1f%%   ·   sistema %.1f%%",
                cpu * 100, Math.max(0, muestra.cpuSistema()) * 100));
        barraCpu.setProgress(Math.max(0, cpu));

        textoHilos.setText(muestra.totalHilos() + " hilos vivos, " + muestra.hilosDeAplicacion()
                + " creados por MiniWindows");
        textoEncendido.setText("Encendido hace " + tiempo(muestra.tiempoEncendido()));

        historialCpu.addLast(Math.max(0, cpu));
        while (historialCpu.size() > MUESTRAS_GRAFICO) {
            historialCpu.removeFirst();
        }
        dibujarCpu();

        sincronizar(tablaHilos, muestra.hilos().aLista());
        refrescarTareas();
    }

    private void refrescarTareas() {
        List<TareaAbierta> filas = new ArrayList<>();
        filas.add(new TareaAbierta("MiniWindows (núcleo)", "Sistema", "Ejecutando", null, true));
        for (VentanaInterna ventana : gestor.getVentanas()) {
            filas.add(new TareaAbierta(ventana.getAplicacion().titulo(), "Aplicación",
                    ventana.estaMinimizada() ? "Minimizada" : "Ejecutando", ventana,
                    AppAdminTareas.ID.equals(ventana.getAplicacion().id())));
        }
        sincronizar(tablaTareas, filas);
    }

    private <T> void sincronizar(TableView<T> tabla, List<T> filas) {
        List<T> items = tabla.getItems();
        for (int posicion = 0; posicion < filas.size(); posicion++) {
            if (posicion < items.size()) {
                if (!items.get(posicion).equals(filas.get(posicion))) {
                    items.set(posicion, filas.get(posicion));
                }
            } else {
                items.add(filas.get(posicion));
            }
        }
        while (items.size() > filas.size()) {
            items.remove(items.size() - 1);
        }
    }

    private void finalizarSeleccion() {
        TareaAbierta tarea = tablaTareas.getSelectionModel().getSelectedItem();
        if (tarea == null) {
            return;
        }
        if (tarea.protegida() || tarea.ventana() == null) {
            Dialogos.informacion(this, "Tarea protegida",
                    "\"" + tarea.nombre() + "\" forma parte del sistema y no se puede finalizar.");
            return;
        }
        if (Dialogos.confirmar(this, "Finalizar tarea",
                "¿Cerrar \"" + tarea.nombre() + "\"? Se perderá lo que no esté guardado.")) {
            gestor.cerrar(tarea.ventana());
            estado.setText("Se finalizó \"" + tarea.nombre() + "\"");
        }
    }

    private void dibujarCpu() {
        double ancho = lienzoCpu.getWidth();
        double alto = lienzoCpu.getHeight();
        lineaCpu.getPoints().clear();
        if (ancho <= 0 || alto <= 0 || historialCpu.size() < 2) {
            return;
        }
        double paso = ancho / (MUESTRAS_GRAFICO - 1);
        int posicion = MUESTRAS_GRAFICO - historialCpu.size();
        for (double valor : historialCpu) {
            lineaCpu.getPoints().addAll(posicion * paso, alto - Math.clamp(valor, 0, 1) * (alto - 4) - 2);
            posicion++;
        }
    }

    private String tiempo(long milisegundos) {
        long segundos = milisegundos / 1000;
        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;
        if (horas > 0) {
            return horas + " h " + minutos + " min";
        }
        if (minutos > 0) {
            return minutos + " min " + (segundos % 60) + " s";
        }
        return segundos + " s";
    }

    private record TareaAbierta(String nombre, String tipo, String estado, VentanaInterna ventana, boolean protegida) {
    }
}
