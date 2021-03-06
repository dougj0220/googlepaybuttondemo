package com.bhn.gpay.service;

import com.bhn.gpay.service.impl.DecryptServiceImpl;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DecryptServiceTest {

    @Test
    public void testDecryptSamsung() {
        DecryptServiceImpl decryptService = new DecryptServiceImpl();
        String base64EncodedPayload = "eyJyZXF1ZXN0SWQiOiI4MzY5Yzk5Mi03ZWEzLTRlY2YtOTkyMS02NjkyZTAzZmVmYTQiLCJtZXRob2ROYW1lIjoiaHR0cHM6Ly9zcGF5LnNhbXN1bmcuY29tIiwiZGV0YWlscyI6eyJtZXRob2QiOiIzRFMiLCJwYXltZW50Q3JlZGVudGlhbCI6eyIzRFMiOnsiZGF0YSI6ImV5SmhiR2NpT2lKU1UwRXhYelVpTENKcmFXUWlPaUpUUm1sblN6Rm5SVll4Y2pOTGEzRnJkSGRMU2t4dlIyMWliV3BETjAxSGQwaHhlRU5JV0Vac1VXSXdQU0lzSW5SNWNDSTZJa3BQVTBVaUxDSmphR0Z1Ym1Wc1UyVmpkWEpwZEhsRGIyNTBaWGgwSWpvaVVsTkJYMUJMU1NJc0ltVnVZeUk2SWtFeE1qaEhRMDBpZlEucFJITmhPQXhZVGR0Nk1MM040VWhnN1FPVmFiMS16WVlONi10bzBNaFVod1NlbFlNeFQ2cWhxbm5wMkdWdWt0WWxnZGRoLXpKMzlNWnVsVDFSbTBVRl9kWjBnRTMwd0hyMmRMdVJrUFN4ak5qTDgtdUkxU25FSVBNSmY3R184QVowc3hjbzBHLXVDSGVXVndndnJ2TldfMEZLNW5meHFRWTVROHM0OXJ6M0JMd2oxOUxFV1htRDdzQ3NDc0VhNjN6Nzd6ZkdWQ3RiR1A0SUtYOGFoNDFvZm11UzF0VUhtbUlUV1VkaHhTWmliMVlZdTktMFdYZjk2UjcteDN3R1dmdnZidEVnRzBJSldxYjk3ZUVQZERDeFAweDZiZjh2cjZjQkduUGdHSXhwM1hJYTVNTk9SX3FmWWFRcEZOYlRveXVtUFJ5ajJ1MEplanl4ZjRUT3FHMGVRLnpyeGdKam83UUR0Zm1vM24uQmc2NV9DZUUtSU0yWUNDbHZfdFp0dXkyd1h0d1VHUGlsY21tLVNhS3l6QmdTcDNlSloyRXJScVh4R09sSldrS2swOHlJY3IzbXVBVUx0NWtkZzFZQTRfTXJ6QzhISk5nXzZha1NEWHFsT1dHUml4V0ZTeEtrdFN6cHVzdjA5NEdXTEQ4czBDdm5TQ1FpVEc2R1lVUkhqT2ZTNkRwR2d5WUpidGs5UmFpUFhWVjdNVTdBWHNGRXhRX3R4WVdubGRoRXcxVzRzLXF3SFBYemNwcHdDWHd0ZVU5YUw4Vm1BYWl4WkY5Yk1PTnBTWTl5MWNqQWhUQlRRLnl0QVM3TXRKUzNBTkZxTGoySmtWOHciLCJ0eXBlIjoiUyIsInZlcnNpb24iOiIxMDAifSwicmVjdXJyaW5nX3BheW1lbnQiOiJmYWxzZSJ9LCJwYXltZW50SW5mbyI6eyJjYXJkX2xhc3Q0ZGlnaXRzIjoiNDk4MSIsImNhcmRCcmFuZCI6InZpc2EiLCJvcmRlck51bWJlciI6Ikx6MDI1MjZuOEJLdyJ9fSwic2hpcHBpbmdBZGRyZXNzIjpudWxsLCJzaGlwcGluZ09wdGlvbiI6bnVsbCwicGF5ZXJOYW1lIjoiRG91ZyBUZXN0IiwicGF5ZXJFbWFpbCI6ImRvdWcuamVyZW1pYXNAYmhuZXR3b3JrLmNvbSIsInBheWVyUGhvbmUiOiIoNDEyKSA1NTUtMTExMSJ9";
        String payload = new String(Base64.decodeBase64(base64EncodedPayload));
        System.out.println(payload);
        System.out.println(decryptService.decryptSamsungPayload(payload));
    }
}
