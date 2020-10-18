import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  currentCard: string;

  constructor() {
    this.currentCard = null;
  }

  ngOnInit() {
  }

  onClick(event) {
    const target = event.target || event.currentTarget;
    this.currentCard = target.attributes.id.nodeValue;
  }

}