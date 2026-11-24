package com.rpgmanager.campaign;

import com.rpgmanager.campaign.dto.CampaignRequest;
import com.rpgmanager.campaign.dto.CampaignResponse;
import com.rpgmanager.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository repository;

    @Mock
    private ApplicationEventPublisher events;

    @InjectMocks
    private CampaignService service;

    @Test
    void createDefaultsStatusToPlanning() {
        when(repository.saveAndFlush(any(Campaign.class))).thenAnswer(inv -> inv.getArgument(0));

        CampaignResponse response = service.create(
                new CampaignRequest("Nova campanha", GameSystem.DND_5E, null, "Mestre", null));

        assertThat(response.status()).isEqualTo(CampaignStatus.PLANNING);
        assertThat(response.gameSystem()).isEqualTo(GameSystem.DND_5E);
    }

    @Test
    void updateKeepsStatusWhenOmitted() {
        Campaign existing = new Campaign("Antiga", GameSystem.DND_5E, CampaignStatus.ACTIVE, "Mestre", null);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.saveAndFlush(existing)).thenReturn(existing);

        CampaignResponse response = service.update(1L,
                new CampaignRequest("Renomeada", GameSystem.ORDEM_PARANORMAL, null, "Mestre", "nova descrição"));

        assertThat(response.name()).isEqualTo("Renomeada");
        assertThat(response.gameSystem()).isEqualTo(GameSystem.ORDEM_PARANORMAL);
        assertThat(response.status()).isEqualTo(CampaignStatus.ACTIVE);
    }

    @Test
    void getThrowsNotFoundWhenMissing() {
        when(repository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(42L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("42");
    }

    @Test
    void deletePublishesEventForCleanup() {
        Campaign existing = new Campaign("Fim", GameSystem.DND_5E, CampaignStatus.FINISHED, "Mestre", null);
        when(repository.findById(7L)).thenReturn(Optional.of(existing));

        service.delete(7L);

        verify(repository).delete(existing);
        verify(events).publishEvent(new CampaignDeletedEvent(7L));
    }
}
