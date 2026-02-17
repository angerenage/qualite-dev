package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ParamsResponse", description = "Query/path parameter demonstration payload")
public class ParamsResponseDto {
	@Schema(example = "15")
	private Long pathId;
	@Schema(example = "bike")
	private String query;
	@Schema(example = "3")
	private Integer page;
	@Schema(example = "true")
	private Boolean verbose;
	@Schema(example = "QueryParam example on GET /api/params")
	private String message;

	public ParamsResponseDto() {
	}

	public ParamsResponseDto(Long pathId, String query, Integer page, Boolean verbose, String message) {
		this.pathId = pathId;
		this.query = query;
		this.page = page;
		this.verbose = verbose;
		this.message = message;
	}

	public Long getPathId() {
		return pathId;
	}

	public void setPathId(Long pathId) {
		this.pathId = pathId;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Boolean getVerbose() {
		return verbose;
	}

	public void setVerbose(Boolean verbose) {
		this.verbose = verbose;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
