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

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

//@Slf4j
@RequestMapping("/test")
@Controller
public class TestController {
	@GetMapping(value = {"", "/"})
	public String index(
			HttpServletRequest request,
			ModelMap modelMap)
	{
//		String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
//		        .replacePath(null)
//		        .build()
//		        .toUriString();
//
//		modelMap.put("baseUrl", baseUrl);
//
//		log.info("baseUrl", baseUrl);

		return "test/index";
	}
}