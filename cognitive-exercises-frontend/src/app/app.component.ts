import {Component, OnDestroy} from '@angular/core';
import {TranslationService} from './shared/service/translation.service';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {TranslateService} from '@ngx-translate/core';
import {Subscription} from 'rxjs';
import {LocaleDto} from './shared/model/locale-dto';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnDestroy {
  private subscriptions: Subscription[] = [];

  constructor(private translationService: TranslationService, private translate: TranslateService) {
    translate.addLangs(['en', 'es']);
    translate.setDefaultLang('en');
    this.subscriptions.push(
      this.translationService.getLocale().subscribe(
        (response: LocaleDto) => {
          translate.langs.find(lang => lang === response.language) === undefined
            ? translate.use('en')
            : translate.use(response.languageTag);
        },
        (errorResponse: HttpErrorResponse) => {
          translate.setDefaultLang('en');
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }
}
