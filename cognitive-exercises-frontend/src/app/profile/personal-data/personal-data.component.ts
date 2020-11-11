import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, NgForm} from '@angular/forms';
import {HttpErrorResponse} from '@angular/common/http';
import {PortfolioService} from '../../service/portfolio.service';
import {Subscription} from 'rxjs';
import {UserDto} from '../../model/user-dto';
import {AuthenticationService} from '../../auth/service/authentication.service';
import {PortfolioDto} from '../../model/portfolio-dto';

@Component({
  selector: 'app-personal-data',
  templateUrl: './personal-data.component.html',
  styleUrls: ['./personal-data.component.css'],
})
export class PersonalDataComponent implements OnInit {
  @Input() private userData: UserDto;
  public fileName: string;
  public profileImage: File;
  loading: boolean;

  constructor(private formBuilder: FormBuilder, private portfolioService: PortfolioService) {
    this.loading = false;
  }

  ngOnInit(): void {
  }

  onSubmit(personalDataForm: NgForm): void {
    this.loading = true;
    const formData = new FormData();
    formData.append('avatar', this.profileImage);
    this.portfolioService.updateAvatar(formData).subscribe(
      (response: PortfolioDto) => {
        this.loading = false;
        this.userData.portfolio = response;
      },
      (error: HttpErrorResponse) => {
        console.error(error);
        this.loading = false;
      }
    );
  }

  public onImageChange(fileName: string, profileImage: File): void {
    this.fileName = fileName;
    this.profileImage = profileImage;
  }
}