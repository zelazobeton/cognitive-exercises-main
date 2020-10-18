import {Component, OnDestroy, OnInit, Output} from '@angular/core';
import {Subscription} from 'rxjs';
import {Router} from '@angular/router';
import {AuthenticationService} from '../auth/service/authentication.service';
import {HttpErrorResponse} from '@angular/common/http';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {AuthForm} from '../model/auth-form';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent implements OnInit, OnDestroy {
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
    const loginFormData: AuthForm = {
      username: this.loginForm.value.username,
      password: this.loginForm.value.password,
    };
    this.loginForm.reset();
    this.subscriptions.push(
      this.authenticationService.login(loginFormData).subscribe(
        () => {
          this.router.navigateByUrl('/');
          this.showLoading = false;
        },
        (error: HttpErrorResponse) => this.showLoading = false
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }
}
