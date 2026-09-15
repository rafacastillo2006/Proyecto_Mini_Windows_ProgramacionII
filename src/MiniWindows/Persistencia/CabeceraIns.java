package MiniWindows.Persistencia;

import MiniWindows.Modelo.TipoArchivo;

public record CabeceraIns(TipoArchivo tipo, String nombreVisible, long creado, long modificado, int longitud) {

    public static CabeceraIns nueva(TipoArchivo tipo, String nombreVisible, int longitud) {
        long ahora = System.currentTimeMillis();
        return new CabeceraIns(tipo, nombreVisible, ahora, ahora, longitud);
    }

    public CabeceraIns conNombre(String nuevoNombre) {
        return new CabeceraIns(TipoArchivo.desdeNombre(nuevoNombre), nuevoNombre, creado, System.currentTimeMillis(), longitud);
    }

    public CabeceraIns conLongitud(int nuevaLongitud) {
        return new CabeceraIns(tipo, nombreVisible, creado, System.currentTimeMillis(), nuevaLongitud);
    }
}
