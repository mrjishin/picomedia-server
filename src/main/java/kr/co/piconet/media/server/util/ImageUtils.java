/**
 * This file is part of Pico Media Server.
 *
 * Copyright (C) 2024 PICONET
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 **/

package kr.co.piconet.media.server.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

public class ImageUtils
{
	public static final int THUMB_WIDTH = 1280;
	public static final int THUMB_HEIGHT = 720;

	public static BufferedImage fromBytes(byte[] bytes) throws Exception {
		BufferedImage image = null;
		ByteArrayInputStream in = null;
		try {
			in = new ByteArrayInputStream(bytes);
			image = ImageIO.read(in);
		} finally {
			if(in!=null) { try { in.close(); } catch (IOException e) { } }
		}
		return image;
	}

	public static byte[] toBytes(BufferedImage image, String formatName) throws Exception {
		byte[] bytes = null;
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			ImageIO.write(image, formatName, out);
			out.flush();
			bytes = out.toByteArray();
		} finally {
			if(out!=null) { try { out.close(); } catch (IOException e) { } }
		}
		return bytes;
	}

	public static BufferedImage resize(
			BufferedImage image,
			int width,
			int height,
			boolean crop) throws Exception
	{
		if(width <= 0 && height <= 0) throw new Exception("invalid image size");

		if(width > image.getWidth() || height > image.getHeight()) {
			width = image.getWidth();
			height = image.getHeight();
		}

		if(width == 0) {
			double ratio = image.getWidth() * 1.0D / image.getHeight();
			width = (int)(height * ratio);
		} else if(height == 0) {
			double ratio = image.getHeight() * 1.0D / image.getWidth();
			height = (int)(height * ratio);
		}

		BufferedImage workingImage = image;
		if(crop) {
			int imgwidth = Math.min(image.getHeight(), image.getWidth());
			int imgheight = imgwidth;
			int x = (int)((image.getWidth() - imgwidth) / 2.0);
			int y = (int)((image.getHeight() - imgheight)/2.0);
			workingImage = Scalr.crop(workingImage, x, y, imgwidth, imgheight);
		}
		return Scalr.resize(workingImage, width, height);
	}
}
