import {Injectable} from '@angular/core';
import {Observable, Subject, Subscription, throwError} from 'rxjs';
import {MemoryTileDto} from '../memory-tile-dto';
import {MemoryBoardDto} from '../memory';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {environment} from '../../../../../environments/environment';
import {catchError, tap} from 'rxjs/operators';
import {NotificationType} from '../../../../shared/notification/notification-type.enum';
import {NotificationService} from '../../../../shared/notification/notification.service';
import {CustomHttpResponse} from '../../../../model/custom-http-response';

@Injectable()
export class MemoryService {
  private static ERROR_MSG = 'Sorry, there was an internal issue. Please try again';
  public tileNotification: Subject<{ id: number, match: boolean }>;
  private readonly host = environment.apiUrl;

  constructor(private http: HttpClient, private notificationService: NotificationService) {
    console.log('MemoryService constr');
    this.tileNotification = new Subject<{ id: number, match: boolean }>();
  }

  notifyMatchTiles(firstTileId: number, secondTileId: number) {
    this.tileNotification.next({id: firstTileId, match: true});
    this.tileNotification.next({id: secondTileId, match: true});
  }

  coverTiles(firstTileId: number, secondTileId: number) {
    this.tileNotification.next({id: firstTileId, match: false});
    this.tileNotification.next({id: secondTileId, match: false});
  }

  clearCachedBoard() {
    localStorage.removeItem('memory-tiles');
    localStorage.removeItem('memory-tiles-num');
  }

  fetchBoardData(continueLastGame: boolean): Observable<MemoryBoardDto> {
    if (continueLastGame) {
      return this.continueLastGame();
    }
    return this.startNewGame();
  }

  saveGame(board: MemoryBoardDto): void {
    this.http.post<CustomHttpResponse>(`${this.host}/memory/save`, board)
      .subscribe((response: CustomHttpResponse) => {
        this.notificationService.notify(NotificationType.SUCCESS, response.message);
      }, (errorRes => {
        this.notificationService.notify(NotificationType.ERROR, MemoryService.ERROR_MSG);
        console.error(errorRes);
      }));
  }

  private startNewGame() {
    return this.http.get<MemoryBoardDto>(`${this.host}/memory/new-game`)
      .pipe(
        catchError(errorRes => {
          this.notificationService.notify(NotificationType.ERROR, MemoryService.ERROR_MSG);
          return throwError(errorRes);
        })
      );
  }

  private continueLastGame() {
    return this.http.get<MemoryBoardDto>(`${this.host}/memory/continue`)
      .pipe(
        catchError(errorRes => {
          this.notificationService.notify(NotificationType.ERROR, MemoryService.ERROR_MSG);
          return throwError(errorRes);
        })
      );
  }
}