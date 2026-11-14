package com.vocablearn.service.impl;

import com.vocablearn.domain.Passage;
import com.vocablearn.dto.PassageDetailDto;
import com.vocablearn.dto.PassageSummaryDto;
import com.vocablearn.exception.ResourceNotFoundException;
import com.vocablearn.mapper.PassageMapper;
import com.vocablearn.repository.PassageRepository;
import com.vocablearn.service.PassageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassageServiceImpl implements PassageService {

	private final PassageRepository passageRepository;
	private final PassageMapper passageMapper;

	@Override
	public List<PassageSummaryDto> listPassages(String level) {
		List<Passage> passages = (level == null || level.isBlank())
				? passageRepository.findAllByOrderByOrderIndexAsc()
				: passageRepository.findAllByLevelOrderByOrderIndexAsc(level);
		return passages.stream().map(passageMapper::toSummary).toList();
	}

	@Override
	public PassageDetailDto getPassageDetail(Long passageId) {
		Passage passage = passageRepository.findById(passageId)
				.orElseThrow(() -> new ResourceNotFoundException("Passage not found: " + passageId));
		return passageMapper.toDetail(passage);
	}
}
