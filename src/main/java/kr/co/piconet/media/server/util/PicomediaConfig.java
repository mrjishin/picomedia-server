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