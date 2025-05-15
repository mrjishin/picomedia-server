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

import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'package:mime/mime.dart';

enum PicomediaTarget { public, private }

/**
Usage)
  final baseUrl = "http://localhost:9090";
  final folder = "folder/subfolder";
  final client = PicomediaClient(endpoint: baseUrl, token: 'YOUR_API_KEY', target: PicomediaTarget.public);
  UploadResult uploadResult = await client.upload(folder, File(imageFile.path));
  if(uploadResult.url.isNotEmpty) {
    userApi.updatePhoto(uploadResult.url).then((res) async {
      setState(() {
        _photo = uploadResult.url;
        setState(() { _loading = false; });
      });
    }).catchError((err) {
      Fluttertoast.showToast(msg: 'fail.');
      setState(() { _loading = false; });
    });
  }
 */
class PicomediaClient {
  String endpoint;
  String token;
  PicomediaTarget target;
  PicomediaClient(
      {required this.endpoint, required this.token, required this.target});

  Future<UploadResult> upload(String? folder, File file) async {
    var uri = Uri.parse("$endpoint/upload");
    var req = http.MultipartRequest('POST', uri);
    req.headers.addAll({"Authorization": "Bearer $token"});

    req.fields['target'] = target.name;

    if (folder != null) {
      var refolder = folder;
      if (refolder!.startsWith("/")) {
        refolder = refolder!.substring(1);
      }
      if (refolder!.endsWith("/")) {
        refolder = refolder!.substring(0, refolder.length - 1);
      }
      req.fields['folder'] = refolder;
    }

    var partFile = await http.MultipartFile.fromPath('file', file.path);
    req.files.add(partFile);

    var streamedResponse = await req.send();
    var res = await http.Response.fromStream(streamedResponse);
    UploadResult uploadResult = UploadResult(url: "");
    if (res.statusCode == 200) {
      uploadResult = UploadResult.fromJson(jsonDecode(res.body));
    }
    return uploadResult;
  }
}

class UploadResult {
  String url;
  UploadResult({required this.url});

  factory UploadResult.fromJson(Map<String, dynamic> parsedJson) {
    return UploadResult(url: parsedJson['url'].toString());
  }
}

/**
Usage)
  String resizedUrl = TransformUrl.wrap('http://localhost:9090/public/test/sample.jpg')
    .width(300)
    .height(200)
    .crop(true)
    .url();
 */
class TransformUrl {
  String url;
  int? w;
  int? h;
  bool? c;
  TransformUrl({required this.url});

  static TransformUrl wrap(String url) {
    return TransformUrl(url: url);
  }

  TransformUrl width(int width) {
    this?.w = width;
    return this;
  }

  TransformUrl height(int width) {
    this?.h = width;
    return this;
  }

  TransformUrl crop(bool crop) {
    this?.c = crop;
    return this;
  }

  String url() {
    final contentType = lookupMimeType(url);
    Uri uri = Uri.parse(url);
    var paths = [...uri.pathSegments];
    if (paths[0] != 'public' && paths[0] != 'private') {
      return uri.toString();
    }
    List<String> options = [];
    if (contentType!.startsWith("image/")) {
      if (w != null) {
        options.add("w_${w!}");
      }
      if (h != null) {
        options.add("h_${h!}");
      }
      if (c != null && c!) {
        options.add("c_crop");
      }
      if (options.isNotEmpty) {
        paths.insert(1, options.join(','));
      }
    }
    if (options.isEmpty) {
      return uri.toString();
    }
    return uri.replace(pathSegments: paths).toString();
  }
}