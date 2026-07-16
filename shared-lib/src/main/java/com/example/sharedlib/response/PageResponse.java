package com.example.sharedlib.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * A generic wrapper for paginated API responses.
 * This class provides a consistent structure for responses that return a list of items
 * with pagination details.
 *
 * @param <T> The type of the content in the page.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The list of items on the current page.
     */
    private List<T> content;

    /**
     * The current page number (0-indexed).
     */
    private int pageNumber;

    /**
     * The number of items per page.
     */
    private int pageSize;

    /**
     * The total number of items across all pages.
     */
    private long totalElements;

    /**
     * The total number of pages.
     */
    private int totalPages;

    /**
     * Indicates if this is the last page.
     */
    private boolean isLast;

    /**
     * Indicates if this is the first page.
     */
    private boolean isFirst;

    /**
     * Indicates if there is a next page.
     */
    private boolean hasNext;

    /**
     * Indicates if there is a previous page.
     */
    private boolean hasPrevious;
}