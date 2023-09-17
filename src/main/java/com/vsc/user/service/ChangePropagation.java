package com.vsc.user.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Extension;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.InstanceValue;
import org.eclipse.uml2.uml.LiteralBoolean;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.vsc.user.infra.dao.AttributeEntity;
import com.vsc.user.infra.dao.ClassEntity;
import com.vsc.user.infra.dao.DiagramEntity;
import com.vsc.user.infra.dao.HistoryChangeEntity;
import com.vsc.user.infra.dao.OperationEntity;
import com.vsc.user.infra.dao.OperationParameterEntity;
import com.vsc.user.infra.dao.VersionEntity;
import com.vsc.user.infra.repository.AttributeRepository;
import com.vsc.user.infra.repository.ClassRepository;
import com.vsc.user.infra.repository.DiagramRepository;
import com.vsc.user.infra.repository.HistoryChangeRepository;
import com.vsc.user.infra.repository.OperationParameterRepository;
import com.vsc.user.infra.repository.OperationRepository;
import com.vsc.user.infra.repository.VersionRepository;
import com.vsc.user.uml.loader.ModelLoader;
import com.vsc.user.uml.reader.XMLUtils;
import com.vsc.user.uml.reader.service.UMLModelReaderService;
import com.vsc.user.utils.HTTP;
import com.vsc.user.utils.HTTPResponse;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Component("parseUMLModel")
public class ChangePropagation {

	private static final Logger logger = LoggerFactory.getLogger(ChangePropagation.class);

	@Autowired
	private HistoryChangeRepository historyChangeRepository;
	@Autowired
	private ClassRepository classRepository;
	@Autowired
	private DiagramRepository diagramRepository;
	@Autowired
	private AttributeRepository attributeRepository;
	@Autowired
	private OperationRepository operationRepository;
	@Autowired
	private OperationParameterRepository parameterRepository;
	@Autowired
	private VersionRepository versionRepository;

	public static boolean DEBUG = true;

	private static final String urlServer = "http://localhost:8080/server/uml";

	private String username = "user1";
	private String versionToken = "";
	private String folder = "/home/luanl/Documents/Eclipse/mdt/UmlModel/";
//	private String file = "new_uml";
	private String file = "MyProjectToReceiveModification";

	ChangePropagation() {
	}

	public URI getPathUMLFile() {
		File fileFolder = new File(this.folder);
		
//		return URI.createFileURI(fileFolder.getAbsolutePath()).appendSegment(file + "_" + username)
//				.appendFileExtension(UMLResource.FILE_EXTENSION);
		
		return URI.createFileURI(fileFolder.getAbsolutePath()).appendSegment(file)
				.appendFileExtension(UMLResource.FILE_EXTENSION);
	}

	public Boolean sendUMLChanges() throws Exception {
		boolean validFileOfUml = validateFileRead(this.getPathUMLFile(), folder);
		if (!validFileOfUml) {
			throw new Exception("Local UML file not found.");
		}

		String umlContent = new String(Files.readAllBytes(Paths.get(this.getPathUMLFile().toFileString())));

		Map<String, String> parameters = new HashMap<>();
		parameters.put("umlContent", umlContent);
		parameters.put("version", this.versionToken);
		parameters.put("username", this.username);

		HTTPResponse httpResponse = HTTP.sendRequest(urlServer, parameters);

		if (httpResponse.getStatusCode() == HttpServletResponse.SC_OK) {
			this.versionToken = ""; // set version token to saved
			return true;
		} else {
			logger.error(httpResponse.getErrorMsg());
			return false;
		}
	}

