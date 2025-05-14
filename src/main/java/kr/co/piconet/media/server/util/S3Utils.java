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