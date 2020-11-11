import {Injectable} from '@angular/core';
import {Observable, Subject, throwError} from 'rxjs';
import {MemoryTileDto} from '../memory-tile-dto';
import {MemoryBoardDto} from '../memory';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {environment} from '../../../../../environments/environment';
import {catchError, tap} from 'rxjs/operators';
import {NotificationType} from '../../../../shared/notification/notification-type.enum';
import {NotificationService} from '../../../../shared/notification/notification.service';

@Injectable()
export class MemoryService {
  public tileNotification: Subject<{id: number, match: boolean}>;
  ids = [0, 1, 2, 2, 0, 1];
  private readonly host = environment.apiUrl;

  constructor(private http: HttpClient, private notificationService: NotificationService) {
    console.log('MemoryService constr');
    this.tileNotification = new Subject<{id: number, match: boolean}>();
  }

  notifyMatchTiles(firstTileId: number, secondTileId: number) {
    this.tileNotification.next({id: firstTileId, match: true});
    this.tileNotification.next({id: secondTileId, match: true});
  }

  coverTiles(firstTileId: number, secondTileId: number) {
    this.tileNotification.next({id: firstTileId, match: false});
    this.tileNotification.next({id: secondTileId, match: false});
  }

  fetchBoardData(continueLastGame: boolean): Observable<MemoryBoardDto> {
    if (continueLastGame) {
      return this.continueLastGame();
    }
    return this.startNewGame();
  }

  private startNewGame() {
    return this.http.get<MemoryBoardDto>(`${this.host}/memory/new-game`)
      .pipe(
        catchError(errorRes => {
          this.notificationService.notify(NotificationType.ERROR,
            'Sorry, there was an internal issue. Please try again');
          return throwError(errorRes);
        })
      );
  }

  private continueLastGame() {
    return this.http.get<MemoryBoardDto>(`${this.host}/memory/continue`)
      .pipe(
        catchError(errorRes => {
          this.notificationService.notify(NotificationType.ERROR,
            'Sorry, there was an internal issue. Please try again');
          return throwError(errorRes);
        })
      );
  }

  saveBoard(board: MemoryTileDto[][]): void {
  }

  updateNumOfUncoveredTiles(numOfUncoveredTiles: number) {
  }
}