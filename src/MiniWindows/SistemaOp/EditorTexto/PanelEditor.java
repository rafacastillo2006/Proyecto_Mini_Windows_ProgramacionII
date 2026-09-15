package MiniWindows.SistemaOp.EditorTexto;

import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Modelo.TipoArchivo;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import MiniWindows.SistemaOp.Archivos.RutaVirtual;
import MiniWindows.SistemaOp.Archivos.SistemaArchivos;
import MiniWindows.SistemaOp.Escritorio.Dialogos;
import MiniWindows.SistemaOp.Escritorio.Estilos;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import MiniWindows.SistemaOp.Nucleo.Sesion;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;

import java.nio.charset.StandardCharsets;

public class PanelEditor extends BorderPane {

    private static final String NOMBRE_NUEVO = "Documento nuevo.txt";
    private static final String CUERPO_VACIO = "<html><body contenteditable=\"true\"></body></html>";

    private final SistemaArchivos archivos;
    private final Sesion sesion;
    private final HTMLEditor editor = new HTMLEditor();
    private final Label estado = Estilos.leyenda("");

    private NodoArchivo archivoActual;
    private boolean sucio;

    public PanelEditor(ContextoApp contexto, NodoArchivo archivo) {
        this.archivos = contexto.getArchivos();
        this.sesion = contexto.getSesion();

        setTop(barraHerramientas());
        setCenter(editor);
        editor.addEventFilter(KeyEvent.KEY_PRESSED, evento -> sucio = true);
        editor.addEventFilter(MouseEvent.MOUSE_CLICKED, evento -> sucio = true);
        setBottom(barraEstado());
        setStyle(Estilos.PANEL);

        if (archivo != null) {
            cargar(archivo);
        } else {
            documentoNuevo();
        }
    }

    private FlowPane barraHerramientas() {
        Button nuevo = Estilos.botonHerramienta(Iconos.NUEVO_DOCUMENTO, "Nuevo", "Empezar un documento vacío");
        nuevo.setOnAction(evento -> {
            if (confirmarDescartar()) {
                documentoNuevo();
            }
        });

        Button abrir = Estilos.botonHerramienta(Iconos.CARPETA, "Abrir", "Abrir un documento de Z:");
        abrir.setOnAction(evento -> abrirDesdeDialogo());

        Button guardar = Estilos.botonHerramienta(Iconos.COPIAR, "Guardar", "Guardar los cambios");
        guardar.setOnAction(evento -> guardar());

        Button guardarComo = Estilos.botonHerramienta(Iconos.PEGAR, "Guardar como", "Guardar con otro nombre");
        guardarComo.setOnAction(evento -> guardarComo());

        Button tabla = Estilos.botonHerramienta(Iconos.TABLA, "Insertar tabla", "Insertar una tabla en el texto");
        tabla.setOnAction(evento -> insertarTabla());

        FlowPane barra = new FlowPane(4, 4, nuevo, abrir, guardar, guardarComo,
                Estilos.separadorVertical(), tabla);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setPadding(new Insets(8, 10, 8, 10));
        barra.setStyle(Estilos.BARRA_HERRAMIENTAS);
        return barra;
    }

    private HBox barraEstado() {
        HBox barra = Estilos.fila(12, estado);
        barra.setPadding(new Insets(6, 12, 6, 12));
        barra.setStyle(Estilos.BARRA_ESTADO);
        return barra;
    }

    private void documentoNuevo() {
        archivoActual = null;
        editor.setHtmlText(CUERPO_VACIO);
        sucio = false;
        actualizarEstado("Documento nuevo sin guardar");
    }

    private void abrirDesdeDialogo() {
        if (!confirmarDescartar()) {
            return;
        }
        new DialogoArchivo(this, archivos, sesion, DialogoArchivo.Modo.ABRIR, null)
                .showAndWait()
                .ifPresent(resultado -> cargar(resultado.archivo()));
    }

    private void cargar(NodoArchivo archivo) {
        try {
            String texto = archivos.abrir(archivo).comoTexto();
            editor.setHtmlText(texto.isBlank() ? CUERPO_VACIO : comoHtml(texto));
            archivoActual = archivo;
            sucio = false;
            actualizarEstado("Abierto");
        } catch (MiniWindowsException error) {
            Dialogos.error(this, "No se pudo abrir", error.getMessage());
        }
    }

    private void guardar() {
        if (archivoActual == null || !archivos.existe(archivoActual)) {
            guardarComo();
            return;
        }
        try {
            archivos.guardarContenido(archivoActual, editor.getHtmlText().getBytes(StandardCharsets.UTF_8));
            sucio = false;
            actualizarEstado("Guardado");
        } catch (MiniWindowsException error) {
            Dialogos.error(this, "No se pudo guardar", error.getMessage());
        }
    }

