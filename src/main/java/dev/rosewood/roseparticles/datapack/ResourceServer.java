package dev.rosewood.roseparticles.datapack;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseparticles.config.SettingKey;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ResourceServer {

    public static final String PACK_NAME = "pack.zip";
    private static final String RESOURCE_PACK_URL = "http://%s:%d/" + PACK_NAME;

    private final RosePlugin rosePlugin;
    private HttpServer server;

    private boolean started = false;
    private int port;
    private String externalHostname;
    private byte[] resourcePackHash = new byte[0];

    public ResourceServer(RosePlugin rosePlugin) {
        this.rosePlugin = rosePlugin;
    }

    public void start() {
        if (this.started)
            throw new IllegalStateException("Server is already started");

        byte[] oldHash = this.resourcePackHash;
        this.resourcePackHash = ResourcePacker.pack(this.rosePlugin, new File(this.rosePlugin.getDataFolder(), "pack"));

        String hostname = SettingKey.RESOURCE_PACK_SERVER_HOSTNAME.get();
        this.port = SettingKey.RESOURCE_PACK_SERVER_PORT.get();
        this.externalHostname = SettingKey.RESOURCE_PACK_EXTERNAL_HOSTNAME.get();
        if (this.externalHostname.isBlank())
            this.externalHostname = hostname;

        try {
            this.server = HttpServer.create(new InetSocketAddress(hostname, this.port), 0);
            this.server.createContext("/", this::handleDownload);
            this.server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!Arrays.equals(oldHash, this.resourcePackHash))
            this.rosePlugin.getScheduler().runTaskLater(() -> Bukkit.getOnlinePlayers().forEach(this::setResourcePack), 20L);

        this.started = true;
    }

    public void shutdown() {
        if (this.started) {
            this.server.stop(0);
            this.server = null;
            this.started = false;
        }
    }

    public void setResourcePack(Player player) {
        if (!this.started)
            return;

        InetSocketAddress address = player.getAddress();
        boolean localhost = address != null && address.getAddress().toString().contains("127.0.0.1");
        String url;
        if (localhost) {
            url = RESOURCE_PACK_URL.formatted("127.0.0.1", this.port);
        } else {
            url = RESOURCE_PACK_URL.formatted(this.externalHostname, this.port);
        }
        player.setResourcePack(url, this.resourcePackHash);
    }

    private void handleDownload(HttpExchange exchange) throws IOException {
        try (OutputStream out = exchange.getResponseBody()) {
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/zip");
            headers.add("Content-Disposition", "attachment; filename=" + PACK_NAME + ";");

            URI uri = exchange.getRequestURI();
            String name = uri.getPath().substring(1);
            if (!name.equals(PACK_NAME)) {
                exchange.sendResponseHeaders(403, 0);
                out.write("Illegal Path".getBytes());
                return;
            }

            File path = new File(this.rosePlugin.getDataFolder(), PACK_NAME);
            if (path.exists()) {
                exchange.sendResponseHeaders(200, path.length());
                out.write(Files.readAllBytes(path.toPath()));
            } else {
                exchange.sendResponseHeaders(404, 0);
                out.write("404 Pack Failed To Generate".getBytes());
            }
        }
    }

}
