package com.vocablearn.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vocablearn.domain.Passage;
import com.vocablearn.dto.PassageDetailDto;
import com.vocablearn.dto.PassageSummaryDto;
import com.vocablearn.exception.ResourceNotFoundException;
import com.vocablearn.mapper.PassageMapper;
import com.vocablearn.repository.PassageRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PassageServiceImplTest {

	@Mock
	private PassageRepository passageRepository;

	@Mock
	private PassageMapper passageMapper;

	@InjectMocks
	private PassageServiceImpl passageService;

	@Test
	void listPassages_withoutLevel_returnsAllOrderedByIndex() {
		Passage passage = new Passage();
		PassageSummaryDto summary = new PassageSummaryDto(1L, 1, "Title", "C1", 10);
		when(passageRepository.findAllByOrderByOrderIndexAsc()).thenReturn(List.of(passage));
		when(passageMapper.toSummary(passage)).thenReturn(summary);

		List<PassageSummaryDto> result = passageService.listPassages(null);

		assertThat(result).containsExactly(summary);
		verify(passageRepository, never()).findAllByLevelOrderByOrderIndexAsc(any());
	}

	@Test
	void listPassages_withBlankLevel_treatedAsNoFilter() {
		when(passageRepository.findAllByOrderByOrderIndexAsc()).thenReturn(List.of());

		passageService.listPassages("  ");

		verify(passageRepository).findAllByOrderByOrderIndexAsc();
		verify(passageRepository, never()).findAllByLevelOrderByOrderIndexAsc(any());
	}

	@Test
	void listPassages_withLevel_filtersByLevel() {
		when(passageRepository.findAllByLevelOrderByOrderIndexAsc("C1")).thenReturn(List.of());

		passageService.listPassages("C1");

		verify(passageRepository).findAllByLevelOrderByOrderIndexAsc("C1");
		verify(passageRepository, never()).findAllByOrderByOrderIndexAsc();
	}

	@Test
	void getPassageDetail_found_returnsMappedDetail() {
		Passage passage = new Passage();
		PassageDetailDto detail = new PassageDetailDto(1L, 1, "Title", "C1", List.of(), List.of(), List.of());
		when(passageRepository.findById(1L)).thenReturn(Optional.of(passage));
		when(passageMapper.toDetail(passage)).thenReturn(detail);

		PassageDetailDto result = passageService.getPassageDetail(1L);

		assertThat(result).isEqualTo(detail);
	}

	@Test
	void getPassageDetail_notFound_throwsResourceNotFoundException() {
		when(passageRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> passageService.getPassageDetail(99L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("99");
	}
}
