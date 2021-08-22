import {NgModule} from '@angular/core';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {CompressInterceptor} from './interceptor/compress-interceptor.service';

@NgModule({
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: CompressInterceptor,
      multi: true
    }
  ],
  exports: []
})
export class CompressModule {
}