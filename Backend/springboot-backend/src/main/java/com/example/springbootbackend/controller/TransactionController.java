package com.example.springbootbackend.controller;

import com.example.springbootbackend.model.Transaction;
import com.example.springbootbackend.model.DTO.TransactionDTO;
import com.example.springbootbackend.Service.TransactionService;
import com.example.springbootbackend.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.AbstractMap;
import java.util.UUID;

@RestController
@RequestMapping("/Transaction")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private TransactionService transactionService;

    // Create transaction
    @Operation(
        summary = "Add a new transaction",
        description = "Creates a new transaction record in the system."
    )
    @ApiResponse(
        responseCode = "201",
        description = "Transaction created successfully",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "SuccessExample",
                summary = "Transaction information Created",
                value = """
                        {
                          "message": "Transaction created successfully, TransactionID: <uuid>"
                        }
                        """
            )
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "ErrorExample",
                summary = "Validation error",
                value = """
                        {
                          "message": "Invalid input data. Please check your request."
                        }
                        """
            )
        )
    )
    @PostMapping("/add")
    public ResponseEntity<Object> addTransaction(@RequestBody Transaction transaction) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(transaction);

        logger.info("API route Called(Transaction): '/add'");
        logger.info("(Transaction): '/add - Information from request: " + json);

        AbstractMap<String, Object> validationData = transactionService.createTransaction(transaction);

        if (validationData.size() != 0 && !validationData.containsKey("Transaction Status")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationData);
        }

        if (validationData.containsKey("Transaction Status")) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Transaction information Created: " + validationData.get("Transaction Status"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Transaction information Created: " + validationData);
    }

    // Get transaction
    @Operation(
        summary = "Get a transaction by ID",
        description = "Fetches a transaction record using its unique transaction ID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Transaction found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TransactionDTO.class),
                examples = @ExampleObject(
                    name = "TransactionFound",
                    summary = "Transaction retrieved successfully",
                    value = """
                            {
                              "transactionId": "f81f92ce-b922-444e-81a4-5e4ab1fcf60a",
                              "userId": "ab0c8000-f90c-48f3-b37b-41b471a4485d",
                              "amount": 215.0,
                              "location": "123 7 Ave SW, Calgary, Alberta, T2P 0L4, Canada",
                              "status": "DECLINED",
                              "transaction_date": "2025-09-14T00:23:07.5351782Z"
                            }
                            """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid transaction ID or bad input data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "InvalidIdError",
                    summary = "Bad request example",
                    value = """
                            {
                              "message": "Invalid transaction ID format. Must be a valid UUID."
                            }
                            """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Transaction not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "NotFoundError",
                    summary = "Transaction not available",
                    value = """
                            {
                              "message": "Transaction with ID f81f92ce-b922-444e-81a4-5e4ab1fcf60a not found."
                            }
                            """
                )
            )
        )
    })
    @GetMapping("/{transactionID}")
    public ResponseEntity<Object> getTransactionById(
            @PathVariable String transactionID,
            @RequestBody Transaction transaction) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(transaction);

        logger.info("API route Called(Transaction): '/{id}'");
        logger.info("(Transaction): '/{id} - Information from request(Header): " + transactionID);
        logger.info("(Transaction): '/{id} - Information from request(Body): " + json);

        UUID uuid = UUID.fromString(transactionID);
        AbstractMap<String, Object> validationData = transactionService.getTransaction(uuid, transaction);

        if (validationData.size() != 0 && !validationData.containsKey("Transaction Data")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationData);
        }

        if (validationData.containsKey("Transaction Data")) {
            TransactionDTO transactionDTO = new TransactionDTO((Transaction) validationData.get("Transaction Data"));
            return ResponseEntity.status(HttpStatus.OK).body(transactionDTO);
        }

        return ResponseEntity.status(HttpStatus.OK).body(validationData);
    }

    // Update transaction
    @Operation(
        summary = "Update an existing transaction",
        description = "Updates a transaction record using its unique transaction ID. "
                    + "The data that is to be updated must be provided in the request body."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Transaction updated successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "UpdateSuccess",
                    summary = "Transaction update confirmation",
                    value = """
                            {
                              "message": "Transaction information Updated: APPROVED"
                            }
                            """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid transaction data or ID format",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "BadRequest",
                    summary = "Invalid request example",
                    value = """
                            {
                              "message": "Invalid transaction data provided. Please check the request payload."
                            }
                            """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Transaction not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "NotFound",
                    summary = "Transaction not found",
                    value = """
                            {
                              "message": "Transaction with ID f81f92ce-b922-444e-81a4-5e4ab1fcf60a not found."
                            }
                            """
                )
            )
        )
    })
    @PutMapping("/update/{transactionID}")
    public ResponseEntity<Object> updateTransaction(
            @PathVariable String transactionID,
            @RequestBody Transaction transaction) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(transaction);

        UUID uuid = UUID.fromString(transactionID);
        AbstractMap<String, Object> validationData = transactionService.updateTransaction(uuid, transaction);

        if (validationData.size() != 0 && !validationData.containsKey("Transaction Status")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationData);
        }

        if (validationData.containsKey("Transaction Status")) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Transaction information Updated: " + validationData.get("Transaction Status"));
        }

        return ResponseEntity.status(HttpStatus.OK).body("Transaction information Updated: " + validationData);
    }

    // Delete transaction
    @Operation(
        summary = "Delete a transaction based on data found in body",
        description = "Deletes a transaction record using its unique transaction ID. "
                    + "The body of the request must contain the full transaction details to authorize deletion."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Transaction deleted successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "DeleteSuccess",
                    summary = "Successful deletion message",
                    value = """
                            {
                              "message": "Transaction information Deleted: SUCCESS"
                            }
                            """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid transaction data or ID format",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "BadRequest",
                    summary = "Invalid request",
                    value = """
                            {
                              "message": "Invalid transaction ID format or bad request payload."
                            }
                            """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Transaction not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "NotFound",
                    summary = "Transaction not found example",
                    value = """
                            {
                              "message": "Transaction with ID f81f92ce-b922-444e-81a4-5e4ab1fcf60a not found."
                            }
                            """
                )
            )
        )
    })
    @DeleteMapping("/delete/{transactionID}")
    public ResponseEntity<Object> deleteTransaction(
            @PathVariable String transactionID,
            @RequestBody Transaction transaction) throws JsonProcessingException {

        UUID uuid = UUID.fromString(transactionID);
        AbstractMap<String, Object> validationData = transactionService.deleteTransaction(uuid, transaction);

        if (validationData.size() != 0 && !validationData.containsKey("Transaction Status")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationData);
        }

        if (validationData.containsKey("Transaction Status")) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Transaction information Deleted: " + validationData.get("Transaction Status"));
        }

        return ResponseEntity.status(HttpStatus.OK).body("Transaction information Deleted: " + validationData);
    }
}
