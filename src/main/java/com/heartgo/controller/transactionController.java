package com.heartgo.controller;

import com.heartgo.model.AjaxResponseBody;
import com.heartgo.model.SearchCriteria;
import com.heartgo.model.Transaction;
import com.heartgo.model.TransactionCriteria;
import com.heartgo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class transactionController {
    TransactionService transactionService;
    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    @PostMapping("/admin/yz")
    public ResponseEntity<?> getSearchResultViaAjaxTransaction(
            @Valid @RequestBody TransactionCriteria searchs, Errors errors) {

        AjaxResponseBody result = new AjaxResponseBody();

        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {

            result.setMsg(errors.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));

            return ResponseEntity.badRequest().body(result);

        }

        List<Transaction> transactions = transactionService.findByUserName(searchs.getOrganizationid());
        if (transactions.isEmpty()) {
            result.setMsg("no user found!");
        } else {
            result.setMsg("success");
        }
        result.setTransactionsResult(transactions);

        System.out.println(transactions);
        return ResponseEntity.ok(result);

    }

}
