import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MemoryService} from '../memory.service';
import {Subscription} from 'rxjs';
import {NotificationType} from '../../../shared/notification/notification-type.enum';
import {NotificationMessages} from '../../../shared/notification/notification-messages.enum';
import {NotificationService} from '../../../shared/notification/notification.service';

@Component({
  selector: 'app-memory-start',
  templateUrl: './memory-start.component.html',
  styleUrls: ['./memory-start.component.css']
})
export class MemoryStartComponent implements OnInit, OnDestroy {
  difficultyLvl: number;
  savedGameExists: boolean;
  subscriptions: Subscription[] = [];

  constructor(private router: Router,
              private route: ActivatedRoute,
              private memoryService: MemoryService,
              private notificationService: NotificationService) {}

  ngOnInit() {
    this.savedGameExists = false;
    this.memoryService.clearCachedBoard();
    this.subscriptions.push(this.memoryService.fetchSavedGameBoard().subscribe(
      response => this.savedGameExists = response != null,
      error => this.notificationService.notify(NotificationType.ERROR, NotificationMessages.SERVER_ERROR)));
    this.difficultyLvl = 1;
  }

  onStart() {
    this.subscriptions.push(this.memoryService.fetchNewBoard(this.difficultyLvl).subscribe(
      res => this.router.navigate(['play'], {relativeTo: this.route}),
      error => {
        this.notificationService.notify(NotificationType.ERROR, NotificationMessages.SERVER_ERROR);
      }
    ));
  }

  onGoBack() {
    this.router.navigate(['../../../'], {relativeTo: this.route});
  }

  onContinue() {
    this.router.navigate(['play'], {relativeTo: this.route});
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }
}
