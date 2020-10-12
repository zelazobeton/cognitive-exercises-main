import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthenticationService} from '../service/authentication.service';
import {UserDto} from '../model/user-dto';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {
  private userSub: Subscription;
  public usernameLogged: string = null;

  constructor(private authenticationService: AuthenticationService) {}

  ngOnInit() {
    this.userSub = this.authenticationService.loggedInUser.subscribe((userDto: UserDto) => {
      this.usernameLogged = userDto == null ? null : userDto.username;
    });
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
  }

  public onLogout() {
    console.log('onLogout');
      this.authenticationService.logout();
  }
}
