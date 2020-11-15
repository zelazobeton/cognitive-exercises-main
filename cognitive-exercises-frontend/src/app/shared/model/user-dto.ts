import {PortfolioDto} from './portfolio-dto';

export class UserDto {
  public username: string;
  public email: string;
  public lastLoginDate: Date;
  public lastLoginDateDisplay: Date;
  public joinDate: Date;
  public active: boolean;
  public notLocked: boolean;
  public portfolio: PortfolioDto;

  constructor() {
    this.username = '';
    this.email = '';
    this.lastLoginDate = null;
    this.lastLoginDateDisplay = null;
    this.joinDate = null;
    this.active = false;
    this.notLocked = false;
    this.portfolio = null;
  }

}
