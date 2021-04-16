import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {Router} from '@angular/router';
import {AuthenticationService} from '../../auth/service/authentication.service';
import {HttpErrorResponse} from '@angular/common/http';
import {UserDto} from '../../shared/model/user-dto';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import { RegisterForm} from '../../shared/model/input-forms';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit, OnDestroy {
  private registerForm: FormGroup;
  public showLoading: boolean;
  private subscriptions: Subscription[] = [];
  private submitError: string | null = null;
  public formErrors = [];

  onValueChange() {
    this.formErrors = [];
    if (this.formControls.username.errors == null) {
      return;
    }
    if ('pattern' in this.formControls.username.errors) {
      this.formErrors.push('Username can contain only letters, digits and special signs: .@');
    }
    if ('maxlength' in this.formControls.username.errors) {
      this.formErrors.push('Max username length is 50 chars');
    }
    if (this.formControls.email.invalid && this.formControls.email.dirty && this.formControls.email.value !== '') {
      this.formErrors.push('Please enter a valid email address');
    }
  }

  constructor(private router: Router, private authenticationService: AuthenticationService) {}

  ngOnInit(): void {
    if (this.authenticationService.isUserLoggedIn()) {
      this.router.navigateByUrl('/');
    }
    this.registerForm = new FormGroup({
      username: new FormControl(null, [
        Validators.required, Validators.pattern('^[a-zA-Z0-9@.]+'), Validators.maxLength(50)]),
      email: new FormControl(null, [Validators.required, Validators.email])
    });
  }

  public onSubmit(): void {
    this.showLoading = true;
    const registerForm: RegisterForm = {
      username: this.registerForm.value.username,
      email: this.registerForm.value.email
    };
    this.registerForm.reset();

    this.subscriptions.push(
      this.authenticationService.register(registerForm).subscribe(
        (response: UserDto) => {
          this.showLoading = false;
          this.submitError = null;
        },
        (errorResponse: HttpErrorResponse) => {
          console.error(errorResponse);
          this.submitError = errorResponse.error.message;
          this.showLoading = false;
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  get formControls() {
    return this.registerForm.controls;
  }
}
