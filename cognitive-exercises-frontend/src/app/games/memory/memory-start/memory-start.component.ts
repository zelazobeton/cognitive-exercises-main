import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-memory-start',
  templateUrl: './memory-start.component.html',
  styleUrls: ['./memory-start.component.css']
})
export class MemoryStartComponent implements OnInit {
  difficultyLvl: number;
  @Output() startGame = new EventEmitter<number>();

  constructor() {}

  ngOnInit() {
    this.difficultyLvl = 1;
  }

  onStart() {
    this.startGame.emit(this.difficultyLvl);
  }
}
