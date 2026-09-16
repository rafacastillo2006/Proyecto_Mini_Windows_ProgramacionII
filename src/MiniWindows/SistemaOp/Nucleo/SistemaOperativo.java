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
import MiniWindows.Util.Rutas;

import java.nio.file.Path;

public class SistemaOperativo {

    private final SistemaArchivos sistemaArchivos;
    private final RepositorioUsuarios repositorioUsuarios;
    private final ServicioCuentas servicioCuentas;
    private final RegistroAplicaciones aplicaciones = new RegistroAplicaciones();

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
