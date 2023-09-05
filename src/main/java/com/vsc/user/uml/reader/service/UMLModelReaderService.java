package com.vsc.user.uml.reader.service;

import java.io.File;

import org.eclipse.uml2.uml.Package;

import com.vsc.user.uml.loader.ModelLoader;
import com.vsc.user.uml.models._class.UMLModel;
import com.vsc.user.uml.reader.diagram.UMLModelReader;

public class UMLModelReaderService {

	public static UMLModel classDiagramReader(String filePath) throws Exception {
		File model = new File(filePath);
		Package aPackage = new ModelLoader().loadModel(model);
		return UMLModelReader.getRefModelDetails(aPackage);
	}
	
	public static Package modelReader(String filePath) throws Exception {
		File model = new File(filePath);
		return new ModelLoader().loadModel(model);
	}
}
