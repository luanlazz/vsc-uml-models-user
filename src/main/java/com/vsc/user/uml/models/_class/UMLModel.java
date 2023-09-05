package com.vsc.user.uml.models._class;

import java.io.Serializable;
import java.util.ArrayList;

import com.vsc.user.uml.models._enum.EnumStructure;

public class UMLModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String type = "model";
	private ArrayList<ClassStructure> classes = new ArrayList<>();
	private ArrayList<ClassInstance> instances = new ArrayList<>();
	private ArrayList<EnumStructure> enumerations = new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<ClassStructure> getClasses() {
		return classes;
	}

	public void setClasses(ArrayList<ClassStructure> classes) {
		this.classes = classes;
	}

	public void addClass(ClassStructure _class) {
		this.classes.add(_class);
	}

	public ArrayList<ClassInstance> getInstances() {
		return instances;
	}

	public void setInstances(ArrayList<ClassInstance> instances) {
		this.instances = instances;
	}

	public void addInstance(ClassInstance instance) {
		this.instances.add(instance);
	}

	public ArrayList<EnumStructure> getEnumerations() {
		return enumerations;
	}

	public void setEnumerations(ArrayList<EnumStructure> enumerations) {
		this.enumerations = enumerations;
	}

	public void addEnumeration(EnumStructure enumeration) {
		this.enumerations.add(enumeration);
	}
}
