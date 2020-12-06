import {Injectable, OnDestroy} from '@angular/core';
import {Observable, Subject, Subscription, throwError} from 'rxjs';
import {MemoryBoardDto} from './memory-board/memory';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {catchError, tap} from 'rxjs/operators';
import {NotificationType} from '../../shared/notification/notification-type.enum';
import {NotificationService} from '../../shared/notification/notification.service';
import {CustomHttpResponse} from '../../shared/model/custom-http-response';
import {NotificationMessages} from '../../shared/notification/notification-messages.enum';

@Injectable()
export class MemoryService implements OnDestroy {
  public tileNotification: Subject<{ id: number, match: boolean }>;
  private readonly host = environment.apiUrl;
  private board: MemoryBoardDto;
  private saveSub: Subscription;

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
    this.saveSub = this.http.post<CustomHttpResponse>(`${this.host}/memory/save-game`, board)
      .subscribe((response: CustomHttpResponse) => {
        this.notificationService.notify(NotificationType.SUCCESS, response.message);
      }, (errorRes => {
        this.notificationService.notify(NotificationType.ERROR, NotificationMessages.SERVER_ERROR);
      }));
  }

  saveScore(board: MemoryBoardDto): Observable<number> {
    return this.http.post<number>(`${this.host}/memory/save-score`, board);
  }

  fetchNewBoard(difficultyLvl: number): Observable<MemoryBoardDto> {
    return this.http.post<MemoryBoardDto>(`${this.host}/memory/new-game`, difficultyLvl)
      .pipe(
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

  ngOnDestroy(): void {
    this.saveSub.unsubscribe();
  }
}