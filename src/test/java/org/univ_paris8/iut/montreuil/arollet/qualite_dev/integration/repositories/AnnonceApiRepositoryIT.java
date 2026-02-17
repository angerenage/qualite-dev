package org.univ_paris8.iut.montreuil.arollet.qualite_dev.integration.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.entities.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.repositories.api.AnnonceApiRepository;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.testing.JpaIntegrationTestSupport;

class AnnonceApiRepositoryIT extends JpaIntegrationTestSupport {
	private final AnnonceApiRepository repository = new AnnonceApiRepository();

	@Test
	void shouldReturnFirstPageWithRequestedSizeAndOrder() {
		List<Annonce> page = repository.findAll(em, 0, 2);

		assertEquals(2, page.size());
		assertEquals(List.of(6L, 5L), extractIds(page));
	}

	@Test
	void shouldReturnSecondPageWithExpectedItems() {
		List<Annonce> page = repository.findAll(em, 1, 2);

		assertEquals(2, page.size());
		assertEquals(List.of(4L, 3L), extractIds(page));
	}

	@Test
	void shouldReturnTotalCountForPaginationMetadata() {
		long total = repository.countAll(em);

		assertEquals(6L, total);
	}

	@Test
	void shouldHandleEdgeCasesForPaging() {
		List<Annonce> outOfRange = repository.findAll(em, 100, 2);
		List<Annonce> invalidSizeDefaults = repository.findAll(em, -1, 0);

		assertTrue(outOfRange.isEmpty());
		assertEquals(6, invalidSizeDefaults.size());
		assertEquals(List.of(6L, 5L, 4L, 3L, 2L, 1L), extractIds(invalidSizeDefaults));
	}

	private List<Long> extractIds(List<Annonce> annonces) {
		return annonces.stream().map(Annonce::getId).collect(Collectors.toList());
	}
}
