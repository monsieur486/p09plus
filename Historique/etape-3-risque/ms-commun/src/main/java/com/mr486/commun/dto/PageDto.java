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
public class PageDto<T> {
    private List<T> contenu;

    private int page;

    private int taille;

    private long totalElements;

    private int totalPages;

    public boolean aSuivante() {
        return page + 1 < totalPages;
    }

    public boolean aPrecedente() {
        return page > 0;
    }
}
