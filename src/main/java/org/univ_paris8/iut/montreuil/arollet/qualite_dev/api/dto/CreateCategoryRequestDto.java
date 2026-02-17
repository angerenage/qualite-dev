package org.univ_paris8.iut.montreuil.arollet.qualite_dev.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "CreateCategoryRequest", description = "Payload to create a category")
public class CreateCategoryRequestDto {
	@Schema(example = "Housing")
	@NotBlank(message = "label is required.")
	@Size(max = 128, message = "label must contain at most 128 characters.")
	private String label;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
