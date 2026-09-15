package MiniWindows.SistemaOp.Apps;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.SistemaOp.Nucleo.Sesion;

public class RegistroAplicaciones {

    private final ListaEnlazada<Aplicacion> aplicaciones = new ListaEnlazada<>();

    public void registrar(Aplicacion aplicacion) {
        aplicaciones.agregar(aplicacion);
    }

    public ListaEnlazada<Aplicacion> disponiblesPara(Sesion sesion) {
        return aplicaciones.filtrar(aplicacion -> aplicacion.disponiblePara(sesion));
    }

    public Aplicacion porId(String id) {
        return aplicaciones.buscar(aplicacion -> aplicacion.id().equals(id));
    }
}
