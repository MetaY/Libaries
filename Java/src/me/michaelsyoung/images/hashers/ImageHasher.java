package me.michaelsyoung.images.hashers;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * An abstract base class that represents all of the shared functionality of all
 * image hash algorithms.
 *
 * @author Michael Young
 *
 */
public abstract class ImageHasher implements Comparable<ImageHasher> {

    protected static int N_GRAY = 256;

    /**
     * The default width of the hashed image if no width parameter is provided.
     */
    public static final int DEFAULT_WIDTH = 8;

    /**
     * The default height of the hashed image if no height parameter is
     * provided.
     */
    public static final int DEFAULT_HEIGHT = 8;

    /**
     * The default radix used for the hash if no radix parameter is provided.
     */
    public static final int DEFAULT_RADIX = 16;

    /**
     * The width used by the hashing algorithm to scale the image to reduce the
     * number of pixels to compare and to make aspect ratio not as much of a
     * problem.
     */
    private int width;

    /**
     * The height used by the hashing algorithm to scale the image to reduce the
     * number of pixels to compare and to make aspect ratio not as much of a
     * problem.
     */
    private int height;

    /**
     * Construct an ImageHasher object with default width and height parameters.
     */
    public ImageHasher() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Construct an ImageHasher with given width and height parameters.
     *
     * @param width
     *            The width of the scaled image used by the hashing algorithm.
     * @param height
     *            The height of the scaled image used by the hashing algorithm.
     */
    public ImageHasher(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Get the height the hashing algorithm uses to scale an image.
     *
     * @return The height of the scaled images
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Get the width the hashing algorithm uses to scale an image.
     *
     * @return The width of the scaled images.
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * @return
     */
    public int getBitCount() {
        return this.width * this.height;
    }

    /**
     * Set the height the hashing algorithm uses to scale an image.
     *
     * @param height
     *            The new height for the hashing algorithm to use.
     */
    public void setHeight(final int height) {
        this.height = height;
    }

    /**
     * Set the width the hashing algorithm uses to scale an image.
     *
     * @param width
     *            The new width for the hashing algorithm to use.
     */
    public void setWidth(final int width) {
        this.width = width;
    }

    /**
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public String hash(final String filePath) throws IOException {
        return this.hash(ImageIO.read(new File(filePath)));
    }

    /**
     * @param file
     * @return
     * @throws IOException
     */
    public String hash(final File file) throws IOException {
        return this.hash(ImageIO.read(file));
    }

    /**
     * @param uri
     * @return
     * @throws IOException
     */
    public String hash(final URL uri) throws IOException {
        return this.hash(ImageIO.read(uri));
    }

    public String hash(final Image image) {
        return this.hash(image, DEFAULT_RADIX);
    }

    /**
     * @param image
     * @param radix
     * @return
     */
    public abstract String hash(Image image, int radix);

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final ImageHasher imageHasher) {
        if (this.width < imageHasher.width) {
            return -1;
        }
        else if (this.width > imageHasher.width) {
            return 1;
        }
        else if (this.height < imageHasher.height) {
            return -1;
        }
        else if (this.height > imageHasher.height) {
            return 1;
        }
        else {
            return 0;
        }
    }

    protected String pad(final String hashString, final int radix) {
        int expectedLength = (int) (Math.ceil(this.getBitCount() / this.log2(radix)));
        StringBuilder padded = new StringBuilder();
        for (int i = 0; i < expectedLength - hashString.length(); i++) {
            padded.append('0');
        }
        return padded.append(hashString).toString();
    }

    protected double log2(final int a) {
        return Math.log(a) / Math.log(2);
    }
}
