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

package kr.co.piconet.media.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
Usage:
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
 */
public class PicoMediaUploader
{
	public enum Target {
		PRIVATE, PUBLIC
	}

	public static class PicoMediaUploadResult {
		private String url;
		public PicoMediaUploadResult(String url) {
			this.url = url;
		}
		public String url() {
			return url;
		}
	}

	private String endpoint;
	private String apiKey;
	private Target target;
	private String folder;

	public static PicoMediaUploader of(String endpoint, String apiKey) {
		return new PicoMediaUploader(endpoint, apiKey);
	}

	public PicoMediaUploader(String endpoint, String apiKey) {
		this.endpoint = endpoint;
		this.apiKey = apiKey;
	}

	public PicoMediaUploader target(Target target) {
		this.target = target;
		return this;
	}

	public PicoMediaUploader folder(String folder) {
		this.folder = folder;
		return this;
	}

//	public PicoMediaUploader input(InputStream input) {
//		this.input = input;
//		return this;
//	}

	public PicoMediaUploadResult upload(String filePath) throws Exception
	{
		return upload(new File(filePath));
	}

	public PicoMediaUploadResult upload(File file) throws Exception
	{
		InputStream in  = null;
		try {
			in = new FileInputStream(file);
			return upload(file.getName(), in);
		} finally {
			if(in != null) { try { in.close(); } catch (IOException e) { } }
		}
	}

	public PicoMediaUploadResult upload(String fileName, InputStream input) throws Exception
	{
		if(endpoint == null) {
			throw new RuntimeException(new NullPointerException("endpoint is null."));
		}

		if(target == null) {
			throw new RuntimeException(new NullPointerException("target is null."));
		}

		if(fileName == null) {
			throw new RuntimeException(new NullPointerException("fileName is null."));
		}

		if(input == null) {
			throw new RuntimeException(new NullPointerException("input is null."));
		}

		final MultipartEntityBuilder httpEntityBuilder = MultipartEntityBuilder.create()
			.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

		httpEntityBuilder.addTextBody("target", target.toString().toLowerCase());
		if(folder != null && folder.trim().length() > 0) {
			httpEntityBuilder.addTextBody("folder", folder);
		}
		httpEntityBuilder.addBinaryBody("file", input, ContentType.MULTIPART_FORM_DATA, fileName);

		HttpEntity httpEntity = httpEntityBuilder.build();

		HttpUriRequest request = RequestBuilder
                .post(endpoint + "/upload")
                .setHeader("Authorization", "Bearer " + apiKey)
                .setEntity(httpEntity)
                .build();

		ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        };

        String responseBody = HttpClients.createDefault().execute(request, responseHandler);

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject)parser.parse(responseBody);
        String url = (String)jsonObject.get("url");

        PicoMediaUploadResult result = new PicoMediaUploadResult(url);

        return result;
	}
}