import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthenticationService} from '../../auth/service/authentication.service';
import {UserDto} from '../../shared/model/user-dto';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {
  private userSub: Subscription;
  public usernameLogged: string;

  constructor(private authenticationService: AuthenticationService) {}

  ngOnInit() {
    this.usernameLogged = this.authenticationService.getLoggedUsernameFromLocalStorage();
    this.userSub = this.authenticationService.loggedInUser.subscribe((user: UserDto) => {
      this.usernameLogged = user == null ? null : user.username;
    });
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
  }

  public onLogout() {
    this.authenticationService.logout();
  }
}