	public Boolean mergeUMLVersionChanges(String newVersion) throws Exception {
		boolean validFilePropagate = validateFileWrite(this.getPathUMLFile(), folder);
		if (!validFilePropagate) {
			throw new Exception("Local UML file not found.");
		}

		VersionEntity localVersionEntity = null;
		if (!this.versionToken.isEmpty()) {
			localVersionEntity = this.versionRepository.getByToken(this.versionToken);
			if (localVersionEntity != null && !(localVersionEntity.getId() > 0)) {
				throw new Exception("Invalid local version");
			}
		}

		Boolean createNewModel = localVersionEntity == null;

		VersionEntity newVersionEntity = this.versionRepository.getByToken(newVersion);
		if (!(newVersionEntity.getId() > 0)) {
			throw new Exception("Invalid new version");
		}

		List<HistoryChangeEntity> historyChanges = this.historyChangeRepository.findVersionRange(localVersionEntity,
				newVersionEntity);

		if (createNewModel) {
//			URI outputURI = this.getPathUMLFile();
			
//			Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
//	        Map<String, Object> m = reg.getExtensionToFactoryMap();
//	        m.put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
//	        
//	        ResourceSet resource = new ResourceSetImpl();
//	        
//	        resource.createResource(outputURI);
//			resource.getContents().add(createModel(historyChanges));			
//			
//			Map<String, Object> options = new HashMap<>();
//			options.put(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
//			options.put(XMLResource.OPTION_RECORD_UNKNOWN_FEATURE, Boolean.TRUE);
//			options.put(XMLResource.OPTION_ENCODING, "UTF-8");
			
//			Model modelInit = createModel(historyChanges);		
//			new ModelLoader().save(modelInit, outputURI);
		}
		
		UMLResource resource = loadModel();

		this.mergeChangesToModel(resource, historyChanges);

		resource.save(null);

		this.versionToken = newVersion;
		return true;
	}

	protected Model createModel(List<HistoryChangeEntity> historyChanges) {
		Model model = null;

		for (HistoryChangeEntity historyChange : historyChanges) {
			if (historyChange.getEntityType().equals(DiagramEntity.Classname)) {
				DiagramEntity diagram = diagramRepository.getReferenceById(historyChange.getEntity());
				model = createModel(diagram.getName(), diagram.getIdUml());
				
				PrimitiveType testPrimitiveType = createPrimitiveType(model, "test");
				PrimitiveType intPrimitiveType = createPrimitiveType(model, "int");
				PrimitiveType stringPrimitiveType = createPrimitiveType(model, "String");
				PrimitiveType datePrimitiveType = createPrimitiveType(model, "Date");
				
				break;
			}
		}

		return model;
	}

	protected UMLResource loadModel() throws Exception {
		UMLResource resource = UMLModelReaderService.modelReaderUmlResource(this.getPathUMLFile());
//		Map<String, EObject> objMapId = resource.getIDToEObjectMap();

//		Collection<Object> models = EcoreUtil.getObjectsByType(resource.getContents(), UMLPackage.Literals.MODEL);
//
//		Model model = null;
//		if (!models.isEmpty()) {
//			model = (Model) models.iterator().next();
//		}

		return resource;
	}

