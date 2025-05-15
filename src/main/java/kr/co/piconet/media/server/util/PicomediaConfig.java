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

package kr.co.piconet.media.server.util;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class PicomediaConfig {

	private Picomedia picomedia;

	@Getter @Builder
	public static class Picomedia {
		private String type;
		private String publicKeyPath;
		private Aws aws;
		private Fs fs;

		@Getter @Builder
		public static class Aws {
			private String accessKey;
			private String secretKey;
			private String region;
			private S3 s3;

			@Getter @Builder
			public static class S3 {
				private String bucket;
			}
		}

		@Getter @Builder
		public static class Fs {
			private String baseDir;
		}
	}
}