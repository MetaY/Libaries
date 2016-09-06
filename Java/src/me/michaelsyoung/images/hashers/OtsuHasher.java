package me.michaelsyoung.images.hashers;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.math.BigInteger;

public class OtsuHasher extends ImageHasher {

    private static final int DEFAULT_WIDTH = 8;
    private static final int DEFAULT_HEIGHT = 8;

    public OtsuHasher() {
        super(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public OtsuHasher(final int size) {
        super(size, size);
    }

    public OtsuHasher(final int width, final int height) {
        super(width, height);
    }

    @Override
    public String hash(final Image src, final int radix) {
        BufferedImage sourceImage = (BufferedImage) src;

        BufferedImage otsuImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D graphics = otsuImage.createGraphics();

        graphics.drawImage(sourceImage, 0, 0, this.getWidth(), this.getHeight(), 0, 0, sourceImage.getWidth(),
                sourceImage.getHeight(), null);
        graphics.dispose();

        int offset = this.getBitCount() - 1;

        //BufferedImage otsuImage = Images.otsuReduce(a);

        double threshold = this.otsuReduction(otsuImage);

        //Images.displayImages(Images.scale(sourceImage, .3), Images.scale(a, 20), Images.scale(otsuImage, 20));

        BigInteger hash = BigInteger.ZERO;
        for (int y = 0; y < otsuImage.getHeight(); y++) {
            for (int x = 0; x < otsuImage.getWidth(); x++) {
                if ((otsuImage.getRGB(x, y) & 0xFF) > threshold) {
                    hash = hash.setBit(offset);
                }
                --offset;
            }
        }
        return this.pad(hash.toString(radix), radix);
    }

    private int[] createHistogram(final BufferedImage img) {
        int[] histogram = new int[256];
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                ++histogram[img.getRGB(x, y) & 0xFF];
            }
        }
        return histogram;
    }

    private double otsuReduction(final BufferedImage img) {
        int[] histogram = this.createHistogram(img);
        return this.otsuReductionThreshold(histogram, img.getWidth() * img.getHeight());
    }

    private double otsuReductionThreshold(final int[] histogram, final int numberOfPixels) {
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
}
