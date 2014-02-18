package com.ecwork.great.dao;

/**
 * User: ecsark
 * Date: 2/8/14
 * Time: 10:44 PM
 */
public class Edge {

    public static final int TYPE_DEFAULT = -1;
    public static final int VALUE_DEFAULT = -1;

    public long id;
    public long start, end;
    public int type;
    public int value;

    public Edge() {}

    public Edge(long start, long end, int type, int value) {
        this.start = start;
        this.end = end;
        this.type = type;
        this.value = value;
    }

    public Edge (long start, long end) {
        this(start, end, TYPE_DEFAULT, VALUE_DEFAULT);
    }
}
