package com.ecwork.great.common;

import android.content.Context;
import com.ecwork.great.dao.Node;
import com.ecwork.great.dao.StorageAgent;
import com.ecwork.great.db.StorageException;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ecsark
 * Date: 2/11/14
 * Time: 1:43 PM
 */
public class Item {

    Node node;

    protected Item(){
        incomingLinks = new ArrayList<Link>();
        outgoingLinks = new ArrayList<Link>();
    }

    public Item(Node node) {
        this();
        this.node = node;
    }

    public List<Link> getIncomingLinks() {
        return incomingLinks;
    }

    public List<Link> getOutgoingLinks() {
        return outgoingLinks;
    }

    protected List<Link> incomingLinks;
    protected List<Link> outgoingLinks;


    public String getValue() {
        return node.value;
    }

    public void updateValue(Context context, String value) throws StorageException {
        node.value = value;
        StorageAgent.updateNode(context, node);
    }

}
