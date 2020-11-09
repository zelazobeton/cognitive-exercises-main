import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpResponse} from '@angular/common/http';
import { environment } from '../../environments/environment';
import {Observable, throwError} from 'rxjs';
import { UserDto } from '../model/user-dto';
import {CustomHttpResponse} from '../model/custom-http-response';
import {ChangePasswordForm} from '../model/input-forms';
import {catchError, tap} from 'rxjs/operators';
import {NotificationType} from '../shared/notification/notification-type.enum';
import {NotificationService} from '../shared/notification/notification.service';

@Injectable()
export class UserService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient, private notificationService: NotificationService) {}

  public changePassword(changePasswordForm: ChangePasswordForm) {
    return this.http.post<HttpResponse<string> | HttpErrorResponse>(
      `${this.host}/user/change-password`, changePasswordForm, {observe: 'body'})
      .pipe(
        catchError(errorRes => {
          return throwError(errorRes);
        }),
        tap((response: HttpResponse<string>) => {
          this.notificationService.notify(NotificationType.SUCCESS, `Password successfully changed.`);
        })
      );
  }

  public resetPassword(email: FormData): Observable<CustomHttpResponse> {
    return this.http.post<CustomHttpResponse>(`${this.host}/user/reset-password`, email)
      .pipe(
        catchError(errorRes => {
          return throwError(errorRes);
        }),
        tap((response: CustomHttpResponse) => {
          this.notificationService.notify(NotificationType.SUCCESS, response.message);
        })
      );
  }

}
