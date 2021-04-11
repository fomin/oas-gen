package com.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;


public final class DownloadWithModelRequestParams {


    @Nullable
    @JsonProperty("name")
    public final java.lang.String name;


    @Nullable
    @JsonProperty("id")
    public final java.lang.String id;


    @Nullable
    @JsonProperty("list")
    public final java.util.List<java.lang.String> list;

    @JsonCreator
    public DownloadWithModelRequestParams(
            @Nullable @JsonProperty("name") java.lang.String name,
            @Nullable @JsonProperty("id") java.lang.String id,
            @Nullable @JsonProperty("list") java.util.List<java.lang.String> list
    ) {

        this.name = name;
        this.id = id;
        this.list = list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.example.DownloadWithModelRequestParams other = (com.example.DownloadWithModelRequestParams) o;
        return Objects.equals(name, other.name) &&
                Objects.equals(id, other.id) &&
                Objects.equals(list, other.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                name,
                id,
                list
        );
    }

    @Override
    public String toString() {
        return "DownloadWithModelRequestParams{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", list='" + list + '\'' +
                '}';
    }
}
