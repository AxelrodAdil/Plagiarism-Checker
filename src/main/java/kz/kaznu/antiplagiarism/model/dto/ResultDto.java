package kz.kaznu.antiplagiarism.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
public class ResultDto {

    private Double percentage;
    private ArrayList<String> urls;
}
