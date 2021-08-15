import {Injectable, OnDestroy} from '@angular/core';
import {Observable, Subject, Subscription, throwError} from 'rxjs';
import {MemoryBoardDto} from './memory-board/memory';
import {HttpClient, HttpParams, HttpResponse} from '@angular/common/http';
import {environment} from '../../../../environments/environment';
import {catchError, tap} from 'rxjs/operators';
import {NotificationType} from '../../../shared/notification/notification-type.enum';
import {NotificationService} from '../../../shared/notification/notification.service';
import {CustomHttpResponse} from '../../../shared/model/custom-http-response';
import {TranslateService} from '@ngx-translate/core';

@Injectable()
export class MemoryService implements OnDestroy {
  public tileNotification: Subject<{ id: number, match: boolean }>;
  private readonly versionedHost = environment.versionedApiUrl;
  private board: MemoryBoardDto;
  private saveSub: Subscription;

  constructor(private http: HttpClient, private notificationService: NotificationService,
              private translate: TranslateService) {
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
    this.saveSub = this.http.post<CustomHttpResponse>(`${this.versionedHost}/memory/game`, board)
      .subscribe((response: CustomHttpResponse) => {
        this.notificationService.notify(NotificationType.SUCCESS, response.message);
      }, (errorRes => {
        this.notificationService.notify(NotificationType.ERROR,
          this.translate.instant('notifications.something went wrong on our side'));
      }));
  }

  saveScore(board: MemoryBoardDto): Observable<number> {
    return this.http.post<number>(`${this.versionedHost}/memory/score`, board);
  }

  fetchNewBoard(difficultyLvl: number): Observable<MemoryBoardDto> {
    return this.http.get<MemoryBoardDto>(`${this.versionedHost}/memory/game`,
      {params: new HttpParams().set('level', String(difficultyLvl))})
      .pipe(
        tap(response => {
          this.board = response;
          localStorage.setItem('memory-tiles', JSON.stringify(this.board.memoryTiles));
          localStorage.setItem('memory-tiles-num', JSON.stringify(this.board.numOfUncoveredTiles));
        }),
        catchError((error) => {
          this.notificationService.notify(NotificationType.ERROR,
            this.translate.instant('notifications.something went wrong on our side'));
          return throwError(error);
        })
      );
  }

  fetchSavedGameBoard(): Observable<MemoryBoardDto> {
    return this.http.get<MemoryBoardDto>(`${this.versionedHost}/memory/game`)
      .pipe(
        tap(response => {
            if (response != null) {
              this.board = response;
              localStorage.setItem('memory-tiles', JSON.stringify(response.memoryTiles));
              localStorage.setItem('memory-tiles-num', JSON.stringify(response.numOfUncoveredTiles));
            }
          }
        ),
        catchError((error) => {
          this.notificationService.notify(NotificationType.ERROR,
            this.translate.instant('notifications.something went wrong on our side'));
          return throwError(error);
        })
      );
  }

  ngOnDestroy(): void {
    this.saveSub.unsubscribe();
  }
}