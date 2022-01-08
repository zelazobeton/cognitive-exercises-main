import {Injectable} from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor, HttpHeaders
} from '@angular/common/http';
import {Observable} from 'rxjs';
import {HttpHeader, HttpHeaderContentType, HttpRequestMethod, HttpEncodingType} from '../../app/shared/http.enum';
import * as pako from 'pako';
import _ from 'lodash';
import {environment} from '../../environments/environment';

@Injectable()
export class CompressInterceptor implements HttpInterceptor {

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (this.isPostRequest(request) && this.isToBeCompressed(request)) {
      const headers: HttpHeaders = request.headers.set(HttpHeader.ContentType, HttpHeaderContentType.JSON)
        .set(HttpHeader.ContentEncoding, HttpEncodingType.GZIP);
      const zippedBodyRequest: Uint8Array = pako.gzip(JSON.stringify(request.body));
      const zippedRequest: HttpRequest<any> = request.clone({
        body: new Blob([this.binaryArrayToBase64(zippedBodyRequest)]),
        headers
      });
      return next.handle(zippedRequest);
    }
    return next.handle(request);
  }

  private isToBeCompressed(request: HttpRequest<any>): boolean {
      if (this.isAuthorizationServiceRequest(request.url)) {
        return false;
      }
      return !request.headers.has(HttpHeader.ContentEncoding) ||
        !_.includes(request.headers.getAll(HttpHeader.ContentEncoding), HttpEncodingType.NONE);
  }

  private isPostRequest(request: HttpRequest<any>): boolean {
    return request.method === HttpRequestMethod.POST;
  }

  private binaryArrayToBase64(array: any): string {
    const accumulator: string[] = _.transform(array, (result: string[], value: any) => {
      result.push(String.fromCharCode(value));
    }, [] as string[]);
    return btoa(accumulator.join(''));
  }

  private isAuthorizationServiceRequest(url: string) {
    return url.startsWith(environment.authorizationHost);
  }
}