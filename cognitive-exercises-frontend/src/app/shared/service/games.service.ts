import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { environment } from '../../../environments/environment';
import {Observable} from 'rxjs';
import {GameDataDto} from '../model/game-data-dto';

@Injectable()
export class GamesService {
  private readonly host = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public getGamesData(): Observable<GameDataDto[]> {
    return this.http.get<GameDataDto[]>(`${this.host}/games/data`, {observe: 'body'});
  }
}
