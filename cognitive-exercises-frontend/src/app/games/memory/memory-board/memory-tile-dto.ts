export class MemoryTileDto {
  public imgAddress: string;
  public memoryId: number;
  public uncovered: boolean;

  constructor(imgAddress: string, memoryId: number, uncovered: boolean) {
    this.imgAddress = imgAddress;
    this.memoryId = memoryId;
    this.uncovered = uncovered;
  }
}