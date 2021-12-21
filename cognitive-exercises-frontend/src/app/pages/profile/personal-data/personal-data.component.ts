import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {HttpErrorResponse} from '@angular/common/http';
import {PortfolioService} from '../../../shared/service/portfolio.service';
import {Subscription} from 'rxjs';
import {UserDto} from '../../../shared/model/user-dto';
import {PortfolioDto} from '../../../shared/model/portfolio-dto';
import {NotificationType} from '../../../shared/notification/notification-type.enum';
import {NotificationService} from '../../../shared/notification/notification.service';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-personal-data',
  templateUrl: './personal-data.component.html',
  styleUrls: ['./personal-data.component.css'],
})
export class PersonalDataComponent implements OnInit, OnDestroy {
  @Input() public userData: UserDto;
  public fileName: string;
  public profileImage: File;
  loading: boolean;
  private subscriptions: Subscription[] = [];

  constructor(private formBuilder: FormBuilder, private portfolioService: PortfolioService,
              private notificationService: NotificationService,
              private translate: TranslateService) {
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
        this.notificationService.notify(NotificationType.SUCCESS, this.translate.instant('notifications.Avatar updated'));
      },
      (error: HttpErrorResponse) => this.loading = false)
    );
  }

  public onImageChange(fileName: string, profileImage: File): void {
    this.fileName = fileName;
    this.profileImage = profileImage;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }
}