package MiniWindows.SistemaOp.Archivos;

import MiniWindows.SistemaOp.Nucleo.RutasSistema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class RutaVirtual {

    public static final char SEPARADOR = '\\';

    private static final RutaVirtual RAIZ = new RutaVirtual(List.of());

    private final List<String> segmentos;

    private RutaVirtual(List<String> segmentos) {
        this.segmentos = Collections.unmodifiableList(segmentos);
    }

    public static RutaVirtual raiz() {
        return RAIZ;
    }

    public static RutaVirtual de(String texto) {
        if (texto == null) {
            return RAIZ;
        }
        String limpio = texto.trim();
        if (limpio.regionMatches(true, 0, RutasSistema.UNIDAD, 0, RutasSistema.UNIDAD.length())) {
            limpio = limpio.substring(RutasSistema.UNIDAD.length());
        }
        List<String> partes = new ArrayList<>();
        for (String segmento : limpio.split("[\\\\/]")) {
            String nombre = segmento.trim();
            if (nombre.isEmpty() || nombre.equals(".")) {
                continue;
            }
            if (nombre.equals("..")) {
                if (!partes.isEmpty()) {
                    partes.remove(partes.size() - 1);
                }
                continue;
            }
            partes.add(nombre);
        }
        return partes.isEmpty() ? RAIZ : new RutaVirtual(partes);
    }

    public RutaVirtual hijo(String nombre) {
        List<String> partes = new ArrayList<>(segmentos);
        partes.add(nombre);
        return new RutaVirtual(partes);
    }

    public RutaVirtual padre() {
        if (esRaiz()) {
            return RAIZ;
        }
        return new RutaVirtual(new ArrayList<>(segmentos.subList(0, segmentos.size() - 1)));
    }

    public String nombre() {
        return esRaiz() ? RutasSistema.UNIDAD : segmentos.get(segmentos.size() - 1);
    }

    public boolean esRaiz() {
        return segmentos.isEmpty();
    }

    public int profundidad() {
        return segmentos.size();
    }

    public List<String> segmentos() {
        return segmentos;
    }

    public boolean contieneA(RutaVirtual otra) {
        if (otra.segmentos.size() < segmentos.size()) {
            return false;
        }
        for (int posicion = 0; posicion < segmentos.size(); posicion++) {
            if (!segmentos.get(posicion).equalsIgnoreCase(otra.segmentos.get(posicion))) {
                return false;
            }
        }
        return true;
    }

    public String texto() {
        StringBuilder ruta = new StringBuilder(RutasSistema.UNIDAD);
        ruta.append(SEPARADOR);
        for (int posicion = 0; posicion < segmentos.size(); posicion++) {
            ruta.append(segmentos.get(posicion));
            if (posicion < segmentos.size() - 1) {
                ruta.append(SEPARADOR);
            }
        }
        return ruta.toString();
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof RutaVirtual otra)) {
            return false;
        }
        return segmentos.equals(otra.segmentos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(segmentos);
    }

    @Override
    public String toString() {
        return texto();
    }
}
