package com.ecwork.great.dao;

/**
 * User: ecsark
 * Date: 2/8/14
 * Time: 10:41 PM
 */
public class Node {
    public long id;
    public int type;
    public String value;

    public long timeCreated;
    public long timeModified;

    public Node () {}

    public Node (int type, String value, long timeCreated, long timeModified) {
        this.type = type;
        this.value = value;
        this.timeCreated = timeCreated;
        this.timeModified = timeModified;
    }
}
