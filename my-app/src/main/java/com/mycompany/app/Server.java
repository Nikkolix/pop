package com.mycompany.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

/**
 * HTTP Server running on a specified port with specific handlers for incoming requests in another thread
 */
public class Server {
    //attributes
    /**
     * the actual server Object
     */
    private final HttpServer server;

    /**
     * absolute root path to the data of the webserver
     */
    private static final String root = Paths.get("./web/dist/").toAbsolutePath().toString();

    /**
     * absolute root path to the data of the webserver
     */
    private static final String dataRoot = Paths.get("./web/data/").toAbsolutePath().toString();

    //constructor

    /**
     * construct a new server with a specified port and start it in another thread
     *
     * @param port to run server on
     * @throws IOException if (HttpServer.create throws)
     */
    public Server(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.createContext("/", new Handler());
        this.server.setExecutor(null); // creates a default executor
        this.server.start();
    }

    public void stop() {
        this.server.stop(0);
    }

    //subclasses

    /**
     * the handler for incoming HTTP requests
     */
    static class Handler implements HttpHandler {
        //attributes
        /**
         * the Planner created from the web ui, over createPlanner
         */
        private Planner planner;

        //public methods

        /**
         * handle the incoming HTTP requests
         *
         * @param t the exchange containing the request from the
         *          client and used to send the response
         * @throws IOException           if (thrown by handleGet)
         * @throws IllegalStateException if (t.getRequestMethod() is unexpected)
         */
        public void handle(@NotNull HttpExchange t) throws IOException, IllegalStateException {
            this.logger(t);
            try {
                switch (t.getRequestMethod()) {
                    case "GET" -> this.handleGet(t);
                    case "POST" -> this.handlePost(t);
                    case "PUT" -> this.handlePut(t);
                    case "DELETE" -> this.handleDelete(t);
                    default -> throw new IllegalStateException("Unexpected value: " + t.getRequestMethod());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * logs all incoming requests
         *
         * @param t the request to log
         */
        private void logger(@NotNull HttpExchange t) {
            System.out.println("log: http request " + t.getRequestMethod() + " " + t.getRequestURI());
        }

        /**
         * get the data of a file specified by path
         *
         * @param path to find the file
         * @return the data of read file
         * @throws IOException           if (thrown by FileInputStream.readAllBytes)
         * @throws FileNotFoundException if (thrown by new FileInputStream(String path))
         */
        private byte[] readFile(@NotNull String path) throws IOException, FileNotFoundException {
            FileInputStream fileInputStream = new FileInputStream(root + path);
            byte[] data = fileInputStream.readAllBytes();
            fileInputStream.close();
            return data;
        }

        private byte[] readDataFile(@NotNull String path) throws IOException, FileNotFoundException {
            FileInputStream fileInputStream = new FileInputStream(dataRoot + path);
            byte[] data = fileInputStream.readAllBytes();
            fileInputStream.close();
            return data;
        }


        /**
         * handle all get requests
         *
         * @param t incoming get request to handle
         * @throws IOException if (thrown by readAllBytes)
         */
        private void handleGet(@NotNull HttpExchange t) throws IOException {
            if (t.getRequestURI().toString().equals("/colors.json")) {
                try {
                    byte[] data = this.readDataFile(t.getRequestURI().toString());
                    t.getResponseHeaders().add("Content-Type", this.getMimeType(t.getRequestURI().getPath()));
                    t.sendResponseHeaders(200, data.length);
                    OutputStream os = t.getResponseBody();
                    os.write(data);
                    os.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return;
            }
            try {
                byte[] data = this.readFile(t.getRequestURI().toString());
                t.getResponseHeaders().add("Content-Type", this.getMimeType(t.getRequestURI().getPath()));
                t.sendResponseHeaders(200, data.length);
                OutputStream os = t.getResponseBody();
                os.write(data);
                os.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                byte[] data = this.readFile("/404.html");
                t.getResponseHeaders().add("Content-Type", this.getMimeType(t.getRequestURI().getPath()));
                t.sendResponseHeaders(200, data.length);
                OutputStream os = t.getResponseBody();
                os.write(data);
                os.close();
            }
        }


        /**
         * handle all post requests
         *
         * @param t incoming post request to handle
         */
        private void handlePost(@NotNull HttpExchange t) {
            if (t.getRequestURI().toString().equals("/runPlanner")) {
                try {
                    boolean undoable = false;
                    try {
                        this.planner.planSteps();
                    } catch (PlannerUndoableException e) {
                        undoable = true;
                        e.printStackTrace();
                    }
                    this.sendPlannerOutput(t, undoable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (t.getRequestURI().toString().equals("/nextPlanStep") || t.getRequestURI().toString().equals("/undoPlanStep")) {
                try {
                    boolean undoable = false;
                    try {
                        this.planner.planStep(true);
                    } catch (PlannerCompleteException e) {
                        e.printStackTrace();
                    } catch (PlannerUndoableException e) {
                        undoable = true;
                        e.printStackTrace();
                    }
                    this.sendPlannerOutput(t, undoable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (t.getRequestURI().toString().equals("/previousPlanStep")) {
                    try {
                        boolean undoable = false;
                        try {
                            this.planner.previousPlanStep();
                        } catch (PlannerUndoableException e) {
                            undoable = true;
                            e.printStackTrace();
                        }
                        this.sendPlannerOutput(t, undoable);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            } else if (t.getRequestURI().toString().equals("/createPlanner")) {
                try {
                    byte[] body = t.getRequestBody().readAllBytes();
                    JSONInput input = new JSONInput(new String(body));
                    this.planner = input.toPlanner();
                    this.sendPlannerOutput(t, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (t.getRequestURI().toString().equals("/loadtxt")) {
                try {
                    byte[] body = t.getRequestBody().readAllBytes();
                    FileReader fr = new FileReader(new StringReader(new String(body)));

                    PlannerList<ActionEffect> actionEffects = new PlannerList<>();
                    for (OperationEffect operationEffect : fr.readS0()) {
                        actionEffects.add(new ActionEffect(operationEffect));
                    }

                    PlannerList<ActionPrecondition> actionPreconditions = new PlannerList<>();
                    for (OperationPrecondition operationPrecondition : fr.readSz()) {
                        actionPreconditions.add(new ActionPrecondition(operationPrecondition));
                    }

                    this.planner = new Planner(fr.readOperations(),actionEffects,actionPreconditions);
                    this.sendPlannerOutput(t, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (t.getRequestURI().toString().equals("/colors.json")) {
                try {
                    byte[] body = t.getRequestBody().readAllBytes();
                    FileOutputStream fileOutputStream = new FileOutputStream(dataRoot + "/colors.json");
                    fileOutputStream.write(body);
                    fileOutputStream.close();
                    t.sendResponseHeaders(200, 0);
                    t.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //todo add comment
        private void handlePut(@NotNull HttpExchange t) {
            //todo
        }

        //todo add comment
        private void handleDelete(@NotNull HttpExchange t) {
            //todo
        }

        //todo add comment
        private String getMimeType(@NotNull String uri) {
            String[] uriParts = uri.split("\\.");
            switch (uriParts[uriParts.length - 1]) {
                case "js" -> {
                    return "text/javascript";
                }
                case "css" -> {
                    return "text/css";
                }
                case "json" -> {
                    return "application/json";
                }
                default -> {
                    return "text/html";
                }
            }
        }

        private void sendPlannerOutput(@NotNull HttpExchange t, boolean undoable) throws IOException {
            JSONOutput out = new JSONOutput(this.planner, undoable);
            JSONObject outObj = out.toJSON();
            byte[] data = outObj.toString(2).getBytes();
            t.getResponseHeaders().add("Content-Type", "application/json");
            t.sendResponseHeaders(200, data.length);
            t.getResponseBody().write(data);
            t.close();
        }
    }

    //getter

    //setter

    //public methods
}
