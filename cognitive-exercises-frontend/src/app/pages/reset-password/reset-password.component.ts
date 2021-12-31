import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {Router} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../shared/service/user.service';
import {AuthenticationService} from '../../auth/service/authentication.service';
import {NotificationType} from '../../shared/notification/notification-type.enum';
import {NotificationService} from '../../shared/notification/notification.service';
import {CustomHttpResponse} from '../../shared/model/custom-http-response';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit, OnDestroy {
  public resetPasswordForm: FormGroup;
  public showLoading: boolean;
  private subscriptions: Subscription[] = [];
  public error: string | null = null;
  public emailInputInvalid = false;

  showEmailInvalidError() {
    this.emailInputInvalid = this.formControls.email.invalid && this.formControls.email.dirty && this.formControls.email.value !== '';
  }

  constructor(private router: Router, private userService: UserService, private authenticationService: AuthenticationService,
              private notificationService: NotificationService) {}

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
    this.subscriptions.push(
      this.userService.resetPassword(this.resetPasswordForm.value.email).subscribe(
        (response: CustomHttpResponse) => {
          this.notificationService.notify(NotificationType.SUCCESS, response.message);
          this.showLoading = false;
          this.error = null;
        },
        (errorResponse: HttpErrorResponse) => {
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
