import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {MemoryMainComponent} from './memory/memory-main.component';

const libRoutes: Routes = [
  {
    path: 'memory',
    component: MemoryMainComponent
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