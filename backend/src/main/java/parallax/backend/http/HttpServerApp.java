package parallax.backend.http;

import com.sun.net.httpserver.HttpServer;
import parallax.backend.config.AppConfig;
import parallax.backend.db.UserRepository;
import parallax.backend.db.VehicleRepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpServerApp {
    public static void main(String[] args) throws IOException {
        AppConfig config = new AppConfig();
        UserRepository userRepository = new UserRepository();
        VehicleRepository vehicleRepository = new VehicleRepository();

        HttpServer server = HttpServer.create(new InetSocketAddress(config.getPort()), 0);
        server.createContext("/api/health", new HealthHandler());
        server.createContext("/api/auth/login", new AuthLoginHandler(userRepository, config));
        server.createContext("/api/auth/register", new AuthRegisterHandler(userRepository, config));
        server.createContext("/api/vehicles", new VehiclesHandler(vehicleRepository, userRepository, config));
        server.setExecutor(Executors.newCachedThreadPool());

        System.out.println("Started Parallax backend on port " + config.getPort());
        server.start();
    }
}
