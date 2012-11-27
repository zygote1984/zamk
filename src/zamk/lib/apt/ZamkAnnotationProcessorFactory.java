package zamk.lib.apt;

import java.util.*;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

/**
 * @since 1.0
 */
public class ZamkAnnotationProcessorFactory implements
		AnnotationProcessorFactory {

	@Override
	public Collection<String> supportedOptions() {
		return Collections.emptyList();
	}

	@Override
	public Collection<String> supportedAnnotationTypes() {
		return Collections.singletonList("annotation.Splitter");
	}

	@Override
	public AnnotationProcessor getProcessorFor(
			Set<AnnotationTypeDeclaration> atds,
			AnnotationProcessorEnvironment env) {
	
		AnnotationProcessor result = null;
		if(atds.isEmpty()) {
			result = AnnotationProcessors.NO_OP;
		}
		else {
			for(AnnotationTypeDeclaration atd: atds)
				if(atd.getSimpleName().equals("Splitter"))
					result = (AnnotationProcessor) new SplitterAnnotationProcessor(env);
				else if(atd.getSimpleName().equals("Aggregator"))
					result = (AnnotationProcessor) new AggregatorAnnotationProcessor(env);
		}
		return result;
		
		
	}

}
