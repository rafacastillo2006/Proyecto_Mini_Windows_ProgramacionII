package MiniWindows.SistemaOp.Explorador;

import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Modelo.TipoArchivo;
import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import MiniWindows.SistemaOp.Archivos.SistemaArchivos;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import javafx.scene.control.TreeItem;

public class ItemCarpeta extends TreeItem<NodoArchivo> {

    private final SistemaArchivos archivos;

    private boolean cargado;

    public ItemCarpeta(NodoArchivo carpeta, SistemaArchivos archivos) {
        super(carpeta, Iconos.deTipo(TipoArchivo.CARPETA, 16));
        this.archivos = archivos;
        expandedProperty().addListener((observable, anterior, expandido) -> {
            if (expandido) {
                cargarHijos();
            }
        });
    }

    public void cargarHijos() {
        if (cargado) {
            return;
        }
        cargado = true;
        try {
            for (NodoArchivo subcarpeta : archivos.listarCarpetas(getValue().getRuta())) {
                getChildren().add(new ItemCarpeta(subcarpeta, archivos));
            }
        } catch (MiniWindowsException error) {
            getChildren().clear();
        }
    }

    public void recargar() {
        boolean expandido = isExpanded();
        getChildren().clear();
        cargado = false;
        if (expandido) {
            cargarHijos();
        }
    }

    @Override
    public boolean isLeaf() {
        return cargado && getChildren().isEmpty();
    }
}
