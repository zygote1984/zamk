package zamk.lib.generator

import com.sun.mirror.declaration.TypeDeclaration
import com.sun.mirror.declaration.MethodDeclaration
import java.util.HashMap
import com.sun.mirror.declaration.ConstructorDeclaration
import com.sun.mirror.declaration.ParameterDeclaration

class AggregatorGenerator {
	
	
	def static generate(String packageName, TypeDeclaration type, ConstructorDeclaration cons, MethodDeclaration agg){
	
	var clazz = ""
	var className = type.simpleName

	clazz = className + "Impl" 
	
	'''
	package «packageName»;
	
	public class «clazz» implements «className»
	{
		public «cons.declaringType.qualifiedName» aggregate(«FOR ParameterDeclaration p: agg.parameters»«p.type.toString» «p.simpleName» «IF agg.parameters.last != p» , «ENDIF»«ENDFOR»)
		{
			return new «cons.declaringType.qualifiedName»(«FOR ParameterDeclaration p: agg.parameters»«p.simpleName» «IF agg.parameters.last != p» , «ENDIF»«ENDFOR»);
			
		}
	
	}
	
	'''
	}
	
}