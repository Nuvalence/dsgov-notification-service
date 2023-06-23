package io.nuvalence.platform.notification.service.model;

import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Locale;

/**
 * Base class for all filters.
 */
@SuperBuilder
public abstract class BaseFilter {
    protected String sortBy;
    protected String sortOrder;
    protected Integer page;
    protected Integer size;

    /**
     * Returns a PageRequest object based on the filter's page, size, sort by, and sort order.
     *
     * @return PageRequest object
     */
    public PageRequest getPageRequest() {
        Sort sort = Sort.unsorted();

        switch (sortBy.toLowerCase(Locale.ENGLISH)) {
            case "key":
            case "name":
            case "version":
            case "createdtimestamp":
            case "lastupdatedtimestamp":
            case "status":
                if (sortOrder.equalsIgnoreCase("desc")) {
                    sort = Sort.by(Sort.Direction.DESC, sortBy);
                } else {
                    sort = Sort.by(Sort.Direction.ASC, sortBy);
                }
                break;
            default:
                break;
        }

        return PageRequest.of(page, size, sort);
    }
}
