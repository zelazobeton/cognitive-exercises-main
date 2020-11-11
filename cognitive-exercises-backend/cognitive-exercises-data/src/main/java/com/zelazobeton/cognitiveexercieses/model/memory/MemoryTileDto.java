package com.zelazobeton.cognitiveexercieses.model.memory;

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
public class MemoryTileDto {
    private String imgAddress;
    private Long memoryId;
    private boolean uncovered;

    public MemoryTileDto(MemoryTile memoryTile) {
        this.imgAddress = memoryTile.getMemoryImg().getAddress();
        this.memoryId = memoryTile.getMemory_img_id();
        this.uncovered = memoryTile.isUncovered();
    }
}
