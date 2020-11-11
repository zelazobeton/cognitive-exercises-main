import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MemoryService} from '../memory-board/memory.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-memory-start',
  templateUrl: './memory-start.component.html',
  styleUrls: ['./memory-start.component.css']
})
export class MemoryStartComponent implements OnInit, OnDestroy {
  difficultyLvl: number;
  savedGameExists: boolean;
  newBoardSub: Subscription;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private memoryService: MemoryService) {}

  ngOnInit() {
    this.savedGameExists = false;
    this.memoryService.clearCachedBoard();
    this.memoryService.fetchSavedGameBoard().subscribe(response => {
      this.savedGameExists = response != null;
    });
    this.difficultyLvl = 1;
  }

  onStart() {
    this.newBoardSub = this.memoryService.fetchNewBoard(this.difficultyLvl).subscribe(res => {
      this.router.navigate(['play'], {relativeTo: this.route});
    });
  }

  onContinue() {
    this.router.navigate(['play'], {relativeTo: this.route});
  }

  ngOnDestroy(): void {
    if (this.newBoardSub != null) {
      this.newBoardSub.unsubscribe();
    }
  }
}
