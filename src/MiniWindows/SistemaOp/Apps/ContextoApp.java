package MiniWindows.SistemaOp.Apps;

import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import MiniWindows.SistemaOp.Archivos.SistemaArchivos;
import MiniWindows.SistemaOp.Cuentas.ServicioCuentas;
import MiniWindows.SistemaOp.Escritorio.AccionesArchivos;
import MiniWindows.SistemaOp.Escritorio.Dialogos;
import MiniWindows.SistemaOp.Escritorio.GestorVentanas;
import MiniWindows.SistemaOp.Nucleo.Sesion;
import javafx.scene.Node;

public class ContextoApp {

    private final Sesion sesion;
    private final SistemaArchivos archivos;
    private final ServicioCuentas cuentas;
    private final GestorVentanas ventanas;
    private final RegistroAplicaciones aplicaciones;
    private final AccionesArchivos acciones;

    public ContextoApp(Sesion sesion, SistemaArchivos archivos, ServicioCuentas cuentas,
                       GestorVentanas ventanas, RegistroAplicaciones aplicaciones, AccionesArchivos acciones) {
        this.sesion = sesion;
        this.archivos = archivos;
        this.cuentas = cuentas;
        this.ventanas = ventanas;
        this.aplicaciones = aplicaciones;
        this.acciones = acciones;
    }

    public void abrir(Aplicacion aplicacion) {
        ventanas.abrir(aplicacion);
    }

    public void abrirArchivo(Node origen, NodoArchivo archivo) {
        Aplicacion aplicacion = aplicaciones.disponiblesPara(sesion)
                .buscar(candidata -> candidata.puedeAbrir(archivo));
        if (aplicacion == null) {
            Dialogos.informacion(origen, archivo.getNombre(),
                    "Tipo: " + archivo.getTipo().getEtiqueta()
                            + "\nTamaño: " + archivo.tamanoLegible()
                            + "\nModificado: " + archivo.modificadoLegible()
                            + "\n\nTodavía no hay una aplicación instalada para este tipo de archivo.");
            return;
        }
        ventanas.abrir(aplicacion, archivo);
    }

    public Sesion getSesion() {
        return sesion;
    }

    public SistemaArchivos getArchivos() {
        return archivos;
    }

    public ServicioCuentas getCuentas() {
        return cuentas;
    }

    public GestorVentanas getVentanas() {
        return ventanas;
    }

    public RegistroAplicaciones getAplicaciones() {
        return aplicaciones;
    }

    public AccionesArchivos getAcciones() {
        return acciones;
    }
}
