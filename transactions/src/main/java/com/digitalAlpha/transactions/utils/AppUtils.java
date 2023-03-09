package com.digitalAlpha.transactions.utils;

import com.digitalAlpha.transactions.model.Transaction;
import com.digitalAlpha.transactions.model.dto.ReducedTransactionDTO;
import com.digitalAlpha.transactions.model.dto.TransactionDTO;
import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.beans.BeanUtils;

public class AppUtils {

    public static TransactionDTO entityToDto(Transaction transaction){
        TransactionDTO transactionDTO = new TransactionDTO();
        BeanUtils.copyProperties(transaction,transactionDTO);
        return  transactionDTO;
    }

    public static ReducedTransactionDTO entityToReducedDto(Transaction transaction){
        ReducedTransactionDTO reducedTransactionDTO = new ReducedTransactionDTO();
        BeanUtils.copyProperties(transaction,reducedTransactionDTO);
        return  reducedTransactionDTO;
    }

}
