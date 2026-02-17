package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CategoryResponse", description = "Category details exposed by REST API")
public class CategoryResponseDto {
	@Schema(example = "1")
	private Long id;
	@Schema(example = "Housing")
	private String label;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
