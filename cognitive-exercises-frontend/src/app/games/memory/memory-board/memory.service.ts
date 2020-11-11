import {Injectable} from '@angular/core';
import {Observable, Subject, Subscription, throwError} from 'rxjs';
import {MemoryBoardDto} from './memory';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {environment} from '../../../../environments/environment';
import {catchError, tap} from 'rxjs/operators';
import {NotificationType} from '../../../shared/notification/notification-type.enum';
import {NotificationService} from '../../../shared/notification/notification.service';
import {CustomHttpResponse} from '../../../model/custom-http-response';

@Injectable()
export class MemoryService {
  private static ERROR_MSG = 'Sorry, there was an internal issue. Please try again';
  public tileNotification: Subject<{ id: number, match: boolean }>;
  private readonly host = environment.apiUrl;
  private board: MemoryBoardDto;

  constructor(private http: HttpClient, private notificationService: NotificationService) {
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

  getMemoryBoard(): MemoryBoardDto {
    if (this.board != null) {
      return this.board;
    }
    const memoryTiles = JSON.parse(localStorage.getItem('memory-tiles'));
    const numOfUncoveredTiles = JSON.parse(localStorage.getItem('memory-tiles-num'));
    if (memoryTiles != null && numOfUncoveredTiles != null) {
      this.board = {memoryTiles, numOfUncoveredTiles};
      return this.board;
    }
    return null;
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

  fetchNewBoard(): Observable<MemoryBoardDto> {
    return this.http.get<MemoryBoardDto>(`${this.host}/memory/new-game`)
      .pipe(
        catchError(errorRes => {
          this.notificationService.notify(NotificationType.ERROR, MemoryService.ERROR_MSG);
          return throwError(errorRes);
        }),
        tap(response => {
          this.board = response;
          localStorage.setItem('memory-tiles', JSON.stringify(this.board.memoryTiles));
          localStorage.setItem('memory-tiles-num', JSON.stringify(this.board.numOfUncoveredTiles));
        })
      );
  }

  fetchSavedGameBoard(): Observable<MemoryBoardDto> {
    return this.http.get<MemoryBoardDto>(`${this.host}/memory/continue`)
      .pipe(
        catchError(errorRes => {
          this.notificationService.notify(NotificationType.ERROR, MemoryService.ERROR_MSG);
          return throwError(errorRes);
        }),
        tap(response => {
          if (response != null) {
            this.board = response;
            localStorage.setItem('memory-tiles', JSON.stringify(response.memoryTiles));
            localStorage.setItem('memory-tiles-num', JSON.stringify(response.numOfUncoveredTiles));
          }
        }
      )
    );
  }
}