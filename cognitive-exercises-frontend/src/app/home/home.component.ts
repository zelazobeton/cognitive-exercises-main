import {Component, OnDestroy, OnInit} from '@angular/core';
import {GamesService} from '../shared/service/games.service';
import {Subscription} from 'rxjs';
import {GameDataDto} from '../shared/model/game-data-dto';
import {HttpClient} from '@angular/common/http';
import {NotificationService} from '../shared/notification/notification.service';
import {NotificationType} from '../shared/notification/notification-type.enum';
import {NotificationMessages} from '../shared/notification/notification-messages.enum';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
  private gamesSub: Subscription;
  private gamesData: GameDataDto[];

  constructor(private gamesService: GamesService,
              private notificationService: NotificationService) {
  }

  ngOnInit() {
    this.gamesSub = this.gamesService.getGamesData().subscribe(
      res => this.gamesData = res,
      error => this.notificationService.notify(NotificationType.ERROR, NotificationMessages.SERVER_ERROR)
    );
  }

  ngOnDestroy(): void {
    if (this.gamesSub != null) {
      this.gamesSub.unsubscribe();
    }
  }
}
