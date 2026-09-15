package MiniWindows.SistemaOp.Explorador;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Archivos.CriterioOrden;
import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import MiniWindows.SistemaOp.Archivos.ObservadorArchivos;
import MiniWindows.SistemaOp.Archivos.RutaVirtual;
import MiniWindows.SistemaOp.Archivos.SistemaArchivos;
import MiniWindows.SistemaOp.Escritorio.AccionesArchivos;
import MiniWindows.SistemaOp.Escritorio.Dialogos;
import MiniWindows.SistemaOp.Escritorio.Estilos;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import MiniWindows.SistemaOp.Escritorio.PortapapelesArchivos;
import MiniWindows.SistemaOp.Nucleo.Sesion;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

public class PanelExplorador extends BorderPane {

    private final ContextoApp contexto;
    private final AccionesArchivos acciones;
    private final PortapapelesArchivos portapapeles;
    private final SistemaArchivos archivos;
    private final Sesion sesion;
    private final RutaVirtual raiz;

    private final TreeView<NodoArchivo> arbol = new TreeView<>();
    private final TableView<NodoArchivo> tabla = new TableView<>();
    private final ComboBox<CriterioOrden> orden = new ComboBox<>();
    private final TextField busqueda = Estilos.campo("Buscar en esta carpeta");
    private final Label rutaActual = Estilos.etiqueta("");
    private final Label estado = Estilos.leyenda("");

    private final ObservadorArchivos observador = this::alCambiarCarpeta;
    private final Deque<RutaVirtual> historialAtras = new ArrayDeque<>();
    private final Deque<RutaVirtual> historialAdelante = new ArrayDeque<>();

    private Button botonAtras;
    private Button botonAdelante;
    private Button botonArriba;
    private ItemCarpeta itemRaiz;
    private ListaEnlazada<NodoArchivo> contenido = new ListaEnlazada<>();
    private RutaVirtual carpetaActual;
    private RutaVirtual origenPortapapeles;
    private boolean cortando;
    private boolean sincronizandoArbol;
    private boolean iniciando = true;

    public PanelExplorador(ContextoApp contexto) {
        this(contexto, null);
    }

    public PanelExplorador(ContextoApp contexto, RutaVirtual carpetaInicial) {
        this.contexto = contexto;
        this.acciones = contexto.getAcciones();
        this.portapapeles = contexto.getAcciones().getPortapapeles();
        this.archivos = contexto.getArchivos();
        this.sesion = contexto.getSesion();
        this.raiz = sesion.getRaiz();

        setBottom(crearBarraEstado());
        prepararRaiz();
        setTop(new VBox(crearBarraHerramientas(), crearBarraRuta()));
        setCenter(crearContenidoPrincipal());
        setStyle(Estilos.PANEL);

        RutaVirtual inicio = carpetaInicial != null && sesion.puedeAcceder(carpetaInicial)
                ? carpetaInicial
                : raiz;
        if (!navegar(inicio, false) && !inicio.equals(raiz)) {
            navegar(raiz, false);
        }
        iniciando = false;

        sceneProperty().addListener((observable, anterior, actual) -> {
            if (actual == null) {
                archivos.quitarObservador(observador);
            } else {
                archivos.agregarObservador(observador);
            }
        });
    }

