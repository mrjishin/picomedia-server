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

package kr.co.piconet.media.server.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.piconet.media.server.storage.FileSystemStorage;
import kr.co.piconet.media.server.storage.S3Storage;
import kr.co.piconet.media.server.storage.Storage;
import kr.co.piconet.media.server.util.PicomediaConfig;
import kr.co.piconet.media.server.util.PicomediaConfig.Picomedia;
import kr.co.piconet.media.server.util.PicomediaConfig.Picomedia.Aws;
import kr.co.piconet.media.server.util.PicomediaConfig.Picomedia.PicomediaBuilder;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class ServerConfig {

	@Bean
	Storage storage(PicomediaConfig config)
	{
 		if(config.getPicomedia().getType().equals("s3"))
		{
			Aws aws = config.getPicomedia().getAws();
			S3Client s3Client = S3Client.builder()
					.region(Region.of(aws.getRegion()))
					.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(aws.getAccessKey(), aws.getSecretKey())))
					.build();
			return new S3Storage(s3Client, config.getPicomedia().getAws().getS3().getBucket());
		}
		else if(config.getPicomedia().getType().equals("fs"))
		{
			File baseDir = new File(config.getPicomedia().getFs().getBaseDir());
			return new FileSystemStorage(baseDir);
		}
		return null;
	}

	@Bean
	PicomediaConfig picomediaConfig() {
		PicomediaConfig config = null;
		String configPath = System.getProperty("config.file");
		Reader reader = null;
		try {
			reader = new FileReader(configPath);
			Properties p = new Properties();
			p.load(reader);
			
			String publicKeyPath = p.getProperty("picomedia.publicKeyPath");
			String type = p.getProperty("picomedia.type");
			String baseDir = p.getProperty("picomedia.baseDir");
			String awsAccessKey = p.getProperty("picomedia.aws.accessKey");
			String awsSecretKey = p.getProperty("picomedia.aws.secretKey");
			String awsRegion = p.getProperty("picomedia.aws.region");
			String awsS3Bucket = p.getProperty("picomedia.aws.s3.bucket");

			PicomediaBuilder builder = Picomedia.builder()
					.type(type)
					.publicKeyPath(publicKeyPath);

			if(type.equals("fs"))
			{
				builder.fs(
					Picomedia.Fs.builder().baseDir(baseDir).build()
				);
			}
			else if(type.equals("fs"))
			{
				builder.aws(
						Picomedia.Aws.builder()
							.accessKey(awsAccessKey)
							.secretKey(awsSecretKey)
							.s3(
								Picomedia.Aws.S3.builder().bucket(awsS3Bucket).build()
							)
							.region(awsRegion)
							.build()
				);
			}
			
			config = PicomediaConfig.builder()
					.picomedia(
						builder.build()
					)
					.build();
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(reader!=null) { try { reader.close(); } catch (IOException e) { } }
		}
		return config;
	}
}