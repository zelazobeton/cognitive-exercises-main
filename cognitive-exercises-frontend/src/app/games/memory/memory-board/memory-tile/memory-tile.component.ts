import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {MemoryService} from './memory.service';
import {Subscription} from 'rxjs';
import {MemoryTileDto} from '../memory-tile-dto';
import {TileClick} from '../memory';

@Component({
  selector: 'app-memory-tile',
  templateUrl: './memory-tile.component.html',
  styleUrls: ['./memory-tile.component.css']
})
export class MemoryTileComponent implements OnInit, OnDestroy {
  @Input() tileData: MemoryTileDto;
  private memoryId: number;
  @Input() tileId: number;
  @Output() clickOnTile = new EventEmitter<TileClick>();
  isVisible: boolean;
  private isNotMatched: boolean;
  private notificationSub: Subscription;

  constructor(private memoryService: MemoryService) {
    this.notificationSub = this.memoryService.tileNotification.subscribe(async (notification: {id: number, match: boolean}) => {
      if (notification.id === this.tileId) {
        if (notification.match) {
          this.isNotMatched = false;
          this.isVisible = true;
        } else {
          this.isVisible = false;
        }
      }
    });
  }

  ngOnInit() {
    this.memoryId = this.tileData.memoryId;
    this.isVisible = this.tileData.uncovered;
    this.isNotMatched = !this.tileData.uncovered;
  }

  onClick() {
    if (this.isNotMatched && !this.isVisible) {
      this.isVisible = !this.isVisible;
      this.clickOnTile.emit({memoryId: this.memoryId, tileId: this.tileId});
    }
  }

  ngOnDestroy(): void {
    this.notificationSub.unsubscribe();
  }
}
