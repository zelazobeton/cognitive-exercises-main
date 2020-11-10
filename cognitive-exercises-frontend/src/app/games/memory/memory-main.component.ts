import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-memory',
  templateUrl: './memory-main.component.html',
  styleUrls: ['./memory-main.component.css']
})
export class MemoryMainComponent implements OnInit {
  difficultyLevel: number;
  gameIsOn: boolean;

  constructor() { }

  ngOnInit() {
    this.gameIsOn = false;
  }

  onStartGame(difficultyLvl: number) {
    this.difficultyLevel = difficultyLvl;
    this.gameIsOn = true;
  }

}
