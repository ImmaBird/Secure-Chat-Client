import java.io.*;
import java.util.*;

import javax.imageio.*; // loading and saving images

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.*;

public class SendableImage implements Serializable {
	private byte[] byteImage = null;

	public SendableImage(String f) {
		try {
			BufferedImage bi = ImageIO.read(new File(f));
			byteImage = toByteArray(bi);
		} catch (IOException e) {
			System.err.println("ERROR loading image: " + e);
			System.exit(-1);
		}
	}

	private byte[] toByteArray(BufferedImage bufferedImage) {
		if (bufferedImage != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ImageIO.write(bufferedImage, "png", baos);
			} catch (IOException e) {
				throw new IllegalStateException(e.toString());
			}
			return baos.toByteArray();
		}
		return new byte[0];
	}

	public BufferedImage fromByteArray() throws IOException {
		return ImageIO.read(new ByteArrayInputStream(byteImage));
	}

	public void SaveImage(String f) {
		try {
			BufferedImage bbii = fromByteArray();
			ImageIO.write(bbii, "png", new File(f));
		} catch (IOException ee) {
			System.err.println("IO ERROR saving image: " + ee);
			System.exit(-1);
		} catch (Exception e) {
			System.out.println("Exception while saving Image: " + e);
			System.exit(-1);
		}
	}
}