	protected void mergeChangesToModel(UMLResource resource, List<HistoryChangeEntity> historyChanges) {
		Collection<Object> models = EcoreUtil.getObjectsByType(resource.getContents(), UMLPackage.Literals.MODEL);
		Map<String, EObject> objMapId = resource.getIDToEObjectMap();

		Model model = null;
		if (!models.isEmpty()) {
			model = (Model) models.iterator().next();
		}

		PrimitiveType testPrimitiveType = createPrimitiveType(model, "test");
		PrimitiveType intPrimitiveType = createPrimitiveType(model, "int");
		PrimitiveType stringPrimitiveType = createPrimitiveType(model, "String");
		PrimitiveType datePrimitiveType = createPrimitiveType(model, "Date");

		for (HistoryChangeEntity historyChange : historyChanges) {
			switch (historyChange.getEntityType()) {
			case DiagramEntity.Classname: {
				switch (historyChange.getChangeType()) {
				case ADD: {
					DiagramEntity diagram = diagramRepository.getReferenceById(historyChange.getEntity());

					if (objMapId.containsKey(diagram.getIdUml())) {
						continue;
					}

					model = createModel(diagram.getName(), diagram.getIdUml());
					break;
				}
				
				case REMOVE: {
					DiagramEntity diagram = diagramRepository.getReferenceById(historyChange.getEntity());

					if (!objMapId.containsKey(diagram.getIdUml())) {
						continue;
					}

					Model modelNode = (Model) objMapId.get(diagram.getIdUml());
					modelNode.destroy();
					break;
				}

				default:
					logger.info("Diagram change not supported: " + historyChange.getChangeType());
				}

				break;
			}
			case ClassEntity.Classname: {
				switch (historyChange.getChangeType()) {
				case ADD: {
					ClassEntity class_ = classRepository.getReferenceById(historyChange.getEntity());

					if (objMapId.containsKey(class_.getIdUml())) {
						continue;
					}
					
					DiagramEntity diagramEntity = class_.getDiagramEntity();
					Model modelImpl = (Model) objMapId.get(diagramEntity.getIdUml());
					if (modelImpl == null) {
						modelImpl = model;
					}

					Class newClass = createClass(modelImpl, class_.getName(), false, class_.getIdUml());

					for (AttributeEntity attribute : class_.getAttributes()) {
						createAttribute(newClass, attribute.getName(), testPrimitiveType, 0, 1, null,
								attribute.getIdUml());
					}

					for (OperationEntity operation : class_.getOperations()) {
						createOperation(newClass, operation.getName(), testPrimitiveType, operation.getParameters(),
								null, operation.getIdUml());
					}
					break;
				}

				case CHANGE: {

				}

				case REMOVE: {
					ClassEntity class_ = classRepository.getReferenceById(historyChange.getEntity());

					if (!objMapId.containsKey(class_.getIdUml())) {
						continue;
					}

					Class classNode = (Class) objMapId.get(class_.getIdUml());
					classNode.destroy();
					break;
				}

				default:
					logger.info("Class change not supported: " + historyChange.getChangeType());
				}

				break;
			}

			case AttributeEntity.Classname: {
				switch (historyChange.getChangeType()) {
				case ADD: {
					AttributeEntity attribute = attributeRepository.getReferenceById(historyChange.getEntity());

					if (objMapId.containsKey(attribute.getIdUml())) {
						continue;
					}

					Long classId = attribute.getClassEntity().getId();
					ClassEntity classDB = classRepository.getReferenceById(classId);
					Class class_ = (Class) objMapId.get(classDB.getIdUml());

					createAttribute(class_, attribute.getName(), testPrimitiveType, 0, 1, null, attribute.getIdUml());
					break;
				}

				case REMOVE: {
					AttributeEntity attribute = attributeRepository.getReferenceById(historyChange.getEntity());

					if (!objMapId.containsKey(attribute.getIdUml())) {
						continue;
					}

					Property attributeNode = (Property) objMapId.get(attribute.getIdUml());
					attributeNode.destroy();
					break;
				}

				default:
					logger.info("Attribute change not supported: " + historyChange.getChangeType());
				}

				break;
			}

			case OperationEntity.Classname: {
				switch (historyChange.getChangeType()) {
				case ADD: {
					OperationEntity operation = operationRepository.getReferenceById(historyChange.getEntity());

					if (objMapId.containsKey(operation.getIdUml())) {
						continue;
					}

					Long classId = operation.getClassEntity().getId();
					ClassEntity classDB = classRepository.getReferenceById(classId);
					Class class_ = (Class) objMapId.get(classDB.getIdUml());

					createOperation(class_, operation.getName(), testPrimitiveType, operation.getParameters(), null,
							operation.getIdUml());
					break;
				}

				case REMOVE: {
					OperationEntity operation = operationRepository.getReferenceById(historyChange.getEntity());

					if (!objMapId.containsKey(operation.getIdUml())) {
						continue;
					}

					Operation operationNode = (Operation) objMapId.get(operation.getIdUml());
					operationNode.destroy();
					break;
				}

				default:
					logger.info("Operation change not supported: " + historyChange.getChangeType());
				}

				break;
			}

			case OperationParameterEntity.Classname: {
				switch (historyChange.getChangeType()) {
				case ADD: {
					OperationParameterEntity parameter = parameterRepository
							.getReferenceById(historyChange.getEntity());

					if (objMapId.containsKey(parameter.getIdUml())) {
						continue;
					}

					Long operationId = parameter.getOperationEntity().getId();
					OperationEntity operationDB = operationRepository.getReferenceById(operationId);
					Operation operation = (Operation) objMapId.get(operationDB.getIdUml());

					createParameter(operation, parameter.getName(), testPrimitiveType, parameter.getValueDefault(),
							parameter.getIdUml());
					break;
				}

				case REMOVE: {
					OperationParameterEntity parameter = parameterRepository
							.getReferenceById(historyChange.getEntity());

					if (!objMapId.containsKey(parameter.getIdUml())) {
						continue;
					}

					Parameter parameterNode = (Parameter) objMapId.get(parameter.getIdUml());
					parameterNode.destroy();
					break;
				}

				default:
					logger.info("Parameter change not supported: " + historyChange.getChangeType());
				}

				break;
			}

			default:
				logger.info("Change merge entity not supported: " + historyChange.getEntityType());
			}
		}
	}

