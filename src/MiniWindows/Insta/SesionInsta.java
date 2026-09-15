package MiniWindows.Insta;

import MiniWindows.Modelo.UsuarioInsta;

public class SesionInsta {
    private static SesionInsta instancia;
    private UsuarioInsta usuarioActual;

    private SesionInsta() {}

    public static SesionInsta getInstancia() {
        if (instancia == null) {
            instancia = new SesionInsta();
        }
        return instancia;
    }

    public UsuarioInsta getUsuarioActual() {
        return usuarioActual;
    }

    public void iniciarSesion(UsuarioInsta usuario) {
        this.usuarioActual = usuario;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }
}