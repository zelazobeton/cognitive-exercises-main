import {Component, OnDestroy, OnInit, NgZone} from '@angular/core';
import {UserService} from '../../shared/service/user.service';
import {Subscription} from 'rxjs';
import {Route, Router} from '@angular/router';
import {UserDto} from '../../shared/model/user-dto';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit, OnDestroy {
  currentCard: string;
  private userSub: Subscription;
  private userData: UserDto;

  constructor(private userService: UserService) {
    this.currentCard = null;
  }

  ngOnInit() {
    this.userSub = this.userService.fetchUserData().subscribe(res => {
      this.userData = res;
    }, (errorRes => {
      console.error(errorRes);
    }));
  }

  onClick(event) {
    const target = event.target || event.currentTarget;
    this.currentCard = target.attributes.id.nodeValue;
  }

  ngOnDestroy(): void {
    if (this.userSub != null) {
      this.userSub.unsubscribe();
    }
  }
}