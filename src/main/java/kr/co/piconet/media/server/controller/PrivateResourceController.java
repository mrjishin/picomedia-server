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
@RequestMapping("/private")
@RestController
public class PrivateResourceController {

	private final StorageService storageService;

	/**
	 * download a file
	 */
	@GetMapping("/{*path}")
	public ResponseEntity<?> downloadFile(
		@PathVariable("path") String path)
	{
		try
		{
			String filePath = StorageService.combinePaths(StorageService.PRIVATE, path);
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

			String filePath = StorageService.combinePaths(StorageService.PRIVATE, path);
			storageService.delete(filePath);

			String derivedPath = StorageService.combinePaths(StorageService.DERIVED, StorageService.PRIVATE, path);
			storageService.rmdir(derivedPath);

			return ResponseEntity.ok().build();
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
}