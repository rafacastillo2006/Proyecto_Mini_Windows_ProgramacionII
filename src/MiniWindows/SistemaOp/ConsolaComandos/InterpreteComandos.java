package MiniWindows.SistemaOp.ConsolaComandos;

import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Excepciones.OperacionArchivoException;
import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import MiniWindows.SistemaOp.Archivos.RutaVirtual;
import MiniWindows.SistemaOp.Archivos.SistemaArchivos;
import MiniWindows.SistemaOp.ConsolaComandos.Comandos.Comando;
import MiniWindows.SistemaOp.Nucleo.RutasSistema;
import MiniWindows.SistemaOp.Nucleo.Sesion;
import MiniWindows.Util.Fechas;

import java.util.LinkedHashMap;
import java.util.Map;

public class InterpreteComandos {

    private final SistemaArchivos archivos;
    private final Sesion sesion;
    private final Map<String, Comando> comandos = new LinkedHashMap<>();

    private RutaVirtual carpetaActual;
    private boolean limpiarPantalla;

    public InterpreteComandos(SistemaArchivos archivos, Sesion sesion) {
        this.archivos = archivos;
        this.sesion = sesion;
        this.carpetaActual = sesion.getRaiz();
        registrarComandos();
    }

    public String getIndicador() {
        return carpetaActual.texto() + ">";
    }

    public boolean pidioLimpiarPantalla() {
        return limpiarPantalla;
    }

    public String ejecutar(String linea) {
        limpiarPantalla = false;
        String texto = linea == null ? "" : linea.trim();
        if (texto.isEmpty()) {
            return "";
        }
        if (texto.equalsIgnoreCase("cd..")) {
            texto = "cd ..";
        }
        int separador = texto.indexOf(' ');
        String nombre = (separador < 0 ? texto : texto.substring(0, separador)).toLowerCase();
        String argumento = separador < 0 ? "" : texto.substring(separador + 1).trim();

        Comando comando = comandos.get(nombre);
        if (comando == null) {
            return "'" + nombre + "' no se reconoce como un comando. Escribe help para ver la lista.";
        }
        try {
            return comando.ejecutar(argumento);
        } catch (MiniWindowsException error) {
            return error.getMessage();
        }
    }

    private void registrarComandos() {
        comandos.put("mkdir", this::crearCarpeta);
        comandos.put("rm", this::eliminar);
        comandos.put("cd", this::cambiarCarpeta);
        comandos.put("dir", argumento -> listar());
        comandos.put("date", argumento -> "Fecha actual: " + Fechas.formatearFecha(Fechas.ahora()));
        comandos.put("time", argumento -> "Hora actual: " + Fechas.formatearHora(Fechas.ahora()));
        comandos.put("cls", argumento -> {
            limpiarPantalla = true;
            return "";
        });
        comandos.put("help", argumento -> ayuda());
    }

    private String crearCarpeta(String nombre) throws MiniWindowsException {
        exigirArgumento(nombre, "mkdir <nombre>");
        NodoArchivo creada = archivos.crearCarpeta(carpetaActual, nombre);
        return "Carpeta creada: " + creada.getRuta().texto();
    }

    private String eliminar(String nombre) throws MiniWindowsException {
        exigirArgumento(nombre, "rm <nombre>");
        NodoArchivo objetivo = buscarEnCarpeta(nombre);
        archivos.eliminar(objetivo);
        return (objetivo.esCarpeta() ? "Carpeta eliminada: " : "Archivo eliminado: ") + objetivo.getNombre();
    }

    private String cambiarCarpeta(String ruta) throws MiniWindowsException {
        if (ruta.isEmpty()) {
            return carpetaActual.texto();
        }
        if (esOtraUnidad(ruta)) {
            throw new OperacionArchivoException("MiniWindows solo tiene la unidad " + RutasSistema.UNIDAD);
        }
        RutaVirtual destino = resolver(ruta);
        if (!sesion.puedeAcceder(destino)) {
            throw new OperacionArchivoException("Acceso denegado a " + destino.texto());
        }
        if (!archivos.existeCarpeta(destino)) {
            throw new OperacionArchivoException("No se encuentra la carpeta " + destino.texto());
        }
        carpetaActual = destino;
        return carpetaActual.texto();
    }

    private RutaVirtual resolver(String ruta) {
        if (empiezaConLaUnidad(ruta)) {
            return RutaVirtual.de(ruta);
        }
        return RutaVirtual.de(carpetaActual.texto() + RutaVirtual.SEPARADOR + ruta);
    }

    private boolean empiezaConLaUnidad(String ruta) {
        return ruta.regionMatches(true, 0, RutasSistema.UNIDAD, 0, RutasSistema.UNIDAD.length());
    }

    private boolean esOtraUnidad(String ruta) {
        return ruta.length() >= 2 && ruta.charAt(1) == ':' && !empiezaConLaUnidad(ruta);
    }

    private String listar() throws MiniWindowsException {
        StringBuilder salida = new StringBuilder(" Directorio de " + carpetaActual.texto());
        salida.append(System.lineSeparator()).append(System.lineSeparator());
        int carpetas = 0;
        int archivosContados = 0;
        for (NodoArchivo nodo : archivos.listar(carpetaActual)) {
            salida.append(String.format("%-22s %-10s %s", Fechas.formatear(nodo.getModificado()),
                    nodo.esCarpeta() ? "<DIR>" : nodo.tamanoLegible(), nodo.getNombre()));
            salida.append(System.lineSeparator());
            if (nodo.esCarpeta()) {
                carpetas++;
            } else {
                archivosContados++;
            }
        }
        salida.append(System.lineSeparator());
        salida.append(archivosContados).append(" archivo(s)   ").append(carpetas).append(" carpeta(s)");
        return salida.toString();
    }

    private String ayuda() {
        return String.join(System.lineSeparator(),
                "mkdir <nombre>   Crea una nueva carpeta",
                "rm <nombre>      Elimina una carpeta o un archivo",
                "cd <carpeta>     Cambia a la carpeta indicada",
                "cd..             Regresa a la carpeta anterior",
                "dir              Lista las carpetas y archivos de la carpeta actual",
                "date             Muestra la fecha actual",
                "time             Muestra la hora actual",
                "cls              Limpia la pantalla",
                "help             Muestra esta ayuda");
    }

    private NodoArchivo buscarEnCarpeta(String nombre) throws MiniWindowsException {
        NodoArchivo objetivo = archivos.listar(carpetaActual)
                .buscar(nodo -> nodo.getNombre().equalsIgnoreCase(nombre));
        if (objetivo == null) {
            throw new OperacionArchivoException("No se encuentra \"" + nombre + "\" en " + carpetaActual.texto());
        }
        return objetivo;
    }

    private void exigirArgumento(String argumento, String uso) throws MiniWindowsException {
        if (argumento.isEmpty()) {
            throw new OperacionArchivoException("Falta el nombre. Uso: " + uso);
        }
    }
}
