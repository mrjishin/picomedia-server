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