package MiniWindows.Insta.Logica;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Modelo.Mensaje;
import MiniWindows.Util.Rutas;

public class GestorBandejaEntrada {

    public void enviar(Mensaje mensaje) throws MiniWindowsException {
        agregarA(mensaje.getEmisor(), mensaje);
        if (!mensaje.getEmisor().equalsIgnoreCase(mensaje.getReceptor())) {
            agregarA(mensaje.getReceptor(), mensaje);
        }
    }

    public ListaEnlazada<Mensaje> bandejaDe(String username) {
        return AlmacenInsta.leer(Rutas.getInbox(username), Mensaje.class);
    }

    public ListaEnlazada<Mensaje> conversacion(String uno, String otro) {
        return bandejaDe(uno).filtrar(mensaje -> entre(mensaje, uno, otro));
    }

    public int sinLeerDe(String username) {
        return bandejaDe(username).filtrar(mensaje ->
                !mensaje.estaLeido() && mensaje.getReceptor().equalsIgnoreCase(username)).tamano();
    }

    public void marcarConversacionLeida(String username, String otro) throws MiniWindowsException {
        ListaEnlazada<Mensaje> bandeja = bandejaDe(username);
        boolean cambio = false;
        for (Mensaje mensaje : bandeja) {
            if (entre(mensaje, username, otro) && !mensaje.estaLeido()
                    && mensaje.getReceptor().equalsIgnoreCase(username)) {
                mensaje.marcarLeido();
                cambio = true;
            }
        }
        if (cambio) {
            AlmacenInsta.escribir(Rutas.getInbox(username), bandeja);
        }
    }

    public void eliminarConversacion(String username, String otro) throws MiniWindowsException {
        AlmacenInsta.escribir(Rutas.getInbox(username),
                bandejaDe(username).filtrar(mensaje -> !entre(mensaje, username, otro)));
    }

    public ListaEnlazada<String> contactosDe(String username) {
        ListaEnlazada<String> contactos = new ListaEnlazada<>();
        for (Mensaje mensaje : bandejaDe(username)) {
            String otro = mensaje.getEmisor().equalsIgnoreCase(username)
                    ? mensaje.getReceptor()
                    : mensaje.getEmisor();
            if (contactos.buscar(nombre -> nombre.equalsIgnoreCase(otro)) == null) {
                contactos.agregar(otro);
            }
        }
        return contactos;
    }

    private boolean entre(Mensaje mensaje, String uno, String otro) {
        return (mensaje.getEmisor().equalsIgnoreCase(uno) && mensaje.getReceptor().equalsIgnoreCase(otro))
                || (mensaje.getEmisor().equalsIgnoreCase(otro) && mensaje.getReceptor().equalsIgnoreCase(uno));
    }

    private void agregarA(String username, Mensaje mensaje) throws MiniWindowsException {
        ListaEnlazada<Mensaje> bandeja = bandejaDe(username);
        bandeja.agregar(mensaje);
        AlmacenInsta.escribir(Rutas.getInbox(username), bandeja);
    }
}
