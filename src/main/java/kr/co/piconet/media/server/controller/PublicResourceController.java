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

package kr.co.piconet.media.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.piconet.media.server.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/public")
@RestController
public class PublicResourceController {

	private final StorageService storageService;

	/**
	 * download a file
	 */
	@GetMapping("/{*path}")
	public ResponseEntity<?> download(
		@PathVariable("path") String path)
	{
		try
		{
			String filePath = StorageService.combinePaths(StorageService.PUBLIC, path);
			return storageService.download(filePath);
		}
		catch(NoSuchKeyException e) {
			log.error("NoSuchKeyException", e);
			return ResponseEntity.notFound().build();
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}

	@DeleteMapping("/{*path}")
	public ResponseEntity<?> deleteFile(
			@PathVariable("path") String path,
			@RequestParam(name = "purge", required=false, defaultValue="false") Boolean purge)
	{
		try {

			String filePath = StorageService.combinePaths(StorageService.PUBLIC, path);
			storageService.delete(filePath);

			try {
				String derivedPath = StorageService.combinePaths(StorageService.DERIVED, StorageService.PUBLIC, path);
				storageService.rmdir(derivedPath);
			}catch(Exception e) {
				e.printStackTrace();
			}

			return ResponseEntity.ok().build();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
}