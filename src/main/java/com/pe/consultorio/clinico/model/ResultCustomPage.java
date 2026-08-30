package com.pe.consultorio.clinico.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultCustomPage<T> {
    private List<T> data;
    private Integer total;
    private Integer all;

}
