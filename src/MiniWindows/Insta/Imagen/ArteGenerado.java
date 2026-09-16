package MiniWindows.Insta.Imagen;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public final class ArteGenerado {

    private ArteGenerado() {
    }

    public static byte[] avatar(String texto, int semilla) {
        BufferedImage lienzo = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D pincel = preparar(lienzo);
        Color claro = colorDe(semilla, 0.62f);
        Color oscuro = colorDe(semilla, 0.38f);
        pincel.setPaint(new GradientPaint(0, 0, claro, 200, 200, oscuro));
        pincel.fillRect(0, 0, 200, 200);

        pincel.setColor(new Color(255, 255, 255, 230));
        pincel.setFont(new Font("SansSerif", Font.BOLD, 88));
        String iniciales = iniciales(texto);
        int ancho = pincel.getFontMetrics().stringWidth(iniciales);
        pincel.drawString(iniciales, (200 - ancho) / 2, 132);
        pincel.dispose();
        return aPng(lienzo);
    }

    public static byte[] publicacion(int semilla, String formato) {
        int ancho = 720;
        int alto = (int) Math.round(720 / ProcesadorImagen.proporcionDe(formato));
        BufferedImage lienzo = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D pincel = preparar(lienzo);

        pincel.setPaint(new GradientPaint(0, 0, colorDe(semilla, 0.58f), ancho, alto, colorDe(semilla + 3, 0.34f)));
        pincel.fillRect(0, 0, ancho, alto);

        Random azar = new Random(semilla * 7919L);
        for (int figura = 0; figura < 7; figura++) {
            int lado = 90 + azar.nextInt(Math.max(120, Math.min(ancho, alto) / 2));
            int x = azar.nextInt(ancho) - lado / 2;
            int y = azar.nextInt(alto) - lado / 2;
            pincel.setColor(new Color(255, 255, 255, 20 + azar.nextInt(35)));
            pincel.fill(new Ellipse2D.Double(x, y, lado, lado));
        }
        pincel.dispose();
        return aPng(lienzo);
    }

    public static byte[] sticker(String nombre) {
        int lado = 200;
        BufferedImage lienzo = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_RGB);
        Graphics2D pincel = preparar(lienzo);
        pincel.setColor(java.awt.Color.WHITE);
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
        pincel.setColor(new Color(237, 73, 86));
        int radio = lado / 4;
        pincel.fill(new Ellipse2D.Double(lado * 0.22, lado * 0.24, radio * 1.6, radio * 1.6));
        pincel.fill(new Ellipse2D.Double(lado * 0.38, lado * 0.24, radio * 1.6, radio * 1.6));
        java.awt.Polygon punta = new java.awt.Polygon();
        punta.addPoint((int) (lado * 0.16), (int) (lado * 0.46));
        punta.addPoint((int) (lado * 0.84), (int) (lado * 0.46));
        punta.addPoint((int) (lado * 0.5), (int) (lado * 0.86));
        pincel.fill(punta);
    }

    private static void dibujarCara(Graphics2D pincel, int lado, String nombre) {
        pincel.setColor(new Color(255, 205, 66));
        pincel.fill(new Ellipse2D.Double(lado * 0.08, lado * 0.08, lado * 0.84, lado * 0.84));

        pincel.setColor(new Color(60, 44, 12));
        double ojoY = lado * 0.36;
        double ojoLado = lado * 0.1;
        pincel.fill(new Ellipse2D.Double(lado * 0.3, ojoY, ojoLado, ojoLado * 1.2));
        pincel.fill(new Ellipse2D.Double(lado * 0.6, ojoY, ojoLado, ojoLado * 1.2));

        pincel.setStroke(new java.awt.BasicStroke(lado * 0.06f, java.awt.BasicStroke.CAP_ROUND,
                java.awt.BasicStroke.JOIN_ROUND));
        int x = (int) (lado * 0.3);
        int ancho = (int) (lado * 0.4);
        if ("Triste".equalsIgnoreCase(nombre)) {
            pincel.drawArc(x, (int) (lado * 0.66), ancho, (int) (lado * 0.24), 0, 180);
        } else if ("Risa".equalsIgnoreCase(nombre)) {
            pincel.fillArc(x, (int) (lado * 0.5), ancho, (int) (lado * 0.32), 180, 180);
        } else if ("Aplauso".equalsIgnoreCase(nombre)) {
            pincel.drawArc(x, (int) (lado * 0.52), ancho, (int) (lado * 0.26), 200, 140);
            pincel.setColor(new Color(255, 255, 255, 180));
            pincel.fill(new Ellipse2D.Double(lado * 0.12, lado * 0.12, lado * 0.16, lado * 0.16));
            pincel.fill(new Ellipse2D.Double(lado * 0.72, lado * 0.12, lado * 0.16, lado * 0.16));
        } else {
            pincel.drawArc(x, (int) (lado * 0.52), ancho, (int) (lado * 0.26), 200, 140);
        }
    }

    private static Graphics2D preparar(BufferedImage lienzo) {
        Graphics2D pincel = lienzo.createGraphics();
        pincel.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        pincel.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        return pincel;
    }

    private static Color colorDe(int semilla, float brillo) {
        float matiz = (semilla * 0.137f) % 1f;
        return Color.getHSBColor(matiz, 0.58f, brillo);
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
