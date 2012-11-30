package zamk.lib.generator

import com.sun.mirror.declaration.TypeDeclaration
import com.sun.mirror.declaration.MethodDeclaration
import java.util.HashMap

class FieldSplitterGenerator {
	
	
	def static generate(String packageName, TypeDeclaration type, HashMap<String, String> map){
	
	var clazz = ""
	var className = type.simpleName

	clazz = className + "Impl" 
	
	'''
	package «packageName»;
	
	public class «clazz» implements «className»
	{
		«var methods = type.methods»
		«var field = map.get("cons")»
		
		«field» split;
		
		public «clazz»(«field» split)
		{
			this.split = split;
			
		}
		
		«FOR MethodDeclaration m: methods»
			«FOR modi: m.modifiers» «IF modi.toString != "abstract"»«modi.toString»«ENDIF»«ENDFOR» «m.returnType» «m.simpleName»(){
			«IF m.returnType.toString != "void"»
				«map.get(m.simpleName)»
			«ENDIF»
			}
			
		«ENDFOR»
	
	}
	
	'''
	}
}