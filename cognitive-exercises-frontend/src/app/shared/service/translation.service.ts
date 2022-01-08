import {Injectable, OnDestroy} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {Observable, Subscription} from 'rxjs';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {LocaleDto} from '../model/locale-dto';

@Injectable()
export class TranslationService implements OnDestroy {
  private subscriptions: Subscription[] = [];
  private readonly translationHost = environment.apiUrl + '/main/lang/v1';

  //constructor(private http: HttpClient) {}
  constructor(private http: HttpClient, private translate: TranslateService) {
  }

  public setLanguage() {
    this.translate.addLangs(['en', 'es']);
    this.translate.setDefaultLang('en');
    this.subscriptions.push(
      this.getLocale().subscribe(
        (response: LocaleDto) => {
          this.translate.langs.find(lang => lang === response.language) === undefined
            ? this.translate.use('en')
            : this.translate.use(response.languageTag);
        },
        (errorResponse: HttpErrorResponse) => {
          this.translate.setDefaultLang('en');
        }
      )
    );
  }

  public getLocale(): Observable<LocaleDto> {
    return this.http.get<LocaleDto>(`${this.translationHost}/locale`);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }
}
