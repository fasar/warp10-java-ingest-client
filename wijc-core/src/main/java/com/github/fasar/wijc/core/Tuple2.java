package com.github.fasar.wijc.core;


import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Tuple2<K, V> {
    K e1;
    V e2;
}
