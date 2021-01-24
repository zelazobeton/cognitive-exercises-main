package com.zelazobeton.cognitiveexercieses.constant;

public enum MemoryDiffLvl {
    EASY(8),
    MEDIUM(15),
    HARD(24);

    public final int numOfImgs;

    MemoryDiffLvl(int numOfImgs) {
        this.numOfImgs = numOfImgs;
    }
}
