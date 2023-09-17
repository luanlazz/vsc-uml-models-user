package com.vsc.user.uml.loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.emf.mapping.ecore2xml.Ecore2XMLPackage;
import org.eclipse.emf.mapping.ecore2xml.util.Ecore2XMLResource;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.resource.UMLResourceImpl;
import org.eclipse.uml2.uml.resource.UML212UMLResource;
import org.eclipse.uml2.uml.resource.UML22UMLResource;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.util.UMLUtil;

import com.vsc.user.utils.ConvertProperties;

public class ModelLoader {

	private static ResourceSet RESOURCE_SET = null;

	public ModelLoader() {
		getResourceSet();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void registerResourceFactories() {
		Map extensionFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
		extensionFactoryMap.put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		extensionFactoryMap.put(UMLResource.FILE_EXTENSION, UML22UMLResource.Factory.INSTANCE);
		extensionFactoryMap.put(UML22UMLResource.FILE_EXTENSION, UML22UMLResource.Factory.INSTANCE);

		extensionFactoryMap.put(Ecore2XMLResource.FILE_EXTENSION, Ecore2XMLResource.Factory.INSTANCE);
		extensionFactoryMap.put("ecore", new EcoreResourceFactoryImpl());

		extensionFactoryMap.put("xml", UMLResource.Factory.INSTANCE);

		extensionFactoryMap.put("xml", new XMLResourceFactoryImpl());

		extensionFactoryMap.put("xmi", UMLResource.Factory.INSTANCE);
		extensionFactoryMap.put("xmi", new XMIResourceFactoryImpl());

		extensionFactoryMap.put("genmodel", new XMIResourceFactoryImpl());

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void registerPackages(ResourceSet resourceSet) {
		Map packageRegistry = resourceSet.getPackageRegistry();
		packageRegistry.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		packageRegistry.put(Ecore2XMLPackage.eNS_URI, Ecore2XMLPackage.eINSTANCE);
		packageRegistry.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
		packageRegistry.put(UML212UMLResource.UML_METAMODEL_NS_URI, UMLPackage.eINSTANCE);

		packageRegistry.put("http://www.eclipse.org/uml2/1.0.0/UML", UMLPackage.eINSTANCE);
		packageRegistry.put("http://www.eclipse.org/uml2/2.0.0/UML", UMLPackage.eINSTANCE);
		packageRegistry.put("http://www.eclipse.org/uml2/3.0.0/UML", UMLPackage.eINSTANCE);
		packageRegistry.put("http://www.eclipse.org/uml2/4.0.0/UML", UMLPackage.eINSTANCE);
		packageRegistry.put("http://www.eclipse.org/uml2/5.0.0/UML", UMLPackage.eINSTANCE);
	}

	public static ResourceSet getResourceSet() {
		if (RESOURCE_SET == null) {
			RESOURCE_SET = new ResourceSetImpl();
			registerPackages(getResourceSet());
			registerResourceFactories();
		}
		return RESOURCE_SET;
	}

	public UMLResource registerModel(File file) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);

		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION,
				UMLResource.Factory.INSTANCE);
		Map<URI, URI> uriMap = resourceSet.getURIConverter().getURIMap();

		String pathToJar = getPathToUML2Package();
		if (!pathToJar.isEmpty()) {
			URI uri = URI.createURI(pathToJar);
			uriMap.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP), uri.appendSegment("libraries").appendSegment(""));
			uriMap.put(URI.createURI(UMLResource.METAMODELS_PATHMAP),
					uri.appendSegment("metamodels").appendSegment(""));
			uriMap.put(URI.createURI(UMLResource.PROFILES_PATHMAP), uri.appendSegment("profiles").appendSegment(""));
		}

