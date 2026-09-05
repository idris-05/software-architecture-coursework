package calc.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/")
public class StaticResource {

    private final byte[] content;
    private final String etag;

    public StaticResource() throws IOException {
        java.nio.file.Path file = Paths.get("web-client", "index.html");
        this.content = Files.readAllBytes(file);
        this.etag = "\"" + Integer.toHexString(file.toString().hashCode() ^ this.content.length) + "\"";
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response index() {
        return Response.ok(new String(content, StandardCharsets.UTF_8), MediaType.TEXT_HTML)
                .header("ETag", etag)
                .build();
    }
}
