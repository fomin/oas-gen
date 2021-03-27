package com.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 *
 */
public final class DownloadWithTypeResponse {

    /**
     *
     */
    @Nullable
    @JsonProperty("name")
    public final java.lang.String name;

    /**
     *
     */
    @Nullable
    @JsonProperty("file")
    public final org.springframework.core.io.Resource file;

    @JsonCreator
    public DownloadWithTypeResponse(
            @Nullable @JsonProperty("name") java.lang.String name,
            @Nullable @JsonProperty("file") org.springframework.core.io.Resource file
    ) {

        this.name = name;
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.example.DownloadWithTypeResponse other = (com.example.DownloadWithTypeResponse) o;
        return Objects.equals(name, other.name) &&
                Objects.equals(file, other.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                name,
                file
        );
    }

    @Override
    public String toString() {
        return "DownloadWithTypeResponse{" +
                "name='" + name + '\'' +
                ", file='" + file + '\'' +
                '}';
    }
}
