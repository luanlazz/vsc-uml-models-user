package com.vsc.user.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
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
import org.eclipse.uml2.uml.ParameterSet;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.VisibilityKind;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.vsc.user.infra.dao.AttributeEntity;
import com.vsc.user.infra.dao.ClassEntity;
import com.vsc.user.infra.dao.HistoryChangeEntity;
import com.vsc.user.infra.dao.HistoryChangeType;
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
import com.vsc.user.uml.reader.service.UMLModelReaderService;
import com.vsc.user.utils.HTTP;
import com.vsc.user.utils.HTTPResponse;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Component("parseUMLModel")
public class ChangePropagation {

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
	private String versionToken = "abd390ec-c232-4f41-93c7-e100afc8b9a2";

	ChangePropagation() {

	}

	public Boolean sendUMLChanges(String localPathUml) throws Exception {
		boolean validFileOfUml = validateFile(localPathUml);
		if (!validFileOfUml) {
			throw new Exception("Local UML file not found.");
		}

		String umlContent = new String(Files.readAllBytes(Paths.get(localPathUml)));

		Map<String, String> parameters = new HashMap<>();
		parameters.put("umlContent", umlContent);
		parameters.put("version", this.versionToken);
		parameters.put("username", this.username);

		HTTPResponse httpResponse = HTTP.sendRequest(urlServer, parameters);

		if (httpResponse.getStatusCode() == HttpServletResponse.SC_OK) {
			this.versionToken = ""; // set version token to saved
			return true;
		} else {
			return false;
		}
	}

	public Boolean receiveUMLChanges(String newVersion, String outputFile) throws Exception {
		boolean validFilePropagate = validateFile(outputFile);
		if (!validFilePropagate) {
			throw new Exception("Local UML file not found.");
		}

		VersionEntity localVersionEntity = this.versionRepository.getByToken(this.versionToken);
		if (!(localVersionEntity.getId() > 0)) {
			throw new Exception("Invalid local version");
		}

		VersionEntity newVersionEntity = this.versionRepository.getByToken(newVersion);
		if (!(newVersionEntity.getId() > 0)) {
			throw new Exception("Invalid new version");
		}

		List<HistoryChangeEntity> historyChanges = this.historyChangeRepository
				.findVersionRange(localVersionEntity, newVersionEntity);

		Package modelPackage = UMLModelReaderService.modelReader(outputFile);
		if (modelPackage == null) {
			throw new Exception("Diagrama de classes não encontrado.");
		}

		for (HistoryChangeEntity historyChange : historyChanges) {
			if (historyChange.getEntityType().equals("class com.vsc.demo.dao.ClassEntity")
					&& historyChange.getChangeType() == HistoryChangeType.ADD) {
				ClassEntity savedClass = classRepository.getById(historyChange.getEntity());

				Class newClass = modelPackage.createOwnedClass(savedClass.getName(), false);

				for (AttributeEntity attribute : savedClass.getAttributes()) {
					newClass.createOwnedAttribute(attribute.getName(), null);
				}

				for (OperationEntity operation : savedClass.getOperations()) {
					Operation newOperation = newClass.createOwnedOperation(operation.getName(), null, null);
					newOperation.setVisibility(VisibilityKind.getByName(operation.getVisibility()));

					for (OperationParameterEntity parameter : operation.getParameters()) {
						Parameter newParameter = newOperation.createOwnedParameter(parameter.getName(), null);
					}
				}
			}
		}

		URI outputURI = URI.createFileURI(outputFile);
		System.out.printf("Saving the model to %s.", outputURI.toFileString());
		ModelLoader.save(modelPackage, outputURI);

		this.versionToken = newVersion;
		return true;
	}

	protected static Enumeration createEnumeration(org.eclipse.uml2.uml.Package package_, String name) {

		Enumeration enumeration = package_.createOwnedEnumeration(name);

		System.out.printf("Enumeration '%s' created.", enumeration.getQualifiedName());

		return enumeration;
	}

