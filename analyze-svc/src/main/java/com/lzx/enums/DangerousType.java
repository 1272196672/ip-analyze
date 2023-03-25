package com.lzx.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@AllArgsConstructor
public enum DangerousType {
    FREQUENT(0),

    NUM(1),

    GEO(2);

    private final int type;
}
