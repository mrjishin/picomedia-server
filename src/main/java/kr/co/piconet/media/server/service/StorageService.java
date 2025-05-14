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

package kr.co.piconet.media.server.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.co.piconet.media.server.convert.ConvertOption;
import kr.co.piconet.media.server.storage.Storage;
import kr.co.piconet.media.server.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@Slf4j
@RequiredArgsConstructor
@Service
public class StorageService
{
	public static final String PUBLIC = "public";
	public static final String PRIVATE = "private";
	public static final String DERIVED = "__derived__";

	private final Storage storage;

	public void upload(
			String path,
			MultipartFile partFile) throws Exception
	{
		long fileSize = partFile.getSize();
		storage.write(partFile.getInputStream(), path, fileSize);
	}

	/**
	 * download a file
	 */
	public ResponseEntity<?> download(String path) throws Exception
	{
		String fileName = FilenameUtils.getName(path);
		String contentType = Files.probeContentType(Paths.get(path));
		if(contentType == null)
			contentType = "application/octet-stream";

		if(contentType.startsWith("image/"))
		{
			List<ConvertOption> options = ConvertOption.parse(path);
			if(options.size() > 0)
			{
				int width = 0, height = 0;
				boolean isCrop = false;
				List<String> opts = new ArrayList<>();
				for(ConvertOption option: options) {
					if(option.getKey().equals(ConvertOption.Key.width)) {
						width = option.getIntValue();
						opts.add(String.format("w_%d", width));
					} else if(option.getKey().equals(ConvertOption.Key.height)) {
						height = option.getIntValue();
						opts.add(String.format("h_%d", height));
					} else if(option.getKey().equals(ConvertOption.Key.crop)) {
						isCrop = option.getBoolValue();
						if(isCrop) {
							opts.add("c_crop");
						}
					}
				}
				if(width > 0 || height > 0 || isCrop) {
					String ext = FilenameUtils.getExtension(fileName);
					List<String> paths = new ArrayList<>(Arrays.asList(path.split("/")));
					String optsPath = paths.remove(1);
					String derivedParentPath = combinePaths(paths.toArray(new String[]{}));
					String derivedFileName = optsPath.replaceAll(",",  "-") + "." + ext;
					String derivedFilePath = combinePaths(DERIVED, derivedParentPath, derivedFileName);
					try
					{
						InputStream in = storage.read(derivedFilePath);
						return downloadFile(in, fileName);
					}
					catch(FileNotFoundException | NoSuchKeyException e)
					{
						try {
							int pos1 = path.indexOf('/');
							int pos2 = path.indexOf('/', pos1 + 1);
							String orgFilePath = combinePaths(path.substring(0, pos1),  path.substring(pos2 + 1));
	
							InputStream orgIn = storage.read(orgFilePath);
							byte[] bytes = orgIn.readAllBytes();
							IOUtils.closeQuietly(orgIn);
	
							BufferedImage image = ImageUtils.fromBytes(bytes);
	
							if(width > image.getWidth())
								width = image.getWidth();
	
							if(height > image.getHeight())
								height = image.getHeight();
							
							BufferedImage resizedImage = ImageUtils.resize(image, width, height, isCrop);
							byte[] resizeBytes = ImageUtils.toBytes(resizedImage, ext);
							InputStream resizedIn = new ByteArrayInputStream(resizeBytes);
	
							storage.write(resizedIn, derivedFilePath, resizeBytes.length);
							IOUtils.closeQuietly(resizedIn);
	
							ByteArrayInputStream bin = new ByteArrayInputStream(resizeBytes);
							return downloadFile(bin, fileName);
						} catch(FileNotFoundException | NoSuchKeyException e2) {
							log.error("file not found. {}", derivedFilePath);
							return ResponseEntity.notFound().build();
						}
					}
				}
			}
		}
		else if(contentType.startsWith("video/"))
		{
			List<ConvertOption> options = ConvertOption.parse(path);
			if(options.size() > 0)
			{
				for(ConvertOption option: options) {
					if(option.getKey().equals(ConvertOption.Key.stream)) {
						// TODO video streamming
						break;
					}
				}
			}
		}

		InputStream in = storage.read(path);
		return downloadFile(in, fileName);
	}

	public boolean exists(String filePath) throws Exception {
		return storage.exists(filePath);
	}
	
	/**
	 * process downloading a file
	 */
	private static ResponseEntity<?> downloadFile(InputStream in, String fileName) throws Exception
	{
		String contentType = Files.probeContentType(Paths.get(fileName));
		if(contentType == null)
			contentType = "application/octet-stream";
		boolean isInline = contentType.startsWith("image/");
		InputStreamResource resource = new InputStreamResource(in);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, (isInline ? "inline" : "attachment") + "; filename=" + new String(fileName.getBytes(), StandardCharsets.ISO_8859_1));
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");

		return ResponseEntity.ok()
	            .headers(headers)
	            .contentType(MediaType.parseMediaType(contentType))
	            .body(resource);
	}

	public void rmdir(String path) throws Exception {
		storage.rmdir(path);
	}
	
	
	public void delete(String path) throws Exception {
		storage.delete(path);
	}

	public static String getParentPath(String path) {
		return path.substring(0, path.lastIndexOf("/"));
	}

	public static String combinePaths(String... paths) {
		List<String> list = new ArrayList<>();
		for(String path: paths) {
			String path2 = removeStartEndSlashs(path);
			String[] items = path2.split("/");
			list.addAll(List.of(items));
		}

		StringBuilder sb = new StringBuilder();
		for(int i=0; i<list.size(); i++) {
			String path = list.get(i);
			if(path.length() == 0) continue;
			sb.append(path);
			if(i<list.size()-1)
				sb.append("/");
		}
		return sb.toString();
	}

	public static String removeStartEndSlashs(String path) {
		String p = path;
		p = p.startsWith("/") ? p.substring(1) : p;
		return p.endsWith("/") ? p.substring(0, p.length() - 1) : p;
	}
}