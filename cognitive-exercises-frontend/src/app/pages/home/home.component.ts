import {Component, OnDestroy, OnInit} from '@angular/core';
import {GamesService} from '../../shared/service/games.service';
import {Subscription} from 'rxjs';
import {GameDataDto} from '../../shared/model/game-data-dto';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
  private gamesSub: Subscription;
  public gamesData: GameDataDto[];

  constructor(private gamesService: GamesService) {
  }

  ngOnInit() {
    this.gamesSub = this.gamesService.getGamesData().subscribe(
      res => this.gamesData = res
    );
  }

  ngOnDestroy(): void {
    if (this.gamesSub != null) {
      this.gamesSub.unsubscribe();
    }
  }
}
