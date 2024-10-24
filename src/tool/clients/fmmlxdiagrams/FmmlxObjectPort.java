package tool.clients.fmmlxdiagrams;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import javafx.geometry.Point2D;

public class FmmlxObjectPort {
	private final Node owner;
	private final HashMap<PortRegion, Vector<Edge<?>.End>> edges;

	public FmmlxObjectPort(Node owner) {
		super();
		this.owner = owner;
		edges = new HashMap<>();
		for(PortRegion direction : PortRegion.values()) {
			edges.put(direction, new Vector<>());
		}
	}
	
	public void removeEdge(Edge<?>.End edge) {
		for(PortRegion direction : PortRegion.values()) {
			if(edges.get(direction).contains(edge)) {
				edges.get(direction).remove(edge);
				sortPorts(direction);
			}
		}
	}
	
	public void addNewEdge(Edge<?>.End edge, PortRegion direction) {
		edges.get(direction).add(edge);
		sortPorts(direction);
	}

	public void sortAllPorts() { for(PortRegion direction : PortRegion.values()) sortPorts(direction); }
	
	private void sortPorts(final PortRegion direction) {
		Vector<Edge<?>.End> ports = edges.get(direction);
		ports.sort(Comparator.comparing(e -> getAngle(e.edge, direction)));
	}
	
	private Double getAngle(Edge<?> edge, PortRegion direction) {
		double startAngle = Math.PI*(
				direction == PortRegion.NORTH?-1./2:
            	direction == PortRegion.EAST?-1.:
            	direction == PortRegion.SOUTH?-3./2:
            	0);
		Node otherEnd = edge.sourceNode==owner?edge.targetNode:edge.sourceNode;
		double angle = Math.atan2(
				owner.getCenterY()    - otherEnd.getCenterY(), 
				otherEnd.getCenterX() - owner.getCenterX());
		while(angle < startAngle) angle += 2*Math.PI;
		while(angle > startAngle + 2*Math.PI) angle -= 2*Math.PI;
		return angle;
	}

	public Point2D getPointForEdge(Edge<?>.End edgeEnd, boolean isStartNode) {
		for(PortRegion direction : PortRegion.values()) {
			Vector<Edge<?>.End> edgesOnOneSide = edges.get(direction);
			int visibleEdgeCount = 0;
			for(int i = 0; i < edgesOnOneSide.size(); i++) {
				if(edgesOnOneSide.get(i).edge.isVisible()) visibleEdgeCount++;
				if(edgesOnOneSide.get(i) == edgeEnd && edgeEnd.getNode() == owner) {//(isStartNode?edgeEnd.startNode:edgeEnd.endNode) == owner) {
					double maxX = direction == PortRegion.NORTH || direction == PortRegion.WEST ? owner.getLeftX() : owner.getRightX();
					double minX = direction == PortRegion.SOUTH || direction == PortRegion.WEST ? owner.getLeftX() : owner.getRightX();
					double minY = direction == PortRegion.NORTH || direction == PortRegion.WEST ? owner.getTopY() : owner.getBottomY();
					double maxY = direction == PortRegion.NORTH || direction == PortRegion.EAST ? owner.getTopY() : owner.getBottomY();
					
					double diffX = maxX - minX;
					double diffY = maxY - minY;
					
					int visibleEdges = 0;
					for(Edge<?>.End E : edgesOnOneSide) if(E.edge.isVisible()) visibleEdges++;
					
					Point2D result = null;
					if(edgeEnd.edge.isVisible()) {
						double share = 1. / (visibleEdges + 1);
						result =  new Point2D(minX + diffX * share * (visibleEdgeCount), minY + diffY * share * (visibleEdgeCount));
					}	else {
						result =  new Point2D(minX + diffX / 2, minY + diffY / 2);
					}
					return owner.rootNodeElement.getDragAffine().transform(result);
					}
			}
		}
		throw new RuntimeException("Point does not exist: Edge " + edgeEnd + " on Node " + owner);
	}

	public PortRegion getDirectionForEdge(Edge<?>.End edgeEnd, boolean isStartNode) {
		for(PortRegion direction : PortRegion.values()) {
			Vector<Edge<?>.End> edgesOnOneSide = edges.get(direction);
			for (Edge<?>.End end : edgesOnOneSide) {
				if (end == edgeEnd && edgeEnd.getNode() == owner) {//(isStartNode?edge.sourceNode:edge.targetNode) == owner) {
					return direction;
				}
			}
		}
		throw new RuntimeException("Point does not exist: Edge " + edgeEnd.edge + " on Node " + owner);
	}

	public void setDirectionForEdge(Edge<?>.End edgeEnd, boolean isStartNode, PortRegion newPortRegion) {
		for(PortRegion direction : PortRegion.values()) {
			Vector<Edge<?>.End> edgesOnOneSide = edges.get(direction);
			for(int i = 0; i < edgesOnOneSide.size(); i++) {
				if(edgesOnOneSide.get(i) == edgeEnd && edgeEnd.getNode() == owner) {//(isStartNode?edge.sourceNode:edge.targetNode) == owner) {
					edgesOnOneSide.remove(i); i--; break;
				}
			}
		}
		edges.get(newPortRegion).add(edgeEnd);
	}
}
