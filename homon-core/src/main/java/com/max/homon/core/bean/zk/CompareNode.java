package com.max.homon.core.bean.zk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CompareNode implements Comparable<CompareNode> {

    private String ip;
    private int port;
    private long compare;

    @Override
    public int compareTo(CompareNode compare) {
        return Long.compare(this.compare, compare.compare);
    }
}