    private FlowPane crearBarraHerramientas() {
        botonAtras = Estilos.botonIcono(Iconos.ATRAS, "Atrás");
        botonAtras.setOnAction(evento -> irAtras());

        botonAdelante = Estilos.botonIcono(Iconos.ADELANTE, "Adelante");
        botonAdelante.setOnAction(evento -> irAdelante());

        botonArriba = Estilos.botonIcono(Iconos.ARRIBA, "Subir un nivel");
        botonArriba.setOnAction(evento -> irArriba());

        Button actualizar = Estilos.botonIcono(Iconos.ACTUALIZAR, "Actualizar");
        actualizar.setOnAction(evento -> refrescar());

        Button nuevaCarpeta = Estilos.botonHerramienta(Iconos.NUEVA_CARPETA, "Nueva carpeta", "Crear una carpeta");
        nuevaCarpeta.setOnAction(evento -> acciones.crearCarpeta(this, carpetaActual));

        Button nuevoDocumento = Estilos.botonHerramienta(Iconos.NUEVO_DOCUMENTO, "Nuevo documento",
                "Crear un documento de texto vacío");
        nuevoDocumento.setOnAction(evento -> acciones.crearDocumento(this, carpetaActual));

        Button importar = Estilos.botonHerramienta(Iconos.IMPORTAR, "Importar",
                "Copiar imágenes o música desde tu computadora");
        importar.setOnAction(evento -> acciones.importar(this, carpetaActual));

        Button renombrar = Estilos.botonIcono(Iconos.RENOMBRAR, "Renombrar");
        renombrar.setOnAction(evento -> acciones.renombrar(this, seleccionado()));

        Button copiar = Estilos.botonIcono(Iconos.COPIAR, "Copiar");
        copiar.setOnAction(evento -> acciones.copiar(seleccionado()));

        Button cortar = Estilos.botonIcono(Iconos.CORTAR, "Cortar");
        cortar.setOnAction(evento -> acciones.cortar(seleccionado()));

        Button pegar = Estilos.botonIcono(Iconos.PEGAR, "Pegar");
        pegar.setOnAction(evento -> acciones.pegar(this, carpetaActual));
        pegar.disableProperty().bind(portapapeles.contenidoProperty().isNull());

        Button eliminar = Estilos.botonIcono(Iconos.ELIMINAR, "Eliminar");
        eliminar.setOnAction(evento -> acciones.eliminar(this, seleccionado()));

        for (Button boton : List.of(renombrar, copiar, cortar, eliminar)) {
            boton.disableProperty().bind(tabla.getSelectionModel().selectedItemProperty().isNull());
        }

        orden.getItems().setAll(CriterioOrden.values());
        orden.setValue(CriterioOrden.NOMBRE);
        orden.setStyle("-fx-font-family: '" + Estilos.FUENTE + "'; -fx-font-size: 12.5px;");
        orden.valueProperty().addListener((observable, anterior, actual) -> aplicarVista());

        busqueda.setPrefWidth(220);
        busqueda.textProperty().addListener((observable, anterior, actual) -> aplicarVista());

        FlowPane barra = new FlowPane(4, 4, botonAtras, botonAdelante, botonArriba, actualizar,
                Estilos.separadorVertical(),
                nuevaCarpeta, nuevoDocumento, importar, Estilos.separadorVertical(),
                renombrar, copiar, cortar, pegar, eliminar, Estilos.separadorVertical(),
                Estilos.etiqueta("Ordenar por"), orden, busqueda);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setPadding(new Insets(8, 10, 8, 10));
        barra.setStyle(Estilos.BARRA_HERRAMIENTAS);
        return barra;
    }

    private HBox crearBarraRuta() {
        HBox barra = Estilos.fila(8, Iconos.crear(Iconos.CARPETA, 14), rutaActual);
        barra.setPadding(new Insets(6, 12, 6, 12));
        barra.setStyle(Estilos.PANEL_ALTERNO);
        return barra;
    }

    private SplitPane crearContenidoPrincipal() {
        itemRaiz = new ItemCarpeta(archivos.describirCarpeta(raiz), archivos);
        itemRaiz.setExpanded(true);
        arbol.setRoot(itemRaiz);
        arbol.setShowRoot(true);
        arbol.setStyle(Estilos.ARBOL);
        arbol.getSelectionModel().selectedItemProperty().addListener((observable, anterior, actual) -> {
            if (!sincronizandoArbol && actual != null) {
                navegar(actual.getValue().getRuta(), true);
            }
        });

        configurarTabla();

        SplitPane division = new SplitPane(arbol, tabla);
        division.setDividerPositions(0.28);
        SplitPane.setResizableWithParent(arbol, Boolean.FALSE);
        division.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        return division;
    }

