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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/demo")
@RestController
public class DemoController {
	@GetMapping(value = {"", "/"})
	public ResponseEntity<?> demo() {
		List<Map<String,Object>> list = new ArrayList<>();
		for(int i=0; i<30; i++) {
			Map<String,Object> itemMap = new LinkedHashMap<>();
			itemMap.put("id", i+1);
			itemMap.put("name", String.format("테스트 이름 %d", i+1));
			list.add(itemMap);
		}
		return ResponseEntity.ok(list);
	}
}