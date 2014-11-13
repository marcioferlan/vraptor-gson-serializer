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
import br.com.caelum.vraptor.view.Results;

@Intercepts
public class JsonInterceptor implements Interceptor {

	@Inject
	private MethodInfo info;

	@Inject
	private Result result;

	public void intercept() {
	}

	private String[] excludedFieldsNames(ControllerMethod method) {
		return method.getMethod().getAnnotation(Json.class).exclude();
	}

	@Override
	public boolean accepts(ControllerMethod method) {
		return method.containsAnnotation(Json.class);
	}

	@Override
	@Transactional
	public void intercept(InterceptorStack stack, ControllerMethod method, Object obj) throws InterceptionException {
		stack.next(method, obj);
		result.use(Results.json()).withoutRoot().from(info.getResult()).exclude(excludedFieldsNames(method)).recursive().serialize();
	}

}
