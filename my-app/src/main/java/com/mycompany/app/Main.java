package com.mycompany.app;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
//        try {
//            FileReader fileReader = new FileReader("input.txt");
//            Planner planner = new Planner(fileReader.readActions(), new State(fileReader.readS0()), new State(fileReader.readSz()));
//            try {
//                planner.planSteps();
//            } catch(PlannerUndoableException e) {
//
//            }
//            System.out.println(planner);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        try {
            Server server = new Server(8000);
            System.out.println("server is running: http://localhost:8000/index.html (press any key to stop server)");
            System.in.read();
            server.stop();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}