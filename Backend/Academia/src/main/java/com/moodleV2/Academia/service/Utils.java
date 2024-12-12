package com.moodleV2.Academia.service;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public class Utils {

    public static <T> PagedModel<EntityModel<T>> createPagedModel(
            List<EntityModel<T>> entities,
            PagedModel.PageMetadata metadata,
            UriComponentsBuilder uriBuilder,
            int currentPage,
            int totalPages) {

        PagedModel<EntityModel<T>> pagedModel = PagedModel.of(entities, metadata);

        // Add self link
        pagedModel.add(Link.of(uriBuilder.cloneBuilder()
                        .queryParam("page", currentPage)
                        .queryParam("size", metadata.getSize())
                        .toUriString())
                .withSelfRel());

        // Add previous page link
        if (currentPage > 0) {
            pagedModel.add(Link.of(uriBuilder.cloneBuilder()
                            .queryParam("page", currentPage - 1)
                            .queryParam("size", metadata.getSize())
                            .toUriString())
                    .withRel("prev"));
        }

        // Add next page link
        if (currentPage < totalPages - 1) {
            pagedModel.add(Link.of(uriBuilder.cloneBuilder()
                            .queryParam("page", currentPage + 1)
                            .queryParam("size", metadata.getSize())
                            .toUriString())
                    .withRel("next"));
        }

        // Add first page link
        pagedModel.add(Link.of(uriBuilder.cloneBuilder()
                        .queryParam("page", 0)
                        .queryParam("size", metadata.getSize())
                        .toUriString())
                .withRel("first"));

        // Add last page link
        pagedModel.add(Link.of(uriBuilder.cloneBuilder()
                        .queryParam("page", totalPages - 1)
                        .queryParam("size", metadata.getSize())
                        .toUriString())
                .withRel("last"));

        return pagedModel;
    }
}
