package com.vsc.user.uml.reader.service;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.resource.UMLResource;

import com.vsc.user.uml.loader.ModelLoader;
import com.vsc.user.uml.models._class.UMLModel;
import com.vsc.user.uml.reader.diagram.UMLModelReader;

public class UMLModelReaderService {

	public static UMLModel classDiagramReader(String filePath) throws Exception {
		File modelFile = new File(filePath);
		Package aPackage = new ModelLoader().loadModel(modelFile);
		return UMLModelReader.getRefModelDetails(aPackage);
	}
	
	public static Package modelReader(String filePath) throws Exception {
		File modelFile = new File(filePath);
		return new ModelLoader().loadModel(modelFile);
	}
	
	public static UMLResource modelReaderUmlResource(URI filePath) throws Exception {
		File modelFile = new File(filePath.toFileString());
		return new ModelLoader().registerModel(modelFile);
	}
}
