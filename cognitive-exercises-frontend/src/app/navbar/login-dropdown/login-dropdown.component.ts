import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Observable, Subscription} from 'rxjs';
import {LoginForm} from '../../model/login-form';
import {AuthenticationService} from '../../service/authentication.service';
import {Router} from '@angular/router';
import {NotificationService} from '../../../notification/notification.service';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {UserDto} from '../../model/user-dto';
import {NotificationType} from '../../../notification/notification-type.enum';
import {HeaderType} from '../../enum/header-type.enum';

@Component({
  selector: 'app-login-dropdown',
  templateUrl: './login-dropdown.component.html',
  styleUrls: ['./login-dropdown.component.css']
})
export class LoginDropdownComponent implements OnInit, OnDestroy {
  private loginForm: FormGroup;
  private subscriptions: Subscription[] = [];
  public showLoading: boolean;

  constructor(private router: Router, private authenticationService: AuthenticationService) {}

  ngOnInit(): void {
    this.loginForm = new FormGroup({
      username: new FormControl(null, [Validators.required]),
      password: new FormControl(null, [Validators.required])
    });
    this.showLoading = false;
  }

  onSubmit(): void {
    this.showLoading = true;
    const loginFormData: LoginForm = {
      username: this.loginForm.value.username,
      password: this.loginForm.value.password,
    };
    this.loginForm.reset();
    this.subscriptions.push(
      this.authenticationService.login(loginFormData).subscribe(
        () => {
          this.showLoading = false;
        }
      )
    );
  }



  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

}
