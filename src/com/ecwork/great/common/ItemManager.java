package com.ecwork.great.common;

import android.content.Context;
import com.ecwork.great.dao.Edge;
import com.ecwork.great.dao.Node;
import com.ecwork.great.dao.StorageAgent;
import com.ecwork.great.db.StorageException;

import java.util.*;

/**
 * User: ecsark
 * Date: 2/11/14
 * Time: 2:31 PM
 */
public class ItemManager {

    protected ArrayList<Item> allItems;
    protected HashMap<Integer, List<Item>> itemsByType;

    private void decategorize(Item item) {
        if (itemsByType.containsKey(item.node.type)) {
            itemsByType.get(item.node.type).remove(item);
        }
    }

    private void categorize(Item item) {
        if (!itemsByType.containsKey(item.node.type)) {
            List<Item> typeItems = new ArrayList<Item>();
            itemsByType.put(item.node.type, typeItems);
        }
        itemsByType.get(item.node.type).add(item);
    }

    protected void _add(Item item) {
        allItems.add(item);
        categorize(item);
    }

    protected void _delete(Item item) {
        allItems.remove(item);
        decategorize(item);
    }

    private ItemManager() {}

    private static ItemManager _instance = null;

    protected Context context;

    public static ItemManager instance(Context context) {
        if (_instance == null) {
            _instance = new ItemManager();
            _instance.context = context;
            _instance.setup();
        }
        return _instance;
    }

    private void setup() {
        List<Edge> edgeList = StorageAgent.getAllEdges(context);
        List<Node> nodeList = StorageAgent.getAllNodes(context);

        HashMap<Long,Item> itemsById = new HashMap<Long, Item>();
        itemsByType = new HashMap<Integer, List<Item>>();
        allItems = new ArrayList<Item>();

        for (Node n : nodeList) {
            _add(new Item(n));
        }
        for (Edge e : edgeList) {
            Item from = itemsById.get(e.start);
            Item to = itemsById.get(e.end);
            Link k = new Link(e, from, to);
            from.outgoingLinks.add(k);
            to.incomingLinks.add(k);
        }

    }


    public Item createItem(int type, String value) {
        Item item = new Item();
        item.node = StorageAgent.createNode(context, type, value);
        _add(item);
        return item;
    }

    /*
     item's type should not be updated before calling this method!
     */
    public void updateType(Item item, int newType) throws StorageException {

        decategorize(item);
        item.node.type = newType;
        categorize(item);

        StorageAgent.updateNode(context, item.node);
    }

    public Link connect(Item from, Item to, int type, int value) {
        Edge edge = StorageAgent.createEdge(context, from.node.id, to.node.id, type, value);
        Link link = new Link(edge, from, to);
        from.outgoingLinks.add(link);
        to.incomingLinks.add(link);
        return link;
    }

    public Link connect(Item from, Item to) {
        return connect(from, to, Edge.TYPE_DEFAULT, Edge.VALUE_DEFAULT);
    }

    public void disconnect(Item from, Item to) {

        Iterator<Link> iter = from.outgoingLinks.iterator();
        while (iter.hasNext()) {
            Link k = iter.next();
            if (k.to == to) {
                Edge e = k.edge;
                StorageAgent.deleteEdgeById(context, e.id);
                iter.remove();
                break;
            }
        }
        iter = to.incomingLinks.iterator();
        while (iter.hasNext()) {
            Link k = iter.next();
            if (k.from == from) {
                iter.remove();
                break;
            }
        }
    }


    public void removeItem(Item item) {
        // delete all connecting links
        for (Link k : item.incomingLinks) {
            k.from.outgoingLinks.remove(k);
        }
        for (Link k : item.outgoingLinks) {
            k.to.incomingLinks.remove(k);
        }

        // update local variables
        _delete(item);

        // sync db
        StorageAgent.deleteNodeById(context, item.node.id);
    }

    public List<Item> getItemsByType(int type) {
        if (itemsByType.containsKey(type)) {
            return itemsByType.get(type);
        } else {
            return new ArrayList<Item>();
        }
    }

    public void removeItemsByIndex(List<Integer> idxList) {
        Collections.sort(idxList);
        for (int i=0; i<idxList.size(); ++i) {
            int index = idxList.get(i)-i;
            removeItem(allItems.get(index));
        }
    }

    public Set<Integer> getAllTypes() {
        return itemsByType.keySet();
    }

    public ArrayList<Item> getAllItems() {
        return allItems;
    }

    public int getItemCounts() {
        return allItems.size();
    }
}
