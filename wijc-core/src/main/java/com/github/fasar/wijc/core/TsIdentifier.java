package com.github.fasar.wijc.core;

import lombok.Value;

import java.util.HashMap;

@Value
public class TsIdentifier {
    String className;
    HashMap<String, String> labels;
    HashMap<String, String> tags;

    /**
     * This is a helper method to create a TsIdentifier object.
     * It is used in the following way:
     * <pre>
     *     TsIdentifier.of("className", Arrays.asList("label1", "value1"), Arrays.asList("label2", "value2"));
     * </pre>
     * @param className
     * @param tuples
     * @return
     */
    public static TsIdentifier of(String className, Tuple2<String, String>... tuples) {
        HashMap<String, String> labels = new HashMap<>();
        HashMap<String, String> tags = new HashMap<>();
        for (Tuple2<String, String> tuple : tuples) {
            labels.put(tuple.getE1(), tuple.getE2());
        }
        return new TsIdentifier(className, labels, tags);
    }
}
