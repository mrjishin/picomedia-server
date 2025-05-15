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