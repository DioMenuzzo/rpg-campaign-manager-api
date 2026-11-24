package com.rpgmanager.sheet;

import com.rpgmanager.campaign.CampaignDeletedEvent;
import com.rpgmanager.character.CharacterDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Mantém o MongoDB coerente com o PostgreSQL: quando um personagem ou campanha é removido,
 * apaga as fichas correspondentes.
 *
 * <p>Roda só depois do commit no PostgreSQL ({@code AFTER_COMMIT}). Se a remoção no Postgres falhar,
 * as fichas continuam intactas. Se a limpeza no Mongo falhar, sobra uma ficha órfã, que é inofensiva
 * (nunca é acessada, pois toda leitura exige o personagem existir). Não existe transação distribuída
 * entre os dois bancos: a consistência aqui é eventual.</p>
 */
@Component
public class SheetCleanupListener {

    private static final Logger log = LoggerFactory.getLogger(SheetCleanupListener.class);

    private final CharacterSheetRepository repository;

    public SheetCleanupListener(CharacterSheetRepository repository) {
        this.repository = repository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCharacterDeleted(CharacterDeletedEvent event) {
        long removed = repository.deleteByCharacterId(event.characterId());
        log.debug("Personagem {} removido: {} ficha(s) apagada(s)", event.characterId(), removed);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCampaignDeleted(CampaignDeletedEvent event) {
        long removed = repository.deleteByCampaignId(event.campaignId());
        log.debug("Campanha {} removida: {} ficha(s) apagada(s)", event.campaignId(), removed);
    }
}
