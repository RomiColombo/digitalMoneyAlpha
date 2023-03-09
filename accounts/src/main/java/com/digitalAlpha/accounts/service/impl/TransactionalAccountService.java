package com.digitalAlpha.accounts.service.impl;

import com.digitalAlpha.accounts.model.UpdateAmountDTO;
import com.digitalAlpha.accounts.repository.IAccountRepository;
import com.digitalAlpha.accounts.repository.ITransactionalAccountRepository;
import com.digitalAlpha.accounts.service.ITransactionalAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TransactionalAccountService implements ITransactionalAccountService {
    private final ITransactionalAccountRepository transactionalRepository;

    private final IAccountRepository accountRepository;

    @Override
    public Mono<Boolean> updateAmount(UpdateAmountDTO updateAmountDTO) {
        Mono<Double> first = accountRepository.getAmountByCVU(updateAmountDTO.getAccountFrom());
        return first.zipWith(accountRepository.getAmountByCVU(updateAmountDTO.getAccountTo())).flatMap(tuple -> {
            updateAmountDTO.setAccountFromAmount(tuple.getT1() - updateAmountDTO.getAccountFromAmount());
            updateAmountDTO.setAccountToAmount(tuple.getT2() + updateAmountDTO.getAccountToAmount());
            return transactionalRepository.updateAmount(updateAmountDTO);
        });
    }
}
