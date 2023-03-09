package com.digitalAlpha.accounts.service;

import com.digitalAlpha.accounts.model.UpdateAmountDTO;
import reactor.core.publisher.Mono;

public interface ITransactionalAccountService {
    Mono<Boolean> updateAmount(UpdateAmountDTO updateAmountDTO);
}
