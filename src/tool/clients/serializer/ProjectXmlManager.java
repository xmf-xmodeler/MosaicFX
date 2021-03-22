package tool.clients.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class ProjectXmlManager  {
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
        Element projects = getProjectsElement();
        return xmlHandler.getChildrenByAttributeValue(projects, XmlConstant.ATTRIBUTE_NAME, name);
    }

    public void add(Element parent, Element element) {
        Element projects = getProjectsElement();
        xmlHandler.addXmlElement(projects, element);
    }

    public void remove(Element element){
        Node projects = getProjectsElement();
        projects.removeChild(element);
    }

    public List<Node> getAll(){
        List<Node> projects = new ArrayList<>();
        Node projectsNode = getProjectsElement();
        NodeList projectNodeList = projectsNode.getChildNodes();

        for(int i =0; i< projectNodeList.getLength(); i++){
            Node tmp = projectNodeList.item(i);
            if(tmp.getNodeType()==Node.ELEMENT_NODE){
                projects.add(tmp);
            }
        }
        return projects;
    }

    public Element getDiagramsElement(){
        Element Root = xmlHandler.getRoot();
        return xmlHandler.getChildWithTag(Root, XmlConstant.TAG_NAME_DIAGRAMS);
    }

    public String getProjectName(String projectPath) {
        String[] projectPathSplit= projectPath.split("::");
        return projectPathSplit[1];
    }

    public Element getProjectsElement() {
        Element Root = xmlHandler.getRoot();
        return xmlHandler.getChildWithTag(Root, XmlConstant.TAG_NAME_PROJECTS);
    }

    public boolean projectIsExist(String packagePath) {
        Node projects = getProjectsElement();
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

    public void removeAll() {
        Element projects = getProjectsElement();
        xmlHandler.removeAllChildren(projects);
    }

    public Element createProjectElement(String packagePath) {
        Element project = xmlHandler.createXmlElement(XmlConstant.TAG_NAME_PROJECT);
        project.setAttribute(XmlConstant.ATTRIBUTE_NAME,packagePath);
        return project;
    }

    public String getProjectPath() {
        String projectPath = "";
        List<Node> projectList = getAll();
        if(projectList.size()==1){
            Element tmp = (Element) projectList.get(0);
            projectPath = tmp.getAttribute(XmlConstant.ATTRIBUTE_NAME);
        }
        return projectPath;
    }
}
