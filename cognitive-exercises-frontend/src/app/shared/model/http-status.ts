export class HttpStatus {
  public value: number;
  public reasonPhrase: string;

  constructor(value: number, reasonPhrase: string) {
    this.value = value;
    this.reasonPhrase = reasonPhrase;
  }
}
