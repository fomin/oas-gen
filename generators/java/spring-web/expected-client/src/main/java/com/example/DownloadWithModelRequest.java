package com.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;


public final class DownloadWithModelRequest {


    @Nullable
    @JsonProperty("file")
    public final org.springframework.core.io.Resource file;


    @Nullable
    @JsonProperty("params")
    public final com.example.DownloadWithModelRequestParams params;

    @JsonCreator
    public DownloadWithModelRequest(
            @Nullable @JsonProperty("file") org.springframework.core.io.Resource file,
            @Nullable @JsonProperty("params") com.example.DownloadWithModelRequestParams params
    ) {

        this.file = file;
        this.params = params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.example.DownloadWithModelRequest other = (com.example.DownloadWithModelRequest) o;
        return Objects.equals(file, other.file) &&
                Objects.equals(params, other.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                file,
                params
        );
    }

    @Override
    public String toString() {
        return "DownloadWithModelRequest{" +
                "file='" + file + '\'' +
                ", params='" + params + '\'' +
                '}';
    }
}
