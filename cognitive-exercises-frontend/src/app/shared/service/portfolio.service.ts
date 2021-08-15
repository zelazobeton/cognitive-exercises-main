import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Observable, throwError} from 'rxjs';
import {PortfolioDto} from '../model/portfolio-dto';
import {catchError, tap} from 'rxjs/operators';
import {UserDto} from '../model/user-dto';
import {NotificationType} from '../notification/notification-type.enum';
import {NotificationService} from '../notification/notification.service';
import {TranslateService} from '@ngx-translate/core';

@Injectable()
export class PortfolioService {
  private readonly versionedHost = environment.versionedApiUrl;

  constructor(private http: HttpClient, private notificationService: NotificationService,
              private translate: TranslateService) {
  }

  public updateAvatar(portfolioForm: FormData): Observable<PortfolioDto> {
    return this.http.post<PortfolioDto>(`${this.versionedHost}/portfolio/avatar`, portfolioForm, {observe: 'body'})
      .pipe(
        tap((response: PortfolioDto) => {
          const user: UserDto = JSON.parse(localStorage.getItem('user'));
          user.portfolio = response;
          localStorage.setItem('user', JSON.stringify(user));
        }),
        catchError((error) => {
          this.notificationService.notify(NotificationType.ERROR,
            this.translate.instant('notifications.something went wrong on our side'));
          return throwError(error);
        })
      );
  }
}
