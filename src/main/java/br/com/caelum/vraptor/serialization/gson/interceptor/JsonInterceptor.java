package br.com.caelum.vraptor.serialization.gson.interceptor;

import javax.inject.Inject;
import javax.transaction.Transactional;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.gson.reflection.JsonReflection;
import br.com.caelum.vraptor.view.Results;

@Intercepts
public class JsonInterceptor implements Interceptor {

	@Inject
	private MethodInfo info;

	@Inject
	private Result result;

	@Override
	public boolean accepts(ControllerMethod method) {
		return method.containsAnnotation(Json.class);
	}

	@Override
	@Transactional
	public void intercept(InterceptorStack stack, ControllerMethod method, Object obj) throws InterceptionException {
		// Invoke controller method...
		stack.next(method, obj);
		// Retrieve @Json annotation
		final Json annotation = method.getMethod().getAnnotation(Json.class);
		// Get the Json serializer for the object returned by the controller method
		final Serializer serializer = result.use(Results.json()).withoutRoot().from(info.getResult());
		// Json fields
		final JsonFields fields = getFields(annotation);
		// Check if there are excluded fields
		if (fields.includes() != null) {
			// Json reflection API
			final JsonReflection reflection = new JsonReflection(method.getMethod());
			// Provide with fields to include
			reflection.include(fields.includes());
			// Remove excluded fields from serialization process
			serializer.exclude(reflection.getExcluded());
		}
		// Serialize all fields (recursively)
		serializer.recursive();
		// Serialize Json
		serializer.serialize();
	}

	private JsonFields getFields(final Json annotation) {
		try {
			return annotation.fields().newInstance();
		} catch (Exception e) {
			return null;
		}
	}

}
