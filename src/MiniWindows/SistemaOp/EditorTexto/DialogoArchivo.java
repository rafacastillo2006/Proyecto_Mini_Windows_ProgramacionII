package MiniWindows.SistemaOp.EditorTexto;

import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Modelo.TipoArchivo;
import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import MiniWindows.SistemaOp.Archivos.RutaVirtual;
import MiniWindows.SistemaOp.Archivos.SistemaArchivos;
import MiniWindows.SistemaOp.Escritorio.Dialogos;
import MiniWindows.SistemaOp.Escritorio.Estilos;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import MiniWindows.SistemaOp.Nucleo.Sesion;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DialogoArchivo extends Dialog<DialogoArchivo.Resultado> {

    public enum Modo {
        ABRIR,
        GUARDAR
    }

    public record Resultado(RutaVirtual carpeta, String nombre, NodoArchivo archivo) {
    }

    private final SistemaArchivos archivos;
    private final Sesion sesion;
    private final Modo modo;
    private final ListView<NodoArchivo> lista = new ListView<>();
    private final Label ruta = Estilos.leyenda("");
    private final TextField nombre = Estilos.campo("Nombre del documento");

    private RutaVirtual carpetaActual;

    public DialogoArchivo(Node origen, SistemaArchivos archivos, Sesion sesion, Modo modo, String nombreSugerido) {
        this.archivos = archivos;
        this.sesion = sesion;
        this.modo = modo;

        Dialogos.preparar(this, origen, modo == Modo.ABRIR ? "Abrir documento" : "Guardar como", null);
        getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().setContent(contenido());
        getDialogPane().setPrefSize(520, 420);
        nombre.setText(nombreSugerido == null ? "" : nombreSugerido);

        setResultConverter(boton -> boton == ButtonType.OK ? construir() : null);
        getDialogPane().lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION, evento -> {
            if (construir() == null) {
                evento.consume();
            }
        });
        navegar(sesion.getEscritorio());
    }

    private BorderPane contenido() {
        Button subir = Estilos.botonIcono(Iconos.ARRIBA, "Subir un nivel");
        subir.setOnAction(evento -> {
            if (carpetaActual != null && !carpetaActual.equals(sesion.getRaiz())) {
                navegar(carpetaActual.padre());
            }
        });
        HBox cabecera = Estilos.fila(8, subir, ruta);
        cabecera.setPadding(new Insets(0, 0, 8, 0));

        lista.setCellFactory(vista -> new ListCell<>() {
            @Override
            protected void updateItem(NodoArchivo nodo, boolean vacio) {
                super.updateItem(nodo, vacio);
                setText(vacio || nodo == null ? null : nodo.getNombre());
                setGraphic(vacio || nodo == null ? null : Iconos.deTipo(nodo.getTipo(), 16));
            }
        });
        lista.setOnMouseClicked(evento -> {
            NodoArchivo seleccion = lista.getSelectionModel().getSelectedItem();
            if (seleccion == null) {
                return;
            }
            if (evento.getClickCount() == 2 && seleccion.esCarpeta()) {
                navegar(seleccion.getRuta());
            } else if (!seleccion.esCarpeta()) {
                nombre.setText(seleccion.getNombre());
            }
        });

        BorderPane panel = new BorderPane(lista);
        panel.setTop(cabecera);
        if (modo == Modo.GUARDAR) {
            VBox pie = new VBox(6, Estilos.etiquetaCampo("Nombre"), nombre);
            pie.setPadding(new Insets(10, 0, 0, 0));
            panel.setBottom(pie);
        }
        return panel;
    }

    private void navegar(RutaVirtual destino) {
        if (!sesion.puedeAcceder(destino)) {
            return;
        }
        try {
            lista.getItems().setAll(archivos.listar(destino)
                    .filtrar(nodo -> nodo.esCarpeta() || nodo.getTipo() == TipoArchivo.TEXTO)
                    .aLista());
            carpetaActual = destino;
            ruta.setText(destino.texto());
        } catch (MiniWindowsException error) {
            ruta.setText(error.getMessage());
            lista.getItems().clear();
        }
    }

    private Resultado construir() {
        NodoArchivo seleccion = lista.getSelectionModel().getSelectedItem();
        if (modo == Modo.ABRIR) {
            return seleccion == null || seleccion.esCarpeta()
                    ? null
                    : new Resultado(carpetaActual, seleccion.getNombre(), seleccion);
        }
        String texto = nombre.getText() == null ? "" : nombre.getText().trim();
        if (texto.isEmpty() || carpetaActual == null) {
            return null;
        }
        return new Resultado(carpetaActual, texto, null);
    }
}
