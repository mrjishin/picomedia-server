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

package kr.co.piconet.media.server.test;

import java.io.File;

import kr.co.piconet.media.sdk.PicoMediaUploader;

public class FileUploadTest {


	public static void main(String[] args) {

		String endpoint = "http://localhost:9090";
		String apiKey = "[YOUR_ACCESS_TOKEN]";
		File file = new File("./sample.jpg");
		try {
			PicoMediaUploader.PicoMediaUploadResult uploadResult = PicoMediaUploader.of(endpoint, apiKey)
				.target(PicoMediaUploader.Target.PUBLIC)
				.folder("test1/test2/test3")
				.upload(file);

			String url = uploadResult.url();
			System.out.format("picomedia.upload.result.url: %s%n", url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done...");
	}
}