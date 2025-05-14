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

package kr.co.piconet.media.server.convert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvertOption {
	public enum Key {
		width, height, crop, stream
	}

	private Key key;
	private Object value;

	public ConvertOption(Key key, Integer value) {
		this.key = key;
		this.value = value;
	}

	public ConvertOption(Key key, Boolean value) {
		this.key = key;
		this.value = value;
	}

	public Key getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	public int getIntValue() {
		return (int)value;
	}

	public boolean getBoolValue() {
		return (boolean)value;
	}

	public static List<ConvertOption> parse(String path) {

		List<ConvertOption> options = new ArrayList<>();

		long slashCnt = path.chars().filter(ch -> ch == '/').count();
		if(slashCnt <= 2)
			return options;

		String stripPath = path.startsWith("/") ? path.substring(1) : path;

		int pos1 = stripPath.indexOf('/');
		int pos2 = stripPath.indexOf('/', pos1 + 1);

		String strOpts = stripPath.substring(pos1 + 1, pos2);
		boolean hasOption = strOpts.contains("w_") ||
							strOpts.contains("h_") ||
							strOpts.contains("c_crop") ||
							strOpts.contains("s_stream");
		if(hasOption)
		{
			String contentType = "";
			try {
				contentType = Files.probeContentType(Paths.get(path));
			} catch (IOException e) {
				e.printStackTrace();
			}
			Map<Key,ConvertOption> optionsMap = new HashMap<>();
			String[] opts = strOpts.split(",");
			for(String opt: opts) {
				if(contentType.startsWith("image/")) {
					if(opt.startsWith("w_")) {
						try {
							int width = Integer.parseInt(opt.substring(opt.indexOf("_")+1));
							optionsMap.put(Key.width, new ConvertOption(Key.width, width));
						} catch (Exception e) { }
					} else if(opt.startsWith("h_")) {
						try {
							int height = Integer.parseInt(opt.substring(opt.indexOf("_")+1));
							optionsMap.put(Key.height, new ConvertOption(Key.height, height));
						} catch (Exception e) { }
					} else if(opt.equals("c_crop")) {
						optionsMap.put(Key.crop, new ConvertOption(Key.crop, true));
					}
				} else if(contentType.startsWith("video/")) {
					if(opt.equals("s_stream")) {
						optionsMap.put(Key.stream, new ConvertOption(Key.stream, true));
					}
				}
			}
			if(optionsMap.containsKey(Key.width)) {
				options.add(optionsMap.get(Key.width));
			}
			if(optionsMap.containsKey(Key.height)) {
				options.add(optionsMap.get(Key.height));
			}
			if(optionsMap.containsKey(Key.crop)) {
				options.add(optionsMap.get(Key.crop));
			}
			if(optionsMap.containsKey(Key.stream)) {
				options.add(optionsMap.get(Key.stream));
			}
		}
		return options;
	}
}