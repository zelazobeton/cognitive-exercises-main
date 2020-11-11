import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {MemoryService} from './memory.service';
import {ActivatedRoute, Router} from '@angular/router';
import {MemoryBoardDto, TileClick} from './memory';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-memory-board',
  templateUrl: './memory-board.component.html',
  styleUrls: ['./memory-board.component.css']
})
export class MemoryBoardComponent implements OnInit, OnDestroy {
  board: MemoryBoardDto;
  private firstTileId: TileClick;
  private secondTileId: TileClick;
  private fetchBoardSub: Subscription;

  constructor(private memoryService: MemoryService, private route: ActivatedRoute, private router: Router) {}

  ngOnInit() {
    this.board = this.memoryService.getMemoryBoard();
    this.firstTileId = null;
    this.secondTileId = null;
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
    if (this.fetchBoardSub != null) {
      this.fetchBoardSub.unsubscribe();
    }
    this.memoryService.clearCachedBoard();
  }

  private handleMatch(tileClick: TileClick) {
    this.memoryService.notifyMatchTiles(this.firstTileId.tileId, tileClick.tileId);
    this.board.memoryTiles[this.firstTileId.tileId].uncovered = true;
    this.board.memoryTiles[tileClick.tileId].uncovered = true;
    localStorage.setItem('memory-tiles', JSON.stringify(this.board.memoryTiles));
    this.firstTileId = null;
  }

  private coverTilesIfTwoWereUncovered(): void {
    if (this.secondTileId != null) {
      this.memoryService.coverTiles(this.firstTileId.tileId, this.secondTileId.tileId);
      this.firstTileId = null;
      this.secondTileId = null;
    }
  }
}
