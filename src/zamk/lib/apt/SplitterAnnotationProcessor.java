package zamk.lib.apt;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;

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
		try {
			env.getFiler().createClassFile("Kardelen");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		Collection<TypeDeclaration> typeDec = env.getTypeDeclarations();
		
		
		Set<? extends Element> elements = Collections.emptySet();

		for (TypeDeclaration t : typeDec) {

			Collection<AnnotationMirror> am = t.getAnnotationMirrors();
			for (AnnotationMirror a : am)
				if (a.getAnnotationType().getDeclaration().getQualifiedName()
						.equals("annotation.Splitter"))
				{
					env.getMessager().printError(t.getPosition(), t.getSimpleName());
					processSplitter(t, a);
				}
		}

		System.out.println("Hello");

	}

	public void processSplitter(TypeDeclaration e, AnnotationMirror anno) {
		
		Map<AnnotationTypeElementDeclaration, AnnotationValue> values = anno.getElementValues();
		Set<AnnotationTypeElementDeclaration> keySet = values.keySet();
		String clazz = "";
		
		for(AnnotationTypeElementDeclaration key: keySet)
		{
			if(key.getSimpleName().equals("input"))
				clazz = values.get(key).toString();
			
			if(key.getSimpleName().equals("splitType"))
				if(values.get(key).toString().equals("field")){
					fieldSplitter(e, clazz);
				}
		}	

		if (!checkSplitMethod(e))
			env.getMessager().printError(e.getPosition(), "You must implement a split method!");
	}

	public void fieldSplitter(TypeDeclaration e, String sobj) {
		
		TypeDeclaration td = env.getTypeDeclaration(sobj);
		System.out.println(td.getQualifiedName());
		

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
//	public void checkMethods(Element e) {
//		Splitter anno = e.getAnnotation(Splitter.class);
//		List<String> outputs = Arrays.asList(anno.output());
//		List<? extends Element> elements = e.getEnclosedElements();
//		ArrayList<String> methods = new ArrayList<String>();
//
//		for (Element i : elements) {
//			if (i.getKind().equals(ElementKind.METHOD)) {
//				methods.add(i.getSimpleName().toString());
//				if (i.getSimpleName().toString().equals("split"))
//					splitMethod = i;
//
//			}
//		}
//
//		for (String o : outputs) {
//
//			if (!methods.contains(o))
//				processingEnv.getMessager().printMessage(Kind.ERROR,
//						"Splitter does nor provide method: " + o, e);
//		}
//
//	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		// TODO Auto-generated method stub
		return false;
	}

}
