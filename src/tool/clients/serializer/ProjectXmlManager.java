package tool.clients.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.serializer.interfaces.IXmlManager;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.List;

public class ProjectXmlManager implements IXmlManager {
    private final XmlHandler xmlHandler;

    public ProjectXmlManager(XmlHandler xmlHandler) {
        this.xmlHandler = xmlHandler;
    }

    public Node createProject(String name){
        Element project = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_PROJECT);
        project.setAttribute(XmlConstant.ATTRIBUTE_NAME, name);
        return project;
    }
    
    public Node get(String name){
        Node projects = xmlHandler.getProjectsNode();
        return xmlHandler.getXmlHelper().getChildrenByAttributeValue(projects, XmlConstant.ATTRIBUTE_NAME, name);
    }

    @Override
    public void add(Element element) throws TransformerException {
        Node projects = xmlHandler.getProjectsNode();
        xmlHandler.addElement(projects, element);
    }

    @Override
    public void remove(Element element){
        Node projects = xmlHandler.getProjectsNode();
        projects.removeChild(element);
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

    public String getProjectName() {
        String projectPath = "";
        List<Node> projectList = getAll();
        if(projectList.size()==1){
            Element tmp = (Element) projectList.get(0);
            projectPath = tmp.getAttribute(XmlConstant.ATTRIBUTE_NAME);
        }
        return getProjectName(projectPath);
    }

    private String getProjectName(String projectPath) {
        String[] projectPathSplit= projectPath.split("::");
        return projectPathSplit[1];
    }

    public Node getProjectsNode() {
        return xmlHandler.getProjectsNode();
    }

    public boolean projectIsExist(String packagePath) {
        Node projects = getProjectsNode();
        NodeList projectList = projects.getChildNodes();

        for(int i =0; i< projectList.getLength(); i++){
            Node tmp = projectList.item(i);
            if(tmp.getNodeType()==Node.ELEMENT_NODE){
                Element projectElement = (Element) tmp;
                if(projectElement.getAttribute(XmlConstant.ATTRIBUTE_NAME).equals(packagePath)){
                    return true;
                }
            }
        }
        return false;
    }

    public void removeAll() throws TransformerException {
        xmlHandler.removeAllProject();
    }

    public Element createProjectElement(FmmlxDiagram diagram) {
        Element project = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_PROJECT);
        project.setAttribute(XmlConstant.ATTRIBUTE_NAME, diagram.getPackagePath());
        return project;
    }
}
