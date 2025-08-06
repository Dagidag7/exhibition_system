package com.exhibition;

import com.exhibition.model.Attendee;
import com.exhibition.repository.AttendeeRepository;
import com.exhibition.repository.AttendeeRepositoryImpl;
import com.exhibition.repository.ConferenceRepository;
import com.exhibition.repository.ConferenceRepositoryImpl;
import com.exhibition.repository.ExhibitorRepository;
import com.exhibition.repository.ExhibitorRepositoryImpl;
import com.exhibition.repository.FloorRepository;
import com.exhibition.repository.FloorRepositoryImpl;
import com.exhibition.repository.PartnerRepository;
import com.exhibition.repository.PartnerRepositoryImpl;
import com.exhibition.repository.ProductRepository;
import com.exhibition.repository.ProductRepositoryImpl;
import com.exhibition.repository.SpeakerRepository;
import com.exhibition.repository.SpeakerRepositoryImpl;
import com.exhibition.repository.SponsorRepository;
import com.exhibition.repository.SponsorRepositoryImpl;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import java.util.HashSet;
import java.util.Set;
import io.vertx.core.http.HttpServer;
import com.exhibition.service.AttendeeService;
import com.exhibition.service.AttendeeServiceImpl;
import com.exhibition.service.ConferenceService;
import com.exhibition.service.ConferenceServiceImpl;
import com.exhibition.service.ExhibitorService;
import com.exhibition.service.ExhibitorServiceImpl;
import com.exhibition.service.FloorService;
import com.exhibition.service.PartnerService;
import com.exhibition.service.PartnerServiceImpl;
import com.exhibition.service.ProductService;
import com.exhibition.service.ProductServiceImpl;
import com.exhibition.service.SpeakerService;
import com.exhibition.service.SpeakerServiceImpl;
import com.exhibition.service.SponsorService;
import com.exhibition.service.SponsorServiceImpl;
import com.exhibition.service.AuthService;
import com.exhibition.service.AuthServiceImpl;
import com.exhibition.controller.AttendeeController;
import com.exhibition.controller.ConferenceController;
import com.exhibition.controller.ExhibitorController;
import com.exhibition.controller.FloorController;
import com.exhibition.controller.PartnerController;
import com.exhibition.controller.ProductController;
import com.exhibition.controller.SpeakerController;
import com.exhibition.controller.SponsorController;
import com.exhibition.controller.AuthController;
import com.exhibition.controller.DatabaseController;
import com.exhibition.controller.ConferenceRegistrationController;
import com.exhibition.service.DatabaseService;
import com.exhibition.repository.ConferenceRegistrationRepository;
import com.exhibition.repository.ConferenceRegistrationRepositoryImpl;

import io.vertx.ext.web.handler.BodyHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.json.jackson.DatabindCodec;

public class MainVerticle extends AbstractVerticle {

    public static SQLClient jdbc; // For repository access
    public static Vertx vertx;    // For global Vertx access

    public static void main(String[] args) {
        // Configure Vertx with longer blocked thread checker timeout for initialization
        VertxOptions options = new VertxOptions()
            .setBlockedThreadCheckInterval(10000) // 10 seconds
            .setMaxEventLoopExecuteTime(10000);   // 10 seconds
        
        vertx = Vertx.vertx(options);
        vertx.deployVerticle(new MainVerticle());
    }

    @Override
    public void start() {
        // Use executeBlocking to handle blocking operations
        vertx.executeBlocking(promise -> {
            try {
                // Register JavaTimeModule for LocalDateTime serialization
                ObjectMapper mapper = DatabindCodec.mapper();
                mapper.registerModule(new JavaTimeModule());
                DatabindCodec.prettyMapper().registerModule(new JavaTimeModule());
                
                // Database configuration
                JsonObject config = new JsonObject()
                    .put("url", "jdbc:postgresql://localhost:5432/exhibition_db")
                    .put("driver_class", "org.postgresql.Driver")
                    .put("user", "exhibition_system")
                    .put("password", "Dagikelem129@");

                // Create the JDBC client
                jdbc = JDBCClient.createShared(vertx, config);
                
                promise.complete();
            } catch (Exception e) {
                promise.fail(e);
            }
        }, false, ar -> {
            if (ar.succeeded()) {
                // Continue with non-blocking operations
                setupRouter();
            } else {
                System.err.println("Failed to initialize backend: " + ar.cause().getMessage());
            }
        });
    }

    private void setupRouter() {
        Router router = Router.router(vertx);
        router.route("/images/*").handler(StaticHandler.create("images"));

        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("x-requested-with");
        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("origin");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("accept");
        allowedHeaders.add("Authorization");

        router.route().handler(
            CorsHandler.create("*") // Allow all origins for development
            .allowedHeaders(allowedHeaders)
            .allowedMethod(io.vertx.core.http.HttpMethod.GET)
            .allowedMethod(io.vertx.core.http.HttpMethod.POST)
            .allowedMethod(io.vertx.core.http.HttpMethod.PUT)
            .allowedMethod(io.vertx.core.http.HttpMethod.DELETE)
        );

        router.route().handler(BodyHandler.create());

        // Initialize services
        AttendeeRepository attendeeRepo = new AttendeeRepositoryImpl();
        AttendeeService attendeeService = new AttendeeServiceImpl(attendeeRepo);
        
        ExhibitorRepository exhibitorRepo = new ExhibitorRepositoryImpl();
        ExhibitorService exhibitorService = new ExhibitorServiceImpl(exhibitorRepo);

        ProductRepository productRepo = new ProductRepositoryImpl();
        ProductService productService = new ProductServiceImpl(productRepo);

        ConferenceRepository conferenceRepo = new ConferenceRepositoryImpl(jdbc);
        ConferenceService conferenceService = new ConferenceServiceImpl(conferenceRepo);
 
        SpeakerRepository speakerRepo = new SpeakerRepositoryImpl(jdbc);
        SpeakerService speakerService = new SpeakerServiceImpl(speakerRepo);

        SponsorRepository sponsorRepo = new SponsorRepositoryImpl();
        SponsorService sponsorService = new SponsorServiceImpl(sponsorRepo);

        PartnerRepository partnerRepo = new PartnerRepositoryImpl();
        PartnerService partnerService = new PartnerServiceImpl(partnerRepo);

        FloorService floorService = new FloorService(jdbc);

        AuthService authService = new AuthServiceImpl(jdbc);
        
        DatabaseService databaseService = new DatabaseService(jdbc);

        // Initialize conference registration
        ConferenceRegistrationRepository registrationRepo = new ConferenceRegistrationRepositoryImpl(jdbc);
        ConferenceRegistrationController registrationController = new ConferenceRegistrationController(registrationRepo);

        // Register controllers
        AttendeeController.registerRoutes(router, attendeeService);
        ExhibitorController.registerRoutes(router, exhibitorService);
        ProductController.registerRoutes(router, productService);
        ConferenceController.registerRoutes(router, conferenceService);
        SpeakerController.registerRoutes(router, speakerService);
        SponsorController.registerRoutes(router, sponsorService);
        PartnerController.registerRoutes(router, partnerService);
        FloorController.registerRoutes(router, floorService);
        AuthController.registerRoutes(router, authService);
        DatabaseController.registerRoutes(router, databaseService);
        registrationController.registerRoutes(router);

        // Start HTTP server
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8888, http -> {
                if (http.succeeded()) {
                    System.out.println("HTTP server started on port 8888");
                    System.out.println("Vert.x backend started and JDBC client is ready!");
                } else {
                    System.out.println("HTTP server failed to start: " + http.cause().getMessage());
                }
            });
    }
}