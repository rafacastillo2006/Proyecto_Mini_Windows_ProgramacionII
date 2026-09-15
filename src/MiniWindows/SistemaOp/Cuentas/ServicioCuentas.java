package MiniWindows.SistemaOp.Cuentas;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.CredencialesInvalidasException;
import MiniWindows.Excepciones.CuentaDesactivadaException;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Excepciones.PermisoDenegadoException;
import MiniWindows.Excepciones.UsernameDuplicadoException;
import MiniWindows.Modelo.Rol;
import MiniWindows.Modelo.Usuario;
import MiniWindows.SistemaOp.Archivos.SistemaArchivos;
import MiniWindows.Util.Fechas;
import MiniWindows.Util.Validador;

import java.util.Locale;

public class ServicioCuentas {

    public static final String ADMINISTRADOR_POR_DEFECTO = "admin";
    public static final String CONTRASENA_POR_DEFECTO = "admin";

    private final RepositorioUsuarios repositorio;
    private final SistemaArchivos sistemaArchivos;
    private final ListaEnlazada<Usuario> usuarios = new ListaEnlazada<>();

    private boolean primerArranque;

    public ServicioCuentas(RepositorioUsuarios repositorio, SistemaArchivos sistemaArchivos) {
        this.repositorio = repositorio;
        this.sistemaArchivos = sistemaArchivos;
    }

    public synchronized void inicializar() throws MiniWindowsException {
        usuarios.vaciar();
        usuarios.agregarTodos(repositorio.cargar());
        primerArranque = usuarios.estaVacia();
        if (primerArranque) {
            registrar(new SolicitudUsuario("Administrador del sistema", 'M', ADMINISTRADOR_POR_DEFECTO,
                    CONTRASENA_POR_DEFECTO, 18, Rol.ADMINISTRADOR));
            repositorio.guardar(usuarios);
        }
        for (Usuario usuario : usuarios) {
            sistemaArchivos.crearEspacioDeUsuario(usuario.getUsername());
        }
    }

    public synchronized Usuario iniciarSesion(String username, String contrasena) throws MiniWindowsException {
        Usuario usuario = buscar(username);
        if (usuario == null || !Contrasenas.coincide(contrasena, usuario.getHashContrasena())) {
            throw new CredencialesInvalidasException();
        }
        if (!usuario.estaActiva()) {
            throw new CuentaDesactivadaException(usuario.getUsername());
        }
        primerArranque = false;
        return usuario;
    }

    public synchronized Usuario crearUsuario(SolicitudUsuario solicitud) throws MiniWindowsException {
        validar(solicitud);
        if (buscar(solicitud.username()) != null) {
            throw new UsernameDuplicadoException(solicitud.username());
        }
        Usuario creado = registrar(solicitud);
        try {
            repositorio.guardar(usuarios);
        } catch (MiniWindowsException error) {
            usuarios.eliminar(creado);
            throw error;
        }
        return creado;
    }

    public synchronized void cambiarEstado(Usuario usuario, boolean activa, Usuario solicitante)
            throws MiniWindowsException {
        exigirOtraCuenta(usuario, solicitante, "No puedes cambiar el estado de tu propia cuenta");
        if (!activa && esUltimoAdministradorActivo(usuario)) {
            throw new PermisoDenegadoException("Debe quedar al menos un administrador activo");
        }
        boolean anterior = usuario.estaActiva();
        usuario.setActiva(activa);
        deshacerSiFalla(() -> usuario.setActiva(anterior));
    }

    public synchronized void cambiarRol(Usuario usuario, Rol rol, Usuario solicitante) throws MiniWindowsException {
        exigirOtraCuenta(usuario, solicitante, "No puedes cambiar tu propio rol mientras estas dentro del sistema");
        if (rol != Rol.ADMINISTRADOR && esUltimoAdministradorActivo(usuario)) {
            throw new PermisoDenegadoException("Debe quedar al menos un administrador activo");
        }
        Rol anterior = usuario.getRol();
        usuario.setRol(rol);
        deshacerSiFalla(() -> usuario.setRol(anterior));
    }

