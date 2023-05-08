package tool.clients.customui;

import java.awt.Button;
import java.awt.TextField;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javafx.collections.ObservableArray;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.GridPane;
import javafx.scene.transform.Transform;

// This class is only capable of extracting a specific kind of nodes from JFX so far
// Mostly, the GridPanes generated within the ObjectBrowser!
public class FXMLExporter {

	
	// This class is supposed to extract a given JavaFX node into a FXML document
	/** Export a 3D MeshView to FXML file. */

	    private PrintWriter printWriter;
	    private Set<String> imports = new TreeSet<>();
	    private Map<String, String> simpleNames = new HashMap<>();
	    
	    // Create a new file and instantiate the PrintWriter
	    public FXMLExporter(String filename) {
	        File file = new File(filename);
	        try {
	            printWriter = new PrintWriter(file);
	            System.out.println("Saving FMXL to " + file.getAbsolutePath());
	        } catch (FileNotFoundException ex) {
	            throw new RuntimeException("Failed to export FXML to " + file.getAbsolutePath(), ex);
	        }
	    }
	    
	    // This method will write a given node into the PrintWriter
	    public void export(Node node) {
	    	// Header
	        printWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	        
	        // This variable holds the node translated into a FXML tree
	        FXML fxmlTree = exportToFXML(node);
	        
	        // Write the required imports
	        for (String importString : imports) {
	            printWriter.println("<?import " + importString + ".*?>");
	        }
	        
	        // Now write the translated FXML tree of the GUI
	        printWriter.println();
	        fxmlTree.export("");
	        printWriter.close();
	    }
	    
	    // Custom Exception if something is cannot be handled
	    private class NoHandling extends Throwable {
	        static final long serialVersionUID = -3387516993124229948L;

	        public NoHandling() {
	            super();
	        }

	        public NoHandling(String message) {
	            super(message);
	        }

	        public NoHandling(String message, Throwable cause) {
	            super(message, cause);
	        }

	        public NoHandling(Throwable cause) {
	            super(cause);
	        }

	        protected NoHandling(String message, Throwable cause, boolean enableSuppression, boolean     writableStackTrace) {
	            super(message, cause, enableSuppression, writableStackTrace);
	        }
	    }
	    
	    // This adds the properties for JavaFX Classes
	    private List<Property> getProperties(Class aClass) {
	        List<Property> res = new ArrayList<>();
	        boolean found = false;
	        try {
	            if (Node.class.isAssignableFrom(aClass)) {
	                res.add(new Property(aClass.getMethod("getId"), "fx:id"));
	                res.add(new Property(aClass.getMethod("getTransforms"), "transforms"));
	                found = true;
	            }
	            //if (Parent.class.isAssignableFrom(aClass)) {
	            //	res.add(new Property(aClass.getMethod("getChildrenUnmodifiable"), "children"));
	            //    found = true;
	            // Klassenproperties f�r den Object Browser
	            //} 
	            if(Label.class.isAssignableFrom(aClass)) {
	                // zus�tzlich GridPane Ids ermitteln
	            	found = true;
	            } if(Labeled.class.isAssignableFrom(aClass)) {
	            	// every Subclass of Labeled is affected
	            	res.add(new Property(aClass.getMethod("getText"), "text")); 
	            }      
	            if(Button.class.isAssignableFrom(aClass)) { // does not work..?
	            	// zus�tzlich Default on Action ermitteln 
	            	// zus�tzlich GridPane Ids ermitteln
	            	found = true;
	            } if(TextField.class.isAssignableFrom(aClass)) { // does not work..?
	            	// zus�tzlich GridPane Ids ermitteln
	            	found = true;
	            } if(GridPane.class.isAssignableFrom(aClass)) {
	            	//res.add(new Property(aClass.getMethod("getChildren"), "children")); 
	            	res.add(new Property(aClass.getMethod("getChildrenUnmodifiable"), "children"));
	            	res.add(new Property(aClass.getMethod("getPadding"), "padding")); 
	            	res.add(new Property(aClass.getMethod("getHgap"), "hgap")); 
	            	res.add(new Property(aClass.getMethod("getVgap"), "vgap")); 
	            	found = true;
	            } // required for the padding of the gridpane 
	            if(Insets.class.isAssignableFrom(aClass)) {
	            	res.add(new Property(aClass.getMethod("getTop"), "top")); 
	            	res.add(new Property(aClass.getMethod("getBottom"), "bottom")); 
	            	res.add(new Property(aClass.getMethod("getLeft"), "left")); 
	            	res.add(new Property(aClass.getMethod("getRight"), "right"));
	            	found = true;
	            } 
	            
	            // Fehlerausgabe, wenn ein GUI-Element nicht exportiert werden konnte
	            if( !found ) {	            	
	            	throw new NoHandling(aClass.getName() + " Properties could not be exported to FXML!");
	            }
	        } catch (NoSuchMethodException | SecurityException ex) {
	        	System.err.println(ex.getMessage());
	        } catch (NoHandling ex) {
	        	System.err.println( ex.getMessage() );
	        }
	        	
	        return res;
	    }

