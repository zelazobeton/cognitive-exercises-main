import {NgModule} from '@angular/core';
import {MemoryMainComponent} from './memory/memory-main.component';
import {GamesModuleRouting} from './games-routing.module';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { MemoryStartComponent } from './memory/memory-start/memory-start.component';
import { MemoryBoardComponent } from './memory/memory-board/memory-board.component';

@NgModule({
  declarations: [MemoryMainComponent, MemoryStartComponent, MemoryBoardComponent],
  providers: [],
  exports: [MemoryMainComponent],
  imports: [CommonModule, GamesModuleRouting, ReactiveFormsModule, FormsModule]
})
export class GamesModule {}