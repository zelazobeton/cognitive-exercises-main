import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { environment } from '../../environments/environment';
import {Observable, throwError} from 'rxjs';
import {PortfolioDto} from '../model/portfolio-dto';
import {catchError, tap} from 'rxjs/operators';
import {NotificationType} from '../shared/notification/notification-type.enum';
import {NotificationService} from '../shared/notification/notification.service';
import {UserDto} from '../model/user-dto';
import {AuthenticationService} from '../auth/service/authentication.service';

@Injectable()
export class PortfolioService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient, private notificationService: NotificationService,
              private authenticationService: AuthenticationService) {}

  public updateAvatar(portfolioForm: FormData): Observable<PortfolioDto> {
    return this.http.post<PortfolioDto>(`${this.host}/portfolio/update-avatar`, portfolioForm, {observe: 'body'})
      .pipe(
        catchError(errorRes => {
          this.notificationService.notify(NotificationType.ERROR, `Something went wrong, please try again`);
          return throwError(errorRes);
        }),
        tap((response: PortfolioDto) => {
          const user: UserDto = JSON.parse(localStorage.getItem('user'));
          user.portfolio = response;
          localStorage.setItem('user', JSON.stringify(user));
          this.authenticationService.loggedInUser.next(user);
          this.notificationService.notify(NotificationType.SUCCESS, `Avatar updated`);
        })
      );
  }

}
