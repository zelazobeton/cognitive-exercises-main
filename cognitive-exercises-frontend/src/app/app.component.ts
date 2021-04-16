import {Component} from '@angular/core';
import {TranslationService} from './shared/service/translation.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(private translationService: TranslationService) {
    translationService.setLanguage();
  }
}
