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

export const MimeTypes = {
  'text/html':                                        ['html', 'htm', 'shtml'],
  'text/css':                                         'css',
  'text/xml':                                         'xml',
  'image/gif':                                        'gif',
  'image/jpeg':                                       ['jpeg', 'jpg'],
  'application/javascript':                           'js',
  'application/atom+xml':                             'atom',
  'application/rss+xml':                              'rss',
  'text/mathml':                                      'mml',
  'text/plain':                                       'txt',
  'text/vnd.sun.j2me.app-descriptor':                 'jad',
  'text/vnd.wap.wml':                                 'wml',
  'text/x-component':                                 'htc',
  'image/avif':                                       'avif',
  'image/png':                                        'png',
  'image/svg+xml':                                    ['svg', 'svgz'],
  'image/tiff':                                       ['tif', 'tiff'],
  'image/vnd.wap.wbmp':                               'wbmp',
  'image/webp':                                       'webp',
  'image/x-icon':                                     'ico',
  'image/x-jng':                                      'jng',
  'image/x-ms-bmp':                                   'bmp',
  'font/woff':                                        'woff',
  'font/woff2':                                       'woff2',
  'application/java-archive':                         ['jar', 'war', 'ear'],
  'application/json':                                 'json',
  'application/mac-binhex40':                         'hqx',
  'application/msword':                               'doc',
  'application/pdf':                                  'pdf',
  'application/postscript':                           ['ps', 'eps', 'ai'],
  'application/rtf':                                  'rtf',
  'application/vnd.apple.mpegurl':                    'm3u8',
  'application/vnd.google-earth.kml+xml':             'kml',
  'application/vnd.google-earth.kmz':                 'kmz',
  'application/vnd.ms-excel':                         'xls',
  'application/vnd.ms-fontobject':                    'eot',
  'application/vnd.ms-powerpoint':                    'ppt',
  'application/vnd.oasis.opendocument.graphics':      'odg',
  'application/vnd.oasis.opendocument.presentation':  'odp',
  'application/vnd.oasis.opendocument.spreadsheet':   'ods',
  'application/vnd.oasis.opendocument.text':          'odt',
  'application/vnd.openxmlformats-officedocument.presentationml.presentation': 'pptx',
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'xlsx',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document': 'docx',
  'application/vnd.wap.wmlc':                         'wmlc',
  'application/wasm':                                 'wasm',
  'application/x-7z-compressed':                      '7z',
  'application/x-cocoa':                              'cco',
  'application/x-java-archive-diff':                  'jardiff',
  'application/x-java-jnlp-file':                     'jnlp',
  'application/x-makeself':                           'run',
  'application/x-perl':                               ['pl', 'pm'],
  'application/x-pilot':                              ['prc', 'pdb'],
  'application/x-rar-compressed':                     'rar',
  'application/x-redhat-package-manager':             'rpm',
  'application/x-sea':                                'sea',
  'application/x-shockwave-flash':                    'swf',
  'application/x-stuffit':                            'sit',
  'application/x-tcl':                                ['tcl', 'tk'],
  'application/x-x509-ca-cert':                       ['der', 'pem', 'crt'],
  'application/x-xpinstall':                          'xpi',
  'application/xhtml+xml':                            'xhtml',
  'application/xspf+xml':                             'xspf',
  'application/zip':                                  'zip',
  'application/octet-stream':                         ['bin', 'exe', 'dll'],
  'application/octet-stream':                         'deb',
  'application/octet-stream':                         'dmg',
  'application/octet-stream':                         ['iso', 'img'],
  'application/octet-stream':                         ['msi', 'msp', 'msm'],
  'audio/midi':                                       ['mid', 'midi', 'kar'],
  'audio/mpeg':                                       'mp3',
  'audio/ogg':                                        'ogg',
  'audio/x-m4a':                                      'm4a',
  'audio/x-realaudio':                                'ra',
  'video/3gpp':                                       ['3gpp', '3gp'],
  'video/mp2t':                                       'ts',
  'video/mp4':                                        'mp4',
  'video/mpeg':                                       ['mpeg', 'mpg'],
  'video/ogg':                                        'ogv',
  'video/quicktime':                                  'mov',
  'video/webm':                                       'webm',
  'video/x-flv':                                      'flv',
  'video/x-m4v':                                      'm4v',
  'video/x-matroska':                                 'mkv',
  'video/x-mng':                                      'mng',
  'video/x-ms-asf':                                   ['asx', 'asf'],
  'video/x-ms-wmv':                                   'wmv',
  'video/x-msvideo':                                  'avi'
};

export const Mime = class {
  static detect(name) {
    if(!name) return name;
    const pos = name.lastIndexOf('.');
    if(pos != -1) {
      const ext = name.substring(pos+1).toLowerCase();
      for(let mimeType in MimeTypes) {
        const val = MimeTypes[mimeType];
        if(Array.isArray(val)) {
          for(let v of val) {
            if(v == ext) {
              return mimeType;
            }
          }
        } else {
          if(ext == val) {
            return mimeType;
          }
        }
      }
    }
    return undefined;
  }
}
// Not yet
/*
export const PicomediaClient = class {
  constructor(endpoint, token, file) {
    this._endpoint = endpoint;
    this._token = token;
    this._file = file;
  }
  static of(endpoint, token, file) {
    return new PicomediaClient(endpoint, token, file);
  }
  async upload() {
    console.log(`------------> _endpoint: ${this._endpoint}`);
    console.log(`------------> _token: ${this._token}`);
  }
};
*/

/**
 * Usage:
 *   const transformUrl = TransformUrl.wrap("http://localhost:9090/test/sample.jpg")
 *     .width(150)
 *     .height(150)
 *     .crop(true)
 *     .url();
 */
export const TransformUrl = class {
  constructor(url) {
    this._url = url;
  }
  static wrap(url) {
    return new TransformUrl(url);
  }
  width(width) {
    this._width = width;
    return this;
  }
  height(height) {
    this._height = height;
    return this;
  }
  crop(crop) {
    this._crop = crop;
    return this;
  }
  url() {
    try {
      const contentType = Mime.detect(this._url);
      const pos = this._url.indexOf('/', this._url.indexOf('://') + 3);
      const baseUrl = this._url.substring(0, pos);
      let path = this._url.substring(pos + 1);
      const paths = path.split('/');
      if (paths[0] != 'public' && paths[0] != 'private') {
        return this.url;
      }
      if (!contentType.startsWith("image/")) {
        return this._url;
      }
      let opts = [];
      if(this._width) {
        opts.push('w_'+this._width);
      }
      if(this._height) {
        opts.push('h_'+this._height);
      }
      if(this._crop) {
        opts.push('c_crop');
      }
      if(opts.length == 0) {
        return this._url;
      }
      paths.splice(1, 0, opts.join(','));
      return baseUrl + '/' + paths.join('/');
    }catch(e) {
      return '';
    }
  }
};