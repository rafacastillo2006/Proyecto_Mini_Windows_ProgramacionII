package MiniWindows.Insta;

import MiniWindows.Insta.Hilos.CanalAvisos;
import MiniWindows.Insta.Hilos.AvisosRemotos;
import MiniWindows.Insta.Hilos.Notificaciones;
import MiniWindows.Insta.Servicio.ServicioInsta;
import MiniWindows.Insta.Servicio.ServicioRemoto;
import MiniWindows.Red.EventoInsta;
import MiniWindows.SistemaOp.Apps.ContextoApp;

import java.util.function.Consumer;

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

    public boolean usaServidor() {
        return servicio instanceof ServicioRemoto;
    }

    public boolean servidorCaido() {
        return servicio instanceof ServicioRemoto remoto && !remoto.hayConexion();
    }

    public CanalAvisos crearAvisos(Consumer<EventoInsta> alLlegar) {
        if (servicio instanceof ServicioRemoto remoto) {
            return new AvisosRemotos(remoto.getHost(), remoto.getPuerto(), getUsuarioActual(), alLlegar);
        }
        return new Notificaciones(servicio, getUsuarioActual(), alLlegar);
    }

    public String getUsuarioActual() {
        return sesion.hayCuentaAbierta() ? sesion.getUsuarioActual().getUsername() : "";
    }
}
