package com.revature.CLIBank;

import com.revature.CLIBank.API.API;
import org.junit.jupiter.api.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CLIBankTest {
    private API api;
    private Object previousApi;
    private Field apiField;
    private PrintStream originalOut, originalErr;
    private ByteArrayOutputStream output, errors;

    @BeforeEach void setup() throws Exception {
        // Inject a mock only in tests; no production setter is needed.
        apiField = CLIBank.class.getDeclaredField("api");
        apiField.setAccessible(true);
        previousApi = apiField.get(null);
        api = mock(API.class);
        when(api.getResult()).thenReturn("history output\n");
        apiField.set(null, api);
        originalOut = System.out;
        originalErr = System.err;
        output = new ByteArrayOutputStream();
        errors = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        System.setErr(new PrintStream(errors));
    }

    @AfterEach void cleanup() throws Exception {
        System.setOut(originalOut);
        System.setErr(originalErr);
        apiField.set(null, previousApi);
    }

    @Test void exec_blank() {
        CLIBank.exec(List.of(""));
        assertTrue(errors.toString().contains("Unrecognized command"));
    }

    @Test void historyWithoutArgumentsPrintsResult() {
        CLIBank.exec(List.of("transactions"));
        verify(api).getTransactions();
        assertEquals("history output\n", output.toString());
    }

    @Test void historyCountLimitsResult() {
        CLIBank.exec(List.of("transactions", "5"));
        verify(api).getTransactions(5);
    }

    @Test void accountHistoryKeepsUuidAsText() {
        String id = UUID.randomUUID().toString();
        CLIBank.exec(List.of("transactions", id, "5"));
        verify(api).getAcctTransactions(id, 5);
        assertEquals("history output\n", output.toString());
    }

    @Test void accountHistoryAllowsOmittedCount() {
        String id = UUID.randomUUID().toString();
        CLIBank.exec(List.of("transactions", id));
        verify(api).getAcctTransactions(id, -1);
    }

    @Test void invalidHistoryCountShowsHelpfulMessage() {
        CLIBank.exec(List.of("transactions", UUID.randomUUID().toString(), "abc"));
        verifyNoInteractions(api);
        assertTrue(errors.toString().contains("Count must be 0-1000"));
    }

    @Test void outOfRangeHistoryCountIsRejected() {
        CLIBank.exec(List.of("transactions", "1001"));
        verifyNoInteractions(api);
        assertTrue(errors.toString().contains("Count must be 0-1000"));
    }
}