	    private Map<Class, List<Property>> propertiesCache = new HashMap<>();
	    
	    // This class is used to define properties of JFX classes
	    private class Property {
	        Method getter;
	        String name;

	        public Property(String name) {
	            this.name = name;
	        }

	        public Property(Method getter, String name) {
	            this.getter = getter;
	            this.name = name;
	        }
	    }
	    
	    // This method builds the FXML tree
	    private FXML exportToFXML(Object object) {
	        if (object instanceof Transform && ((Transform) object).isIdentity()) {
	            return null;
	        }
	        
	        // create FXML node
	        FXML fxml = new FXML(object.getClass());

	        List<Property> properties = propertiesCache.get(object.getClass());
	        if (properties == null) {
	            properties = getProperties(object.getClass());
	            propertiesCache.put(object.getClass(), properties);
	        }
	        
	        // Additionally try to set property f�r Row and ColumnIndex of GridPane
	        // if object is label, button or textfield
	        try {
	        	Object value;
	        	switch( fxml.tagName ) {
	        		case "GridPane":
	        			fxml.addProperty("xmlns:fx", "http://javafx.com/fxml/1"); // Default Action
	        			fxml.addProperty("xmlns", "http://javafx.com/javafx/17"); // Default Action
	        			
	        			// hier ist ggf. auch ein Controller zu definieren
	        			// F.H. mit der Änderung auf das neue Controller Modell und dem Mapping innerhalb des
	        			// Diagramms, ist es nicht mehr notwendig hier einen Controller festzulegen.
	        			//fxml.addProperty("fx:controller", "tool.clients.customui.CustomGUIController");
	        			
	        			break;
	        	
	        		case "Label":
	        			value = GridPane.getColumnIndex((Node)object);
	        			if( (int) value != 0 ) fxml.addProperty("GridPane.columnIndex", String.valueOf(value));
	        			
	        			value = GridPane.getRowIndex((Node)object);
	        			if( (int) value != 0 ) fxml.addProperty("GridPane.rowIndex", String.valueOf(value));
	        			break;
	        			
	        		case "Button":
	        			fxml.addProperty("onAction", "#setSlot"); // Default Action
	        			
	        			value = GridPane.getColumnIndex((Node)object);
	        			if( (int) value != 0 ) fxml.addProperty("GridPane.columnIndex", String.valueOf(value));
	        			
	        			value = GridPane.getRowIndex((Node)object);
	        			if( (int) value != 0 ) fxml.addProperty("GridPane.rowIndex", String.valueOf(value));
	        			break;
	        			
	        		case "TextField":
	        			value = GridPane.getColumnIndex((Node)object);
	        			if( (int) value != 0 ) fxml.addProperty("GridPane.columnIndex", String.valueOf(value));
	        			
	        			value = GridPane.getRowIndex((Node)object);
	        			if( (int) value != 0 ) fxml.addProperty("GridPane.rowIndex", String.valueOf(value));
	        			break;
	        			
	        		case "ListView":
	        			value = GridPane.getColumnIndex((Node)object);
	        			if( (int) value != 0 ) fxml.addProperty("GridPane.columnIndex", String.valueOf(value));
	        			
	        			value = GridPane.getRowIndex((Node)object);
	        			if( (int) value != 0 ) fxml.addProperty("GridPane.rowIndex", String.valueOf(value));
	        			break;
	        	}
	        } catch (Exception e) {
	        	System.err.println(e.getMessage());
	        }
	        
	        	
	        for (Property property : properties) {
	            try {
	                Object[] parameters = new Object[property.getter.getParameterTypes().length];
	                
		            Object value = property.getter.invoke(object, parameters);
		            
	                if (value != null) {
	                    if (value instanceof Collection) {
	                        Collection collection = (Collection) value;
	                        if (!collection.isEmpty()) {
	                            FXML container = fxml.addContainer(property.name);
	                            for (Object item : collection) {
	                                container.addChild(exportToFXML(item));
	                            }
	                        }
	                    } else if (value instanceof ObservableArray) {
	                        int length = ((ObservableArray) value).size();
	                        if (length > 0) {
	                            FXML container = fxml.addContainer(property.name);
	                            container.setValue(value);
	                        }
	                    } else if (property.getter.getReturnType().isPrimitive()
	                            || String.class.equals(value.getClass())) {
	                        fxml.addProperty(property.name, String.valueOf(value));
	                    } else {
	                        FXML container = fxml.addContainer(property.name);
	                        container.addChild(exportToFXML(value));
	                    }
	                }
	            } catch (Exception ex) {
	                System.err.println(ex.getMessage());
	            }
	        }

	        return fxml;
	    }