    private void configurarTabla() {
        TableColumn<NodoArchivo, String> columnaNombre = new TableColumn<>("Nombre");
        columnaNombre.setCellValueFactory(dato -> new ReadOnlyStringWrapper(dato.getValue().getNombre()));
        columnaNombre.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String valor, boolean vacio) {
                super.updateItem(valor, vacio);
                NodoArchivo nodo = getTableRow() == null ? null : getTableRow().getItem();
                if (vacio || nodo == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                setText(valor);
                setGraphic(Iconos.deTipo(nodo.getTipo(), 16));
                setGraphicTextGap(8);
            }
        });
        columnaNombre.setPrefWidth(320);

        TableColumn<NodoArchivo, String> columnaTipo = new TableColumn<>("Tipo");
        columnaTipo.setCellValueFactory(dato -> new ReadOnlyStringWrapper(dato.getValue().getTipo().getEtiqueta()));
        columnaTipo.setPrefWidth(160);

        TableColumn<NodoArchivo, String> columnaFecha = new TableColumn<>("Modificado");
        columnaFecha.setCellValueFactory(dato -> new ReadOnlyStringWrapper(dato.getValue().modificadoLegible()));
        columnaFecha.setPrefWidth(170);

        TableColumn<NodoArchivo, String> columnaTamano = new TableColumn<>("Tamaño");
        columnaTamano.setCellValueFactory(dato -> new ReadOnlyStringWrapper(dato.getValue().tamanoLegible()));
        columnaTamano.setPrefWidth(100);

        tabla.getColumns().setAll(List.of(columnaNombre, columnaTipo, columnaFecha, columnaTamano));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setPlaceholder(Estilos.leyenda("Esta carpeta está vacía"));
        tabla.setStyle(Estilos.TABLA);
        tabla.setContextMenu(crearMenuContextual());
        tabla.setRowFactory(vista -> {
            TableRow<NodoArchivo> fila = new TableRow<>();
            fila.setOnMouseClicked(evento -> {
                if (evento.getButton() == MouseButton.PRIMARY && evento.getClickCount() == 2 && !fila.isEmpty()) {
                    abrir(fila.getItem());
                }
            });
            return fila;
        });
    }

    private ContextMenu crearMenuContextual() {
        MenuItem abrir = new MenuItem("Abrir");
        abrir.setOnAction(evento -> abrir(seleccionado()));

        MenuItem renombrar = new MenuItem("Renombrar");
        renombrar.setOnAction(evento -> acciones.renombrar(this, seleccionado()));

        MenuItem copiar = new MenuItem("Copiar");
        copiar.setOnAction(evento -> acciones.copiar(seleccionado()));

        MenuItem cortar = new MenuItem("Cortar");
        cortar.setOnAction(evento -> acciones.cortar(seleccionado()));

        MenuItem pegar = new MenuItem("Pegar");
        pegar.setOnAction(evento -> acciones.pegar(this, carpetaActual));
        pegar.disableProperty().bind(portapapeles.contenidoProperty().isNull());

        MenuItem eliminar = new MenuItem("Eliminar");
        eliminar.setOnAction(evento -> acciones.eliminar(this, seleccionado()));

        return new ContextMenu(abrir, renombrar, copiar, cortar, pegar, eliminar);
    }

    private HBox crearBarraEstado() {
        HBox barra = Estilos.fila(12, estado);
        barra.setPadding(new Insets(6, 12, 6, 12));
        barra.setStyle(Estilos.BARRA_ESTADO);
        return barra;
    }

    private void prepararRaiz() {
        try {
            archivos.asegurarCarpeta(raiz);
        } catch (MiniWindowsException error) {
            estado.setText(error.getMessage());
        }
    }

    private boolean navegar(RutaVirtual destino, boolean guardarHistorial) {
        if (!sesion.puedeAcceder(destino)) {
            informarFallo("Acceso denegado", "No tienes permiso para entrar a " + destino.texto());
            return false;
        }
        try {
            ListaEnlazada<NodoArchivo> nuevoContenido = archivos.listar(destino);
            if (guardarHistorial && carpetaActual != null && !carpetaActual.equals(destino)) {
                historialAtras.push(carpetaActual);
                historialAdelante.clear();
            }
            contenido = nuevoContenido;
            carpetaActual = destino;
            rutaActual.setText(destino.texto());
            aplicarVista();
            seleccionarEnArbol(destino);
            actualizarNavegacion();
            return true;
        } catch (MiniWindowsException error) {
            informarFallo("No se pudo abrir la carpeta", error.getMessage());
            actualizarNavegacion();
            return false;
        }
    }

    private void informarFallo(String titulo, String mensaje) {
        if (iniciando) {
            estado.setText(mensaje);
        } else {
            Dialogos.error(this, titulo, mensaje);
        }
    }

    private void irAtras() {
        if (historialAtras.isEmpty()) {
            return;
        }
        RutaVirtual anterior = carpetaActual;
        if (navegar(historialAtras.peek(), false)) {
            historialAtras.pop();
            if (anterior != null) {
                historialAdelante.push(anterior);
            }
        }
        actualizarNavegacion();
    }

    private void irAdelante() {
        if (historialAdelante.isEmpty()) {
            return;
        }
        RutaVirtual anterior = carpetaActual;
        if (navegar(historialAdelante.peek(), false)) {
            historialAdelante.pop();
            if (anterior != null) {
                historialAtras.push(anterior);
            }
        }
        actualizarNavegacion();
    }

    private void irArriba() {
        if (carpetaActual == null || carpetaActual.equals(raiz)) {
            return;
        }
        navegar(carpetaActual.padre(), true);
    }

    private void refrescar() {
        recargarEnArbol(carpetaActual);
        if (carpetaActual != null) {
            navegar(carpetaActual, false);
        }
    }

    private void alCambiarCarpeta(RutaVirtual carpeta) {
        if (Platform.isFxApplicationThread()) {
            aplicarCambioExterno(carpeta);
        } else {
            Platform.runLater(() -> aplicarCambioExterno(carpeta));
        }
    }

    private void aplicarCambioExterno(RutaVirtual carpeta) {
        ItemCarpeta item = itemDe(carpeta, false);
        if (item != null) {
            item.recargar();
        }
        if (carpeta.equals(carpetaActual)) {
            recargarContenido();
        }
    }

    private void recargarContenido() {
        try {
            contenido = archivos.listar(carpetaActual);
            aplicarVista();
        } catch (MiniWindowsException error) {
            estado.setText(error.getMessage());
        }
    }

    private void recargarEnArbol(RutaVirtual carpeta) {
        ItemCarpeta item = itemDe(carpeta, true);
        if (item != null) {
            item.recargar();
        }
    }

    private void abrir(NodoArchivo nodo) {
        if (nodo == null) {
            return;
        }
        if (nodo.esCarpeta()) {
            navegar(nodo.getRuta(), true);
            return;
        }
        contexto.abrirArchivo(this, nodo);
    }

    private void actualizarNavegacion() {
        botonAtras.setDisable(historialAtras.isEmpty());
        botonAdelante.setDisable(historialAdelante.isEmpty());
        botonArriba.setDisable(carpetaActual == null || carpetaActual.equals(raiz));
    }

    private void aplicarVista() {
        if (carpetaActual == null) {
            tabla.getItems().clear();
            return;
        }
        String filtro = busqueda.getText() == null ? "" : busqueda.getText().trim().toLowerCase(Locale.ROOT);
        ListaEnlazada<NodoArchivo> visibles = filtro.isEmpty()
                ? contenido.filtrar(nodo -> true)
                : contenido.filtrar(nodo -> nodo.getNombre().toLowerCase(Locale.ROOT).contains(filtro));
        visibles.ordenar(orden.getValue().comparador());
        tabla.getItems().setAll(visibles.aLista());
        estado.setText(visibles.tamano() + " elemento(s) en " + carpetaActual.texto());
    }

    private void seleccionarEnArbol(RutaVirtual destino) {
        sincronizandoArbol = true;
        try {
            ItemCarpeta item = itemDe(destino, true);
            if (item != null) {
                arbol.getSelectionModel().select(item);
            } else {
                arbol.getSelectionModel().clearSelection();
            }
        } finally {
            sincronizandoArbol = false;
        }
    }

    private ItemCarpeta itemDe(RutaVirtual destino, boolean expandiendo) {
        if (destino == null || itemRaiz == null) {
            return null;
        }
        ItemCarpeta actual = itemRaiz;
        List<String> segmentos = destino.segmentos();
        for (int posicion = raiz.profundidad(); posicion < segmentos.size(); posicion++) {
            if (expandiendo) {
                actual.cargarHijos();
                actual.setExpanded(true);
            }
            ItemCarpeta hijo = hijoLlamado(actual, segmentos.get(posicion));
            if (hijo == null) {
                return null;
            }
            actual = hijo;
        }
        return actual;
    }

    private ItemCarpeta hijoLlamado(ItemCarpeta padre, String nombre) {
        for (TreeItem<NodoArchivo> hijo : padre.getChildren()) {
            if (hijo instanceof ItemCarpeta carpeta && carpeta.getValue().getNombre().equalsIgnoreCase(nombre)) {
                return carpeta;
            }
        }
        return null;
    }

    private NodoArchivo seleccionado() {
        return tabla.getSelectionModel().getSelectedItem();
    }
}
