import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Observable, throwError} from 'rxjs';
import {GameDataDto} from '../model/game-data-dto';
import {catchError} from 'rxjs/operators';
import {NotificationType} from '../notification/notification-type.enum';
import {NotificationService} from '../notification/notification.service';
import {TranslateService} from '@ngx-translate/core';

@Injectable()
export class GamesService {
  private readonly versionedGamesHost = environment.apiUrl + '/main/games/v1';

  constructor(private http: HttpClient,
              private notificationService: NotificationService,
              private translate: TranslateService) {
  }

  public getGamesData(): Observable<GameDataDto[]> {
    return this.http.get<GameDataDto[]>(`${this.versionedGamesHost}/data`, {observe: 'body'})
      .pipe(
        catchError((error) => {
          this.notificationService.notify(NotificationType.ERROR, this.translate.instant('notifications.something went wrong on our side'));
          return throwError(error);
        }));
  }
}
