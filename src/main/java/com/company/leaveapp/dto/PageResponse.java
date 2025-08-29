// src/main/java/com/company/leaveapp/dto/PageResponse.java
// A generic DTO for paginated responses to keep our API consistent.
package com.company.leaveapp.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLast
) {}