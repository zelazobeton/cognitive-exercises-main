import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { environment } from '../../../environments/environment';
import {Observable} from 'rxjs';
import {PortfolioDto} from '../model/portfolio-dto';
import {tap} from 'rxjs/operators';
import {UserDto} from '../model/user-dto';

@Injectable()
export class PortfolioService {
  private readonly host = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public updateAvatar(portfolioForm: FormData): Observable<PortfolioDto> {
    return this.http.post<PortfolioDto>(`${this.host}/portfolio/update-avatar`, portfolioForm, {observe: 'body'})
      .pipe(
        tap((response: PortfolioDto) => {
          const user: UserDto = JSON.parse(localStorage.getItem('user'));
          user.portfolio = response;
          localStorage.setItem('user', JSON.stringify(user));
        })
      );
  }
}
