package at.ac.tuwien.dsg.bakk.rest.base;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import model.BaseEntity;

public class ResourceUtils {

	public static <T, V extends BaseEntity> T createBean(V sourceObject, Class<T> targetClazz) throws ParseException {
		try {
			T result = targetClazz.getConstructor().newInstance();
			BeanUtils.copyProperties(result, sourceObject);
			return result;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new ParseException("Parsing bean failed!", e);
		}
	}

	public static <T, V extends BaseEntity> V createEntity(T sourceObject, Class<V> targetClazz) throws ParseException {
		return createEntity(sourceObject, targetClazz, null);
	}

	public static <T, V extends BaseEntity> V createEntity(T sourceObject, Class<V> targetClazz, Long id)
			throws ParseException {
		try {
			V result = targetClazz.getConstructor().newInstance();
			BeanUtils.copyProperties(result, sourceObject);
			result.setId(id);
			return result;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new ParseException("Parsing bean failed!", e);
		}
	}

}