    private void guardarComo() {
        String sugerido = archivoActual == null ? NOMBRE_NUEVO : archivoActual.getNombre();
        new DialogoArchivo(this, archivos, sesion, DialogoArchivo.Modo.GUARDAR, sugerido)
                .showAndWait()
                .ifPresent(resultado -> escribirEn(resultado.carpeta(), resultado.nombre()));
    }

    private void escribirEn(RutaVirtual carpeta, String nombre) {
        byte[] contenido = editor.getHtmlText().getBytes(StandardCharsets.UTF_8);
        try {
            NodoArchivo existente = archivos.listar(carpeta)
                    .buscar(nodo -> !nodo.esCarpeta() && nodo.getNombre().equalsIgnoreCase(nombre));
            if (existente != null && existente.getTipo() != TipoArchivo.TEXTO) {
                Dialogos.error(this, "No se puede guardar",
                        "\"" + nombre + "\" ya existe y no es un documento de texto.");
                return;
            }
            if (existente != null) {
                if (!Dialogos.confirmar(this, "Reemplazar",
                        "Ya existe \"" + nombre + "\" en esa carpeta. ¿Reemplazarlo?")) {
                    return;
                }
                archivos.guardarContenido(existente, contenido);
                archivoActual = existente;
            } else {
                archivoActual = archivos.crearArchivo(carpeta, nombre, TipoArchivo.TEXTO, contenido);
            }
            sucio = false;
            actualizarEstado("Guardado");
        } catch (MiniWindowsException error) {
            Dialogos.error(this, "No se pudo guardar", error.getMessage());
        }
    }

    private void insertarTabla() {
        Dialog<int[]> dialogo = new Dialog<>();
        Dialogos.preparar(dialogo, this, "Insertar tabla", null);
        Spinner<Integer> filas = new Spinner<>(1, 20, 3);
        Spinner<Integer> columnas = new Spinner<>(1, 10, 3);
        GridPane formulario = new GridPane();
        formulario.setHgap(12);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(8, 4, 4, 4));
        formulario.addRow(0, Estilos.etiqueta("Filas"), filas);
        formulario.addRow(1, Estilos.etiqueta("Columnas"), columnas);
        dialogo.getDialogPane().setContent(formulario);
        dialogo.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        dialogo.setResultConverter(boton -> boton == ButtonType.OK
                ? new int[]{filas.getValue(), columnas.getValue()} : null);
        dialogo.showAndWait().ifPresent(medidas -> insertarHtml(htmlDeTabla(medidas[0], medidas[1])));
    }

    private String htmlDeTabla(int filas, int columnas) {
        StringBuilder html = new StringBuilder(
                "<table style=\"border-collapse:collapse;width:100%\">");
        for (int fila = 0; fila < filas; fila++) {
            html.append("<tr>");
            for (int columna = 0; columna < columnas; columna++) {
                html.append("<td style=\"border:1px solid #9aa0a6;padding:6px\">&nbsp;</td>");
            }
            html.append("</tr>");
        }
        return html.append("</table><p>&nbsp;</p>").toString();
    }

    private void insertarHtml(String html) {
        WebView vista = webView();
        if (vista == null) {
            Dialogos.error(this, "Editor no disponible", "No se pudo acceder al área de edición.");
            return;
        }
        editor.requestFocus();
        String escapado = html.replace("\\", "\\\\").replace("'", "\\'");
        Platform.runLater(() -> {
            vista.getEngine().executeScript("document.body.focus();"
                    + "document.execCommand('insertHTML', false, '" + escapado + "')");
            sucio = true;
            actualizarEstado("Tabla insertada");
        });
    }

    private WebView webView() {
        return editor.lookup("WebView") instanceof WebView vista ? vista : null;
    }

    private String comoHtml(String texto) {
        String limpio = texto.trim().toLowerCase();
        if (limpio.startsWith("<html") || limpio.contains("<body")) {
            return texto;
        }
        String escapado = texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\n", "<br>");
        return "<html><body contenteditable=\"true\"><p>" + escapado + "</p></body></html>";
    }

    private boolean confirmarDescartar() {
        if (!hayCambios()) {
            return true;
        }
        return Dialogos.confirmar(this, "Cambios sin guardar",
                "El documento tiene cambios sin guardar. ¿Descartarlos?");
    }

    private boolean hayCambios() {
        return sucio;
    }

    private void actualizarEstado(String accion) {
        estado.setText(archivoActual == null
                ? accion
                : accion + ": " + archivoActual.getRuta().texto());
    }
}
