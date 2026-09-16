package MiniWindows.Util;

import MiniWindows.SistemaOp.Nucleo.RutasSistema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Rutas {

    public static final String INSTA_RAIZ = "INSTA_RAIZ";
    public static final String USERS = "users.ins";
    public static final String SESIONES = "sesiones.ins";
    public static final String FOLLOWING = "following.ins";
    public static final String FOLLOWERS = "followers.ins";
    public static final String INSTA = "insta.ins";
    public static final String INBOX = "inbox.ins";
    public static final String STICKERS = "stickers.ins";
    public static final String STICKERS_GLOBALES = "stickers_globales";
    public static final String IMAGENES = "imagenes";
    public static final String FOLDERS_PERSONALES = "folders_personales";
    public static final String STICKERS_PERSONALES = "stickers_personales";

    private static Path raiz;

    private Rutas() {
    }

    public static void usarRaiz(Path raizFisicaDeLaUnidad) {
        raiz = raizFisicaDeLaUnidad.resolve(RutasSistema.CARPETA_SISTEMA).resolve(INSTA_RAIZ);
        crear(raiz);
        crear(raiz.resolve(STICKERS_GLOBALES));
    }

    public static Path getRaiz() {
        if (raiz == null) {
            usarRaiz(RutasSistema.raizFisica());
        }
        return raiz;
    }

    public static Path getUsers() {
        return getRaiz().resolve(USERS);
    }

    public static Path getSesiones() {
        return getRaiz().resolve(SESIONES);
    }

    public static Path getStickersGlobales() {
        return getRaiz().resolve(STICKERS_GLOBALES);
    }

    public static Path getCarpetaDe(String username) {
        Path carpeta = getRaiz().resolve(username.toLowerCase());
        crear(carpeta);
        return carpeta;
    }

    public static void crearEstructuraDe(String username) {
        Path carpeta = getCarpetaDe(username);
        crear(carpeta.resolve(IMAGENES));
        crear(carpeta.resolve(FOLDERS_PERSONALES));
        crear(carpeta.resolve(STICKERS_PERSONALES));
    }

    public static Path getImagenesDe(String username) {
        return getCarpetaDe(username).resolve(IMAGENES);
    }

    public static Path getFoldersPersonalesDe(String username) {
        return getCarpetaDe(username).resolve(FOLDERS_PERSONALES);
    }

    public static Path getStickersPersonalesDe(String username) {
        return getCarpetaDe(username).resolve(STICKERS_PERSONALES);
    }

    public static Path getFollowing(String username) {
        return getCarpetaDe(username).resolve(FOLLOWING);
    }

    public static Path getFollowers(String username) {
        return getCarpetaDe(username).resolve(FOLLOWERS);
    }

    public static Path getInsta(String username) {
        return getCarpetaDe(username).resolve(INSTA);
    }

    public static Path getInbox(String username) {
        return getCarpetaDe(username).resolve(INBOX);
    }

    public static Path getStickers(String username) {
        return getCarpetaDe(username).resolve(STICKERS);
    }

    private static void crear(Path carpeta) {
        try {
            Files.createDirectories(carpeta);
        } catch (IOException error) {
            throw new IllegalStateException("No se pudo preparar " + carpeta, error);
        }
    }
}
