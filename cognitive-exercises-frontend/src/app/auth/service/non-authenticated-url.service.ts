import {Injectable, OnInit} from '@angular/core';

@Injectable()
export class NonAuthenticatedUrlService {
  private urlsSet: Set<string> = new Set<string>();
  private readonly USER = '/user';

  constructor() {
    this.urlsSet.add(this.USER + '/login');
    this.urlsSet.add(this.USER + '/register');
    this.urlsSet.add(this.USER + '/scoring-list');
    this.urlsSet.add(this.USER + '/reset-password');
    this.urlsSet.add('/games/data');
    this.urlsSet.add('/portfolio/scoreboard');
    this.urlsSet.add('/token/refresh');
  }

  contain(url: string) {
    return this.urlsSet.has(url);
  }
}