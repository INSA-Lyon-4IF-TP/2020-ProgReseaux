///A Simple Web Server (WebServer.java)

package http.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static java.lang.System.exit;

/**
 * Java class representing WebServer.
 */
public class WebServer {

    /**
     * Starts servers according to a certain port given in parameter.
     *
     * @param port int port.
     */
    protected void start(int port) {
        ServerSocket s;

        System.out.println("Webserver starting up on port " + port);
        System.out.println("(press ctrl-c to exit)");
        try {
            // create the main server socket
            s = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }

        System.out.println("Waiting for connection");
        while (true) {
            try {
                Socket remote = s.accept();

                System.out.println("Connection from " + remote.getInetAddress().getHostAddress());
                BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
                PrintWriter out = new PrintWriter(remote.getOutputStream());


                String str = in.readLine();
                System.out.println(str);
                System.out.flush();

                String[] request = str.split(" "); // GET /index.html HTTP/1.1
                String method = request[0];
                String resource = request[1];
                String httpVersion = request[2];
                while (str != null && !str.equals("")) { // Reading the headers. They are ignored.
                    str = in.readLine();
                }

                switch (method) {
                    case "GET":
                        handleGETRequest(remote, out, resource, httpVersion);
                        break;
                    case "DELETE":
                        handleDELETERequest(out, resource, httpVersion);
                        break;
                    case "PUT":
                        handlePUTRequest(remote, out, resource, httpVersion);
                        break;
                    case "POST":
                        handlePOSTRequest(remote, out, resource, httpVersion);
                        break;
                    case "HEAD":
                        handleHEADRequest(remote, out, resource, httpVersion);
                        break;
                    case "CONNECT":
                    case "OPTIONS":
                    case "TRACE":
                    case "PATCH":
                        handleNotImplemented(out, httpVersion, method);
                        break;
                    default:
                        handleBadRequest(out, httpVersion, method);
                        break;
                }

                out.flush();
                remote.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles GET Request with a remote socket, a writer, the resource to get, and http Version.
     * Transmits requested file's content.
     *
     * @param remote      Remote socket.
     * @param out         PrintWriter
     * @param resource    Resource to get.
     * @param httpVersion String.
     */
    private void handleGETRequest(Socket remote, PrintWriter out, String resource, String httpVersion) {
        if (resource.equals("/")) resource = "index.html";

        if (resource.charAt(0) == '/') resource = resource.substring(1);

        String[] resourcePath = resource.split("/");
        if (resourcePath[0].equals("src")) {
            out.println(httpVersion + " 403 Forbidden");
            out.println("Server: Pierre&Nico's Handmade Web Server");
            out.println("");
            out.println("<h1>Access to this resource is forbidden</h1>");
            return;
        }

        if (Files.isDirectory(Path.of(resource))) {
            out.println(httpVersion + " 412 Precondition Failed");
            out.println("Server: Pierre&Nico's Handmade Web Server");
            out.println("");
            out.println("<h1> Acces denied : " + resource + " is a directory</h1>");
            return;
        }

        try {
            String filetype;
            filetype = Files.probeContentType(Path.of(resource)); // text/html or image/jpg or mp3
            // It seems like Files.probeContentType(path) does not recognize Javascript files correctly
            String[] fileTypeSplit = resource.split("\\.");
            if (filetype == null && fileTypeSplit.length >= 2 && fileTypeSplit[1].equals("js")) {
                filetype = "text/javascript";
            } else if (filetype == null) {
                filetype = "data/undefined";
            }
            File file = new File(resource);
            String filecategory = filetype.split("/")[0];
            FileReader fileReader = new FileReader(file); // If file is not found, FileNotFoundException is thrown and caught
            out.println(httpVersion + " 200 OK");
            out.println("Content-Type: " + filetype);
            out.println("Server: Pierre&Nico's Handmade Web Server");
            out.println("");
            out.flush();
            Files.copy(file.toPath(), remote.getOutputStream()); // Send the bytes directly
//            if (filecategory.equals("text")) { // It's a text file
//                BufferedReader bufferedReader = new BufferedReader(fileReader);
//                String line;
//                while ((line = bufferedReader.readLine()) != null) { // Reading the file line per line and sending each line to the client
//                    out.println(line);
//                }
//            } else { // Else, it's an image or a song or a video or anything else
//                Files.copy(file.toPath(), remote.getOutputStream()); // Send the bytes directly
//            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            out.println(httpVersion + " 404 NOT FOUND");
            out.println("Server: Pierre&Nico's Handmade Web Server");
            out.println("");
            out.println("<h1>404 Not Found</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles HEADER Request with a remote socket, a writer, the resource to get, and http Version.
     * Transmits header of requested file.
     *
     * @param remote      Remote socket.
     * @param out         PrintWriter
     * @param resource    Resource to get.
     * @param httpVersion String.
     */
    private void handleHEADRequest(Socket remote, PrintWriter out, String resource, String httpVersion) {
        if (resource.equals("/")) resource = "index.html";

        if (resource.charAt(0) == '/') resource = resource.substring(1);

        String[] resourcePath = resource.split("/");
        if (resourcePath[0].equals("src")) {
            out.println(httpVersion + " 403 Forbidden");
            out.println("Server: Pierre&Nico's Handmade Web Server");
            out.println("");
            return;
        }

        if (Files.isDirectory(Path.of(resource))) {
            out.println(httpVersion + " 412 Precondition Failed");
            out.println("Server: Pierre&Nico's Handmade Web Server");
            out.println("");
            return;
        }

        try {
            String filetype;
            filetype = Files.probeContentType(Path.of(resource)); // text/html or image/jpg or mp3
            // It seems like Files.probeContentType(path) does not recognize Javascript files correctly
            String[] fileTypeSplit = resource.split("\\.");
            if (filetype == null && fileTypeSplit.length >= 2 && fileTypeSplit[1].equals("js")) {
                filetype = "text/javascript";
            } else if (filetype == null) {
                filetype = "data/undefined";
            }
            if (Files.exists(Path.of(resource))) {
                out.println(httpVersion + " 200 OK");
                out.println("Content-Type: " + filetype);
                out.println("Server: Pierre&Nico's Handmade Web Server");
                out.println("");
                out.flush();
            } else {
                out.println(httpVersion + " 404 NOT FOUND");
                out.println("Server: Pierre&Nico's Handmade Web Server");
                out.println("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles POST Request with a remote socket, a writer, the resource to get, and http Version.
     * Appends to a given file what is given as a body of the request.
     *
     * @param remote      Remote socket.
     * @param out         PrintWriter
     * @param resource    Resource to get.
     * @param httpVersion String.
     */
    private void handlePOSTRequest(Socket remote, PrintWriter out, String resource, String httpVersion) {
        if (resource.equals("/")) resource = "index.html";

        if (resource.charAt(0) == '/') resource = resource.substring(1);

        String filetype;
        try {
            filetype = Files.probeContentType(Path.of(resource)); // text/html
            System.out.println(filetype);
            if (filetype.split("/")[0].equals("text")) {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(resource, true));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(remote.getInputStream()));
                String line;
                try {
                    remote.setSoTimeout(5000);
                    while ((line = bufferedReader.readLine()) != null) {
                        System.out.println(line);
                        bufferedWriter.append(line).append(String.valueOf('\n'));
                        bufferedWriter.flush();
                    }
                    bufferedWriter.close();
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    out.println(httpVersion + " 204 No Content");
                    out.println("Server: Pierre&Nico's Handmade Web Server");
                    out.println("");
                }
            } else {
                out.println(httpVersion + " 412 PRECONDITION FAILED");
                out.println("Server: Pierre&Nico's Handmade Web Server");
                out.println("");
            }
        } catch (IOException e) {
            e.printStackTrace();
            out.println(httpVersion + " 500 INTERNAL SERVER ERROR");
            out.println("Server: Pierre&Nico's Handmade Web Server");
            out.println("");
        }
    }

    /**
     * Handles DELETE Request with a writer, the resource to get, and http Version.
     * Deletes a given file.
     *
     * @param out         PrintWriter
     * @param resource    Resource to get.
     * @param httpVersion String.
     */
    private void handleDELETERequest(PrintWriter out, String resource, String httpVersion) {
        if (resource.equals("/")) resource = "index.html";

        if (resource.charAt(0) == '/') resource = resource.substring(1);

        try {
            if (Files.deleteIfExists(Path.of(resource))) {
                out.println(httpVersion + " 204 No Content");
                out.println("Server: Pierre&Nico's Handmade Web Server");
                out.println("");
            } else {
                out.println(httpVersion + " 404 Not Found");
                out.println("Server: Pierre&Nico's Handmade Web Server");
                out.println("");
                out.println("<h1>File not found</h1>");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles PUT Request with a remote socket, a writer, the resource to get, and http Version.
     * Writes to a given file what is given as a body of the request.
     *
     * @param remote      Remote socket.
     * @param out         PrintWriter
     * @param resource    Resource to get.
     * @param httpVersion String.
     */
    private void handlePUTRequest(Socket remote, PrintWriter out, String resource, String httpVersion) {
        if (resource.equals("/")) resource = "index.html";
        if (resource.charAt(0) == '/') resource = resource.substring(1);

        String responseCode, responseBody;
        if (Files.exists(Path.of(resource))) {
            responseCode = "200 OK";
            responseBody = "File replaced";
        } else {
            responseCode = "201 Created";
            responseBody = "File created";
        }

        try {
            remote.setSoTimeout(5000);
            Files.copy(remote.getInputStream(), Path.of(resource), StandardCopyOption.REPLACE_EXISTING);
        } catch (SocketTimeoutException e) {
            System.out.println(responseCode);
            out.println(httpVersion + " " + responseCode);
            out.println("Server: Pierre&Nico's Handmade Web Server");
            out.println("");
            out.println("<h1>" + responseBody + "</h1>");
        } catch (IOException e) {
            e.printStackTrace();
            out.println(httpVersion + " 500 Internal Server Error");
            out.println("Server: Pierre&Nico's Handmade Web Server");
            out.println("");
            out.println("<h1>Internal Server Error</h1>");
        }
    }

    /**
     * Function for 501 response.
     *
     * @param out         PrintWriter
     * @param httpVersion String.
     * @param method      String.
     */
    private void handleNotImplemented(PrintWriter out, String httpVersion, String method) {
        out.println(httpVersion + " 501 Not Implemented");
        out.println("Server: Pierre&Nico's Handmade Web Server");
        out.println("");
        out.println("<h1>" + method + " is not supported</h1>");
    }

    /**
     * Functio for 400 response.
     *
     * @param out         PrintWriter
     * @param httpVersion String.
     * @param method      String.
     */
    private void handleBadRequest(PrintWriter out, String httpVersion, String method) {
        out.println(httpVersion + " 400 Bad Request");
        out.println("Server: Pierre&Nico's Handmade Web Server");
        out.println("");
        out.println("<h1>" + method + " is wrong</h1>");
    }

    /**
     * Start the application.
     *
     * @param args Command line parameters are not used.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage : WebServer <port>");
            exit(1);
        }
        WebServer ws = new WebServer();
        ws.start(Integer.parseInt(args[0]));
    }
}
