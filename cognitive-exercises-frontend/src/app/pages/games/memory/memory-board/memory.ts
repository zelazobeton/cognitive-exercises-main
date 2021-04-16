import {MemoryTileDto} from './memory-tile-dto';

export interface MemoryBoardDto {
  memoryTiles: MemoryTileDto[];
  numOfUncoveredTiles: number;
}

export interface TileClick {
  memoryId: number;
  tileId: number;
}