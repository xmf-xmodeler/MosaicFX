package tool.clients.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.serializer.interfaces.IXmlManager;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.List;

public class ProjectXmlManager implements IXmlManager {
    private final XmlHandler xmlHandler;

    public ProjectXmlManager() {
        this.xmlHandler = new XmlHandler();
    }

    public Node createProject(String name){
        Element project = (Element) xmlHandler.createXmlElement(XmlConstant.TAG_NAME_PROJECT);
        project.setAttribute(XmlConstant.ATTRIBUTE_NAME, name);
        return project;
    }
    
    public Node get(String name){
        Node projects = xmlHandler.getProjectsNode();
        return xmlHandler.getXmlHelper().getChildrenByAttributeValue(projects, XmlConstant.ATTRIBUTE_NAME, name);
    }

    @Override
    public void add(Node node) throws TransformerException {
        Node projects = xmlHandler.getProjectsNode();
        xmlHandler.addElement(projects, node);
    }

    @Override
    public void remove(Node node){
        Node projects = xmlHandler.getProjectsNode();
        projects.removeChild(node);
    }

    @Override
    public List<Node> getAll(){
        List<Node> projects = new ArrayList<>();
        Node projectsNode = xmlHandler.getProjectsNode();
        NodeList projectNodeList = projectsNode.getChildNodes();

        for(int i =0; i< projectNodeList.getLength(); i++){
            Node tmp = projectNodeList.item(i);
            if(tmp.getNodeType()==Node.ELEMENT_NODE){
                projects.add(tmp);
            }
        }
        return projects;
    }
}
