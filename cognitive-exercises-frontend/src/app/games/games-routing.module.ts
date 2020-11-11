import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {MemoryMainComponent} from './memory/memory-main.component';
import {MemoryBoardComponent} from './memory/memory-board/memory-board.component';
import {MemoryStartComponent} from './memory/memory-start/memory-start.component';

const libRoutes: Routes = [
  {
    path: 'memory',
    component: MemoryMainComponent,
    children: [
      {
        path: '',
        component: MemoryStartComponent
      },
      {
        path: 'play',
        component: MemoryBoardComponent
      },
    ]
  },
  {
    path: '**',
    component: MemoryMainComponent
  }
];

@NgModule({
  imports: [
    RouterModule.forChild(libRoutes)
  ],
  exports: [RouterModule]
})
export class GamesModuleRouting {
}