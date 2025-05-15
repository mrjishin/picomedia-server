/**
 * This file is part of Pico Media Server.
 *
 * Copyright 2024 PICONET
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

package kr.co.piconet.media.server.controller;

import java.security.Principal;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.piconet.media.server.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/upload")
@RestController
public class UploadController {
	private final StorageService storageService;

	/**
	 * upload a file
	 */
	@PostMapping(value = { "", "/" })
	public ResponseEntity<?> uploadFile(
			@RequestParam(name = "target", required = false, defaultValue = "") String target,
			@RequestParam(name = "folder", required = false, defaultValue = "") String folder,
			@RequestParam(name = "file") MultipartFile partFile, HttpServletRequest request, Principal principal)
	{
		if (!target.equals("public") && !target.equals("private")) {
			return ResponseEntity.badRequest().body("Invalid target. (public or private)");
		}

		try {
			String fileName = partFile.getOriginalFilename();

			// check exists
			String fullPath = StorageService.combinePaths(
					target,
					folder,
					fileName);

			String path = FilenameUtils.getPath(fullPath);
			path = path.endsWith("/") ? path.substring(0, path.length()-1) : path;
			String baseName = FilenameUtils.getBaseName(fileName);
			String ext = FilenameUtils.getExtension(fileName);
			boolean hasExt = ext.length() > 0;

			String tmpFilePath = fullPath;
			int i = 0;
			while(!Thread.interrupted()) {
				boolean exists = storageService.exists(tmpFilePath);
				if(!exists) {
					fullPath = tmpFilePath;
					break;
				}
				tmpFilePath = String.format("%s/%s %d%s",
						path,
						baseName,
						(++i) + 1,
						hasExt ? "." + ext : "");
			}

			storageService.upload(fullPath, partFile);

			String url = UriComponentsBuilder.fromUriString(request.getRequestURL().toString()).replacePath(fullPath)
					.build().toUriString();

			Map<String, Object> resultMap = Map.of("url", url);
			return ResponseEntity.ok(resultMap);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
}