package com.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 *
 */
public final class DownloadfileRequest {

    /**
     *
     */
    @Nullable
    @JsonProperty("file")
    public final org.springframework.web.multipart.MultipartFile file;

    @JsonCreator
    public DownloadfileRequest(
            @Nullable @JsonProperty("file") org.springframework.web.multipart.MultipartFile file
    ) {

        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.example.DownloadfileRequest other = (com.example.DownloadfileRequest) o;
        return Objects.equals(file, other.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                file
        );
    }

    @Override
    public String toString() {
        return "DownloadfileRequest{" +
                "file='" + file + '\'' +
                '}';
    }
}
