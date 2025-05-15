/**
 * Copyright 2024 Jaeik Shin
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