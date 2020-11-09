import {NgModule} from '@angular/core';
import {MemoryMainComponent} from './memory/memory-main.component';
import {GamesModuleRouting} from './games-routing.module';
import {CommonModule} from '@angular/common';
import {ReactiveFormsModule} from '@angular/forms';

@NgModule({
  declarations: [MemoryMainComponent],
  providers: [],
  exports: [MemoryMainComponent],
  imports: [CommonModule, GamesModuleRouting, ReactiveFormsModule]
})
export class GamesModule {}