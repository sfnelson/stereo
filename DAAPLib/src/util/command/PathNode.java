package util.command;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public abstract class PathNode implements RequestNode {

	public Map<String, Method> methods() {
		
		Map<String, Method> methods = new HashMap<String, Method>();
		
		for (Method m: this.getClass().getMethods()) {
			if (m.getDeclaringClass() != this.getClass()) continue;
			if (!RequestNode.class.isAssignableFrom(m.getReturnType())) continue;
			if (m.getParameterTypes().length != 0) continue;
			if (m.getModifiers() != Modifier.PUBLIC) continue;
			methods.put(m.getName(), m);
		}
		
		return methods;
	}
}
