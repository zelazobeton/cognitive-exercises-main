import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {Router} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../service/user.service';
import {AuthenticationService} from '../auth/service/authentication.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit, OnDestroy {
  private resetPasswordForm: FormGroup;
  public showLoading: boolean;
  private subscriptions: Subscription[] = [];
  private error: string | null = null;
  public emailInputInvalid = false;

  showEmailInvalidError() {
    this.emailInputInvalid = this.formControls.email.invalid && this.formControls.email.dirty && this.formControls.email.value !== '';
  }

  constructor(private router: Router, private userService: UserService, private authenticationService: AuthenticationService) {}

  ngOnInit(): void {
    if (this.authenticationService.isUserLoggedIn()) {
      this.router.navigateByUrl('/');
    }
    this.resetPasswordForm = new FormGroup({
      email: new FormControl(null, [Validators.required, Validators.email])
    });
  }

  public onSubmit(): void {
    this.showLoading = true;
    const formData = new FormData();
    formData.append('email', this.resetPasswordForm.value.email);
    this.resetPasswordForm.reset();
    this.subscriptions.push(
      this.userService.resetPassword(formData).subscribe(
        () => {
          this.showLoading = false;
          this.error = null;
        },
        (errorResponse: HttpErrorResponse) => {
          console.error(errorResponse);
          this.error = errorResponse.error.message;
          this.showLoading = false;
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  get formControls() {
    return this.resetPasswordForm.controls;
  }
}
