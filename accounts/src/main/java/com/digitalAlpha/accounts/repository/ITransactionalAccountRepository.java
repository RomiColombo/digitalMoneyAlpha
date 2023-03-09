package com.digitalAlpha.accounts.repository;

import com.digitalAlpha.accounts.model.UpdateAmountDTO;
import reactor.core.publisher.Mono;

public interface ITransactionalAccountRepository {
    Mono<Boolean> updateAmount(UpdateAmountDTO updateAmountDTO);
}
