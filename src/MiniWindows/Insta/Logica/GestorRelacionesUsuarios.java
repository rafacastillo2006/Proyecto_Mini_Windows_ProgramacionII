package MiniWindows.Insta.Logica;
import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Persistencia.GestorBinario;
import MiniWindows.Util.Rutas;

public class GestorRelacionesUsuarios {
    public void seguir(String origen, String destino) {
        GestorBinario<String> gFollowing = new GestorBinario<>(Rutas.getRutaFollowing(origen));
        ListaEnlazada<String> following = gFollowing.leerLista();
        if (!contiene(following, destino)) {
            following.agregar(destino);
            gFollowing.guardarLista(following);
        }

        GestorBinario<String> gFollowers = new GestorBinario<>(Rutas.getRutaFollowers(destino));
        ListaEnlazada<String> followers = gFollowers.leerLista();
        if (!contiene(followers, origen)) {
            followers.agregar(origen);
            gFollowers.guardarLista(followers);
        }
    }

    public void dejarDeSeguir(String origen, String destino) {
        GestorBinario<String> gFollowing = new GestorBinario<>(Rutas.getRutaFollowing(origen));
        ListaEnlazada<String> following = gFollowing.leerLista();
        eliminar(following, destino);
        gFollowing.guardarLista(following);

        GestorBinario<String> gFollowers = new GestorBinario<>(Rutas.getRutaFollowers(destino));
        ListaEnlazada<String> followers = gFollowers.leerLista();
        eliminar(followers, origen);
        gFollowers.guardarLista(followers);
    }

    private boolean contiene(ListaEnlazada<String> lista, String valor) {
        for (int i = 0; i < lista.getTamano(); i++) {
            if (lista.obtener(i).equalsIgnoreCase(valor)) return true;
        }
        return false;
    }

    private void eliminar(ListaEnlazada<String> lista, String valor) {
        ListaEnlazada<String> nueva = new ListaEnlazada<>();
        for (int i = 0; i < lista.getTamano(); i++) {
            if (!lista.obtener(i).equalsIgnoreCase(valor)) {
                nueva.agregar(lista.obtener(i));
            }
        }
        lista.limpiar();
        for (int i = 0; i < nueva.getTamano(); i++) {
            lista.agregar(nueva.obtener(i));
        }
    }
}
