package MiniWindows.SistemaOp.Nucleo;

import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Insta.AppInsta;
import MiniWindows.SistemaOp.AdminCuentas.AppCuentas;
import MiniWindows.SistemaOp.AdminTareas.AppAdminTareas;
import MiniWindows.SistemaOp.Apps.RegistroAplicaciones;
import MiniWindows.SistemaOp.Archivos.SistemaArchivos;
import MiniWindows.SistemaOp.ConsolaComandos.AppConsola;
import MiniWindows.SistemaOp.Cuentas.RepositorioUsuarios;
import MiniWindows.SistemaOp.Cuentas.ServicioCuentas;
import MiniWindows.SistemaOp.EditorTexto.AppEditorTexto;
import MiniWindows.SistemaOp.Explorador.AppExplorador;
import MiniWindows.SistemaOp.ReproductorMusica.AppReproductor;
import MiniWindows.SistemaOp.VisorImagenes.AppVisorImagenes;
import MiniWindows.Insta.Servicio.ServicioLocal;
import MiniWindows.Red.Cliente.ClienteInsta;
import MiniWindows.Red.Servidor.ServidorInsta;
import MiniWindows.Util.InicializadorInsta;
import MiniWindows.Util.Rutas;

import java.io.IOException;
import java.nio.file.Path;

public class SistemaOperativo {

    private final SistemaArchivos sistemaArchivos;
    private final RepositorioUsuarios repositorioUsuarios;
    private final ServicioCuentas servicioCuentas;
    private final RegistroAplicaciones aplicaciones = new RegistroAplicaciones();

    private ServidorInsta servidorInsta;

    public SistemaOperativo() {
        this(RutasSistema.raizFisica());
    }

    public SistemaOperativo(Path raizFisica) {
        this.sistemaArchivos = new SistemaArchivos(raizFisica);
        this.repositorioUsuarios = new RepositorioUsuarios(sistemaArchivos);
        this.servicioCuentas = new ServicioCuentas(repositorioUsuarios, sistemaArchivos);
    }

    public void iniciar() throws MiniWindowsException {
        sistemaArchivos.montar();
        Rutas.usarRaiz(sistemaArchivos.getRaizFisica());
        servicioCuentas.inicializar();
        registrarAplicaciones();
        InicializadorInsta.sembrarSiHaceFalta(new ServicioLocal());
    }

    public void encenderServidorInsta() {
        if (servidorInsta != null) {
            return;
        }
        ServidorInsta servidor = new ServidorInsta(ClienteInsta.PUERTO_POR_DEFECTO, new ServicioLocal());
        try {
            servidor.encender();
            servidorInsta = servidor;
        } catch (IOException ocupado) {
            System.out.println("INSTA+ se conectara al servidor que ya escucha en el puerto "
                    + ClienteInsta.PUERTO_POR_DEFECTO);
        }
    }

    public void apagarServidorInsta() {
        if (servidorInsta != null) {
            servidorInsta.apagar();
            servidorInsta = null;
        }
    }

    public ServidorInsta getServidorInsta() {
        return servidorInsta;
    }

    public SistemaArchivos getSistemaArchivos() {
        return sistemaArchivos;
    }

    public ServicioCuentas getServicioCuentas() {
        return servicioCuentas;
    }

    public RegistroAplicaciones getAplicaciones() {
        return aplicaciones;
    }

    private void registrarAplicaciones() {
        aplicaciones.registrar(new AppExplorador());
        aplicaciones.registrar(new AppEditorTexto());
        aplicaciones.registrar(new AppVisorImagenes());
        aplicaciones.registrar(new AppReproductor());
        aplicaciones.registrar(new AppConsola());
        aplicaciones.registrar(new AppInsta());
        aplicaciones.registrar(new AppAdminTareas());
        aplicaciones.registrar(new AppCuentas());
    }
}
