import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {ChangePasswordForm} from '../../../shared/model/input-forms';
import {HttpErrorResponse} from '@angular/common/http';
import {UserService} from '../../../shared/service/user.service';
import {Subscription} from 'rxjs';
import {NotificationType} from '../../../shared/notification/notification-type.enum';
import {NotificationService} from '../../../shared/notification/notification.service';
import {NotificationMessages} from '../../../shared/notification/notification-messages.enum';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit, OnDestroy {
  private changePasswordForm: FormGroup;
  private loading: boolean;
  private subscriptions: Subscription[] = [];
  private error: string = null;

  checkPasswords = (group: FormGroup) => {
    const passwordInput = group.controls.password;
    const passwordConfirmationInput = group.controls.passwordConfirmation;
    if (passwordInput.value === passwordConfirmationInput.value) {
      return passwordConfirmationInput.setErrors(null);
    }
    passwordConfirmationInput.setErrors({notEquivalent: true});
  };

  constructor(private router: Router, private userService: UserService,
              private formBuilder: FormBuilder,
              private notificationService: NotificationService) {
    this.loading = false;
  }

  ngOnInit(): void {
    this.changePasswordForm = this.formBuilder.group({
        oldPassword: ['', Validators.required],
        password: ['', Validators.required],
        passwordConfirmation: ['', Validators.required]
      }, {validator: this.checkPasswords}
    );
  }

  onSubmit(): void {
    this.loading = true;
    const changePasswordForm: ChangePasswordForm = {
      oldPassword: this.changePasswordForm.value.oldPassword,
      newPassword: this.changePasswordForm.value.password,
    };
    this.changePasswordForm.reset();
    this.subscriptions.push(
      this.userService.changePassword(changePasswordForm).subscribe(
        () => {
          this.error = null;
          this.notificationService.notify(NotificationType.SUCCESS, `Password successfully changed.`);
          this.loading = false;
        },
        (error: HttpErrorResponse) => {
          if (error.status === 406) {
            this.error = error.error.message;
          } else {
            this.notificationService.notify(NotificationType.ERROR, NotificationMessages.SERVER_ERROR_TRY_AGAIN);
          }
          this.loading = false;
        }
      ));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }
}
