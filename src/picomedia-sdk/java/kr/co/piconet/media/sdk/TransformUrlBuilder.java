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

package kr.co.piconet.media.sdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Usage:
 * 	String url = "http://localhost:9090/public/test/sample.jpg";
 * 	String transformUrl = TransformUrlBuilder.of(url).width(240).height(200).crop(false).build();
 * 	System.out.println(transformUrl); 
 */
public class TransformUrlBuilder {
	private String url;
	private int width;
	private int height;
	private boolean crop;

	private boolean hasWidth = false;
	private boolean hasHeight = false;
	private boolean hasCrop = false;

	private TransformUrlBuilder(String url) {
		this.url = url;
	}

	public static TransformUrlBuilder of(String url) {
		return new TransformUrlBuilder(url);
	}

	public int width() {
		return width;
	}
	public TransformUrlBuilder width(int width) {
		this.width = width;
		hasWidth = true;
		return this;
	}

	public int height() {
		return height;
	}
	public TransformUrlBuilder height(int height) {
		this.height = height;
		hasHeight = true;
		return this;
	}

	public boolean crop() {
		return crop;
	}
	public TransformUrlBuilder crop(boolean crop) {
		this.crop = crop;
		hasCrop = true;
		return this;
	}

	public String build()
	{
		int begin = url.indexOf("/", url.indexOf("://") + 3) + 1;
		int end = url.lastIndexOf("/");
		String path = url.substring(begin, end);
		List<String> pathItems = new ArrayList<>(Arrays.asList(path.split("/")));
		if(!pathItems.get(0).startsWith("public") && !pathItems.get(0).startsWith("private"))
			return url;

		List<String> options = new ArrayList<>();
		if(hasWidth) {
			options.add(String.format("w_%d", width));
		}
		if(hasHeight) {
			options.add(String.format("h_%d", height));
		}
		if(hasCrop && crop) {
			options.add("c_crop");
		}

		if(!options.isEmpty()) {
			pathItems.add(1, String.join(",", options));
			try {
				return url.substring(0, begin) + String.join("/", pathItems) + url.substring(end);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return url;
	}
}