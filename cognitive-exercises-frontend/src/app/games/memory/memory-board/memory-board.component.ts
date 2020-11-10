import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-memory-board',
  templateUrl: './memory-board.component.html',
  styleUrls: ['./memory-board.component.css']
})
export class MemoryBoardComponent implements OnInit {
  @Input() difficultyLvl: number;

  constructor() { }

  ngOnInit() {
  }

}
