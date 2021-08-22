export const enum HttpHeader {
  ContentType = 'Content-Type',
  ContentEncoding = 'Content-Encoding'
}

export const enum HttpHeaderContentType {
  Any = '*/*',
  FormData = 'application/x-www-form-urlencoded',
  JavaScript = 'text/javascript',
  JSON = 'application/json',
  Text = 'text/plain'
}

export const enum HttpEncodingType {
  GZIP = 'gzip',
  NONE = 'none'
}

export const enum HttpRequestMethod {
  GET = 'GET',
  POST = 'POST',
  PUT = 'PUT',
  DELETE = 'DELETE',
}
