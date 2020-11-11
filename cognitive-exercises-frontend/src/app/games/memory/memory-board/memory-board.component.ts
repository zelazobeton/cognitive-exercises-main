import {Component, Input, OnInit} from '@angular/core';
import {MemoryTileDto} from './memory-tile-dto';
import {MemoryService} from './memory-tile/memory.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-memory-board',
  templateUrl: './memory-board.component.html',
  styleUrls: ['./memory-board.component.css']
})
export class MemoryBoardComponent implements OnInit {
  difficultyLvl: number;
  continueLastGame: boolean;
  board: MemoryTileDto[][];
  private firstTileId: {memoryId: number, tileId: number};
  private secondTileId: {memoryId: number, tileId: number};
  private numOfUncoveredTiles: number;

  constructor(private memoryService: MemoryService, private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
    this.difficultyLvl = this.route.snapshot.queryParams.lvl || null;
    this.continueLastGame = this.route.snapshot.queryParams.continue || false;
    this.getBoardData();
    this.firstTileId = null;
    this.secondTileId = null;
    this.numOfUncoveredTiles = 0;
  }

  onTileClick(tileClick: {memoryId: number, tileId: number}) {
    this.memoryService.updateNumOfUncoveredTiles(this.numOfUncoveredTiles++);
    this.coverTilesIfTwoWereUncovered();
    if (this.firstTileId == null) {
      this.firstTileId = {memoryId: tileClick.memoryId, tileId: tileClick.tileId};
    } else {
      if (this.firstTileId.memoryId === tileClick.memoryId && this.firstTileId.tileId !== tileClick.tileId) {
        this.memoryService.notifyMatchTiles(tileClick.tileId, this.firstTileId.tileId);
        this.updateBoard(this.firstTileId.tileId, tileClick.tileId);
        this.firstTileId = null;
      } else {
        this.secondTileId = {memoryId: tileClick.memoryId, tileId: tileClick.tileId};
      }
    }
  }

  numOfUncoveredPairs(): number {
    // tslint:disable-next-line:no-bitwise
    return this.numOfUncoveredTiles / 2 >> 0;
  }

  updateBoard(firstTileId: number, secondTileId: number): void {
    // tslint:disable-next-line:no-bitwise
    const firstRowNumber = firstTileId / 10 >> 0;
    // tslint:disable-next-line:no-bitwise
    const secondRowNumber = secondTileId / 10 >> 0;
    this.board[firstRowNumber][firstTileId - firstRowNumber * 10].uncovered = true;
    this.board[secondRowNumber][secondTileId - secondRowNumber * 10].uncovered = true;
    this.memoryService.saveBoard(this.board);
  }

  private getBoardData(): void {
    this.memoryService.fetchBoardData(this.continueLastGame)
      .subscribe(resData => {
        console.log(resData);
        this.board = resData.memoryTiles;
        this.numOfUncoveredTiles = resData.numOfUncoveredTiles;
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
