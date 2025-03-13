package com.leon.datalink.web.model;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * page query，set page query condition
 * @author Leon
 * @date 2018年3月21日 上午9:31:26
 * @version 1.0.0
 */
public class PageQueryDTO<E> implements Serializable {
	private static final long serialVersionUID = -134459139009770321L;
	/**
	 * should be the dto object
	 */
	@Valid
	private E dto;

	/**
	 * page
	 */
	private int page = 1;

	/**
	 * limit
	 */
	private int limit = 20;

	public E getDto() {
		return dto;
	}

	public void setDto(E dto) {
		this.dto = dto;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
}
