package zamk.lib.apt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;

import zamk.lib.generator.FieldSplitterGenerator;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.declaration.TypeDeclaration;

/**
 * @since 1.0
 */
enum SplitterType {
	MANUAL, FIELD, METHOD;
}

/**
 * @since 1.0
 */
@SupportedAnnotationTypes("annotation.Splitter")
public class SplitterAnnotationProcessor extends AbstractProcessor implements
		AnnotationProcessor {

	MethodDeclaration splitMethod = null;
	AnnotationProcessorEnvironment env;

	public SplitterAnnotationProcessor(AnnotationProcessorEnvironment env) {
		this.env = env;
	}

	@Override
	public void process() {
		Collection<TypeDeclaration> typeDec = env.getTypeDeclarations();
		
		for (TypeDeclaration t : typeDec) {
			Collection<AnnotationMirror> am = t.getAnnotationMirrors();
			for (AnnotationMirror a : am)
				if (a.getAnnotationType().getDeclaration().getQualifiedName()
						.equals("annotation.Splitter")) {
					processSplitter(t, a);
				}
		}

	}

	public void processSplitter(TypeDeclaration e, AnnotationMirror anno) {

		Map<AnnotationTypeElementDeclaration, AnnotationValue> values = anno
				.getElementValues();
		Set<AnnotationTypeElementDeclaration> keySet = values.keySet();
		String clazz = "";

		for (AnnotationTypeElementDeclaration key : keySet) {
			if (key.getSimpleName().equals("inputType"))
				clazz = values.get(key).toString();

			if (key.getSimpleName().equals("type"))
				if (values.get(key).toString().equals("field")) {
					fieldSplitter(e, clazz);
				} else if (values.get(key).toString().equals("manual")) {
					manualSplitter(e, clazz);
				}
		}

		if (!checkSplitMethod(e))
			env.getMessager().printError(e.getPosition(),
					"You must implement a split method!");
	}

	private void manualSplitter(TypeDeclaration e, String clazz) {
		// TODO Auto-generated method stub
		
	}

	public void fieldSplitter(TypeDeclaration e, String sobj) {

		// This map hold the method bodies of the concrete splitter class
		HashMap<String, String> methodBody = new HashMap<String, String>();

		// Method declarations of the declared splitter
		Collection<? extends MethodDeclaration> elements = e.getMethods();
		// get the type declaration for the inputType
		TypeDeclaration td = env.getTypeDeclaration(sobj);
		// get inputTypes methods and fields
		Collection<? extends FieldDeclaration> targetFields = td.getFields();
		Collection<? extends MethodDeclaration> targetMethods = td.getMethods();

		// first put the constructor
		methodBody.put("cons", sobj);

		for (MethodDeclaration method : elements) {
			String returnType = method.getReturnType().toString();
			String mName = method.getSimpleName();
			if (returnType.equals(sobj))
				methodBody.put(mName, "return this.split;");
			else
				for (FieldDeclaration f : targetFields) {
					String fieldType = f.getType().toString();
					System.out.println(fieldType + " " + returnType);
					if (fieldType.equals(returnType)) {
						boolean flag = false;
						for (MethodDeclaration meth : targetMethods)
							if (meth.getSimpleName().startsWith("get")) {
								String methName = meth.getSimpleName();
								if (methName.equalsIgnoreCase("get"
										+ f.getSimpleName())) {
									methodBody.put(mName, "return this.split."
											+ meth.getSimpleName() + "();");
									flag = true;
								}
							}
						if (flag = false)
							for (Modifier m : f.getModifiers())
								if (m == Modifier.PUBLIC) {
									methodBody.put(mName, "return this.split."
											+ f.getSimpleName());
									flag = true;
								}
					}

				}
		}

		generateSplitter(methodBody, e);

	}

	private void generateSplitter(HashMap<String, String> methodBody,
			TypeDeclaration e) {
		String className = e.getSimpleName();
		String packageName = e.getPackage().getSimpleName();
		String clazz = "";
	
		clazz = className.concat("Impl");

		PrintWriter writer = null;
		try {
			writer = env.getFiler().createSourceFile(packageName + "." + clazz);
			writer.write(FieldSplitterGenerator.generate(packageName, e,
					methodBody).toString());

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			writer.close();
		}

	}

	public boolean checkSplitMethod(TypeDeclaration e) {
		boolean flag = false;
		Collection<? extends MethodDeclaration> elements = e.getMethods();
		for (MethodDeclaration i : elements) {

			if (i.getSimpleName().toString().equals("split")) {
				splitMethod = i;
				flag = true;
			}

		}
		return flag;
	}

	//
	// public void checkMethods(Element e) {
	// Splitter anno = e.getAnnotation(Splitter.class);
	// List<String> outputs = Arrays.asList(anno.output());
	// List<? extends Element> elements = e.getEnclosedElements();
	// ArrayList<String> methods = new ArrayList<String>();
	//
	// for (Element i : elements) {
	// if (i.getKind().equals(ElementKind.METHOD)) {
	// methods.add(i.getSimpleName().toString());
	// if (i.getSimpleName().toString().equals("split"))
	// splitMethod = i;
	//
	// }
	// }
	//
	// for (String o : outputs) {
	//
	// if (!methods.contains(o))
	// processingEnv.getMessager().printMessage(Kind.ERROR,
	// "Splitter does nor provide method: " + o, e);
	// }
	//
	// }

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		// TODO Auto-generated method stub
		return false;
	}

}
