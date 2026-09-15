package MiniWindows.SistemaOp.Cuentas;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.ArchivoCorruptoException;
import MiniWindows.Excepciones.OperacionArchivoException;
import MiniWindows.Modelo.Usuario;
import MiniWindows.Persistencia.GestorBinario;
import MiniWindows.SistemaOp.Archivos.SistemaArchivos;
import MiniWindows.SistemaOp.Nucleo.RutasSistema;

import java.nio.file.Path;

public class RepositorioUsuarios {

    private final Path archivo;

    public RepositorioUsuarios(SistemaArchivos sistemaArchivos) {
        this.archivo = sistemaArchivos.archivoDeSistema(RutasSistema.ARCHIVO_USUARIOS);
    }

    public ListaEnlazada<Usuario> cargar() throws ArchivoCorruptoException {
        return GestorBinario.cargar(archivo, Usuario.class);
    }

    public void guardar(ListaEnlazada<Usuario> usuarios) throws OperacionArchivoException {
        GestorBinario.guardar(archivo, usuarios);
    }

    public Path getArchivo() {
        return archivo;
    }
}
