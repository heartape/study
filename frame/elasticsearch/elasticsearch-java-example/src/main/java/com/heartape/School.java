package com.heartape;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class School implements Serializable {
    @Serial
    private static final long serialVersionUID = -2835003383088314306L;

    private String name;
    private String tele;
    private Integer tuition;
}
