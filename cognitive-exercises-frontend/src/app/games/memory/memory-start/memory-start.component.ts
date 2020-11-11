import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MemoryService} from '../memory-board/memory-tile/memory.service';

@Component({
  selector: 'app-memory-start',
  templateUrl: './memory-start.component.html',
  styleUrls: ['./memory-start.component.css']
})
export class MemoryStartComponent implements OnInit {
  difficultyLvl: number;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private memoryService: MemoryService) {}

  ngOnInit() {
    this.difficultyLvl = 1;
  }

  onStart() {
    this.memoryService.clearCachedBoard();
    this.router.navigate(['play'], {queryParams: {lvl: this.difficultyLvl}, relativeTo: this.route});
  }

  onContinue() {
    this.memoryService.clearCachedBoard();
    this.router.navigate(['play'], {queryParams: {continue: true}, relativeTo: this.route});
  }
}
