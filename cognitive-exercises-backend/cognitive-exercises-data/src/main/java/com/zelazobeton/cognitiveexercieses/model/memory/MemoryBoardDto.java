package com.zelazobeton.cognitiveexercieses.model.memory;

import java.util.ArrayList;
import java.util.List;

import com.zelazobeton.cognitiveexercieses.domain.memory.MemoryBoard;
import com.zelazobeton.cognitiveexercieses.domain.memory.MemoryTile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryBoardDto {
    private List<List<MemoryTileDto>> memoryTiles;
    private int numOfUncoveredTiles;

    public MemoryBoardDto(MemoryBoard memoryBoard) {
        this.numOfUncoveredTiles = memoryBoard.getNumOfUncoveredTiles();
        this.memoryTiles = new ArrayList<>();
        List<MemoryTile> tiles = memoryBoard.getMemoryTiles();
        List<MemoryTileDto> row;
        for (int rowIdx = 0; rowIdx < memoryBoard.getNumOfRows(); rowIdx++) {
            row = new ArrayList<>();
            for (int colIdx = 0; colIdx < memoryBoard.getNumOfCols(); colIdx++) {
                row.add(new MemoryTileDto(
                        tiles.get(rowIdx * memoryBoard.getNumOfCols() + colIdx)));
            }
            this.memoryTiles.add(row);
        }
    }
}
