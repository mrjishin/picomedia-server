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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

@Slf4j
public class S3Utils
{
	public static void rmdir(S3Client s3Client, String bucket, String prefix) throws Exception
	{
		ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
	            .bucket(bucket)
	            .prefix(prefix)
	            .build();

	    ListObjectsV2Iterable paginatedListResponse = s3Client.listObjectsV2Paginator(listRequest);

	    for (ListObjectsV2Response listResponse : paginatedListResponse) {
	    	List<ObjectIdentifier> objects = listResponse.contents().stream()
	              .map(s3Object -> ObjectIdentifier.builder().key(s3Object.key()).build())
	              .toList();
	    	if (objects.isEmpty()) {
	    		break;
	    	}
	    	DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
              .bucket(bucket)
              .delete(Delete.builder().objects(objects).build())
              .build();

	    	DeleteObjectsResponse deleteObjectsResponse = s3Client.deleteObjects(deleteRequest);
	    	log.info("s3.rmdir.deleteCount: {}", deleteObjectsResponse.deleted().size());
	    }
	}

	public static void deletes(S3Client s3Client, String bucket, Set<String> keys) throws Exception
	{
		Set<ObjectIdentifier> identifiers = new HashSet<>();
		for(String key: keys) {
			identifiers.add(ObjectIdentifier.builder().key(key).build());
		}
		
		Delete del = Delete.builder()
                .objects(identifiers)
                .build();

		DeleteObjectsRequest multiObjectDeleteRequest = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(del)
                .build();

		DeleteObjectsResponse deleteObjectsResponse = s3Client.deleteObjects(multiObjectDeleteRequest);
		log.info("s3.rmdir.deletes: {}", deleteObjectsResponse.deleted().size());
	}
}