	//
	// Model-building utilities
	//

	protected static Model createModel(String name, String umlId) {
		Model model = UMLFactory.eINSTANCE.createModel();
		model.setName(name);

		if (!umlId.isEmpty()) {
			XMLUtils.setXMLId(model, umlId);
		}

		logger.info(String.format("Model '%s' created.", model.getQualifiedName()));

		return model;
	}

	protected static Package createPackage(Package nestingPackage, String name) {
		Package package_ = nestingPackage.createNestedPackage(name);

		logger.info(String.format("Package '%s' created.", package_.getQualifiedName()));

		return package_;
	}

	protected static PrimitiveType createPrimitiveType(Package package_, String name) {
		PrimitiveType primitiveType = package_.createOwnedPrimitiveType(name);

		logger.info(String.format("Primitive type '%s' created.", primitiveType.getQualifiedName()));

		return primitiveType;
	}

	protected static Enumeration createEnumeration(org.eclipse.uml2.uml.Package package_, String name) {
		
		Enumeration enumeration = package_.createOwnedEnumeration(name);

		logger.info(String.format("Enumeration '%s' created.", enumeration.getQualifiedName()));

		return enumeration;
	}

	protected static EnumerationLiteral createEnumerationLiteral(Enumeration enumeration, String name) {

		EnumerationLiteral enumerationLiteral = enumeration.createOwnedLiteral(name);

		logger.info(String.format("Enumeration literal '%s' created.", enumerationLiteral.getQualifiedName()));

		return enumerationLiteral;
	}

	protected static Stereotype createStereotype(Profile profile, String name, boolean isAbstract) {

		Stereotype stereotype = profile.createOwnedStereotype(name, isAbstract);

		logger.info(String.format("Stereotype '%s' created.", stereotype.getQualifiedName()));

		return stereotype;
	}

	protected static Generalization createGeneralization(Classifier specificClassifier, Classifier generalClassifier) {

		Generalization generalization = specificClassifier.createGeneralization(generalClassifier);

		logger.info(String.format("Generalization %s --|> %s created.", specificClassifier.getQualifiedName(),
				generalClassifier.getQualifiedName()));

		return generalization;
	}

	protected static Class createClass(Package package_, String name, boolean isAbstract, String umlId) {
		Class class_ = package_.createOwnedClass(name, isAbstract);

		if (!umlId.isEmpty()) {
			XMLUtils.setXMLId(class_, umlId);
		}

		logger.info(String.format("Class '%s' created.", class_.getQualifiedName()));

		return class_;
	}

	protected static Property createAttribute(Class class_, String name, Type type, int lowerBound, int upperBound,
			Object defaultValue, String umlId) {

		Property attribute = class_.createOwnedAttribute(name, type, lowerBound, upperBound);

		if (!umlId.isEmpty()) {
			XMLUtils.setXMLId(attribute, umlId);
		}

		if (defaultValue instanceof Boolean) {
			LiteralBoolean literal = (LiteralBoolean) attribute.createDefaultValue(null, null,
					UMLPackage.Literals.LITERAL_BOOLEAN);
			literal.setValue(((Boolean) defaultValue).booleanValue());
		} else if (defaultValue instanceof String) {
			if (type instanceof Enumeration) {
				InstanceValue value = (InstanceValue) attribute.createDefaultValue(null, null,
						UMLPackage.Literals.INSTANCE_VALUE);
				value.setInstance(((Enumeration) type).getOwnedLiteral((String) defaultValue));
			} else {
				LiteralString literal = (LiteralString) attribute.createDefaultValue(null, null,
						UMLPackage.Literals.LITERAL_STRING);
				literal.setValue((String) defaultValue);
			}
		}

		logger.info(String.format("Attribute '%s' : %s [%s..%s]%s created.", //
				attribute.getQualifiedName(), // attribute name
				type.getQualifiedName(), // type name
				lowerBound, // no special case for multiplicity lower bound
				(upperBound == LiteralUnlimitedNatural.UNLIMITED) ? "*" // special case for unlimited bound
						: upperBound, // finite upper bound
				(defaultValue == null) ? "" // no default value (use type's intrinsic default)
						: String.format(" = %s", defaultValue)));

		return attribute;
	}

