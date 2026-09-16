package MiniWindows.SistemaOp.Explorador;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Modelo.TipoArchivo;
import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import MiniWindows.SistemaOp.Archivos.RutaVirtual;
import MiniWindows.SistemaOp.Archivos.SistemaArchivos;
import javafx.application.Platform;

import java.util.function.Consumer;

public class OrganizadorArchivos {

    public static final String NOMBRE_HILO = "MiniWindows-Organizador";

    private final SistemaArchivos archivos;

    public OrganizadorArchivos(SistemaArchivos archivos) {
        this.archivos = archivos;
    }

    public Thread organizar(RutaVirtual carpeta, Consumer<Resultado> alTerminar) {
        Thread hilo = new Thread(() -> {
            Resultado resultado = clasificar(carpeta);
            Platform.runLater(() -> alTerminar.accept(resultado));
        }, NOMBRE_HILO);
        hilo.setDaemon(true);
        hilo.start();
        return hilo;
    }

    public Resultado clasificar(RutaVirtual carpeta) {
        int movidos = 0;
        try {
            for (NodoArchivo nodo : porMover(carpeta)) {
                String destino = nodo.getTipo().getCarpetaSugerida();
                RutaVirtual subcarpeta = carpeta.hijo(destino);
                if (!archivos.existeCarpeta(subcarpeta)) {
                    archivos.crearCarpeta(carpeta, destino);
                }
                archivos.mover(nodo, subcarpeta);
                movidos++;
            }
            return new Resultado(movidos, null);
        } catch (MiniWindowsException error) {
            return new Resultado(movidos, error.getMessage());
        }
    }

    private ListaEnlazada<NodoArchivo> porMover(RutaVirtual carpeta) throws MiniWindowsException {
        return archivos.listar(carpeta).filtrar(nodo -> !nodo.esCarpeta()
                && nodo.getTipo().tieneCarpetaSugerida()
                && !carpeta.nombre().equalsIgnoreCase(nodo.getTipo().getCarpetaSugerida()));
    }

    public record Resultado(int movidos, String error) {

        public boolean correcto() {
            return error == null;
        }

        public String mensaje() {
            if (!correcto()) {
                return error;
            }
            return movidos == 0
                    ? "No habia archivos sueltos que ordenar"
                    : "Se ordenaron " + movidos + " archivo(s) en sus carpetas";
        }
    }
}