//		Map<String, Object> options = new HashMap<>();
//		options.put(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
//		options.put(XMLResource.OPTION_RECORD_UNKNOWN_FEATURE, Boolean.TRUE);
//		options.put(XMLResource.OPTION_ENCODING, "UTF-8");
//		
//		resource = getResourceSet().createResource(URI.createFileURI(file.getCanonicalPath()));
//		resource.load(options);

		URI uriModel = URI.createFileURI(file.getCanonicalPath());

		UMLResource resource = new UMLResourceImpl(uriModel);
		resource.load(uriMap);
		return resource;
	}

	public Package loadModel(File file) throws IOException {
		if (file.getName().endsWith("uml")) {
			return loadUMLModel(file);
		} else if (file.getName().endsWith("ecore")) {
			return loadEcoreModel(file);
		}
		return null;
	}

	private Package loadUMLModel(File file) throws IOException {
		Resource resource = registerModel(file);

		Package _package;

//        for (EObject ojEObject : resource.getContents()) {
//            System.out.println(ojEObject.getClass());
//        }

		_package = (Model) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.MODEL);

		if (_package == null) {
			_package = (Package) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.PACKAGE);
		}

		if (_package == null) {

			EPackage _package2 = (EPackage) EcoreUtil.getObjectByType(resource.getContents(),
					EcorePackage.Literals.EPACKAGE);
			if (_package2 != null) {
				Collection<Package> ecorePackages = UMLUtil.convertFromEcore(_package2,
						ConvertProperties.optionsToProcess());

				if (ecorePackages != null && !ecorePackages.isEmpty()) {
					_package = (Package) ecorePackages.toArray()[0];
				}

			}
		}
		return _package;

	}

	public void save(Package package_, URI uri) {
		// Create the resource to be saved and add the package to it
		Resource resource = RESOURCE_SET.createResource(uri);
		resource.getContents().add(package_);

		// And save.
		try {
			resource.save(null);
			System.out.println("Done.");
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}

	public static Package load(URI uri) {
		Package package_ = null;

		try {
			// Load the requested resource
			Resource resource = RESOURCE_SET.getResource(uri, true);

			// Get the first (should be only) package from it
			package_ = (Package) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.PACKAGE);
		} catch (WrappedException we) {
			System.out.println(we.getMessage());
			System.exit(1);
		}

		return package_;
	}

	private Package loadEcoreModel(File file) throws IOException {
		Resource resource = registerModel(file);

		EPackage _package;

		_package = (EPackage) EcoreUtil.getObjectByType(resource.getContents(), EcorePackage.Literals.EPACKAGE);

		Package _UMLPackage = null;

		if (_package != null) {
			Collection<Package> ecorePackages = UMLUtil.convertFromEcore(_package,
					ConvertProperties.optionsToProcess());
			if (ecorePackages != null && !ecorePackages.isEmpty()) {
				_UMLPackage = (Package) ecorePackages.toArray()[0];
			}
		}

		return _UMLPackage;

	}

	@SuppressWarnings("unused")
	private void registerPathmaps(URI uri) {
		URIConverter.URI_MAP.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP),
				uri.appendSegment("libraries").appendSegment(""));
		URIConverter.URI_MAP.put(URI.createURI(UMLResource.METAMODELS_PATHMAP),
				uri.appendSegment("metamodels").appendSegment(""));
		URIConverter.URI_MAP.put(URI.createURI(UMLResource.PROFILES_PATHMAP),
				uri.appendSegment("profiles").appendSegment(""));
	}

	public String getPathToUML2Package() {
		String pathToJar = null;

		final char PKG_SEPARATOR = '.';
		final char DIR_SEPARATOR = '/';

		String scannedPackage = "org.eclipse.uml2.uml";
		String scannedPath = scannedPackage.replace(PKG_SEPARATOR, DIR_SEPARATOR);
		URL url = getClass().getClassLoader().getResource(scannedPath);
		if (url != null) {
			if (url.getPath().contains("file:")) {
				pathToJar = url.getPath().substring(5, url.getPath().indexOf(".jar") + 4);
			} else {
				pathToJar = url.getPath().substring(0, url.getPath().indexOf(".jar") + 4);
			}
		}

		return pathToJar;
	}
}
