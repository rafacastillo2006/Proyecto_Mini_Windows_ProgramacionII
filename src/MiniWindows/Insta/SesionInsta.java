package MiniWindows.Insta;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Persistencia.GestorBinario;
import MiniWindows.Util.Rutas;

public class SesionInsta {

    private final String usuarioWindows;
    private UsuarioInsta usuarioActual;

    public SesionInsta(String usuarioWindows) {
        this.usuarioWindows = usuarioWindows;
    }

    public String getUsuarioWindows() {
        return usuarioWindows;
    }

    public UsuarioInsta getUsuarioActual() {
        return usuarioActual;
    }

    public boolean hayCuentaAbierta() {
        return usuarioActual != null;
    }

    public void abrir(UsuarioInsta usuario) {
        this.usuarioActual = usuario;
        guardarRecordatorio(usuario.getUsername());
    }

    public void cerrar() {
        this.usuarioActual = null;
        guardarRecordatorio(null);
    }

    public String getCuentaRecordada() {
        for (SesionGuardada guardada : leerSesiones()) {
            if (guardada.usuarioWindows().equalsIgnoreCase(usuarioWindows)) {
                return guardada.usuarioInsta();
            }
        }
        return null;
    }

    private void guardarRecordatorio(String usuarioInsta) {
        ListaEnlazada<SesionGuardada> restantes = new ListaEnlazada<>();
        for (SesionGuardada guardada : leerSesiones()) {
            if (!guardada.usuarioWindows().equalsIgnoreCase(usuarioWindows)) {
                restantes.agregar(guardada);
            }
        }
        if (usuarioInsta != null) {
            restantes.agregar(new SesionGuardada(usuarioWindows, usuarioInsta));
        }
        try {
            GestorBinario.guardar(Rutas.getSesiones(), restantes);
        } catch (MiniWindowsException error) {
            System.err.println("No se pudo recordar la sesión de INSTA+: " + error.getMessage());
        }
    }

    private ListaEnlazada<SesionGuardada> leerSesiones() {
        try {
            return GestorBinario.cargar(Rutas.getSesiones(), SesionGuardada.class);
        } catch (MiniWindowsException error) {
            return new ListaEnlazada<>();
        }
    }
}