	    // FXML tree
	    private class FXML {
	    	// every FXML node has a tag name, a value, attributes and possibly children
	        private String tagName;
	        List<Entry> properties;
	        List<FXML> nested;
	        Object value;

	        private FXML addContainer(String containerTag) {
	            if (nested != null) {
	                for (FXML n : nested) {
	                    if (n.tagName.equals(containerTag)) {
	                        return n;
	                    }
	                }
	            }
	            FXML fxml = new FXML(containerTag);
	            addChild(fxml);
	            return fxml;
	        }
	        
	        // called for childs if the tagname is already known
	        public FXML(String tagName) {
	            this.tagName = tagName;
	        }
	        
	        // Get tag for a certain class of a GUI object
	        public FXML(Class cls) {
	            String fullName = simpleNames.get(cls.getSimpleName());
	            if (fullName == null) {

	                // this short name is not used, so adding it to imports and
	                // to shortNames map
	                fullName = cls.getName();
	                imports.add(cls.getPackage().getName());
	                simpleNames.put(cls.getSimpleName(), fullName);
	                tagName = cls.getSimpleName();
	            } else if (!fullName.equals(cls.getName())) {

	                // short name is already used for some other class so we have
	                // to use full name
	                tagName = cls.getName();
	            } else {

	                // short name matches this class
	                tagName = cls.getSimpleName();
	            }
	        }
	        
	        // Simple key-value-pair
	        private class Entry {
	            String key;
	            String value;

	            public Entry(String key, String value) {
	                this.key = key;
	                this.value = value;
	            }
	        }

	        void setValue(Object value) {
	            this.value = value;
	        }
	        
	        // every property of a tag is a key value pair
	        void addProperty(String key, String value) {
	            if (properties == null) {
	                properties = new ArrayList<>();
	            }
	            properties.add(new Entry(key, value));
	        }

	        // export the node (and children recursively with indent) to the printWriter
	        void export(String indent) {
	            printWriter.append(indent).append('<').append(tagName);
	            if (properties != null) {
	                for (Entry entry : properties) {
	                    printWriter.append(' ').append(entry.key).append("=\"")
	                               .append(entry.value).append("\"");
	                }
	            }
	            if (nested != null || value != null) {
	                printWriter.append(">\n");
	                String indent1 = indent + "  ";
	                if (nested != null) {
	                    for (FXML fxml : nested) {
	                        fxml.export(indent1);
	                    }
	                }
	                if (value != null) {
	                    String toString;
	                    if (value instanceof ObservableArray) {
	                        toString = value.toString();
	                    } else {
	                        throw new UnsupportedOperationException("Only ObservableArrays are currently supported");
	                    }
	                    printWriter.append(indent1).append(toString.substring(1, toString.length() - 1)).append("\n");
	                }
	                printWriter.append(indent).append("</").append(tagName).append(">\n");
	            } else {
	                printWriter.append("/>\n");
	            }
	        }
	        
	        // childs are contained by a corresponding attribute of the FXML node
	        private void addChild(FXML fxml) {
	            if (fxml == null) {
	                return;
	            }
	            if (nested == null) {
	                nested = new ArrayList<>();
	            }
	            nested.add(fxml);
	        }
	    }

	}
