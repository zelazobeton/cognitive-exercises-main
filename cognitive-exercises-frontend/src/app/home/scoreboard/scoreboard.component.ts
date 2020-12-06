import {Component, OnDestroy, OnInit} from '@angular/core';
import {UserService} from '../../shared/service/user.service';
import {Subscription} from 'rxjs';
import {UserScoreDto} from '../../shared/model/user-score-dto';
import {NotificationType} from '../../shared/notification/notification-type.enum';
import {NotificationMessages} from '../../shared/notification/notification-messages.enum';
import {NotificationService} from '../../shared/notification/notification.service';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-scoreboard',
  templateUrl: './scoreboard.component.html',
  styleUrls: ['./scoreboard.component.css']
})
export class ScoreboardComponent implements OnInit, OnDestroy {
  private scoreboardSub: Subscription;
  private scoreboard: UserScoreDto[] = null;

  constructor(private userService: UserService, private notificationService: NotificationService) {
  }

  ngOnInit() {
    this.scoreboardSub = this.userService.fetchScoreboard().subscribe(
      (res: UserScoreDto[]) => this.scoreboard = res,
      (errorResponse: HttpErrorResponse) =>
        this.notificationService.notify(NotificationType.ERROR, NotificationMessages.SERVER_ERROR)
    );
  }

  ngOnDestroy(): void {
    if (this.scoreboardSub != null) {
      this.scoreboardSub.unsubscribe();
    }
  }
}
