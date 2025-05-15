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

package kr.co.piconet.media.sdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
Usage:
  String url = "http://localhost:9090/public/test/sample.jpg";
  String transformUrl = TransformUrlBuilder.of(url)
    .width(240)
    .height(200)
    .crop(false)
    .build();
  System.out.println(transformUrl); 
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