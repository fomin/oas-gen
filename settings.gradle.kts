rootProject.name = "oas-gen"

include(":java:oas-gen-jackson-rt")
include(":java:oas-gen-reactor-netty-rt")

include(":test-cases:spring-mvc-server")

include(":test-cases:spring-rest-operations-client")

include(":test-cases:reactor-netty-server")

include(":test-cases:reactor-netty-client")
