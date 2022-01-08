import {Injectable, OnInit} from '@angular/core';

@Injectable()
export class NonAuthenticatedUrlService {
  private urlsSet: Set<string> = new Set<string>();
  private readonly USER = '/user';

  constructor() {
    this.urlsSet.add('/main' + this.USER + '/v1/login');
    this.urlsSet.add('/main' + this.USER + '/v1/register');
    this.urlsSet.add('/main' + this.USER + '/v1/scoring-list');
    this.urlsSet.add('/main' + this.USER + '/v1/reset-password');
    this.urlsSet.add('/main/games/v1/data');
    this.urlsSet.add('/main/portfolio/v1/scoreboard');
    this.urlsSet.add('/main/lang/v1/locale');
  }

  contain(url: string) {
    return this.urlsSet.has(url);
  }
}