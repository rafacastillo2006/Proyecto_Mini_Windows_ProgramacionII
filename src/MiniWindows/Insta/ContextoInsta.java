package MiniWindows.Insta;

import MiniWindows.Insta.Servicio.ServicioInsta;
import MiniWindows.SistemaOp.Apps.ContextoApp;

public class ContextoInsta {

    private final ServicioInsta servicio;
    private final SesionInsta sesion;
    private final ContextoApp contextoApp;

    public ContextoInsta(ServicioInsta servicio, SesionInsta sesion, ContextoApp contextoApp) {
        this.servicio = servicio;
        this.sesion = sesion;
        this.contextoApp = contextoApp;
    }

    public ServicioInsta getServicio() {
        return servicio;
    }

    public SesionInsta getSesion() {
        return sesion;
    }

    public ContextoApp getContextoApp() {
        return contextoApp;
    }

    public String getUsuarioActual() {
        return sesion.hayCuentaAbierta() ? sesion.getUsuarioActual().getUsername() : "";
    }
}
