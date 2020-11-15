import {PortfolioDto} from './portfolio-dto';

export class UserScoreDto {
  public place: number;
  public username: string;
  public portfolio: PortfolioDto

  constructor(place: number, username: string, portfolio: PortfolioDto) {
    this.place = place;
    this.username = username;
    this.portfolio = portfolio;
  }
}
