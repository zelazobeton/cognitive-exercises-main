import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-memory-start',
  templateUrl: './memory-start.component.html',
  styleUrls: ['./memory-start.component.css']
})
export class MemoryStartComponent implements OnInit {
  difficultyLvl: number;

  constructor(private router: Router,
              private route: ActivatedRoute) {}

  ngOnInit() {
    this.difficultyLvl = 1;
  }

  onStart() {
    this.router.navigate(['play'], {queryParams: {lvl: this.difficultyLvl}, relativeTo: this.route});
  }

  onContinue() {
    this.router.navigate(['play'], {queryParams: {continue: true}, relativeTo: this.route});
  }
}
