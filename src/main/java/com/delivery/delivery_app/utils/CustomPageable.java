package com.delivery.delivery_app.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class CustomPageable implements Pageable{
    private final Pageable pageable;

    public CustomPageable(Pageable pageable) {
        if (pageable == null || pageable.getPageNumber() < 1) {
            this.pageable = PageRequest.of(0, pageable != null ? pageable.getPageSize() : 10, Sort.unsorted()); // Default to page 1 (0 in zero-based index) and size 10
        } else {
            this.pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort());
        }
    }

    @Override
    public int getPageNumber() {
        return pageable.getPageNumber() + 1;
    }

    @Override
    public int getPageSize() {
        return pageable.getPageSize();
    }

    @Override
    public long getOffset() {
        return pageable.getOffset();
    }

    @Override
    public Sort getSort() {
        return pageable.getSort();
    }

    @Override
    public Pageable next() {
        return pageable.next();
    }

    @Override
    public Pageable previousOrFirst() {
        return pageable.previousOrFirst();
    }

    @Override
    public Pageable first() {
        return pageable.first();
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return pageable.withPage(pageNumber - 1);
    }

    @Override
    public boolean hasPrevious() {
        return pageable.hasPrevious();
    }
}
