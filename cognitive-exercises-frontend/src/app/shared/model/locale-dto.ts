
export class LocaleDto {
  public language: string;
  public languageTag: string;

  constructor(language: string, languageTag: string) {
    this.languageTag = languageTag;
    this.language = language;
  }
}
