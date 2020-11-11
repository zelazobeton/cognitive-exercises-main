import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {MemoryService} from './memory-tile/memory.service';
import {ActivatedRoute, Router} from '@angular/router';
import {MemoryBoardDto, TileClick} from './memory';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-memory-board',
  templateUrl: './memory-board.component.html',
  styleUrls: ['./memory-board.component.css']
})
export class MemoryBoardComponent implements OnInit, OnDestroy {
  difficultyLvl: number;
  continueLastGame: boolean;
  board: MemoryBoardDto;
  private firstTileId: TileClick;
  private secondTileId: TileClick;
  private fetchBoardSub: Subscription;

  constructor(private memoryService: MemoryService, private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
    this.difficultyLvl = this.route.snapshot.queryParams.lvl || null;
    this.continueLastGame = this.route.snapshot.queryParams.continue || false;
    this.getBoardData();
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
    this.fetchBoardSub.unsubscribe();
    this.memoryService.clearCachedBoard();
  }

  private handleMatch(tileClick: TileClick) {
    this.memoryService.notifyMatchTiles(this.firstTileId.tileId, tileClick.tileId);
    this.board.memoryTiles[this.firstTileId.tileId].uncovered = true;
    this.board.memoryTiles[tileClick.tileId].uncovered = true;
    localStorage.setItem('memory-tiles', JSON.stringify(this.board.memoryTiles));
    this.firstTileId = null;
  }

  private getBoardData(): void {
    const memoryTiles = JSON.parse(localStorage.getItem('memory-tiles'));
    const numOfUncoveredTiles = JSON.parse(localStorage.getItem('memory-tiles-num'));
    if (memoryTiles != null && numOfUncoveredTiles != null) {
      this.board = {memoryTiles, numOfUncoveredTiles};
      return;
    }
    this.fetchBoardSub = this.memoryService.fetchBoardData(this.continueLastGame)
      .subscribe((resData: MemoryBoardDto) => {
        console.log(resData);
        this.board = resData;
        localStorage.setItem('memory-tiles', JSON.stringify(this.board.memoryTiles));
      }, (error => {
        console.error(error);
        this.router.navigateByUrl('/');
      }));
  }

  private coverTilesIfTwoWereUncovered(): void {
    if (this.secondTileId != null) {
      this.memoryService.coverTiles(this.firstTileId.tileId, this.secondTileId.tileId);
      this.firstTileId = null;
      this.secondTileId = null;
    }
  }
}
