import {Injectable, OnDestroy} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {Observable, Subscription} from 'rxjs';
import {HttpClient, HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {LocaleDto} from '../model/locale-dto';

@Injectable()
export class TranslationService {
  //private subscriptions: Subscription[] = [];
  private readonly host = environment.apiUrl;

  constructor(private http: HttpClient) {}
  //constructor(private http: HttpClient, public translate: TranslateService) {
  //  translate.addLangs(['en', 'es']);
  //  this.subscriptions.push(
  //    this.getLocale().subscribe(
  //      (response) => {
  //        console.log(response);
  //        translate.langs.find(lang => lang === response) === undefined
  //          ? translate.setDefaultLang('en')
  //          : translate.setDefaultLang(response);
  //      },
  //      (errorResponse: HttpErrorResponse) => {
  //        translate.setDefaultLang('en');
  //      }
  //    )
  //  );
  //}

  public getLocale(): Observable<LocaleDto> {
    return this.http.get<LocaleDto>(`${this.host}/lang/locale`);
  }

  //ngOnDestroy(): void {
  //  this.subscriptions.forEach(sub => sub.unsubscribe());
  //}
}
