package com.ecwork.great.common;

import com.ecwork.great.dao.Edge;

/**
 * User: ecsark
 * Date: 2/11/14
 * Time: 3:38 PM
 */
public class Link {

    public Item from;

    public Item to;

    protected Edge edge;

    protected Link() {}

    protected Link(Edge edge, Item from, Item to) {
        this.edge = edge;
        this.from = from;
        this.to = to;
    }

}
