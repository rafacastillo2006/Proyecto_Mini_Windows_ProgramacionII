package MiniWindows.Insta.Logica;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Util.Rutas;

public class GestorRelacionesUsuarios {

    public ListaEnlazada<String> seguidosDe(String username) {
        return AlmacenInsta.leer(Rutas.getFollowing(username), String.class);
    }

    public ListaEnlazada<String> seguidoresDe(String username) {
        return AlmacenInsta.leer(Rutas.getFollowers(username), String.class);
    }

    public boolean sigue(String origen, String destino) {
        return contiene(seguidosDe(origen), destino);
    }

    public void seguir(String origen, String destino) throws MiniWindowsException {
        if (origen.equalsIgnoreCase(destino)) {
            return;
        }
        ListaEnlazada<String> seguidos = seguidosDe(origen);
        if (!contiene(seguidos, destino)) {
            seguidos.agregar(destino);
            AlmacenInsta.escribir(Rutas.getFollowing(origen), seguidos);
        }
        ListaEnlazada<String> seguidores = seguidoresDe(destino);
        if (!contiene(seguidores, origen)) {
            seguidores.agregar(origen);
            AlmacenInsta.escribir(Rutas.getFollowers(destino), seguidores);
        }
    }

    public void dejarDeSeguir(String origen, String destino) throws MiniWindowsException {
        AlmacenInsta.escribir(Rutas.getFollowing(origen), sin(seguidosDe(origen), destino));
        AlmacenInsta.escribir(Rutas.getFollowers(destino), sin(seguidoresDe(destino), origen));
    }

    private boolean contiene(ListaEnlazada<String> lista, String valor) {
        return lista.buscar(elemento -> elemento.equalsIgnoreCase(valor)) != null;
    }

    private ListaEnlazada<String> sin(ListaEnlazada<String> lista, String valor) {
        return lista.filtrar(elemento -> !elemento.equalsIgnoreCase(valor));
    }
}
