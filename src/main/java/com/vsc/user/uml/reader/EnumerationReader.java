package com.vsc.user.uml.reader;

import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.PackageableElement;

import com.vsc.user.uml.models._enum.EnumStructure;

public class EnumerationReader {
	public static EnumStructure readEnumeration(PackageableElement element, String packageName) {

		EnumStructure structure = new EnumStructure();
		Enumeration enumeration = (Enumeration) element;
		structure.setName(enumeration.getName());
		structure.setPackage(packageName);
		for (EnumerationLiteral literal : enumeration.getOwnedLiterals()) {
			structure.addLiteral(literal.getName());
		}

		return structure;
	}
}