    public synchronized void cambiarContrasena(Usuario usuario, String contrasena) throws MiniWindowsException {
        if (!Validador.contrasenaValida(contrasena)) {
            throw new PermisoDenegadoException("La contrasena debe tener al menos "
                    + Validador.LONGITUD_MINIMA_CONTRASENA + " caracteres");
        }
        String anterior = usuario.getHashContrasena();
        usuario.setHashContrasena(Contrasenas.cifrar(contrasena));
        deshacerSiFalla(() -> usuario.setHashContrasena(anterior));
    }

    public synchronized void eliminar(Usuario usuario, Usuario solicitante) throws MiniWindowsException {
        exigirOtraCuenta(usuario, solicitante, "No puedes eliminar tu propia cuenta");
        if (esUltimoAdministradorActivo(usuario)) {
            throw new PermisoDenegadoException("Debe quedar al menos un administrador activo");
        }
        sistemaArchivos.eliminarEspacioDeUsuario(usuario.getUsername());
        usuarios.eliminar(usuario);
        try {
            repositorio.guardar(usuarios);
        } catch (MiniWindowsException error) {
            usuarios.agregar(usuario);
            throw error;
        }
    }

    public synchronized Usuario buscar(String username) {
        if (username == null) {
            return null;
        }
        String normalizado = username.trim().toLowerCase(Locale.ROOT);
        return usuarios.buscar(usuario -> usuario.getUsername().toLowerCase(Locale.ROOT).equals(normalizado));
    }

    public synchronized ListaEnlazada<Usuario> listar() {
        ListaEnlazada<Usuario> copia = new ListaEnlazada<>();
        copia.agregarTodos(usuarios);
        return copia;
    }

    public boolean esPrimerArranque() {
        return primerArranque;
    }

    private Usuario registrar(SolicitudUsuario solicitud) throws MiniWindowsException {
        Usuario usuario = new Usuario(solicitud.nombreCompleto().trim(),
                Character.toUpperCase(solicitud.genero()),
                solicitud.username().trim(),
                Contrasenas.cifrar(solicitud.contrasena()),
                solicitud.edad(),
                solicitud.rol(),
                Fechas.ahora());
        sistemaArchivos.crearEspacioDeUsuario(usuario.getUsername());
        usuarios.agregar(usuario);
        return usuario;
    }

    private void deshacerSiFalla(Runnable deshacer) throws MiniWindowsException {
        try {
            repositorio.guardar(usuarios);
        } catch (MiniWindowsException error) {
            deshacer.run();
            throw error;
        }
    }

    private void exigirOtraCuenta(Usuario usuario, Usuario solicitante, String mensaje)
            throws PermisoDenegadoException {
        if (solicitante != null && usuario.getUsername().equalsIgnoreCase(solicitante.getUsername())) {
            throw new PermisoDenegadoException(mensaje);
        }
    }

    private boolean esUltimoAdministradorActivo(Usuario usuario) {
        if (!usuario.esAdministrador() || !usuario.estaActiva()) {
            return false;
        }
        return usuarios.filtrar(otro -> otro.esAdministrador() && otro.estaActiva()).tamano() <= 1;
    }

    private void validar(SolicitudUsuario solicitud) throws PermisoDenegadoException {
        if (!Validador.textoConContenido(solicitud.nombreCompleto())) {
            throw new PermisoDenegadoException("El nombre completo es obligatorio");
        }
        if (!Validador.usernameValido(solicitud.username())) {
            throw new PermisoDenegadoException("El username debe tener entre 3 y 20 caracteres, "
                    + "solo letras, numeros, punto, guion o guion bajo, y no puede ser un nombre reservado");
        }
        if (!Validador.contrasenaValida(solicitud.contrasena())) {
            throw new PermisoDenegadoException("La contrasena debe tener al menos "
                    + Validador.LONGITUD_MINIMA_CONTRASENA + " caracteres");
        }
        if (!Validador.edadValida(solicitud.edad())) {
            throw new PermisoDenegadoException("La edad ingresada no es valida");
        }
        if (!Validador.generoValido(solicitud.genero())) {
            throw new PermisoDenegadoException("El genero debe ser M o F");
        }
    }
}
