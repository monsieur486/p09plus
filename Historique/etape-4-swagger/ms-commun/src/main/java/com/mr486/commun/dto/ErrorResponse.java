package com.mr486.commun.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private String timestamp;

    private String microserviceName;

    private String path;

    private String errorCode;

    private List<String> messages;
}
