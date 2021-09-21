import {Component, OnDestroy, OnInit} from '@angular/core';
import {UserService} from '../../../shared/service/user.service';
import {Subscription} from 'rxjs';
import {ScoreboardPageDto, UserScoreDto} from '../../../shared/model/scoreboard-page-dto';

@Component({
  selector: 'app-scoreboard',
  templateUrl: './scoreboard.component.html',
  styleUrls: ['./scoreboard.component.css']
})
export class ScoreboardComponent implements OnInit, OnDestroy {
  private scoreboardSub: Subscription;
  private scoreboard: UserScoreDto[] = null;
  private pageNumber: number;
  private pagesTotal: number;

  constructor(private userService: UserService) {
  }

  getNextPage() {
    if (!this.isLastPage()) {
      this.scoreboardSub = this.getScoreboardPage(this.pageNumber + 1);
    }
  }

  getPreviousPage() {
    if (!this.isFirstPage()) {
      this.scoreboardSub = this.getScoreboardPage(this.pageNumber - 1);
    }
  }

  ngOnInit() {
    this.scoreboardSub = this.getScoreboardPage();
  }

  ngOnDestroy(): void {
    if (this.scoreboardSub != null) {
      this.scoreboardSub.unsubscribe();
    }
  }

  private getScoreboardPage(pageNum = 0, pageSize = 10): Subscription {
    return this.userService.fetchScoreboard(pageNum, pageSize).subscribe(
      (res: ScoreboardPageDto) => {
        this.scoreboard = res.userScores;
        this.pageNumber = res.pageNumber;
        this.pagesTotal = res.pagesTotal;
      });
  }

  private isLastPage() {
    return this.pageNumber === this.pagesTotal - 1;
  }

  private isFirstPage() {
    return this.pageNumber === 0;
  }
}
