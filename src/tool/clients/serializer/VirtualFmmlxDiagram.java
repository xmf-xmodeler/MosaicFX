package tool.clients.serializer;

import tool.clients.fmmlxdiagrams.FmmlxObject;

import java.util.Vector;

public class VirtualFmmlxDiagram {
    private final int id;
    private final Vector<FmmlxObject> objects;
    private final VirtualDiagramHolder holder;

    public VirtualFmmlxDiagram(Integer id, VirtualDiagramHolder virtualDiagramHolder, Vector<FmmlxObject> objects) {
        this.id = id;
        this.holder = virtualDiagramHolder;
        this.objects = objects;
    }

    public int getId() {
        return id;
    }

    public Vector<FmmlxObject> getObjects() {
        return objects;
    }

    public VirtualDiagramHolder getHolder() {
        return holder;
    }
}
