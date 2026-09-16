package MiniWindows.Insta;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Persistencia.GestorBinario;
import MiniWindows.Util.Rutas;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SesionInsta {

    private static final Set<String> CUENTAS_ABIERTAS = ConcurrentHashMap.newKeySet();

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

    public static boolean estaAbierta(String username) {
        return username != null && CUENTAS_ABIERTAS.contains(username.toLowerCase(Locale.ROOT));
    }

    public void abrir(UsuarioInsta usuario) {
        liberar();
        this.usuarioActual = usuario;
        CUENTAS_ABIERTAS.add(usuario.getUsername().toLowerCase(Locale.ROOT));
        guardarRecordatorio(usuario.getUsername());
    }

    public void cerrar() {
        liberar();
        this.usuarioActual = null;
        guardarRecordatorio(null);
    }

    public void liberar() {
        if (usuarioActual != null) {
            CUENTAS_ABIERTAS.remove(usuarioActual.getUsername().toLowerCase(Locale.ROOT));
        }
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
