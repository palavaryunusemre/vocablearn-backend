package com.vocablearn.service;

import com.vocablearn.dto.PassageDetailDto;
import com.vocablearn.dto.PassageSummaryDto;
import java.util.List;

public interface PassageService {

	List<PassageSummaryDto> listPassages(String level);

	PassageDetailDto getPassageDetail(Long passageId);
}
