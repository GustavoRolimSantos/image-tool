package imagetool;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageTool {

	private static final int THRESHOLD = 35, TRANSPARENT = 0;

	private File inputFile, outputFile;

	private BufferedImage outputImage;

	public ImageTool(File inputFile) {
		this.inputFile = inputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public void removeColor(Color replaceColor) {
		try {
			if (outputImage == null)
				outputImage = ImageIO.read(inputFile);

			int width = outputImage.getWidth(null), height = outputImage.getHeight(null);

			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			Graphics g = image.getGraphics();

			g.drawImage(outputImage, 0, 0, null);

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int pixel = image.getRGB(x, y);
					Color color = new Color(pixel);

					int dr = Math.abs(color.getRed() - replaceColor.getRed()), dg = Math.abs(color.getGreen() - replaceColor.getGreen()), db = Math.abs(color.getBlue() - replaceColor.getBlue());

					if (dr < THRESHOLD && dg < THRESHOLD && db < THRESHOLD) {
						image.setRGB(x, y, TRANSPARENT);
					}
				}
			}

			outputImage = image;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeColor(Color oldColor, Color newColor) {
		try {
			if (outputImage == null)
				outputImage = ImageIO.read(inputFile);

			int RGB_MASK = 0x00ffffff;

			int oldRGB = oldColor.getRed() << 16 | oldColor.getGreen() << 8 | oldColor.getBlue();
			int toggleRGB = oldRGB ^ (newColor.getRed() << 16 | newColor.getGreen() << 8 | newColor.getBlue());

			int w = outputImage.getWidth();
			int h = outputImage.getHeight();

			int[] rgb = outputImage.getRGB(0, 0, w, h, null, 0, w);
			for (int i = 0; i < rgb.length; i++) {
				if ((rgb[i] & RGB_MASK) == oldRGB) {
					rgb[i] ^= toggleRGB;
				}
			}
			outputImage.setRGB(0, 0, w, h, rgb, 0, w);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void overlay(Color color) {
		try {
			if (outputImage == null)
				outputImage = ImageIO.read(inputFile);

			int width = outputImage.getWidth(), height = outputImage.getHeight();
			WritableRaster raster = outputImage.getRaster();

			for (int xx = 0; xx < width; xx++) {
				for (int yy = 0; yy < height; yy++) {
					int[] pixels = raster.getPixel(xx, yy, (int[]) null);
					pixels[0] = color.getRed();
					pixels[1] = color.getGreen();
					pixels[2] = color.getBlue();
					raster.setPixel(xx, yy, pixels);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void makeRoundedCorner(int cornerRadius) {
		try {
			if (outputImage == null)
				outputImage = ImageIO.read(inputFile);

			int w = outputImage.getWidth(), h = outputImage.getHeight();
			BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

			Graphics2D g2 = output.createGraphics();

			g2.setComposite(AlphaComposite.Src);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.WHITE);
			g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

			g2.setComposite(AlphaComposite.SrcAtop);
			g2.drawImage(outputImage, 0, 0, null);

			g2.dispose();
			
			outputImage = output;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void resize(BufferedImage img, int height, int width) {
		try {
			if (outputImage == null)
				outputImage = ImageIO.read(inputFile);

			Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = resized.createGraphics();
			g2d.drawImage(tmp, 0, 0, null);
			g2d.dispose();
			
			outputImage = resized;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getImage() {
		return outputImage;
	}

	public void save(String fileType) throws Exception {
		if (outputFile == null)
			throw new Exception("The output file is null.");

		try {
			ImageIO.write(outputImage, fileType, outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}