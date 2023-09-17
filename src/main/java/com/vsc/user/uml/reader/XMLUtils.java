package com.vsc.user.uml.reader;

import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.uml2.uml.Element;

public final class XMLUtils {
	
	public static String getXMLId(Element element) {
		String id = "";
		
		if (element.eResource() != null) {
			XMLResource xmlResource = (XMLResource) element.eResource();
			id = xmlResource.getID(element);
		}
		
		return id;
	}
	
	public static Boolean setXMLId(Element element, String id) {
		if (element.eResource() != null) {
			XMLResource xmlResource = (XMLResource) element.eResource();
			xmlResource.setID(element, id);
			return true;
		} 
		
		return false;
	}
}
