package MiniWindows.SistemaOp;

import MiniWindows.Persistencia.GestorBinario;
import MiniWindows.Util.Rutas;
import Modelo.Usuario;
import Persistencia.GestorBinario;
import Util.Rutas;
import Estructuras.ListaEnlazada;

public class GestorUsuarios {
    private GestorBinario<Usuario> gestorBinario;

    public GestorUsuarios() {
        Rutas.inicializarEstructuraSO();
        this.gestorBinario = new GestorBinario<>(Rutas.ARCHIVO_USUARIOS);

    }
}
