package com.leon.datalink.web.config;

import com.google.common.collect.Lists;
import com.leon.datalink.web.model.RestResult;
import com.leon.datalink.web.model.RestResultUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;


/**
 * 封装统一返回结果
 */
@RestControllerAdvice
public class ReturnAdviceHandler implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		boolean bePrivate = Modifier.PRIVATE == returnType.getMethod().getModifiers();
		//private  方法直接跳过
		if (bePrivate) {
			return false;
		}
		NotWrap notWrap = returnType.getMethod().getAnnotation(NotWrap.class);
		if (notWrap != null) {
			return false;
		}
		String name = returnType.getMethod().getDeclaringClass().getName();
		//仅对本项目结果进行封装
		return name.startsWith("com.leon.datalink.web");
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
		//封装返回结果
		if (body == null) {
			return RestResultUtils.success();
		}

		if (ModelAndView.class.isInstance(body)) {
			ModelAndView mv = (ModelAndView) body;
			return mv;
		}

		if (RestResult.class.isInstance(body)) {
			return body;
		}

		if (Collection.class.isInstance(body)) {
			Collection<?> ction = (Collection<?>) body;
			List<?> list = null;
			if (ction instanceof List) {
				list = (List<?>) ction;
			} else {
				list = Lists.newArrayList(ction);
			}
			return RestResultUtils.success(list);
		}

		if("OPTIONS".equals(request.getMethod())) {
			response.setStatusCode(HttpStatus.OK);
    	}
		return RestResultUtils.success(body);
	}

}
