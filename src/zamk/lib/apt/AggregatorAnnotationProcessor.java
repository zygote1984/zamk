package zamk.lib.apt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.SupportedAnnotationTypes;

import zamk.lib.generator.AggregatorGenerator;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.ConstructorDeclaration;
import com.sun.mirror.declaration.ExecutableDeclaration;
import com.sun.mirror.declaration.InterfaceDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;

/**
 * @author Kardelen Hatun
 * @since 1.0
 */
@SupportedAnnotationTypes("annotation.Aggregator")
public class AggregatorAnnotationProcessor implements AnnotationProcessor {

	AnnotationProcessorEnvironment env;

	public AggregatorAnnotationProcessor(AnnotationProcessorEnvironment env) {
		this.env = env;
	}

	@Override
	public void process() {
		Collection<TypeDeclaration> typeDec = env.getTypeDeclarations();

		for (TypeDeclaration t : typeDec) {
			Collection<AnnotationMirror> am = t.getAnnotationMirrors();
			for (AnnotationMirror a : am)
				if (a.getAnnotationType().getDeclaration().getSimpleName()
						.equals("Aggregator")) {
					System.out.println("Processing " + t.getSimpleName());
					processAggregator(t, a);
				}
		}

	}

	private void processAggregator(TypeDeclaration e, AnnotationMirror a) {

		Map<AnnotationTypeElementDeclaration, AnnotationValue> values = a
				.getElementValues();
		Set<AnnotationTypeElementDeclaration> keySet = values.keySet();
		String clazz = "";

		String outputType = "";
		String input = "";
		String type = "";
		ArrayList<String> array = new ArrayList<String>();

		for (AnnotationTypeElementDeclaration key : keySet) {
			if (key.getSimpleName().equals("outputType")) {
				outputType = values.get(key).toString();
			} else if (key.getSimpleName().equals("type")) {
				type = values.get(key).toString();
			}
		}

		Collection<? extends MethodDeclaration> methods = e.getMethods();
		boolean aggregate = false;
		MethodDeclaration aggreDeclaration = null;
		for (MethodDeclaration m : methods) {
			if (m.getSimpleName().equals("aggregate")) {
				aggregate = true;
				aggreDeclaration = m;
				for (ParameterDeclaration p : m.getParameters()) {
					array.add(p.getType().toString());
				}
			}
		}

		if (!aggregate) {
			env.getMessager().printError(e.getPosition(),
					"The interface must inculde a method named \"aggregate\"");
		}

		TypeDeclaration otd = env.getTypeDeclaration(outputType);
		if (otd instanceof InterfaceDeclaration)
			env.getMessager().printError(otd.getPosition(),
					"Aggregation target must be a concrete type");
		else if (otd instanceof ClassDeclaration) {
			ConstructorDeclaration cons = searchForConstructor(array,
					(ClassDeclaration) otd);
			if (cons == null)
				env.getMessager()
						.printError(otd.getPosition(),
								"No suitable constructor was found for creating the target object");
			else
				generateAggregator(e, cons, aggreDeclaration);
		}

	}

	private void generateAggregator(TypeDeclaration e,
			ConstructorDeclaration cons, MethodDeclaration array) {
		String className = e.getSimpleName();
		String packageName = e.getPackage().getSimpleName();
		String clazz = "";

		clazz = className.concat("Impl");

		PrintWriter writer = null;
		try {
			writer = env.getFiler().createSourceFile(packageName + "." + clazz);
			writer.write(AggregatorGenerator.generate(packageName, e, cons,
					array).toString());

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			writer.close();
		}

	}

	/*
	 * @param input : String array
	 * 
	 * @param otd : Type to be searched for constructors
	 * 
	 * @return Constructor Declaration Tries to find a suitable constructor for
	 * the given input and type declaration, the type declaration should not be
	 * an interface
	 */
	private ConstructorDeclaration searchForConstructor(
			ArrayList<String> input, ClassDeclaration otd) {

		Collection<? extends ConstructorDeclaration> methods = otd
				.getConstructors();
		ConstructorDeclaration fieldcons = null;
		ConstructorDeclaration defaultcons = null;
		for (ConstructorDeclaration temp : methods) {
			Collection<ParameterDeclaration> params = temp.getParameters();
			if (params.isEmpty()) {
				defaultcons = temp;
			} else {
				if (params.size() == input.size()) {

					HashMap<ParameterDeclaration, String> matches = new HashMap<ParameterDeclaration, String>();

					for (ParameterDeclaration p : params) {
						if (!matches.keySet().contains(p))
							for (String s : input) {
								if (p.getType().toString().equals(s)) {
									matches.put(p, s);
								}
							}
					}
					if (matches.keySet().size() == input.size()) {
						fieldcons = temp;
						return fieldcons;
					}
				}
			}

		}

		return defaultcons;

	}

}
