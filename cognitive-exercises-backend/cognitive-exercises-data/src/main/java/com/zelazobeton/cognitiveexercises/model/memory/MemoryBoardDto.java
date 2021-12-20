package com.zelazobeton.cognitiveexercises.model.memory;

import java.util.List;
import java.util.stream.Collectors;

import com.zelazobeton.cognitiveexercises.domain.memory.MemoryBoard;

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
    private List<MemoryTileDto> memoryTiles;
    private int numOfUncoveredTiles;

    public MemoryBoardDto(MemoryBoard memoryBoard) {
        this.numOfUncoveredTiles = memoryBoard.getNumOfUncoveredTiles();
        this.memoryTiles = memoryBoard.getMemoryTiles().stream().map(MemoryTileDto::new).collect(Collectors.toList());
    }
}
