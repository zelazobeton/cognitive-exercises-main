import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {MemoryService} from '../memory.service';
import {MemoryBoardDto, TileClick} from './memory';
import {Subscription} from 'rxjs';
import {NotificationType} from '../../../shared/notification/notification-type.enum';
import {NotificationMessages} from '../../../shared/notification/notification-messages.enum';
import {NotificationService} from '../../../shared/notification/notification.service';

@Component({
  selector: 'app-memory-board',
  templateUrl: './memory-board.component.html',
  styleUrls: ['./memory-board.component.css']
})
export class MemoryBoardComponent implements OnInit, OnDestroy {
  readonly board: MemoryBoardDto;
  private firstTileId: TileClick;
  private secondTileId: TileClick;
  private gameIsOn: boolean;
  private matchedPairs: number;
  private pointsWon: number;
  private saveScoreSub: Subscription;
  private tileSize: number;

  constructor(private memoryService: MemoryService, private notificationService: NotificationService) {
    this.board = this.memoryService.getMemoryBoard();
  }

  ngOnInit() {
    this.firstTileId = null;
    this.secondTileId = null;
    this.gameIsOn = true;
    this.matchedPairs = 0;
    this.pointsWon = null;
    this.calcTileSize();
  }

  onTileClick(tileClick: TileClick) {
    localStorage.setItem('memory-tiles-num', JSON.stringify(++this.board.numOfUncoveredTiles));
    this.coverTilesIfTwoWereUncovered();
    if (this.firstTileId == null) {
      this.firstTileId = {memoryId: tileClick.memoryId, tileId: tileClick.tileId};
    } else {
      if (this.firstTileId.memoryId === tileClick.memoryId && this.firstTileId.tileId !== tileClick.tileId) {
        this.handleMatch(tileClick);
      } else {
        this.secondTileId = {memoryId: tileClick.memoryId, tileId: tileClick.tileId};
      }
    }
  }

  onSave() {
    this.memoryService.saveGame(this.board);
  }

  numOfUncoveredPairs(): number {
    // tslint:disable-next-line:no-bitwise
    return this.board.numOfUncoveredTiles / 2 >> 0;
  }

  ngOnDestroy(): void {
    if (this.saveScoreSub != null) {
      this.saveScoreSub.unsubscribe();
    }
    this.memoryService.clearCachedBoard();
  }

  private handleMatch(tileClick: TileClick) {
    this.memoryService.notifyMatchTiles(this.firstTileId.tileId, tileClick.tileId);
    this.board.memoryTiles[this.firstTileId.tileId].uncovered = true;
    this.board.memoryTiles[tileClick.tileId].uncovered = true;
    localStorage.setItem('memory-tiles', JSON.stringify(this.board.memoryTiles));
    this.firstTileId = null;
    this.matchedPairs++;
    if (this.matchedPairs === this.board.memoryTiles.length / 2) {
      this.saveScoreSub = this.memoryService.saveScore(this.board)
        .subscribe(
          res => {
            this.pointsWon = res;
            this.gameIsOn = false;
          },
        );
    }
  }

  private coverTilesIfTwoWereUncovered(): void {
    if (this.secondTileId != null) {
      this.memoryService.coverTiles(this.firstTileId.tileId, this.secondTileId.tileId);
      this.firstTileId = null;
      this.secondTileId = null;
    }
  }

  private calcTileSize() {
    if (this.board.memoryTiles.length < 20) {
      this.tileSize = 2;
    } else if (this.board.memoryTiles.length < 35) {
      this.tileSize = 1;
    } else {
      this.tileSize = 0;
    }
  }
}
