package MiniWindows.SistemaOp.Archivos;

import MiniWindows.Modelo.TipoArchivo;
import MiniWindows.Util.Fechas;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;

public class NodoArchivo {

    private final String nombre;
    private final TipoArchivo tipo;
    private final RutaVirtual ruta;
    private final Path rutaFisica;
    private final long tamano;
    private final LocalDateTime creado;
    private final LocalDateTime modificado;

    public NodoArchivo(String nombre, TipoArchivo tipo, RutaVirtual ruta, Path rutaFisica,
                       long tamano, LocalDateTime creado, LocalDateTime modificado) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.ruta = ruta;
        this.rutaFisica = rutaFisica;
        this.tamano = tamano;
        this.creado = creado;
        this.modificado = modificado;
    }

    public boolean esCarpeta() {
        return tipo == TipoArchivo.CARPETA;
    }

    public String tamanoLegible() {
        if (esCarpeta()) {
            return "";
        }
        if (tamano < 1024) {
            return tamano + " B";
        }
        if (tamano < 1024 * 1024) {
            return String.format("%.1f KB", tamano / 1024.0);
        }
        return String.format("%.1f MB", tamano / (1024.0 * 1024.0));
    }

    public String modificadoLegible() {
        return Fechas.formatear(modificado);
    }

    public String getNombre() {
        return nombre;
    }

    public TipoArchivo getTipo() {
        return tipo;
    }

    public RutaVirtual getRuta() {
        return ruta;
    }

    public Path getRutaFisica() {
        return rutaFisica;
    }

    public long getTamano() {
        return tamano;
    }

    public LocalDateTime getCreado() {
        return creado;
    }

    public LocalDateTime getModificado() {
        return modificado;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof NodoArchivo otro)) {
            return false;
        }
        return ruta.equals(otro.ruta) && tipo == otro.tipo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruta, tipo);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
