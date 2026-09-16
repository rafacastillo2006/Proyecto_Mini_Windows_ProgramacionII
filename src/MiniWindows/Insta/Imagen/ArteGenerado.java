package MiniWindows.Insta.Imagen;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public final class ArteGenerado {

    public static final String MONTANA = "Montana";
    public static final String COSTA = "Costa";
    public static final String NOCHE = "Noche";
    public static final String CIUDAD = "Ciudad";
    public static final String COMIDA = "Comida";
    public static final String FIGURAS = "Figuras";

    private static final String[] TEMAS = {MONTANA, COSTA, NOCHE, CIUDAD, COMIDA, FIGURAS};

    private static final Color CIELO = new Color(0xD3E2E8);
    private static final Color CIELO_TARDE = new Color(0xE8D6C3);
    private static final Color SOL = new Color(0xE9A84C);
    private static final Color MONTE_LEJOS = new Color(0x8FA9A4);
    private static final Color MONTE_CERCA = new Color(0x3F6157);
    private static final Color SUELO = new Color(0x2E4A43);
    private static final Color MAR = new Color(0x4C8EA8);
    private static final Color ARENA = new Color(0xE3D3B4);
    private static final Color ESPUMA = new Color(0xEDF3F4);
    private static final Color NOCHE_FONDO = new Color(0x22314A);
    private static final Color NOCHE_CERRO = new Color(0x16223A);
    private static final Color LUNA = new Color(0xF0E6CC);
    private static final Color EDIFICIO = new Color(0x3A4A63);
    private static final Color EDIFICIO_CLARO = new Color(0x51637F);
    private static final Color VENTANA = new Color(0xE9C46A);
    private static final Color MANTEL = new Color(0xD9C4A6);
    private static final Color PLATO = new Color(0xF7F3EC);
    private static final Color GUISO = new Color(0xC2683F);
    private static final Color VERDURA = new Color(0x6E9764);

    private static final Color[] FIGURAS_FONDO = {
            new Color(0xF2EBE1), new Color(0xE4EAE6), new Color(0xEDE3E8), new Color(0xE7E9F0)
    };
    private static final Color[] FIGURAS_TINTA = {
            new Color(0x2A9D8F), new Color(0xE76F51), new Color(0x264653), new Color(0xE9C46A),
            new Color(0x457B9D), new Color(0x8AB17D)
    };
    private static final Color[] COLORES_AVATAR = {
            new Color(0x5B7DB1), new Color(0x2F8F7B), new Color(0xC2683F), new Color(0x7A6AA8),
            new Color(0xB5495B), new Color(0x4A7A4A), new Color(0xB08428), new Color(0x4A6572)
    };

    private ArteGenerado() {
    }

    public static byte[] avatar(String texto, int semilla) {
        int lado = 200;
        BufferedImage lienzo = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_RGB);
        Graphics2D pincel = preparar(lienzo);

        pincel.setColor(COLORES_AVATAR[Math.floorMod(mezcla(texto, semilla), COLORES_AVATAR.length)]);
        pincel.fillRect(0, 0, lado, lado);

        pincel.setColor(Color.WHITE);
        pincel.setFont(new Font("SansSerif", Font.PLAIN, 84));
        String iniciales = iniciales(texto);
        int ancho = pincel.getFontMetrics().stringWidth(iniciales);
        int alto = pincel.getFontMetrics().getAscent();
        pincel.drawString(iniciales, (lado - ancho) / 2, (lado + alto) / 2 - 8);
        pincel.dispose();
        return aPng(lienzo);
    }

    public static byte[] publicacion(int semilla, String formato) {
        return publicacion(semilla, formato, TEMAS[Math.floorMod(semilla, TEMAS.length)]);
    }

    public static byte[] publicacion(int semilla, String formato, String tema) {
        int ancho = 720;
        int alto = (int) Math.round(720 / ProcesadorImagen.proporcionDe(formato));
        BufferedImage lienzo = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D pincel = preparar(lienzo);
        Random azar = new Random(semilla * 7919L + 13);

        if (COSTA.equalsIgnoreCase(tema)) {
            costa(pincel, ancho, alto);
        } else if (NOCHE.equalsIgnoreCase(tema)) {
            noche(pincel, ancho, alto, azar);
        } else if (CIUDAD.equalsIgnoreCase(tema)) {
            ciudad(pincel, ancho, alto, azar);
        } else if (COMIDA.equalsIgnoreCase(tema)) {
            comida(pincel, ancho, alto);
        } else if (FIGURAS.equalsIgnoreCase(tema)) {
            figuras(pincel, ancho, alto, azar);
        } else {
            montana(pincel, ancho, alto);
        }
        pincel.dispose();
        return aPng(lienzo);
    }

    private static void montana(Graphics2D pincel, int ancho, int alto) {
        fondo(pincel, ancho, alto, CIELO);
        circulo(pincel, SOL, ancho * 0.68, alto * 0.14, ancho * 0.15);

        double base = alto * 0.74;
        triangulo(pincel, MONTE_LEJOS, ancho * -0.05, ancho * 0.62, base, alto * 0.24);
        triangulo(pincel, MONTE_CERCA, ancho * 0.30, ancho * 1.05, base, alto * 0.36);

        pincel.setColor(SUELO);
        pincel.fillRect(0, (int) base, ancho, alto - (int) base);
        pincel.setColor(MONTE_CERCA);
        pincel.fillRect(0, (int) base, ancho, Math.max(4, (int) (alto * 0.02)));
    }

    private static void costa(Graphics2D pincel, int ancho, int alto) {
        fondo(pincel, ancho, alto, CIELO_TARDE);
        circulo(pincel, SOL, ancho * 0.40, alto * 0.18, ancho * 0.18);

        int nivelMar = (int) (alto * 0.55);
        int nivelArena = (int) (alto * 0.80);
        pincel.setColor(MAR);
        pincel.fillRect(0, nivelMar, ancho, nivelArena - nivelMar);
        pincel.setColor(ARENA);
        pincel.fillRect(0, nivelArena, ancho, alto - nivelArena);

        pincel.setColor(ESPUMA);
        int grosor = Math.max(4, (int) (alto * 0.016));
        int alturaMar = nivelArena - nivelMar;
        for (int linea = 0; linea < 3; linea++) {
            int y = nivelMar + (int) (alturaMar * (0.28 + linea * 0.22));
            int x = (int) (ancho * (linea % 2 == 0 ? 0.14 : 0.52));
            pincel.fillRoundRect(x, y, (int) (ancho * 0.24), grosor, grosor, grosor);
            pincel.fillRoundRect(x + (int) (ancho * 0.30), y, (int) (ancho * 0.12), grosor,
                    grosor, grosor);
        }
    }

    private static void noche(Graphics2D pincel, int ancho, int alto, Random azar) {
        fondo(pincel, ancho, alto, NOCHE_FONDO);
        circulo(pincel, LUNA, ancho * 0.62, alto * 0.14, ancho * 0.17);

        pincel.setColor(new Color(0xE6EDF5));
        for (int estrella = 0; estrella < 26; estrella++) {
            double lado = ancho * (azar.nextBoolean() ? 0.008 : 0.013);
            double x = azar.nextDouble() * ancho;
            double y = azar.nextDouble() * alto * 0.65;
            pincel.fill(new Ellipse2D.Double(x, y, lado, lado));
        }
        double base = alto * 0.78;
        triangulo(pincel, NOCHE_CERRO, ancho * -0.10, ancho * 0.55, base, alto * 0.40);
        triangulo(pincel, NOCHE_CERRO, ancho * 0.42, ancho * 1.10, base, alto * 0.52);
        pincel.setColor(NOCHE_CERRO);
        pincel.fillRect(0, (int) base, ancho, alto - (int) base);
    }

    private static void ciudad(Graphics2D pincel, int ancho, int alto, Random azar) {
        fondo(pincel, ancho, alto, CIELO_TARDE);
        circulo(pincel, SOL, ancho * 0.18, alto * 0.18, ancho * 0.14);

        int suelo = (int) (alto * 0.86);
        int columna = 0;
        int indice = 0;
        while (columna < ancho) {
            int anchoEdificio = (int) (ancho * (0.10 + azar.nextDouble() * 0.06));
            int altoEdificio = (int) (alto * (0.22 + azar.nextDouble() * 0.38));
            int y = suelo - altoEdificio;
            pincel.setColor(indice % 2 == 0 ? EDIFICIO : EDIFICIO_CLARO);
            pincel.fillRect(columna, y, anchoEdificio, altoEdificio);

            pincel.setColor(VENTANA);
            int ladoVentana = Math.max(4, (int) (ancho * 0.014));
            for (int fila = 0; fila < altoEdificio / (ladoVentana * 3); fila++) {
                for (int hueco = 0; hueco < 2; hueco++) {
                    if (azar.nextInt(3) == 0) {
                        continue;
                    }
                    pincel.fillRect(columna + ladoVentana + hueco * ladoVentana * 3,
                            y + ladoVentana * 2 + fila * ladoVentana * 3, ladoVentana, ladoVentana);
                }
            }
            columna += anchoEdificio + (int) (ancho * 0.015);
            indice++;
        }
        pincel.setColor(EDIFICIO);
        pincel.fillRect(0, suelo, ancho, alto - suelo);
    }

    private static void comida(Graphics2D pincel, int ancho, int alto) {
        fondo(pincel, ancho, alto, MANTEL);

        double lado = Math.min(ancho, alto) * 0.66;
        double x = (ancho - lado) / 2;
        double y = (alto - lado) / 2;
        pincel.setColor(PLATO);
        pincel.fill(new Ellipse2D.Double(x, y, lado, lado));
        pincel.setColor(new Color(0xE2D8C6));
        pincel.setStroke(new BasicStroke((float) (lado * 0.02)));
        pincel.draw(new Ellipse2D.Double(x + lado * 0.10, y + lado * 0.10, lado * 0.80, lado * 0.80));

        pincel.setColor(GUISO);
        pincel.fill(new Ellipse2D.Double(x + lado * 0.22, y + lado * 0.22, lado * 0.56, lado * 0.56));
        pincel.setColor(VERDURA);
        pincel.fill(new Ellipse2D.Double(x + lado * 0.33, y + lado * 0.30, lado * 0.16, lado * 0.16));
        pincel.fill(new Ellipse2D.Double(x + lado * 0.52, y + lado * 0.48, lado * 0.14, lado * 0.14));
        pincel.setColor(new Color(0xE9C46A));
        pincel.fill(new Ellipse2D.Double(x + lado * 0.30, y + lado * 0.52, lado * 0.13, lado * 0.13));
    }

    private static void figuras(Graphics2D pincel, int ancho, int alto, Random azar) {
        fondo(pincel, ancho, alto, FIGURAS_FONDO[azar.nextInt(FIGURAS_FONDO.length)]);

        Color uno = FIGURAS_TINTA[azar.nextInt(FIGURAS_TINTA.length)];
        Color dos = distinto(uno, azar);
        Color tres = distinto(dos, azar);
        boolean espejo = azar.nextBoolean();
        double menor = Math.min(ancho, alto);
        double base = alto * 0.66;

        pincel.setColor(uno);
        pincel.fillRect(0, (int) base, ancho, Math.max(5, (int) (alto * 0.035)));

        double disco = menor * 0.34;
        circulo(pincel, dos, espejo ? ancho * 0.60 : ancho * 0.06, base - disco, disco);

        double medio = menor * 0.30;
        pincel.setColor(tres);
        pincel.fillArc((int) (espejo ? ancho * 0.10 : ancho * 0.66), (int) (base - medio / 2),
                (int) medio, (int) medio, 0, 180);

        triangulo(pincel, tres, ancho * 0.41, ancho * 0.59, base, base - menor * 0.26);

        circulo(pincel, dos, ancho * 0.46, alto * 0.14, menor * 0.10);
    }

    private static Color distinto(Color anterior, Random azar) {
        Color elegido = anterior;
        while (elegido.equals(anterior)) {
            elegido = FIGURAS_TINTA[azar.nextInt(FIGURAS_TINTA.length)];
        }
        return elegido;
    }

    public static byte[] sticker(String nombre) {
        int lado = 200;
        BufferedImage lienzo = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_RGB);
        Graphics2D pincel = preparar(lienzo);
        pincel.setColor(Color.WHITE);
        pincel.fillRect(0, 0, lado, lado);

        if ("Corazon".equalsIgnoreCase(nombre)) {
            dibujarCorazon(pincel, lado);
        } else {
            dibujarCara(pincel, lado, nombre);
        }
        pincel.dispose();
        return aPng(lienzo);
    }

    private static void dibujarCorazon(Graphics2D pincel, int lado) {
        pincel.setColor(new Color(0xED4956));
        int radio = lado / 4;
        pincel.fill(new Ellipse2D.Double(lado * 0.22, lado * 0.24, radio * 1.6, radio * 1.6));
        pincel.fill(new Ellipse2D.Double(lado * 0.38, lado * 0.24, radio * 1.6, radio * 1.6));
        Polygon punta = new Polygon();
        punta.addPoint((int) (lado * 0.16), (int) (lado * 0.46));
        punta.addPoint((int) (lado * 0.84), (int) (lado * 0.46));
        punta.addPoint((int) (lado * 0.5), (int) (lado * 0.86));
        pincel.fill(punta);
    }

    private static void dibujarCara(Graphics2D pincel, int lado, String nombre) {
        pincel.setColor(new Color(0xFFCD42));
        pincel.fill(new Ellipse2D.Double(lado * 0.08, lado * 0.08, lado * 0.84, lado * 0.84));

        pincel.setColor(new Color(0x3C2C0C));
        double ojoY = lado * 0.36;
        double ojoLado = lado * 0.1;
        pincel.fill(new Ellipse2D.Double(lado * 0.3, ojoY, ojoLado, ojoLado * 1.2));
        pincel.fill(new Ellipse2D.Double(lado * 0.6, ojoY, ojoLado, ojoLado * 1.2));

        pincel.setStroke(new BasicStroke(lado * 0.06f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int x = (int) (lado * 0.3);
        int ancho = (int) (lado * 0.4);
        if ("Triste".equalsIgnoreCase(nombre)) {
            pincel.drawArc(x, (int) (lado * 0.66), ancho, (int) (lado * 0.24), 0, 180);
        } else if ("Risa".equalsIgnoreCase(nombre)) {
            pincel.fillArc(x, (int) (lado * 0.5), ancho, (int) (lado * 0.32), 180, 180);
        } else if ("Aplauso".equalsIgnoreCase(nombre)) {
            pincel.drawArc(x, (int) (lado * 0.52), ancho, (int) (lado * 0.26), 200, 140);
            pincel.setColor(new Color(0xF2B705));
            pincel.fill(new Ellipse2D.Double(lado * 0.12, lado * 0.12, lado * 0.16, lado * 0.16));
            pincel.fill(new Ellipse2D.Double(lado * 0.72, lado * 0.12, lado * 0.16, lado * 0.16));
        } else {
            pincel.drawArc(x, (int) (lado * 0.52), ancho, (int) (lado * 0.26), 200, 140);
        }
    }

    private static void fondo(Graphics2D pincel, int ancho, int alto, Color color) {
        pincel.setColor(color);
        pincel.fillRect(0, 0, ancho, alto);
    }

    private static void circulo(Graphics2D pincel, Color color, double x, double y, double lado) {
        pincel.setColor(color);
        pincel.fill(new Ellipse2D.Double(x, y, lado, lado));
    }

    private static void triangulo(Graphics2D pincel, Color color, double izquierda, double derecha,
                                  double base, double cima) {
        Polygon figura = new Polygon();
        figura.addPoint((int) izquierda, (int) base);
        figura.addPoint((int) derecha, (int) base);
        figura.addPoint((int) ((izquierda + derecha) / 2), (int) cima);
        pincel.setColor(color);
        pincel.fill(figura);
    }

    private static Graphics2D preparar(BufferedImage lienzo) {
        Graphics2D pincel = lienzo.createGraphics();
        pincel.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        pincel.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        return pincel;
    }

    private static int mezcla(String texto, int semilla) {
        int suma = semilla * 31;
        if (texto != null) {
            for (int i = 0; i < texto.length(); i++) {
                suma = suma * 31 + texto.charAt(i);
            }
        }
        suma ^= suma >>> 16;
        suma *= 0x7FEB352D;
        suma ^= suma >>> 15;
        suma *= 0x846CA68B;
        return suma ^ (suma >>> 16);
    }

    private static String iniciales(String texto) {
        String limpio = texto == null ? "" : texto.trim();
        if (limpio.isEmpty()) {
            return "?";
        }
        String[] partes = limpio.split("[\\s._]+");
        StringBuilder letras = new StringBuilder();
        for (String parte : partes) {
            if (!parte.isEmpty() && letras.length() < 2) {
                letras.append(Character.toUpperCase(parte.charAt(0)));
            }
        }
        return letras.toString();
    }

    private static byte[] aPng(BufferedImage lienzo) {
        try {
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            ImageIO.write(lienzo, "png", salida);
            return salida.toByteArray();
        } catch (IOException error) {
            return new byte[0];
        }
    }
}
