import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { environment } from '../../../environments/environment';
import {Observable, throwError} from 'rxjs';
import {PortfolioDto} from '../model/portfolio-dto';
import {catchError, tap} from 'rxjs/operators';
import {NotificationType} from '../notification/notification-type.enum';
import {NotificationService} from '../notification/notification.service';
import {UserDto} from '../model/user-dto';
import {GameDataDto} from '../model/game-data-dto';

@Injectable()
export class GamesService {
  private readonly host = environment.apiUrl;

  constructor(private http: HttpClient, private notificationService: NotificationService) {}

  public getGamesData(): Observable<GameDataDto[]> {
    return this.http.get<GameDataDto[]>(`${this.host}/games/data`, {observe: 'body'})
      .pipe(
        catchError(errorRes => {
          this.notificationService.notify(NotificationType.ERROR, `Something went wrong, please try again`);
          return throwError(errorRes);
        })
      );
  }
}
