package MiniWindows.SistemaOp.Nucleo;

import MiniWindows.Modelo.Usuario;
import MiniWindows.SistemaOp.Archivos.RutaVirtual;

public class Sesion {

    private final Usuario usuario;
    private final RutaVirtual raiz;
    private final RutaVirtual carpetaPersonal;

    public Sesion(Usuario usuario) {
        this.usuario = usuario;
        this.carpetaPersonal = RutaVirtual.raiz().hijo(usuario.getUsername());
        this.raiz = usuario.esAdministrador() ? RutaVirtual.raiz() : carpetaPersonal;
    }

    public RutaVirtual getCarpetaPersonal() {
        return carpetaPersonal;
    }

    public RutaVirtual getEscritorio() {
        return carpetaPersonal.hijo(RutasSistema.CARPETA_ESCRITORIO);
    }

    public boolean esAdministrador() {
        return usuario.esAdministrador();
    }

    public boolean puedeAcceder(RutaVirtual ruta) {
        return raiz.contieneA(ruta);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public RutaVirtual getRaiz() {
        return raiz;
    }
}
