package net.seeseekey.marauder.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.FlatteningPathIterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class PolygonOutliner {

    public static void main(String[] arg) throws IOException {

        // Load image
        BufferedImage bufferedImage = ImageIO.read(new File("/path/to/polygon.png"));

        // Convert image to black and white
        BufferedImage bufferedImageBlackAndWhite = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

        Graphics2D graphic = bufferedImageBlackAndWhite.createGraphics();
        graphic.drawImage(bufferedImage, 0, 0, Color.WHITE, null);
        graphic.dispose();

        // Get polygon outline
        Polygon polygonOutline = getPolygonOutline(bufferedImageBlackAndWhite);

        // Save polygon to disc?
        System.out.println("Number of points: " + polygonOutline.npoints);

        PrintWriter printWriter = new PrintWriter("/path/to/polygon.txt");

        for (int i = 0; i < polygonOutline.npoints; i++) {

            int x = polygonOutline.xpoints[i];
            int y = polygonOutline.ypoints[i];

            printWriter.println(x + ", " + y + ",");
        }

        printWriter.close();
    }

    public static Area getOutline(BufferedImage image, Color color, boolean include, int tolerance) {
        Area area = new Area();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color pixel = new Color(image.getRGB(x, y));
                if (include) {
                    if (isIncluded(color, pixel, tolerance)) {
                        Rectangle r = new Rectangle(x, y, 1, 1);
                        area.add(new Area(r));
                    }
                } else {
                    if (!isIncluded(color, pixel, tolerance)) {
                        Rectangle r = new Rectangle(x, y, 1, 1);
                        area.add(new Area(r));
                    }
                }
            }
        }
        return area;
    }

    public static boolean isIncluded(Color target, Color pixel, int tolerance) {
        int rT = target.getRed();
        int gT = target.getGreen();
        int bT = target.getBlue();
        int rP = pixel.getRed();
        int gP = pixel.getGreen();
        int bP = pixel.getBlue();
        return (
                (rP - tolerance <= rT) && (rT <= rP + tolerance) &&
                        (gP - tolerance <= gT) && (gT <= gP + tolerance) &&
                        (bP - tolerance <= bT) && (bT <= bP + tolerance));
    }

    public static Polygon getPolygonOutline(BufferedImage image) {
        Area a = getOutline(image, new Color(0, 0, 0, 0), false, 10); // 10 or whatever color tolerance you want
        Polygon p = new Polygon();
        FlatteningPathIterator fpi = new FlatteningPathIterator(a.getPathIterator(null), 0.1); // 0.1 or how sloppy you want it
        double[] pts = new double[6];
        while (!fpi.isDone()) {
            switch (fpi.currentSegment(pts)) {
                case FlatteningPathIterator.SEG_MOVETO:
                case FlatteningPathIterator.SEG_LINETO:
                    p.addPoint((int) pts[0], (int) pts[1]);
                    break;
            }
            fpi.next();
        }
        return p;
    }
}
