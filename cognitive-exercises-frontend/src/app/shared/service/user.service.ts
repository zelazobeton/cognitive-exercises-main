import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Observable, throwError} from 'rxjs';
import {CustomHttpResponse} from '../model/custom-http-response';
import {ChangePasswordForm} from '../model/input-forms';
import {UserDto} from '../model/user-dto';
import {UserScoreDto} from '../model/user-score-dto';
import {catchError} from 'rxjs/operators';
import {NotificationType} from '../notification/notification-type.enum';
import {NotificationService} from '../notification/notification.service';
import {TranslateService} from '@ngx-translate/core';

@Injectable()
export class UserService {
  private readonly versionedHost = environment.versionedApiUrl;

  constructor(private http: HttpClient, private notificationService: NotificationService,
              private translate: TranslateService) {
  }

  public changePassword(changePasswordForm: ChangePasswordForm) {
    return this.http.post<HttpResponse<string> | HttpErrorResponse>(
      `${this.versionedHost}/user/password`, changePasswordForm, {observe: 'body'});
  }

  public resetPassword(email: FormData): Observable<CustomHttpResponse> {
    return this.http.post<CustomHttpResponse>(`${this.versionedHost}/user/reset-password`, email);
  }

  public fetchUserData(): Observable<UserDto> {
    return this.http.get<UserDto>(`${this.versionedHost}/user`)
      .pipe(
        catchError((error) => {
          this.notificationService.notify(NotificationType.ERROR,
            this.translate.instant('notifications.something went wrong on our side'));
          return throwError(error);
        }));
  }

  public fetchScoreboard(pageNum = 0, pageSize = 10): Observable<UserScoreDto[]> {
    return this.http.get<UserScoreDto[]>(
      `${this.versionedHost}/portfolio/scoreboard`,
      {params: {page: pageNum.toString(), size: pageSize.toString()}})
      .pipe(
        catchError((error) => {
          this.notificationService.notify(NotificationType.ERROR,
            this.translate.instant('notifications.something went wrong on our side'));
          return throwError(error);
        }));
  }
}
