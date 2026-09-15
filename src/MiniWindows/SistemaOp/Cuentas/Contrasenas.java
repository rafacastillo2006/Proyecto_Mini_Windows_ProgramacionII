package MiniWindows.SistemaOp.Cuentas;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Contrasenas {

    private static final String ALGORITMO = "SHA-256";

    private Contrasenas() {
    }

    public static String cifrar(String contrasena) {
        try {
            MessageDigest resumen = MessageDigest.getInstance(ALGORITMO);
            byte[] bytes = resumen.digest(contrasena.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexadecimal = new StringBuilder(bytes.length * 2);
            for (byte dato : bytes) {
                hexadecimal.append(Character.forDigit((dato >> 4) & 0xF, 16));
                hexadecimal.append(Character.forDigit(dato & 0xF, 16));
            }
            return hexadecimal.toString();
        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException("El algoritmo " + ALGORITMO + " no esta disponible", error);
        }
    }

    public static boolean coincide(String contrasena, String hashGuardado) {
        return hashGuardado != null && hashGuardado.equals(cifrar(contrasena));
    }
}
