package MiniWindows.Insta.Logica;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.CuentaDesactivadaException;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Excepciones.UsernameDuplicadoException;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.SistemaOp.Cuentas.Contrasenas;
import MiniWindows.Util.Rutas;

import java.util.Locale;

public class GestorUsuariosIG {

    public ListaEnlazada<UsuarioInsta> todos() {
        return AlmacenInsta.leer(Rutas.getUsers(), UsuarioInsta.class);
    }

    public ListaEnlazada<UsuarioInsta> activos() {
        return todos().filtrar(UsuarioInsta::estaActiva);
    }

    public UsuarioInsta buscarPorUsername(String username) {
        if (username == null) {
            return null;
        }
        return todos().buscar(usuario -> usuario.getUsername().equalsIgnoreCase(username));
    }

    public UsuarioInsta autenticar(String username, String clave) throws CuentaDesactivadaException {
        UsuarioInsta usuario = buscarPorUsername(username);
        if (usuario == null || !Contrasenas.coincide(clave, usuario.getClave())) {
            return null;
        }
        if (!usuario.estaActiva()) {
            throw new CuentaDesactivadaException("La cuenta @" + usuario.getUsername() + " esta desactivada.");
        }
        return usuario;
    }

    public void registrar(UsuarioInsta nuevo) throws MiniWindowsException {
        ListaEnlazada<UsuarioInsta> usuarios = todos();
        if (usuarios.buscar(usuario -> usuario.getUsername().equalsIgnoreCase(nuevo.getUsername())) != null) {
            throw new UsernameDuplicadoException("El usuario @" + nuevo.getUsername() + " ya existe.");
        }
        nuevo.setClave(Contrasenas.cifrar(nuevo.getClave()));
        usuarios.agregar(nuevo);
        AlmacenInsta.escribir(Rutas.getUsers(), usuarios);
        Rutas.crearEstructuraDe(nuevo.getUsername());
    }

    public void actualizar(UsuarioInsta cambiado) throws MiniWindowsException {
        ListaEnlazada<UsuarioInsta> usuarios = todos();
        for (int i = 0; i < usuarios.tamano(); i++) {
            if (usuarios.obtener(i).getUsername().equalsIgnoreCase(cambiado.getUsername())) {
                usuarios.reemplazar(i, cambiado);
                AlmacenInsta.escribir(Rutas.getUsers(), usuarios);
                return;
            }
        }
    }

    public void cambiarEstado(String username, boolean activa) throws MiniWindowsException {
        UsuarioInsta usuario = buscarPorUsername(username);
        if (usuario != null) {
            usuario.setActiva(activa);
            actualizar(usuario);
        }
    }

    public ListaEnlazada<UsuarioInsta> buscar(String criterio) {
        String texto = criterio == null ? "" : criterio.trim().toLowerCase(Locale.ROOT);
        if (texto.isEmpty()) {
            return activos();
        }
        return activos().filtrar(usuario ->
                usuario.getUsername().toLowerCase(Locale.ROOT).contains(texto)
                        || usuario.getNombreCompleto().toLowerCase(Locale.ROOT).contains(texto));
    }
}
