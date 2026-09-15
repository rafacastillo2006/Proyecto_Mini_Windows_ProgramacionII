package MiniWindows.Insta.Logica;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Persistencia.GestorBinario;
import MiniWindows.Excepciones.UsernameDuplicadoException;
import MiniWindows.Excepciones.CuentaDesactivadaException;
import MiniWindows.Util.Rutas;

public class GestorUsuariosIG {
    private GestorBinario<UsuarioInsta> gestorBinario;

    public GestorUsuariosIG() {
        this.gestorBinario = new GestorBinario<>(Rutas.USERS_FILE);    }

    public UsuarioInsta autenticar(String username, String password) throws CuentaDesactivadaException {
        ListaEnlazada<UsuarioInsta> usuarios = gestorBinario.leerLista();
        for (int i = 0; i < usuarios.getTamano(); i++) {
            UsuarioInsta u = usuarios.obtener(i);
            if (u.getUsername().equalsIgnoreCase(username) && u.getPassword().equals(password)) {
                if (!u.isActiva()) {
                    throw new CuentaDesactivadaException("La cuenta se encuentra desactivada.");
                }
                return u;
            }
        }
        return null;
    }

    public boolean registrar(UsuarioInsta nuevo) throws UsernameDuplicadoException {
        ListaEnlazada<UsuarioInsta> usuarios = gestorBinario.leerLista();
        for (int i = 0; i < usuarios.getTamano(); i++) {
            if (usuarios.obtener(i).getUsername().equalsIgnoreCase(nuevo.getUsername())) {
                throw new UsernameDuplicadoException("El usuario @" + nuevo.getUsername() + " ya existe.");
            }
        }
        usuarios.agregar(nuevo);
        gestorBinario.guardarLista(usuarios);
        Rutas.crearEstructuraUsuario(nuevo.getUsername());
        return true;
    }
}