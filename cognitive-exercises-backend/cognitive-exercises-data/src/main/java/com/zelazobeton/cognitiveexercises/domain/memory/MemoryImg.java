package com.zelazobeton.cognitiveexercises.domain.memory;

import javax.persistence.Entity;

import com.zelazobeton.cognitiveexercises.domain.BaseEntity;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class MemoryImg extends BaseEntity {
    private String address;
}
