import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {MemoryService} from './memory.service';
import {Subscription} from 'rxjs';
import {delay} from 'rxjs/operators';

@Component({
  selector: 'app-memory-tile',
  templateUrl: './memory-tile.component.html',
  styleUrls: ['./memory-tile.component.css']
})
export class MemoryTileComponent implements OnInit, OnDestroy {
  @Input() memoryId: number;
  @Input() tileId: number;
  @Input() isGuessed: boolean;
  @Output() clickOnTile = new EventEmitter<{memoryId: number, tileId: number}>();
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
    this.isVisible = this.isGuessed;
    this.isNotMatched = !this.isGuessed;
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
