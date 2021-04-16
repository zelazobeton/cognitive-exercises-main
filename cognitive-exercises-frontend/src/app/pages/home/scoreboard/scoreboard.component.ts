import {Component, OnDestroy, OnInit} from '@angular/core';
import {UserService} from '../../../shared/service/user.service';
import {Subscription} from 'rxjs';
import {UserScoreDto} from '../../../shared/model/user-score-dto';

@Component({
  selector: 'app-scoreboard',
  templateUrl: './scoreboard.component.html',
  styleUrls: ['./scoreboard.component.css']
})
export class ScoreboardComponent implements OnInit, OnDestroy {
  private scoreboardSub: Subscription;
  private scoreboard: UserScoreDto[] = null;

  constructor(private userService: UserService) {
  }

  ngOnInit() {
    this.scoreboardSub = this.userService.fetchScoreboard().subscribe(
      (res: UserScoreDto[]) => this.scoreboard = res);
  }

  ngOnDestroy(): void {
    if (this.scoreboardSub != null) {
      this.scoreboardSub.unsubscribe();
    }
  }
}
