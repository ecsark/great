package com.ecwork.great.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.ecwork.great.db.DatabaseContract.EdgeT;
import com.ecwork.great.db.DatabaseContract.NodeT;
import com.ecwork.great.db.DatabaseHelper;
import com.ecwork.great.db.StorageException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * User: ecsark
 * Date: 2/8/14
 * Time: 9:59 PM
 */
public class StorageAgent {

    private StorageAgent() {}

    private static SQLiteDatabase getRDB(Context context) {
        return DatabaseHelper.getInstance(context).getReadableDatabase();
    }

    private static SQLiteDatabase getWDB(Context context) {
        return DatabaseHelper.getInstance(context).getWritableDatabase();
    }

    private static Node toNode(Cursor cursor) {
        Node n = new Node();
        n.id = cursor.getLong(0);
        n.type = cursor.getInt(1);
        n.value = cursor.getString(2);
        n.timeCreated = cursor.getLong(3);
        n.timeModified = cursor.getLong(4);

        return n;
    }

    private static Edge toEdge(Cursor cursor) {
        Edge e = new Edge();
        e.id = cursor.getLong(0);
        e.start = cursor.getLong(1);
        e.end = cursor.getLong(2);
        e.type = cursor.getInt(3);
        e.value = cursor.getInt(4);

        return e;
    }

    public static Node createNode(Context context, int type, String value) {

        Node n = new Node();
        n.type = type;
        n.value = value;
        n.timeCreated = Calendar.getInstance().getTimeInMillis();
        n.timeModified = n.timeCreated;

        ContentValues values = new ContentValues();
        values.put(NodeT._TYPE, type);
        values.put(NodeT._VALUE, value);
        values.put(NodeT._TIME_CREATED, n.timeCreated);
        values.put(NodeT._TIME_MODIFIED, n.timeModified);

        long id = getWDB(context).insert(NodeT.TABLE_NAME, null, values);
        n.id = id;

        return n;
    }


    public static Edge createEdge(Context context, long start, long end, int type, int value) {

        Edge e = new Edge(start, end, type, value);

        ContentValues values = new ContentValues();
        values.put(EdgeT._START, start);
        values.put(EdgeT._END, end);
        values.put(EdgeT._TYPE, type);
        values.put(EdgeT._VALUE, value);

        long id = getWDB(context).insert(EdgeT.TABLE_NAME, null, values);
        e.id = id;

        return e;
    }

    public static Edge newEdge(Context context, long start, long end) {

        return createEdge(context, start, end, Edge.TYPE_DEFAULT, Edge.VALUE_DEFAULT);
    }

    public static Node getNodeById(Context context, long nodeId) {
        String selection = "SELECT * FROM " + NodeT.TABLE_NAME +
                "WHERE " + NodeT._ID + "=?";
        String []  args = new String [] {Long.toString(nodeId)};

        Cursor cursor = getRDB(context).rawQuery(selection, args);

        if (cursor.moveToFirst()) {
            return toNode(cursor);
        } else {
            return null;
        }
    }


    public static Edge getEdgeById(Context context, long edgeId) {
        String selection = "SELECT * FROM " + EdgeT.TABLE_NAME +
                "WHERE " + EdgeT._ID + "=?";
        String [] args = new String [] {Long.toString(edgeId)};

        Cursor cursor = getRDB(context).rawQuery(selection, args);

        if (cursor.moveToFirst()) {
            return toEdge(cursor);
        } else {
            return null;
        }
    }


    public static List<Node> getNodesByType(Context context, int type) {
        String selection = "SELECT * FROM " + NodeT.TABLE_NAME +
                "WHERE " + NodeT._TYPE + "=?";
        String []  args = new String [] {Integer.toString(type)};

        Cursor cursor = getRDB(context).rawQuery(selection, args);

        List<Node> nodeList = new ArrayList<Node>();

        if (cursor.moveToFirst()) {
            do {
                nodeList.add(toNode(cursor));
            } while(cursor.moveToNext());
        }

        return nodeList;
    }


    public static List<Edge> getEdgesByType(Context context, int type) {
        String selection = "SELECT * FROM " + EdgeT.TABLE_NAME +
                "WHERE " + EdgeT._TYPE + "=?";
        String []  args = new String [] {Integer.toString(type)};

        Cursor cursor = getRDB(context).rawQuery(selection, args);

        List<Edge> edgeList = new ArrayList<Edge>();

        if (cursor.moveToFirst()) {
            do {
                edgeList.add(toEdge(cursor));
            } while(cursor.moveToNext());
        }

        return edgeList;
    }


    public static List<Node> getAllNodes(Context context) {
        String selection = "SELECT * FROM " + NodeT.TABLE_NAME;
        String [] args = new String[] {};

        Cursor cursor = getRDB(context).rawQuery(selection, args);

        List<Node> nodeList = new ArrayList<Node>();

        if (cursor.moveToFirst()) {
            do {
                nodeList.add(toNode(cursor));
            } while(cursor.moveToNext());
        }

        return nodeList;
    }

