package br.com.caelum.vraptor.serialization.gson.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This class reflects over the the fields of a method return object class 
 * to determine which ones should be included and excluded from the Json 
 * serialization process (based on the included fields).
 */
public class JsonReflection {

	private Class<?> objectClass;
	private List<String> included;
	private List<String> excluded;

	/**
	 * Method on which return object the process will run.
	 */
	public JsonReflection(final Method method) {
		if (method == null) {
			throw new IllegalArgumentException("Method cannot be null.");
		}
		// get method's return type
		final Class<?> returnType = method.getReturnType();
		// if return type is not a collection
		if (!Collection.class.isAssignableFrom(returnType)) {
			this.objectClass = returnType;
		}
		// return type is a collection
		else {
			final ParameterizedType colType = (ParameterizedType) method.getGenericReturnType();
			objectClass = (Class<?>) colType.getActualTypeArguments()[0];
		}
	}

	/**
	 * Fields to be included in the serialization process
	 */
	public JsonReflection include(final String[] fields) {
		excluded = new ArrayList<String>();
		included = Arrays.asList(fields);
		exclude(objectClass, included.toString(), null);
		return this;
	}

	private void exclude(final Class<?> objClass, final String campos, final String prefixo) {
		if (shouldProcess(objClass)) {
			final Field[] fields = objClass.getDeclaredFields();
			for (final Field field : fields) {
				final String nomeCampo = buildNomeCampo(prefixo, field);
				final String regexCampo = buildRegexCampo(nomeCampo);
				if (!campos.matches(regexCampo)) {
					excluded.add(nomeCampo);
					continue;
				}
				final Class<?> fieldType = field.getType();
				// collection type
				if (Collection.class.isAssignableFrom(fieldType)) {
					final ParameterizedType colType = (ParameterizedType) field.getGenericType();
					final Class<?> colTypeClass = (Class<?>) colType.getActualTypeArguments()[0];
					exclude(colTypeClass, campos, nomeCampo);
				}
				// object type, not an enum, not static
				else if (!fieldType.isEnum() && !fileldIsStatic(field)) {
					exclude(fieldType, campos, nomeCampo);
				}
			}
		}
	}

	private boolean fileldIsStatic(final Field field) {
		return Modifier.isStatic(field.getModifiers());
	}

	private boolean shouldProcess(final Class<?> objClass) {
		return classIsNotNull(objClass) && packageIsNotNull(objClass) && isNotPrimitive(objClass);
	}

	private boolean isNotPrimitive(Class<?> objClass) {
		return !objClass.isPrimitive() && !objClass.getPackage().getName().matches("(java\\.lang|java\\.util|java\\.sql|java\\.math).*");
	}

	private boolean packageIsNotNull(Class<?> objClass) {
		return objClass.getPackage() != null && objClass.getPackage().getName() != null;
	}

	private boolean classIsNotNull(Class<?> objClass) {
		return objClass != null;
	}

	private String buildNomeCampo(final String prefixo, final Field field) {
		return prefixo == null ? field.getName() : prefixo + "." + field.getName();
	}

	private String buildRegexCampo(final String field) {
		return String.format(".*([\\[ ]%s[\\.,\\]]).*", field.replaceAll("[.]", "\\\\."));
	}

	public String[] getExcluded() {
		return excluded != null ? excluded.toArray(new String[excluded.size()]) : null;
	}

	public String[] getIncluded() {
		return included != null ? included.toArray(new String[included.size()]) : null;
	}
}
