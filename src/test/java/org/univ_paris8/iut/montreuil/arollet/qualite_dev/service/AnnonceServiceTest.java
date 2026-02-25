package org.univ_paris8.iut.montreuil.arollet.qualite_dev.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.Annonce;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.Category;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.entity.User;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.repository.AnnonceRepository;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.repository.CategoryRepository;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.domain.repository.UserRepository;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.security.AppUserPrincipal;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.UpdateAnnonceRequestDto;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.error.ApiException;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.mapper.AnnonceMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnonceServiceTest {

    @Mock
    private AnnonceRepository annonceRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AnnonceMapper annonceMapper;

    @InjectMocks
    private AnnonceService annonceService;

    @BeforeEach
    void setupSecurity() {
        AppUserPrincipal principal = new AppUserPrincipal(1L, "alice", "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(principal, "token", principal.getAuthorities())
        );
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateRejectsPublishedAnnonce() {
        User author = new User();
        author.setId(1L);

        Category category = new Category();
        category.setId(1L);

        Annonce annonce = new Annonce();
        annonce.setId(10L);
        annonce.setAuthor(author);
        annonce.setCategory(category);
        annonce.setStatus(AnnonceStatus.PUBLISHED);
        annonce.setVersion(0L);

        User current = new User();
        current.setId(1L);

        when(annonceRepository.findById(10L)).thenReturn(Optional.of(annonce));
        when(userRepository.findById(1L)).thenReturn(Optional.of(current));

        UpdateAnnonceRequestDto request = new UpdateAnnonceRequestDto();
        request.setTitle("title");
        request.setDescription("desc");
        request.setAddress("Paris");
        request.setMail("alice@example.com");
        request.setStatus("PUBLISHED");
        request.setCategoryId(1L);
        request.setVersion(0L);

        assertThatThrownBy(() -> annonceService.update(10L, request))
            .isInstanceOf(ApiException.class)
            .extracting(ex -> ((ApiException) ex).getStatus())
            .isEqualTo(HttpStatus.CONFLICT);
    }
}

