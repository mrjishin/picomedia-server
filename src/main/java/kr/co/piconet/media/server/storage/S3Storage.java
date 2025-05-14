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

package kr.co.piconet.media.server.storage;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.Set;

import kr.co.piconet.media.server.util.S3Utils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

//@Slf4j
public class S3Storage implements Storage
{
	private final S3Client s3Client;
	private final String bucket;

	public S3Storage(S3Client s3Client, String bucket) {
		this.s3Client = s3Client;
		this.bucket = bucket;
	}

	@Override
	public boolean exists(String filePath) throws Exception {
		try {
		    HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
		        .bucket(bucket)
		        .key(filePath)
		        .build();
		    s3Client.headObject(headObjectRequest);
		    return true;

		} catch (S3Exception e) {
		    if (e.statusCode() == 404) {
		        return false;
		    } else {
		        throw e;
		    }
		}
	}

	@Override
	public void write(InputStream in, String filePath, long size) throws Exception {
		String contentType = URLConnection.guessContentTypeFromName(filePath);
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucket)
				.key(filePath)
				.contentType(contentType)
				.contentLength(size)
				.build();

		PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(in, size));
		if(!response.sdkHttpResponse().statusText().orElse("FAIL").equals("OK")){
            throw new IllegalStateException("Failed to upload the file.");
        }
	}

	@Override
	public InputStream read(String fileName) throws Exception {
		GetObjectRequest req = GetObjectRequest
				.builder()
				.bucket(bucket)
				.key(fileName)
				.build();
		return s3Client.getObject(req, ResponseTransformer.toInputStream());
	}

	@Override
	public void rmdir(String path) throws Exception {
		S3Utils.rmdir(s3Client, bucket, path);
	}

	@Override
	public void delete(String fileName) throws Exception {
		S3Utils.deletes(s3Client, bucket, Set.of(fileName));
	}
}