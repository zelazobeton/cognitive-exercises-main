import { Injectable } from '@angular/core';
import { NotifierService } from 'angular-notifier';
import { NotificationType } from './notification-type.enum';

@Injectable()
export class NotificationService {

  constructor(private notifier: NotifierService) {}

  public notify(type: NotificationType, message: string) {
    this.notifier.notify(type, message);
  }
}
