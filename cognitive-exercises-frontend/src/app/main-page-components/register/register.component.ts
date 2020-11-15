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
  private error: string | null = null;
  public emailInputInvalid = false;

  showEmailInvalidError() {
    this.emailInputInvalid = this.formControls.email.invalid && this.formControls.email.dirty && this.formControls.email.value !== '';
  }

  constructor(private router: Router, private authenticationService: AuthenticationService) {}

  ngOnInit(): void {
    if (this.authenticationService.isUserLoggedIn()) {
      this.router.navigateByUrl('/');
    }
    this.registerForm = new FormGroup({
      username: new FormControl(null, [Validators.required]),
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
    return this.registerForm.controls;
  }
}