	protected static EnumerationLiteral createEnumerationLiteral(Enumeration enumeration, String name) {

		EnumerationLiteral enumerationLiteral = enumeration.createOwnedLiteral(name);

		System.out.printf("Enumeration literal '%s' created.", enumerationLiteral.getQualifiedName());

		return enumerationLiteral;
	}

	protected static Stereotype createStereotype(Profile profile, String name, boolean isAbstract) {

		Stereotype stereotype = profile.createOwnedStereotype(name, isAbstract);

		System.out.printf("Stereotype '%s' created.", stereotype.getQualifiedName());

		return stereotype;
	}

	protected static Generalization createGeneralization(Classifier specificClassifier, Classifier generalClassifier) {

		Generalization generalization = specificClassifier.createGeneralization(generalClassifier);

		System.out.printf("Generalization %s --|> %s created.", specificClassifier.getQualifiedName(),
				generalClassifier.getQualifiedName());

		return generalization;
	}

	protected static Property createAttribute(org.eclipse.uml2.uml.Class class_, String name, Type type, int lowerBound,
			int upperBound, Object defaultValue) {

		Property attribute = class_.createOwnedAttribute(name, type, lowerBound, upperBound);

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

		System.out.printf("Attribute '%s' : %s [%s..%s]%s created.", //
				attribute.getQualifiedName(), // attribute name
				type.getQualifiedName(), // type name
				lowerBound, // no special case for multiplicity lower bound
				(upperBound == LiteralUnlimitedNatural.UNLIMITED) ? "*" // special case for unlimited bound
						: upperBound, // finite upper bound
				(defaultValue == null) ? "" // no default value (use type's intrinsic default)
						: String.format(" = %s", defaultValue));

		return attribute;
	}

	protected static Extension createExtension(org.eclipse.uml2.uml.Class metaclass, Stereotype stereotype,
			boolean required) {

		Extension extension = stereotype.createExtension(metaclass, required);

		System.out.printf("%sxtension '%s' created.", //
				required ? "Required e" // it's a required extension
						: "E", // an optional extension
				extension.getQualifiedName());

		return extension;
	}

	protected static void defineProfile(Profile profile) {
		profile.define();

		System.out.printf("Profile '%s' defined.", profile.getQualifiedName());
	}

	protected static org.eclipse.uml2.uml.Class referenceMetaclass(Profile profile, String name) {

		Model umlMetamodel = (Model) ModelLoader.load(URI.createURI(UMLResource.UML_METAMODEL_URI));

		org.eclipse.uml2.uml.Class metaclass = (org.eclipse.uml2.uml.Class) umlMetamodel.getOwnedType(name);

		profile.createMetaclassReference(metaclass);

		out("Metaclass '%s' referenced.", metaclass.getQualifiedName());

		return metaclass;
	}

	//
	// Logging utilities
	//

	protected static void hrule() {
		System.out.println("------------------------------------");
	}

	protected static void out(String format, Object... args) {
		if (DEBUG) {
			System.out.printf(format, args);
			if (!format.endsWith("%n")) {
				System.out.println();
			}
		}
	}

	protected static void err(String format, Object... args) {
		System.err.printf(format, args);
		if (!format.endsWith("%n")) {
			System.err.println();
		}
	}

	private static boolean validateFile(String filePath) throws IOException {
		File outputDir = new File(filePath).getCanonicalFile();
		if (!outputDir.exists()) {
			System.out.printf("\nNo such directory: %s", outputDir.getAbsolutePath());
			return false;
		}

		if (!outputDir.canRead()) {
			System.out.printf("\nCannot read the file: %s", outputDir.getAbsolutePath());
			return false;
		}
		
		if (!outputDir.canWrite()) {
			System.out.printf("\nCannot write the file: %s", outputDir.getAbsolutePath());
			return false;
		}

		return true;
	}
}
