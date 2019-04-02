//package com.mishas.stuff.ms.service;
//
//import com.mishas.stuff.ms.web.dto.TransactionDto;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.math.BigDecimal;
//import java.math.BigInteger;
//
//import static org.junit.Assert.assertTrue;
//
//public class RecordKeepingServiceTest {
//
//    private IRecordKeepingService recordKeepingService;
//    private TransactionDto transactionDto;
//
//    @Before
//    public void before() {
//        recordKeepingService = RecordKeepingService.getInstance();
//        transactionDto = new TransactionDto(
//                "someIdFrommastercard",
//                new BigDecimal(12.12),
//                "GBP",
//                "Misha",
//                "Feds",
//                new BigInteger("8765987698769876")
//        );
//    }
//
//    @Test
//    public void createCorrelationIDTest() {
//        assertTrue(recordKeepingService.createCorrelationID(transactionDto) != null);
//    }
//}
