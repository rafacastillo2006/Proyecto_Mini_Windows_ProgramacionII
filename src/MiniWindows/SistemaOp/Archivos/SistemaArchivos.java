package MiniWindows.SistemaOp.Archivos;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.ArchivoCorruptoException;
import MiniWindows.Excepciones.OperacionArchivoException;
import MiniWindows.Modelo.TipoArchivo;
import MiniWindows.Persistencia.ArchivoIns;
import MiniWindows.Persistencia.CabeceraIns;
import MiniWindows.Persistencia.ContenidoIns;
import MiniWindows.SistemaOp.Nucleo.RutasSistema;
import MiniWindows.Util.Fechas;
import MiniWindows.Util.Nombres;
import MiniWindows.Util.Validador;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class SistemaArchivos {

    private static final Set<String> NOMBRES_RESERVADOS = Set.of(RutasSistema.CARPETA_SISTEMA);
    private static final String SUFIJO_COPIA = " - copia";

    private final Path raizFisica;
    private final List<ObservadorArchivos> observadores = new CopyOnWriteArrayList<>();

    public SistemaArchivos(Path raizFisica) {
        this.raizFisica = raizFisica;
    }

    public void agregarObservador(ObservadorArchivos observador) {
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void quitarObservador(ObservadorArchivos observador) {
        observadores.remove(observador);
    }

    private void notificar(RutaVirtual carpeta) {
        for (ObservadorArchivos observador : observadores) {
            observador.carpetaCambiada(carpeta);
        }
    }

    public void montar() throws OperacionArchivoException {
        try {
            Files.createDirectories(raizFisica);
            Files.createDirectories(raizFisica.resolve(RutasSistema.CARPETA_SISTEMA));
        } catch (IOException error) {
            throw new OperacionArchivoException("No se pudo montar la unidad " + RutasSistema.UNIDAD, error);
        }
    }

    public Path getRaizFisica() {
        return raizFisica;
    }

    public Path archivoDeSistema(String nombre) {
        return raizFisica.resolve(RutasSistema.CARPETA_SISTEMA).resolve(nombre);
    }

    public Path carpetaFisica(RutaVirtual ruta) {
        Path fisica = raizFisica;
        for (String segmento : ruta.segmentos()) {
            fisica = fisica.resolve(segmento);
        }
        return fisica;
    }

    public Path archivoFisico(RutaVirtual carpeta, String nombreVisible) {
        return carpetaFisica(carpeta).resolve(nombreVisible + ArchivoIns.EXTENSION);
    }

    public boolean existe(NodoArchivo nodo) {
        return nodo != null && Files.exists(nodo.getRutaFisica());
    }

    public boolean existeCarpeta(RutaVirtual ruta) {
        return !esReservada(ruta) && Files.isDirectory(carpetaFisica(ruta));
    }

    public boolean esReservada(RutaVirtual ruta) {
        return !ruta.esRaiz()
                && NOMBRES_RESERVADOS.contains(ruta.segmentos().get(0).toLowerCase(Locale.ROOT));
    }

    private void exigirCarpeta(RutaVirtual ruta) throws OperacionArchivoException {
        if (!existeCarpeta(ruta)) {
            throw new OperacionArchivoException("La carpeta " + ruta.texto() + " no existe");
        }
    }

    public void asegurarCarpeta(RutaVirtual ruta) throws OperacionArchivoException {
        try {
            Files.createDirectories(carpetaFisica(ruta));
        } catch (IOException error) {
            throw new OperacionArchivoException("No se pudo preparar " + ruta.texto(), error);
        }
    }

    public NodoArchivo describirCarpeta(RutaVirtual ruta) {
        Path fisica = carpetaFisica(ruta);
        String nombre = ruta.esRaiz() ? RutasSistema.UNIDAD : ruta.nombre();
        return new NodoArchivo(nombre, TipoArchivo.CARPETA, ruta, fisica, 0,
                fechaDeCreacion(fisica), fechaDeModificacion(fisica));
    }

    public ListaEnlazada<NodoArchivo> listar(RutaVirtual carpeta) throws OperacionArchivoException {
        ListaEnlazada<NodoArchivo> contenido = new ListaEnlazada<>();
        Path fisica = carpetaFisica(carpeta);
        if (esReservada(carpeta) || !Files.isDirectory(fisica)) {
            throw new OperacionArchivoException("La carpeta " + carpeta.texto() + " no existe");
        }
        try (DirectoryStream<Path> entradas = Files.newDirectoryStream(fisica)) {
            for (Path entrada : entradas) {
                NodoArchivo nodo = describir(carpeta, entrada);
                if (nodo != null) {
                    contenido.agregar(nodo);
                }
            }
        } catch (IOException error) {
            throw new OperacionArchivoException("No se pudo leer " + carpeta.texto(), error);
        }
        contenido.ordenar(CriterioOrden.NOMBRE.comparador());
        return contenido;
    }

    public ListaEnlazada<NodoArchivo> listarCarpetas(RutaVirtual carpeta) throws OperacionArchivoException {
        return listar(carpeta).filtrar(NodoArchivo::esCarpeta);
    }

    public NodoArchivo crearCarpeta(RutaVirtual carpeta, String nombre) throws OperacionArchivoException {
        validarNombre(nombre);
        exigirCarpeta(carpeta);
        RutaVirtual destino = carpeta.hijo(nombre);
        Path fisica = carpetaFisica(destino);
        if (Files.exists(fisica)) {
            throw new OperacionArchivoException("Ya existe \"" + nombre + "\" en " + carpeta.texto());
        }
        try {
            Files.createDirectories(fisica);
        } catch (IOException error) {
            throw new OperacionArchivoException("No se pudo crear la carpeta " + nombre, error);
        }
        notificar(carpeta);
        return describirCarpeta(destino);
    }

    public NodoArchivo crearArchivo(RutaVirtual carpeta, String nombre, TipoArchivo tipo, byte[] contenido)
            throws OperacionArchivoException {
        validarNombre(nombre);
        exigirCarpeta(carpeta);
        Path fisica = archivoFisico(carpeta, nombre);
        if (Files.exists(fisica)) {
            throw new OperacionArchivoException("Ya existe \"" + nombre + "\" en " + carpeta.texto());
        }
        TipoArchivo tipoFinal = tipo == null ? TipoArchivo.desdeNombre(nombre) : tipo;
        CabeceraIns cabecera = CabeceraIns.nueva(tipoFinal, nombre, contenido.length);
        ArchivoIns.escribir(fisica, cabecera, contenido);
        notificar(carpeta);
        return nodoDeArchivo(carpeta, fisica, cabecera);
    }

    public ContenidoIns abrir(NodoArchivo archivo) throws ArchivoCorruptoException, OperacionArchivoException {
        exigirArchivo(archivo);
        return ArchivoIns.leer(archivo.getRutaFisica());
    }

    public void guardarContenido(NodoArchivo archivo, byte[] contenido)
            throws ArchivoCorruptoException, OperacionArchivoException {
        exigirArchivo(archivo);
        ArchivoIns.actualizarContenido(archivo.getRutaFisica(), contenido);
        notificar(archivo.getRuta().padre());
    }

    public NodoArchivo renombrar(NodoArchivo nodo, String nuevoNombre)
            throws ArchivoCorruptoException, OperacionArchivoException {
        validarNombre(nuevoNombre);
        if (nodo.getNombre().equals(nuevoNombre)) {
            return nodo;
        }
        RutaVirtual carpeta = nodo.getRuta().padre();
        if (nodo.esCarpeta()) {
            Path destino = carpetaFisica(carpeta.hijo(nuevoNombre));
            trasladar(nodo.getRutaFisica(), destino);
            notificar(carpeta);
            return describirCarpeta(carpeta.hijo(nuevoNombre));
        }
        Path origen = nodo.getRutaFisica();
        Path destino = archivoFisico(carpeta, nuevoNombre);
        trasladar(origen, destino);
        try {
            ArchivoIns.renombrar(destino, nuevoNombre);
        } catch (ArchivoCorruptoException | OperacionArchivoException error) {
            trasladar(destino, origen);
            throw error;
        }
        notificar(carpeta);
        return leerNodo(carpeta, destino);
    }

    public NodoArchivo copiar(NodoArchivo nodo, RutaVirtual destino) throws OperacionArchivoException {
        exigirDestinoValido(nodo, destino);
        String nombre = nombreDisponible(destino, nodo.getNombre(), nodo.esCarpeta());
        Path fisicoDestino = nodo.esCarpeta()
                ? carpetaFisica(destino.hijo(nombre))
                : archivoFisico(destino, nombre);
        try {
            if (nodo.esCarpeta()) {
                copiarCarpeta(nodo.getRutaFisica(), fisicoDestino);
            } else {
                Files.copy(nodo.getRutaFisica(), fisicoDestino, StandardCopyOption.REPLACE_EXISTING);
                ArchivoIns.renombrar(fisicoDestino, nombre);
            }
        } catch (IOException | ArchivoCorruptoException error) {
            throw new OperacionArchivoException("No se pudo copiar \"" + nodo.getNombre() + "\"", error);
        }
        notificar(destino);
        return nodo.esCarpeta() ? describirCarpeta(destino.hijo(nombre)) : leerNodo(destino, fisicoDestino);
    }

    public NodoArchivo mover(NodoArchivo nodo, RutaVirtual destino) throws OperacionArchivoException {
        exigirDestinoValido(nodo, destino);
        if (nodo.getRuta().padre().equals(destino)) {
            return nodo;
        }
        String nombre = nombreDisponible(destino, nodo.getNombre(), nodo.esCarpeta());
        Path fisicoDestino = nodo.esCarpeta()
                ? carpetaFisica(destino.hijo(nombre))
                : archivoFisico(destino, nombre);
        RutaVirtual origen = nodo.getRuta().padre();
        trasladar(nodo.getRutaFisica(), fisicoDestino);
        notificar(origen);
        notificar(destino);
        if (nodo.esCarpeta()) {
            return describirCarpeta(destino.hijo(nombre));
        }
        if (!nombre.equals(nodo.getNombre())) {
            try {
                ArchivoIns.renombrar(fisicoDestino, nombre);
            } catch (ArchivoCorruptoException error) {
                throw new OperacionArchivoException("No se pudo mover \"" + nodo.getNombre() + "\"", error);
            }
        }
        return leerNodo(destino, fisicoDestino);
    }

    public void eliminar(NodoArchivo nodo) throws OperacionArchivoException {
        try {
            if (nodo.esCarpeta()) {
                eliminarCarpeta(nodo.getRutaFisica());
            } else {
                Files.deleteIfExists(nodo.getRutaFisica());
            }
        } catch (IOException error) {
            throw new OperacionArchivoException("No se pudo eliminar \"" + nodo.getNombre() + "\"", error);
        }
        notificar(nodo.getRuta().padre());
    }

    public RutaVirtual crearEspacioDeUsuario(String username) throws OperacionArchivoException {
        RutaVirtual carpeta = RutaVirtual.raiz().hijo(username);
        Path fisica = carpetaFisica(carpeta);
        boolean espacioNuevo = !Files.isDirectory(fisica);
        try {
            Files.createDirectories(fisica);
            Files.createDirectories(carpetaFisica(carpeta.hijo(RutasSistema.CARPETA_ESCRITORIO)));
            if (espacioNuevo) {
                for (String nombre : RutasSistema.CARPETAS_POR_DEFECTO) {
                    Files.createDirectories(carpetaFisica(carpeta.hijo(nombre)));
                }
            }
        } catch (IOException error) {
            throw new OperacionArchivoException("No se pudo crear el espacio de " + username, error);
        }
        notificar(RutaVirtual.raiz());
        return carpeta;
    }

    public void eliminarEspacioDeUsuario(String username) throws OperacionArchivoException {
        try {
            eliminarCarpeta(carpetaFisica(RutaVirtual.raiz().hijo(username)));
        } catch (IOException error) {
            throw new OperacionArchivoException("No se pudo eliminar el espacio de " + username, error);
        }
        notificar(RutaVirtual.raiz());
    }

    private NodoArchivo describir(RutaVirtual carpeta, Path entrada) {
        String nombreEnDisco = entrada.getFileName().toString();
        if (Files.isDirectory(entrada)) {
            if (carpeta.esRaiz() && NOMBRES_RESERVADOS.contains(nombreEnDisco.toLowerCase(Locale.ROOT))) {
                return null;
            }
            return describirCarpeta(carpeta.hijo(nombreEnDisco));
        }
        if (!ArchivoIns.esArchivoIns(entrada)) {
            return null;
        }
        try {
            return nodoDeArchivo(carpeta, entrada, ArchivoIns.leerCabecera(entrada));
        } catch (ArchivoCorruptoException error) {
            return nodoDeArchivo(carpeta, entrada,
                    new CabeceraIns(TipoArchivo.GENERICO, nombreEnDisco, 0, 0, 0));
        }
    }

    private NodoArchivo leerNodo(RutaVirtual carpeta, Path fisica) throws OperacionArchivoException {
        try {
            return nodoDeArchivo(carpeta, fisica, ArchivoIns.leerCabecera(fisica));
        } catch (ArchivoCorruptoException error) {
            throw new OperacionArchivoException("El archivo quedo en un estado invalido", error);
        }
    }

    private NodoArchivo nodoDeArchivo(RutaVirtual carpeta, Path fisica, CabeceraIns cabecera) {
        return new NodoArchivo(cabecera.nombreVisible(), cabecera.tipo(),
                carpeta.hijo(cabecera.nombreVisible()), fisica, cabecera.longitud(),
                Fechas.desdeMilisegundos(cabecera.creado()), Fechas.desdeMilisegundos(cabecera.modificado()));
    }

    public String nombreLibre(RutaVirtual carpeta, String nombre, boolean esCarpeta) {
        return nombreDisponible(carpeta, nombre, esCarpeta);
    }

    private String nombreDisponible(RutaVirtual carpeta, String nombre, boolean esCarpeta) {
        String candidato = nombre;
        int intento = 1;
        while (existeEntrada(carpeta, candidato, esCarpeta)) {
            String sufijo = intento == 1 ? SUFIJO_COPIA : SUFIJO_COPIA + " (" + intento + ")";
            candidato = esCarpeta ? nombre + sufijo : Nombres.conSufijo(nombre, sufijo);
            intento++;
        }
        return candidato;
    }

    private boolean existeEntrada(RutaVirtual carpeta, String nombre, boolean esCarpeta) {
        Path fisica = esCarpeta ? carpetaFisica(carpeta.hijo(nombre)) : archivoFisico(carpeta, nombre);
        return Files.exists(fisica);
    }

    private void exigirDestinoValido(NodoArchivo nodo, RutaVirtual destino) throws OperacionArchivoException {
        if (!Files.isDirectory(carpetaFisica(destino))) {
            throw new OperacionArchivoException("La carpeta destino " + destino.texto() + " no existe");
        }
        if (nodo.esCarpeta() && nodo.getRuta().contieneA(destino)) {
            throw new OperacionArchivoException("No se puede colocar una carpeta dentro de si misma");
        }
    }

    private void trasladar(Path origen, Path destino) throws OperacionArchivoException {
        try {
            if (Files.exists(destino) && !Files.isSameFile(origen, destino)) {
                throw new OperacionArchivoException("Ya existe \"" + destino.getFileName() + "\" en el destino");
            }
            Files.move(origen, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException error) {
            throw new OperacionArchivoException("No se pudo mover \"" + origen.getFileName() + "\"", error);
        }
    }

    private void copiarCarpeta(Path origen, Path destino) throws IOException {
        try (Stream<Path> arbol = Files.walk(origen)) {
            for (Path entrada : arbol.toList()) {
                Path copia = destino.resolve(origen.relativize(entrada).toString());
                if (Files.isDirectory(entrada)) {
                    Files.createDirectories(copia);
                } else {
                    Files.createDirectories(copia.getParent());
                    Files.copy(entrada, copia, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private void eliminarCarpeta(Path carpeta) throws IOException {
        if (!Files.exists(carpeta)) {
            return;
        }
        try (Stream<Path> arbol = Files.walk(carpeta)) {
            for (Path entrada : arbol.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(entrada);
            }
        }
    }

    private void exigirArchivo(NodoArchivo nodo) throws OperacionArchivoException {
        if (nodo.esCarpeta()) {
            throw new OperacionArchivoException("\"" + nodo.getNombre() + "\" es una carpeta");
        }
    }

    private void validarNombre(String nombre) throws OperacionArchivoException {
        if (Validador.nombreReservado(nombre)) {
            throw new OperacionArchivoException("\"" + nombre + "\" es un nombre reservado de Windows");
        }
        if (!Validador.nombreArchivoValido(nombre)) {
            throw new OperacionArchivoException("El nombre \"" + nombre + "\" no es valido. "
                    + "Evita los caracteres " + Validador.CARACTERES_PROHIBIDOS);
        }
    }

    private LocalDateTime fechaDeCreacion(Path fisica) {
        try {
            return Fechas.desdeMilisegundos(
                    Files.readAttributes(fisica, BasicFileAttributes.class).creationTime().toMillis());
        } catch (IOException error) {
            return Fechas.ahora();
        }
    }

    private LocalDateTime fechaDeModificacion(Path fisica) {
        try {
            return Fechas.desdeMilisegundos(Files.getLastModifiedTime(fisica).toMillis());
        } catch (IOException error) {
            return Fechas.ahora();
        }
    }
}
