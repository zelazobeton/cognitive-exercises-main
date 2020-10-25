import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthenticationService} from '../../auth/service/authentication.service';
import {ChangePasswordForm} from '../../model/auth-form';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {
  private changePasswordForm: FormGroup;
  loading: boolean;

  constructor(private router: Router, private authenticationService: AuthenticationService,
              private formBuilder: FormBuilder) {
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

  checkPasswords = (group: FormGroup) => {
    const passwordInput = group.controls.password;
    const passwordConfirmationInput = group.controls.passwordConfirmation;
    if (passwordInput.value === passwordConfirmationInput.value) {
      return passwordConfirmationInput.setErrors(null);
    }
    passwordConfirmationInput.setErrors({notEquivalent: true});
  }

  onSubmit(): void {
    this.loading = true;
    const changePasswordForm: ChangePasswordForm = {
      oldPassword: this.changePasswordForm.value.oldPassword,
      newPassword: this.changePasswordForm.value.password,
    };
    this.changePasswordForm.reset();
    this.authenticationService.changePassword(changePasswordForm).subscribe(
      () => {
        this.loading = false;
        this.router.navigateByUrl('/login');
      },
      (error: HttpErrorResponse) => {
        console.log(error);
        this.loading = false;
      }
    );
  }
}
