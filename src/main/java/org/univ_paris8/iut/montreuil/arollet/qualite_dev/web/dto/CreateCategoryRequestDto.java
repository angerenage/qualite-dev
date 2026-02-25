package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCategoryRequestDto {

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