    public static List<Edge> getAllEdges(Context context) {
        String selection = "SELECT * FROM " + EdgeT.TABLE_NAME;
        String [] args = new String[] {};

        Cursor cursor = getRDB(context).rawQuery(selection, args);

        List<Edge> edgeList = new ArrayList<Edge>();

        if (cursor.moveToFirst()) {
            do {
                edgeList.add(toEdge(cursor));
            } while(cursor.moveToNext());
        }

        return edgeList;
    }

    private static List<Edge> getConnectingEdges(Context context, long nodeId, boolean incoming) {

        String selection = "SELECT * FROM " + EdgeT.TABLE_NAME +
                "WHERE " + (incoming ? EdgeT._END : EdgeT._START) + "=?";
        String [] args = new String [] {Long.toString(nodeId)};

        List<Edge> edges = new ArrayList<Edge>();

        Cursor cursor = getRDB(context).rawQuery(selection, args);

        if (cursor.moveToFirst()) {
            do {
                edges.add(toEdge(cursor));
            } while (cursor.moveToNext());
        }

        return edges;
    }


    public static List<Edge> getIncomingEdges(Context context, long nodeId) {
        return getConnectingEdges(context, nodeId, true);
    }

    public static List<Edge> getOutgoingEdges(Context context, long nodeId) {
        return getConnectingEdges(context, nodeId, false);
    }

    public static List<Edge> getConnectingEdges(Context context, long nodeId) {
        List<Edge> edges = getConnectingEdges(context, nodeId, true);
        edges.addAll(getConnectingEdges(context, nodeId, false));
        return edges;
    }


    private static List<Node> getAdjacentNodes(Context context, long nodeId, boolean incoming) {

        String arg1 = incoming ? EdgeT._START : EdgeT._END;
        String arg2 = incoming ? EdgeT._END : EdgeT._START;

        String selection = "SELECT a." + NodeT._ID + ", a." + NodeT._TYPE + ", a." + NodeT._VALUE +
                " FROM " + NodeT.TABLE_NAME + " a INNER JOIN " +
                EdgeT.TABLE_NAME + " b ON a." + NodeT._ID + "=b." + arg1 +
                " WHERE b." + arg2 + "=?";

        String [] arg = new String[] {Long.toString(nodeId)};

        List<Node> nodes = new ArrayList<Node>();

        Cursor cursor = getRDB(context).rawQuery(selection, arg);

        if (cursor.moveToFirst()) {
            do {
                nodes.add(toNode(cursor));
            } while (cursor.moveToNext());
        }

        return nodes;
    }

    public static List<Node> getIncomingNodes(Context context, long nodeId) {
        return getAdjacentNodes(context, nodeId, true);
    }

    public static List<Node> getOutgoingNodes(Context context, long nodeId) {
        return getAdjacentNodes(context, nodeId, false);
    }

    public static List<Node> getAdjacentNodes(Context context, long nodeId) {
        List<Node> nodes = getAdjacentNodes(context, nodeId, true);
        nodes.addAll(getAdjacentNodes(context, nodeId, false));

        return nodes;
    }

    public static void deleteEdgeById(Context context, long edgeId) {

        String selection = EdgeT._ID + "=?";
        String [] args = new String []  {Long.toString(edgeId)};
        getWDB(context).delete(EdgeT.TABLE_NAME, selection, args);
    }

    public static void deleteNodeById(Context context, long nodeId) {

        //delete all connecting edges
        String selection = EdgeT._START + "=? OR " + EdgeT._END + "=?";
        String [] args = new String[] {Long.toString(nodeId), Long.toString(nodeId)};
        getWDB(context).delete(EdgeT.TABLE_NAME, selection, args);

        //delete the node
        selection = NodeT._ID + "=?";
        getWDB(context).delete(NodeT.TABLE_NAME, selection, new String[] {Long.toString(nodeId)});
    }

    public static void updateNode(Context context, Node node) throws StorageException {
        ContentValues values = new ContentValues();
        values.put(NodeT._ID, node.id);
        values.put(NodeT._TYPE, node.type);
        values.put(NodeT._VALUE, node.value);
        values.put(NodeT._TIME_CREATED, node.timeCreated);
        values.put(NodeT._TIME_MODIFIED, Calendar.getInstance().getTimeInMillis());

        String selection = NodeT._ID + "=?";
        String [] selectionArgs = new String[] {Long.toString(node.id)};

        int count = getRDB(context).update(NodeT.TABLE_NAME, values, selection, selectionArgs);

        if (count != 1) {
            throw new StorageException("Update error: " + Integer.toString(count));
        }
    }

    public static void updateEdge(Context context, Edge edge) throws StorageException {
        ContentValues values = new ContentValues();
        values.put(EdgeT._ID, edge.id);
        values.put(EdgeT._START, edge.start);
        values.put(EdgeT._END, edge.end);
        values.put(EdgeT._TYPE, edge.type);
        values.put(EdgeT._VALUE, edge.value);

        String selection = EdgeT._ID + "=?";
        String [] selectionArgs = new String [] {Long.toString(edge.id)};

        int count = getRDB(context).update(EdgeT.TABLE_NAME, values, selection, selectionArgs);

        if (count != 1) {
            throw new StorageException("Update error: "+ Integer.toString(count));
        }
    }

}
