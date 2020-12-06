import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, NgForm} from '@angular/forms';
import {HttpErrorResponse} from '@angular/common/http';
import {PortfolioService} from '../../../shared/service/portfolio.service';
import {Subscription} from 'rxjs';
import {UserDto} from '../../../shared/model/user-dto';
import {AuthenticationService} from '../../../auth/service/authentication.service';
import {PortfolioDto} from '../../../shared/model/portfolio-dto';
import {NotificationType} from '../../../shared/notification/notification-type.enum';
import {NotificationService} from '../../../shared/notification/notification.service';
import {NotificationMessages} from '../../../shared/notification/notification-messages.enum';

@Component({
  selector: 'app-personal-data',
  templateUrl: './personal-data.component.html',
  styleUrls: ['./personal-data.component.css'],
})
export class PersonalDataComponent implements OnInit, OnDestroy {
  @Input() private userData: UserDto;
  public fileName: string;
  public profileImage: File;
  loading: boolean;
  private subscriptions: Subscription[] = [];

  constructor(private formBuilder: FormBuilder, private portfolioService: PortfolioService,
              private notificationService: NotificationService) {
    this.loading = false;
  }

  ngOnInit(): void {
  }

  onSubmit(): void {
    this.loading = true;
    const formData = new FormData();
    formData.append('avatar', this.profileImage);
    this.subscriptions.push(this.portfolioService.updateAvatar(formData).subscribe(
      (response: PortfolioDto) => {
        this.loading = false;
        this.userData.portfolio = response;
        this.notificationService.notify(NotificationType.SUCCESS, `Avatar updated`);
      },
      (error: HttpErrorResponse) => {
        this.loading = false;
        this.notificationService.notify(NotificationType.ERROR, NotificationMessages.SERVER_ERROR);
      }
    ));
  }

  public onImageChange(fileName: string, profileImage: File): void {
    this.fileName = fileName;
    this.profileImage = profileImage;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }
}