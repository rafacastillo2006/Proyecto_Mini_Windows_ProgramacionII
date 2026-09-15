package MiniWindows.Insta.Logica;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Modelo.Mensaje;
import MiniWindows.Persistencia.GestorBinario;
import MiniWindows.Util.Rutas;

public class GestorBandejaEntrada {

    public void enviarMensaje(String emisor, String receptor, String contenido, String tipo) {
        Mensaje msg = new Mensaje(emisor, receptor, contenido, tipo);

        GestorBinario<Mensaje> gbEmisor = new GestorBinario<>(Rutas.getRutaInbox(emisor));
        ListaEnlazada<Mensaje> inboxEmisor = gbEmisor.leerLista();
        inboxEmisor.agregar(msg);
        gbEmisor.guardarLista(inboxEmisor);

        GestorBinario<Mensaje> gbReceptor = new GestorBinario<>(Rutas.getRutaInbox(receptor));
        ListaEnlazada<Mensaje> inboxReceptor = gbReceptor.leerLista();
        inboxReceptor.agregar(msg);
        gbReceptor.guardarLista(inboxReceptor);
    }

    public ListaEnlazada<Mensaje> obtenerConversacion(String u1, String u2) {
        GestorBinario<Mensaje> gb = new GestorBinario<>(Rutas.getRutaInbox(u1));
        ListaEnlazada<Mensaje> todos = gb.leerLista();
        ListaEnlazada<Mensaje> chat = new ListaEnlazada<>();

        for (int i = 0; i < todos.getTamano(); i++) {
            Mensaje m = todos.obtener(i);
            if ((m.getEmisor().equalsIgnoreCase(u1) && m.getReceptor().equalsIgnoreCase(u2)) ||
                    (m.getEmisor().equalsIgnoreCase(u2) && m.getReceptor().equalsIgnoreCase(u1))) {
                chat.agregar(m);
            }
        }
        return chat;
    }
}