/**
 * Copyright 2024 Jaeik Shin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
