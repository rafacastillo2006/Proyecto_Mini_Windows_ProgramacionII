package MiniWindows.SistemaOp.Escritorio;

import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Modelo.TipoArchivo;
import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import MiniWindows.SistemaOp.Archivos.RutaVirtual;
import MiniWindows.SistemaOp.Archivos.SistemaArchivos;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class AccionesArchivos {

    public static final String NOMBRE_CARPETA_NUEVA = "Nueva carpeta";
    public static final String NOMBRE_DOCUMENTO_NUEVO = "Nuevo documento.txt";

    private final SistemaArchivos archivos;
    private final PortapapelesArchivos portapapeles;

    public AccionesArchivos(SistemaArchivos archivos, PortapapelesArchivos portapapeles) {
        this.archivos = archivos;
        this.portapapeles = portapapeles;
    }

    public PortapapelesArchivos getPortapapeles() {
        return portapapeles;
    }

    public void crearCarpeta(Node origen, RutaVirtual carpeta) {
        Dialogos.pedirTexto(origen, "Nueva carpeta", "Nombre de la carpeta", NOMBRE_CARPETA_NUEVA)
                .ifPresent(nombre -> ejecutar(origen, () -> archivos.crearCarpeta(carpeta, nombre)));
    }

    public void crearDocumento(Node origen, RutaVirtual carpeta) {
        Dialogos.pedirTexto(origen, "Nuevo documento", "Nombre del documento", NOMBRE_DOCUMENTO_NUEVO)
                .ifPresent(nombre -> ejecutar(origen,
                        () -> archivos.crearArchivo(carpeta, nombre, TipoArchivo.TEXTO, new byte[0])));
    }

    public void importar(Node origen, RutaVirtual carpeta) {
        if (carpeta == null) {
            return;
        }
        FileChooser selector = new FileChooser();
        selector.setTitle("Importar archivos a " + carpeta.texto());
        selector.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes y música",
                        "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.mp3", "*.wav", "*.m4a"),
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("Música", "*.mp3", "*.wav", "*.m4a"));
        Window ventana = origen.getScene() == null ? null : origen.getScene().getWindow();
        List<File> elegidos = selector.showOpenMultipleDialog(ventana);
        if (elegidos == null || elegidos.isEmpty()) {
            return;
        }
        int copiadas = 0;
        for (File archivo : elegidos) {
            try {
                byte[] datos = Files.readAllBytes(archivo.toPath());
                String nombre = archivos.nombreLibre(carpeta, archivo.getName(), false);
                archivos.crearArchivo(carpeta, nombre, TipoArchivo.desdeNombre(nombre), datos);
                copiadas++;
            } catch (IOException | MiniWindowsException error) {
                Dialogos.error(origen, "No se pudo importar", archivo.getName() + ": " + error.getMessage());
            }
        }
        if (copiadas > 0) {
            Dialogos.informacion(origen, "Archivos importados",
                    copiadas + " archivo(s) copiados a " + carpeta.texto());
        }
    }

    public void renombrar(Node origen, NodoArchivo nodo) {
        if (nodo == null) {
            return;
        }
        Dialogos.pedirTexto(origen, "Renombrar", "Nuevo nombre para \"" + nodo.getNombre() + "\"", nodo.getNombre())
                .ifPresent(nombre -> ejecutar(origen, () -> archivos.renombrar(nodo, nombre)));
    }

    public void copiar(NodoArchivo nodo) {
        if (nodo != null) {
            portapapeles.copiar(nodo);
        }
    }

    public void cortar(NodoArchivo nodo) {
        if (nodo != null) {
            portapapeles.cortar(nodo);
        }
    }

    public void pegar(Node origen, RutaVirtual destino) {
        NodoArchivo nodo = portapapeles.getContenido();
        if (nodo == null || destino == null) {
            return;
        }
        ejecutar(origen, () -> {
            if (portapapeles.esCorte()) {
                archivos.mover(nodo, destino);
                portapapeles.limpiar();
            } else {
                archivos.copiar(nodo, destino);
            }
        });
    }

    public void eliminar(Node origen, NodoArchivo nodo) {
        if (nodo == null) {
            return;
        }
        String mensaje = nodo.esCarpeta()
                ? "Se eliminará la carpeta \"" + nodo.getNombre() + "\" y todo su contenido. ¿Continuar?"
                : "¿Eliminar \"" + nodo.getNombre() + "\"?";
        if (!Dialogos.confirmar(origen, "Eliminar", mensaje)) {
            return;
        }
        ejecutar(origen, () -> {
            archivos.eliminar(nodo);
            if (nodo.equals(portapapeles.getContenido())) {
                portapapeles.limpiar();
            }
        });
    }

    private void ejecutar(Node origen, Operacion operacion) {
        try {
            operacion.ejecutar();
        } catch (MiniWindowsException error) {
            Dialogos.error(origen, "Operación no completada", error.getMessage());
        } catch (RuntimeException error) {
            Dialogos.error(origen, "Error inesperado", String.valueOf(error.getMessage()));
        }
    }

    @FunctionalInterface
    private interface Operacion {
        void ejecutar() throws MiniWindowsException;
    }
}
