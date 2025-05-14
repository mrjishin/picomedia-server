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
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.piconet.media.server.service.StorageService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/purge")
@RestController
public class PurgeController
{
	private final StorageService storageService;

	@DeleteMapping(value = {"", "/"})
	public ResponseEntity<?> purgeGlobalDerived() {

		pergeAll();
		
		return ResponseEntity.ok().build();
	}

	@Async
	private void pergeAll ()
	{
		try {
			storageService.rmdir(StorageService.DERIVED);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}