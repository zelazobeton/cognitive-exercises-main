import {PortfolioDto} from './portfolio-dto';

export class ScoreboardPageDto {
  public userScores: UserScoreDto[];
  public pageNumber: number;
  public pagesTotal: number;

  constructor(userScores: UserScoreDto[], pageNumber: number, pagesTotal: number) {
    this.userScores = userScores;
    this.pageNumber = pageNumber;
    this.pagesTotal = pagesTotal;
  }
}

export class UserScoreDto {
  public place: number;
  public username: string;
  public portfolio: PortfolioDto;

  constructor(place: number, username: string, portfolio: PortfolioDto) {
    this.place = place;
    this.username = username;
    this.portfolio = portfolio;
  }
}