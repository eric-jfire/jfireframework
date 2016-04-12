package com.jfireframework.sql.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DaoTest.class, SessionTest.class, InterfaceTest.class, TxTest.class, ConcurrentTest.class
})
public class SuitTestSupprt
{

}
