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