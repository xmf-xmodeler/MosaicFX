package tool.clients.fmmlxdiagrams;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import javafx.geometry.Point2D;

public class FmmlxObjectPort {
	private final FmmlxObject owner;
	private final HashMap<PortRegion, Vector<Edge>> edges;

	public FmmlxObjectPort(FmmlxObject owner) {
		super();
		this.owner = owner;
		edges = new HashMap<PortRegion, Vector<Edge>>();
		for(PortRegion direction : PortRegion.values()) {
			edges.put(direction, new Vector<Edge>());
		}
	}
	
	public void removeEdge(Edge edge) {
		for(PortRegion direction : PortRegion.values()) {
			if(edges.get(direction).contains(edge)) {
				edges.get(direction).remove(edge);
				sortPorts(direction);
			}
		}
	}
	
	public void addNewEdge(Edge edge, PortRegion direction) {
		edges.get(direction).add(edge);
		sortPorts(direction);
	}

	public void sortAllPorts() { for(PortRegion direction : PortRegion.values()) sortPorts(direction); }
	
	private void sortPorts(final PortRegion direction) {
		Vector<Edge> ports = edges.get(direction);
		Collections.sort(ports, new Comparator<Edge>() {
			@Override public int compare(Edge e1, Edge e2) {
				return getAngle(e1, direction).compareTo(getAngle(e2, direction));
			}
		});
	}
	
	private Double getAngle(Edge edge, PortRegion direction) {
		double startAngle = Math.PI*(
				direction == PortRegion.NORTH?-1./2:
            	direction == PortRegion.EAST?-1.:
            	direction == PortRegion.SOUTH?-3./2:
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
		for(PortRegion direction : PortRegion.values()) {
			Vector<Edge> edgesOnOneSide = edges.get(direction);
			for(int i = 0; i < edgesOnOneSide.size(); i++) {
				if(edgesOnOneSide.get(i) == edge && (isStartNode?edge.startNode:edge.endNode) == owner) {
					double maxX = direction == PortRegion.NORTH || direction == PortRegion.WEST ? owner.getX() : owner.getMaxRight();
					double minX = direction == PortRegion.SOUTH || direction == PortRegion.WEST ? owner.getX() : owner.getMaxRight();
					double minY = direction == PortRegion.NORTH || direction == PortRegion.WEST ? owner.getY() : owner.getMaxBottom();
					double maxY = direction == PortRegion.NORTH || direction == PortRegion.EAST ? owner.getY() : owner.getMaxBottom();
					
					double diffX = maxX - minX;
					double diffY = maxY - minY;
					
					double share = 1. / (edgesOnOneSide.size() + 1);
					
					return new Point2D(minX + diffX * share * (i+1), minY + diffY * share * (i+1));
				}
			}
		}
		throw new RuntimeException("Point does not exist: Edge " + edge + " on Node " + owner);
	}

	public PortRegion getDirectionForEdge(Edge edge, boolean isStartNode) {
		for(PortRegion direction : PortRegion.values()) {
			Vector<Edge> edgesOnOneSide = edges.get(direction);
			for(int i = 0; i < edgesOnOneSide.size(); i++) {
				if(edgesOnOneSide.get(i) == edge && (isStartNode?edge.startNode:edge.endNode) == owner) {
					return direction;
				}
			}
		}
		throw new RuntimeException("Point does not exist: Edge " + edge + " on Node " + owner);
	}

	public void setDirectionForEdge(Edge edge, boolean isStartNode, PortRegion newPortRegion) {
		for(PortRegion direction : PortRegion.values()) {
			Vector<Edge> edgesOnOneSide = edges.get(direction);
			for(int i = 0; i < edgesOnOneSide.size(); i++) {
				if(edgesOnOneSide.get(i) == edge && (isStartNode?edge.startNode:edge.endNode) == owner) {
					edgesOnOneSide.remove(i); i--; break;
				}
			}
		}
		edges.get(newPortRegion).add(edge);
	}
}
