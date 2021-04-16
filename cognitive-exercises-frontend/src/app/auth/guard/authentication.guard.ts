import { Injectable } from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {AuthenticationService} from '../service/authentication.service';
import {NotificationService} from '../../shared/notification/notification.service';
import {NotificationType} from '../../shared/notification/notification-type.enum';
import {TranslateService} from '@ngx-translate/core';

@Injectable()
export class AuthenticationGuard implements CanActivate {

  constructor(private authenticationService: AuthenticationService, private router: Router,
              private notificationService: NotificationService,
              private translate: TranslateService) {}

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (this.authenticationService.isUserLoggedIn()) {
      return true;
    }
    this.router.navigate(['/login'], {queryParams: {returnUrl: state.url }});
    this.notificationService.notify(NotificationType.ERROR, this.translate.instant('notifications.You need to log in to access this page'));
    return false;
  }
}
