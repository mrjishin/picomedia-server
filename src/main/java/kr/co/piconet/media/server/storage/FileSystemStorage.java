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
package kr.co.piconet.media.server.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.apache.commons.io.IOUtils;

//@Slf4j
public class FileSystemStorage implements Storage {
	private final File baseDir;

	public FileSystemStorage(File baseDir) {
		this.baseDir = baseDir;
	}

	@Override
	public boolean exists(String filePath) throws Exception {
		return new File(baseDir, filePath).exists();
	}

	@Override
	public void write(InputStream in, String filePath, long size) throws Exception {
		File file = new File(baseDir, filePath);
		if(!file.getParentFile().exists())
			file.getParentFile().mkdirs();

		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			IOUtils.copy(in, out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	@Override
	public InputStream read(String filePath) throws Exception {
		File file = new File(baseDir, filePath);
		return new FileInputStream(file);
	}

	@Override
	public void rmdir(String path) throws Exception {
		File dir = new File(baseDir, path);
		Files.walk(dir.toPath())
	    .sorted(Comparator.reverseOrder())
	    .map(Path::toFile)
	    .forEach(File::delete);
	}

	@Override
	public void delete(String filePath) throws Exception {
		File file = new File(baseDir, filePath);
		file.delete();
	}
}