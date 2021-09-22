package io.github.fomin.oasgen.mutator;

import io.github.fomin.oasgen.MutatedRequest;
import io.github.fomin.oasgen.Mutator;
import io.github.fomin.oasgen.MutatorUtils;
import io.github.fomin.oasgen.Request;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class HeaderMutator implements Mutator {

    private static final String[] STANDARD_HEADERS = new String[]{
            "Accept",
            "Accept-Charset",
            "Accept-Encoding",
            "Accept-Language",
            "Accept-Ranges",
            "Age",
            "Allow",
            "Alternates",
            "Authorization",
            "Cache-Control",
            "Connection",
            "Content-Base",
            "Content-Disposition",
            "Content-Encoding",
            "Content-Language",
            "Content-Length",
            "Content-Location",
            "Content-MD5",
            "Content-Range",
            "Content-Type",
            "Content-Version",
            "Date",
            "Derived-From",
            "ETag",
            "Expect",
            "Expires",
            "From",
            "Host",
            "If-Match",
            "If-Modified-Since",
            "If-None-Match",
            "If-Range",
            "If-Unmodified-Since",
            "Last-Modified",
            "Link",
            "Location",
            "Max-Forwards",
            "MIME-Version",
            "Pragma",
            "Proxy-Authenticate",
            "Proxy-Authorization",
            "Public",
            "Range",
            "Referer",
            "Retry-After",
            "Server",
            "Title",
            "TE",
            "Trailer",
            "Transfer-Encoding",
            "Upgrade",
            "URI",
            "User-Agent",
            "Vary",
            "Via",
            "Warning",
            "WWW-Authenticate"
    };

    @Override
    public boolean isAvailable(Request request) {
        return true;
    }

    @Override
    public Stream<Request> mutate(Request request) {
        List<Request> result = new ArrayList<>();
        MutatedRequest mr = new MutatedRequest(request, this);
        mr.addHeader(MutatorUtils.random(STANDARD_HEADERS, request.getHeaders().keySet()), RandomStringUtils.randomAlphanumeric(10));
        result.add(mr);

        if (!request.getHeaders().isEmpty()) {
            mr = new MutatedRequest(request, this);
            mr.removeHeader(MutatorUtils.random(request.getHeaders().keySet()));
            result.add(mr);
            for (Map.Entry<String, Object> header : request.getHeaders().entrySet()) {
                mr = new MutatedRequest(request, this);
                if (!(header.getValue() instanceof List)) {
                    mr.addHeader(header.getKey(), header.getValue().toString());
                }
                result.add(mr);
            }
        }

        return result.stream();
    }

    @Override
    public String toString() {
        return "HeaderMutator";
    }
}
