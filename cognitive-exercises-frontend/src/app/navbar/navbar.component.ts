import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthenticationService} from '../auth/service/authentication.service';
import {UserDto} from '../model/user-dto';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {
  private userSub: Subscription;
  public usernameLogged: string;

  constructor(private authenticationService: AuthenticationService) {
    this.usernameLogged = null;
  }

  ngOnInit() {
    this.userSub = this.authenticationService.loggedInUser.subscribe((username: string) => {
      this.usernameLogged = username == null ? null : username;
    });
    this.authenticationService.isUserLoggedIn();
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
  }

  public onLogout() {
    this.authenticationService.logout();
  }
}
