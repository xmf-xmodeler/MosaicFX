package tool.clients.fmmlxdiagrams;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javafx.geometry.Point2D;

public class FmmlxObjectPort {
	private final FmmlxObject owner;
	private final Vector<Vector<Edge>> edges;
	public  final static int NORTH = 0;
	public  final static int EAST  = 1;
	public  final static int SOUTH = 2;
	public  final static int WEST  = 3;

	public FmmlxObjectPort(FmmlxObject owner) {
		super();
		this.owner = owner;
		edges = new Vector<Vector<Edge>>();
		for(int dir = NORTH; dir <= WEST; dir++) {
			edges.add(new Vector<Edge>());
		}
	}
	
	public void removeEdge(Edge edge) {
		for(int direction = NORTH; direction <= WEST; direction++) {
			if(edges.get(direction).contains(edge)) {
				edges.get(direction).remove(edge);
				sortPorts(direction);
			}
		}
	}
	
	public void addNewEdge(Edge edge, int direction) {
		edges.get(direction).add(edge);
		sortPorts(direction);
	}

	public void sortAllPorts() { for(int direction = NORTH; direction <= WEST; direction++) sortPorts(direction); }
	
	private void sortPorts(final int direction) {
		Vector<Edge> ports = edges.get(direction);
		Collections.sort(ports, new Comparator<Edge>() {
			@Override public int compare(Edge e1, Edge e2) {
				return getAngle(e1, direction).compareTo(getAngle(e2, direction));
			}
		});
	}
	
	private Double getAngle(Edge edge, int direction) {
		double startAngle = Math.PI*(
				direction == NORTH?-3./2:
            	direction == EAST?-1.:
            	direction == SOUTH?-1./2:
            	0);
		FmmlxObject otherEnd = edge.startNode==owner?edge.endNode:edge.startNode;
		double angle = Math.atan2(
				owner.getCenterY()    - otherEnd.getCenterY(), 
				otherEnd.getCenterX() - owner.getCenterX());
		while(angle < startAngle) angle += 2*Math.PI;
		while(angle > startAngle + 2*Math.PI) angle -= 2*Math.PI;
		return angle;
	}

	public Point2D getPointForEdge(Edge edge, boolean isStartNode) {
		for(int direction = NORTH; direction <= WEST; direction++) {
			Vector<Edge> edgesOnOneSide = edges.get(direction);
			for(int i = 0; i < edgesOnOneSide.size(); i++) {
				if(edgesOnOneSide.get(i) == edge && (isStartNode?edge.startNode:edge.endNode) == owner) {
					double maxX = direction == NORTH || direction == WEST ? owner.getX() : owner.getMaxRight();
					double minX = direction == SOUTH || direction == WEST ? owner.getX() : owner.getMaxRight();
					double minY = direction == NORTH || direction == WEST ? owner.getY() : owner.getMaxBottom();
					double maxY = direction == NORTH || direction == EAST ? owner.getY() : owner.getMaxBottom();
					
					double diffX = maxX - minX;
					double diffY = maxY - minY;
					
					double share = 1. / (edgesOnOneSide.size() + 1);
					
					return new Point2D(minX + diffX * share * (i+1), minY + diffY * share * (i+1));
				}
			}
		}
		throw new RuntimeException("Point does not exist: Edge " + edge + " on Node " + owner);
	}
}
