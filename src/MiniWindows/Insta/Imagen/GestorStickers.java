package MiniWindows.Insta.Imagen;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Persistencia.GestorBinario;
import MiniWindows.Util.Rutas;

public class GestorStickers {

    public void guardarSticker(String username, String rutaSticker) {
        if (!ProcesadorImagen.esImagenValida(rutaSticker)) return;

        GestorBinario<String> gb = new GestorBinario<>(Rutas.getRutaStickers(username));
        ListaEnlazada<String> stickers = gb.leerLista();
        stickers.agregar(rutaSticker);
        gb.guardarLista(stickers);
    }

    public ListaEnlazada<String> obtenerStickers(String username) {
        GestorBinario<String> gb = new GestorBinario<>(Rutas.getRutaStickers(username));
        return gb.leerLista();
    }
}