package io.landal.test.model;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

@ApplicationScoped
public class TransactionalTestService {

    @Transactional(value=TxType.MANDATORY)
    public String doSomething() {
        return "Success";
    }
}