	protected static Operation createOperation(Class class_, String name, Type type,
			List<OperationParameterEntity> parameters, Object defaultValue, String umlId) {

		EList<String> parametersNames = new BasicEList<>();
		EList<Type> parametersTypes = new BasicEList<>();
		EList<String> parametersUmlId = new BasicEList<>();

		for (OperationParameterEntity parameter : parameters) {
			parametersNames.add(parameter.getName());
			parametersTypes.add(null);
			parametersUmlId.add(parameter.getIdUml());
		}

		Operation operation = class_.createOwnedOperation(name, parametersNames, parametersTypes);

		if (!umlId.isEmpty()) {
			XMLUtils.setXMLId(operation, umlId);
		}

		int index = 0;
		for (Parameter parameter : operation.getOwnedParameters()) {
			XMLUtils.setXMLId(parameter, parametersUmlId.get(index++));
		}

		logger.info(String.format("Operation '%s' : %s %s created.", //
				operation.getQualifiedName(), // attribute name
				type.getQualifiedName(), // type name
				(defaultValue == null) ? "" // no default value (use type's intrinsic default)
						: String.format(" = %s", defaultValue)));

		return operation;
	}

	protected static Parameter createParameter(Operation operation, String name, Type type, Object defaultValue,
			String umlId) {
		Parameter parameter = operation.createOwnedParameter(name, type);
		XMLUtils.setXMLId(parameter, umlId);

		logger.info(String.format("Parameter OP. '%s' : %s %s created.", //
				operation.getQualifiedName(), // attribute name
				type.getQualifiedName(), // type name
				(defaultValue == null) ? "" // no default value (use type's intrinsic default)
						: String.format(" = %s", defaultValue)));

		return parameter;
	}

	protected static Extension createExtension(Class metaclass, Stereotype stereotype, boolean required) {

		Extension extension = stereotype.createExtension(metaclass, required);

		logger.info(String.format("%sxtension '%s' created.", //
				required ? "Required e" // it's a required extension
						: "E", // an optional extension
				extension.getQualifiedName()));

		return extension;
	}

	protected static void defineProfile(Profile profile) {
		profile.define();

		logger.info(String.format("Profile '%s' defined.", profile.getQualifiedName()));
	}

	protected static Class referenceMetaclass(Profile profile, String name) {

		Model umlMetamodel = (Model) ModelLoader.load(URI.createURI(UMLResource.UML_METAMODEL_URI));

		Class metaclass = (Class) umlMetamodel.getOwnedType(name);

		profile.createMetaclassReference(metaclass);

		logger.info(String.format("Metaclass '%s' referenced.", metaclass.getQualifiedName()));

		return metaclass;
	}

	private static boolean validateFileWrite(URI path, String folderPath) throws IOException {
		File outputDir = new File(path.toFileString()).getCanonicalFile();
		File folder = new File(folderPath);

		if (!folder.exists()) {
			logger.error(String.format("No such directory: %s", outputDir.getAbsolutePath()));
			return false;
		}

		if (!folder.canWrite()) {
			logger.error(String.format("Cannot write to directory: %s", outputDir.getAbsolutePath()));
			return false;
		}

		if (outputDir.exists() && !outputDir.canWrite()) {
			logger.error(String.format("Cannot write the file: %s", outputDir.getAbsolutePath()));
			return false;
		}

		return true;
	}

	private static boolean validateFileRead(URI path, String folderPath) throws IOException {
		File outputFile = new File(path.toFileString()).getCanonicalFile();
		File folder = new File(folderPath);

		if (!folder.exists()) {
			logger.error(String.format("No such directory: %s", folder.getAbsolutePath()));
			return false;
		}

		if (!outputFile.exists()) {
			logger.error(String.format("No such file: %s", outputFile.getAbsolutePath()));
			return false;
		}

		if (!outputFile.canRead()) {
			logger.error(String.format("Cannot read the file: %s", outputFile.getAbsolutePath()));
			return false;
		}

		return true;
	}
}
