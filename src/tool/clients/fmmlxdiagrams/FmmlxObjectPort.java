package tool.clients.fmmlxdiagrams;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import javafx.geometry.Point2D;

public class FmmlxObjectPort {
	private final FmmlxObject owner;
	private final HashMap<PortRegion, Vector<Edge.End>> edges;

	public FmmlxObjectPort(FmmlxObject owner) {
		super();
		this.owner = owner;
		edges = new HashMap<PortRegion, Vector<Edge.End>>();
		for(PortRegion direction : PortRegion.values()) {
			edges.put(direction, new Vector<Edge.End>());
		}
	}
	
	public void removeEdge(Edge.End edge) {
		for(PortRegion direction : PortRegion.values()) {
			if(edges.get(direction).contains(edge)) {
				edges.get(direction).remove(edge);
				sortPorts(direction);
			}
		}
	}
	
	public void addNewEdge(Edge.End edge, PortRegion direction) {
		edges.get(direction).add(edge);
		sortPorts(direction);
	}

	public void sortAllPorts() { for(PortRegion direction : PortRegion.values()) sortPorts(direction); }
	
	private void sortPorts(final PortRegion direction) {
		Vector<Edge.End> ports = edges.get(direction);
		Collections.sort(ports, new Comparator<Edge.End>() {
			@Override public int compare(Edge.End e1, Edge.End e2) {
				return getAngle(e1.edge, direction).compareTo(getAngle(e2.edge, direction));
			}
		});
	}
	
	private Double getAngle(Edge edge, PortRegion direction) {
		double startAngle = Math.PI*(
				direction == PortRegion.NORTH?-1./2:
            	direction == PortRegion.EAST?-1.:
            	direction == PortRegion.SOUTH?-3./2:
            	0);
		FmmlxObject otherEnd = edge.sourceNode==owner?edge.targetNode:edge.sourceNode;
		double angle = Math.atan2(
				owner.getCenterY()    - otherEnd.getCenterY(), 
				otherEnd.getCenterX() - owner.getCenterX());
		while(angle < startAngle) angle += 2*Math.PI;
		while(angle > startAngle + 2*Math.PI) angle -= 2*Math.PI;
		return angle;
	}

	public Point2D getPointForEdge(Edge.End edgeEnd, boolean isStartNode) {
		for(PortRegion direction : PortRegion.values()) {
			Vector<Edge.End> edgesOnOneSide = edges.get(direction);
			int visibleEdgeCount = 0;
			for(int i = 0; i < edgesOnOneSide.size(); i++) {
				if(edgesOnOneSide.get(i).edge.visible) visibleEdgeCount++;
				if(edgesOnOneSide.get(i) == edgeEnd && edgeEnd.getNode() == owner) {//(isStartNode?edgeEnd.startNode:edgeEnd.endNode) == owner) {
					double maxX = direction == PortRegion.NORTH || direction == PortRegion.WEST ? owner.getX() : owner.getRightX();
					double minX = direction == PortRegion.SOUTH || direction == PortRegion.WEST ? owner.getX() : owner.getRightX();
					double minY = direction == PortRegion.NORTH || direction == PortRegion.WEST ? owner.getY() : owner.getBottomY();
					double maxY = direction == PortRegion.NORTH || direction == PortRegion.EAST ? owner.getY() : owner.getBottomY();
					
					double diffX = maxX - minX;
					double diffY = maxY - minY;
					
					int visibleEdges = 0;
					for(Edge.End E : edgesOnOneSide) if(E.edge.visible) visibleEdges++;
					
					if(edgeEnd.edge.visible) {
						double share = 1. / (visibleEdges + 1);
						return new Point2D(minX + diffX * share * (visibleEdgeCount), minY + diffY * share * (visibleEdgeCount));
					}	else {
						return new Point2D(minX + diffX / 2, minY + diffY / 2);
					}
				}
			}
		}
		throw new RuntimeException("Point does not exist: Edge " + edgeEnd + " on Node " + owner);
	}

	public PortRegion getDirectionForEdge(Edge.End edgeEnd, boolean isStartNode) {
		for(PortRegion direction : PortRegion.values()) {
			Vector<Edge.End> edgesOnOneSide = edges.get(direction);
			for(int i = 0; i < edgesOnOneSide.size(); i++) {
				if(edgesOnOneSide.get(i) == edgeEnd && edgeEnd.getNode() == owner) {//(isStartNode?edge.sourceNode:edge.targetNode) == owner) {
					return direction;
				}
			}
		}
		throw new RuntimeException("Point does not exist: Edge " + edgeEnd.edge + " on Node " + owner);
	}

	public void setDirectionForEdge(Edge.End edgeEnd, boolean isStartNode, PortRegion newPortRegion) {
		for(PortRegion direction : PortRegion.values()) {
			Vector<Edge.End> edgesOnOneSide = edges.get(direction);
			for(int i = 0; i < edgesOnOneSide.size(); i++) {
				if(edgesOnOneSide.get(i) == edgeEnd && edgeEnd.getNode() == owner) {//(isStartNode?edge.sourceNode:edge.targetNode) == owner) {
					edgesOnOneSide.remove(i); i--; break;
				}
			}
		}
		edges.get(newPortRegion).add(edgeEnd);
	}
}
