package com.zelazobeton.cognitiveexercieses.domain.memory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.zelazobeton.cognitiveexercieses.domain.BaseEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Setter
@Builder
@Slf4j
@NoArgsConstructor
public class MemoryTile extends BaseEntity {
    @ManyToOne
    @JoinColumn
    private MemoryImg memoryImg;
    @Column(name = "MEMORY_IMG_ID", updatable=false, insertable=false)
    public Long memory_img_id;
    private boolean uncovered;

    public MemoryTile(MemoryImg memoryImg, Long memory_img_id, boolean uncovered) {
        this.memoryImg = memoryImg;
        this.memory_img_id = memory_img_id;
        this.uncovered = uncovered;
    }
}
