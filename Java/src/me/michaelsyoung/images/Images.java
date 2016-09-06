package me.michaelsyoung.images;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author myoun
 */
public class Images {

    /**
     * A method that will create an exact copy of an image.
     *
     * @param img
     *            The source image
     * @return A copy of the image.
     */
    public static BufferedImage deepCopy(final BufferedImage img) {
        return deepCopy(img, img.getType());
    }

    public static BufferedImage deepCopy(final BufferedImage img, final int type) {
        BufferedImage b = new BufferedImage(img.getWidth(), img.getHeight(), type);
        Graphics g = b.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return b;
    }

    /**
     * Reduce an image to a black and white state.
     *
     * @param img
     *            The original source image.
     * @return A new image that is the Otsu reduced image.
     */
    public static BufferedImage otsuReduce(BufferedImage img) {
        if (img.getType() != BufferedImage.TYPE_BYTE_GRAY) {
            img = toGrayScale(img);
        }
        BufferedImage reducedImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        int[] histogram = new int[256];
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                ++histogram[img.getRGB(x, y) & 0xFF];
            }
        }

        double threshold = otsuReductionThreshold(histogram, img.getWidth() * img.getHeight());

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                if ((img.getRGB(x, y) & 0xFF) < threshold) {
                    reducedImage.setRGB(x, y, Color.BLACK.getRGB());
                }
                else {
                    reducedImage.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }

        return reducedImage;
    }

    /**
     * Calculate the Otsu threshold for reducing an image to black and white.
     *
     * @param histogram
     *            A histogram of the pixel values, ranging from 0 to 255
     * @param numberOfPixels
     *            The number of pixels in the image.
     * @return The value of the red/green/blue components of a greyscale image
     *         that is the cutoff point.
     */
    private static double otsuReductionThreshold(final int[] histogram, final int numberOfPixels) {
        long sum = 0;
        for (int i = 1; i < histogram.length; i++) {
            sum += i * histogram[i];
        }
        long sumB = 0;
        long wB = 0;
        long wF;
        double mB;
        double mF;
        double max = 0;
        double between;
        double threshold1 = 0;
        double threshold2 = 0;
        for (int i = 0; i < histogram.length; i++) {
            wB += histogram[i];
            if (wB == 0) {
                continue;
            }
            wF = numberOfPixels - wB;
            if (wF == 0) {
                break;
            }
            sumB += i * histogram[i];
            mB = (double) sumB / (double) wB;
            mF = (double) (sum - sumB) / (double) wF;
            between = wB * wF * (mB - mF) * (mB - mF);
            if (between >= max) {
                threshold1 = i;
                if (between > max) {
                    threshold2 = i;
                }
                max = between;
            }
        }
        return (threshold1 + threshold2) / 2.0;
    }

    public static BufferedImage toGrayScale(final BufferedImage img) {
        BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = newImage.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return newImage;
    }

    /**
     * Invert the pixel values of the image.
     *
     * @param img
     *            The original source image.
     * @return An inverted image.
     */
    public static BufferedImage invert(final BufferedImage img) {
        BufferedImage inverted = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color c = new Color(img.getRGB(x, y));

                inverted.setRGB(x, y, new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue()).getRGB());
            }
        }

        return inverted;
    }

    /**
     * Display multiple images
     *
     * @param imgs
     *            A list of images to be displayed.
     */
    public static void displayImages(final BufferedImage... imgs) {
        displayImages("", imgs);
    }

    /**
     * Display images to make life easier.
     *
     * @param title
     *            Title of the Frame the images will be in
     * @param imgs
     *            The images to be added to the frame.
     */
    public static void displayImages(final String title, final BufferedImage... imgs) {
        JFrame jf = new JFrame(title);
        jf.setLayout(new FlowLayout());
        for (BufferedImage img : imgs) {
            jf.add(new JLabel(new ImageIcon(img)));
        }
        jf.pack();
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jf.setVisible(true);
    }

    public static void writeImages(final String path, final BufferedImage... imgs) {
        JFrame jf = new JFrame();
        jf.setLayout(new FlowLayout());
        for (BufferedImage img : imgs) {
            jf.add(new JLabel(new ImageIcon(img)));
        }
        jf.pack();
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        BufferedImage outputImage = new BufferedImage(jf.getWidth(), jf.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics gr = outputImage.createGraphics();
        jf.setVisible(true);
        jf.printAll(gr);
        jf.dispose();
        gr.dispose();
        try {
            ImageIO.write(outputImage, "png", new File(path));
        }
        catch (Exception e) {
        }
    }

    /**
     * A wrapper method for ImageIO.read because I got tired of typing the full
     * thing.
     *
     * @param path
     *            The file path
     * @return
     * @throws IOException
     *             For whatever reason ImageIO throws it
     */
    public static BufferedImage read(final String path) throws IOException {
        return ImageIO.read(new File(path));
    }

    public static BufferedImage scale(final BufferedImage src, final double scaleFactor) {
        if (scaleFactor < 0) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        int scaledWidth = (int) (width * scaleFactor);
        int scaledHeight = (int) (height * scaleFactor);
        return scale(src, scaledWidth, scaledHeight);
    }

    public static BufferedImage scale(final BufferedImage src, final int scaledWidth, final int scaledHeight) {
        BufferedImage scaled = new BufferedImage(scaledWidth, scaledHeight, src.getType());

        Graphics2D graphics = scaled.createGraphics();
        graphics.drawImage(src, 0, 0, scaledWidth, scaledHeight, 0, 0, src.getWidth(), src.getHeight(), null);
        graphics.dispose();
        return scaled;
    }
}
